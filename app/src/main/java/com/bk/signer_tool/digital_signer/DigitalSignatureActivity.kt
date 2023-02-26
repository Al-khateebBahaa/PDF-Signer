package com.bk.signer_tool.digital_signer

import com.bk.signer_tool.PDF.PDSPDFDocument
import com.bk.signer_tool.Document.PDSPageViewer
import android.graphics.Bitmap
import android.graphics.RectF
import com.bk.signer_tool.Signature.SignatureUtils
import com.bk.signer_tool.PDSModel.PDSElement.PDSElementType
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import com.bk.signer_tool.Signature.SignatureActivity
import com.bk.signer_tool.imageviewer.PDSPageAdapter
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bk.signer_tool.Document.PDSElementViewer
import com.bk.signer_tool.PDF.PDSPDFPage
import com.bk.signer_tool.PDSModel.PDSElement
import com.bk.signer_tool.R
import com.bk.signer_tool.databinding.ComBkSignerActivityDigitalSignatureBinding
import com.bk.signer_tool.databinding.ComBkSignerOptiondialogBinding
import com.bk.signer_tool.utils.ACTIVITY_ACTION
import com.bk.signer_tool.utils.SUBMIT_FILE_NAME
import com.bk.signer_tool.utils.ViewUtils
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfSignatureAppearance
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.pdf.security.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.Certificate
import kotlin.random.Random


const val SIGNER_SDK_FILE_URI = "file_data"
const val SIGNER_SDK_NEW_FILE_NAME = "file_name"
const val SIGNER_SDK_NEW_FILE_ERROR = "file_error"

class DigitalSignatureActivity : AppCompatActivity(), ProgressCallback {

    var pdfData: Uri? = null
    var imageAdapter: PDSPageAdapter? = null
    var isFirstTap: Boolean = true
    private var mVisibleWindowHt: Int = 0
    var document: PDSPDFDocument? = null
        private set
    private var mdigitalID: Uri? = null
    private var mdigitalIDPassword: String? = null
    private var mmenu: Menu? = null
    private val mUIElemsHandler: UIElementsHandler = UIElementsHandler(this)
    private var passwordalertDialog: AlertDialog? = null
    private var signatureOptionDialog: AlertDialog? = null
    private var keyStore: KeyStore? = null
    private var alises: String? = null
    private var isSigned: Boolean = false

    private lateinit var mBinding: ComBkSignerActivityDigitalSignatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ComBkSignerActivityDigitalSignatureBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val fileUriAsStr = intent.getStringExtra(SIGNER_SDK_FILE_URI)
        if (!fileUriAsStr.isNullOrBlank()) {
            openPDFViewer(fileUriAsStr.toUri())
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (requestCode == READ_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (result != null) {
                    pdfData = result.data
                    openPDFViewer(pdfData)
                }
            } else {
                finish()
            }
        }
        if (requestCode == SIGNATURE_Request_CODE && resultCode == RESULT_OK) {
            val returnValue: String? = result!!.getStringExtra(SUBMIT_FILE_NAME)
            val fi = File(returnValue)
            addElement(
                fi, SignatureUtils.getSignatureWidth(
                    resources.getDimension(R.dimen.sign_field_default_height).toInt(),
                    fi,
                    applicationContext
                ).toFloat(), resources.getDimension(R.dimen.sign_field_default_height)
            )
        }
        if (requestCode == DIGITALID_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (result != null) {
                    mdigitalID = result.getData()
                    getFilePassword()
                }
            } else {
                Toast.makeText(
                    this@DigitalSignatureActivity,
                    "Digital certificate is not added with Signature",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (result != null) {
                val imageData: Uri? = result.getData()
                var bitmap: Bitmap? = null
                try {
                    val input: InputStream? = getContentResolver().openInputStream((imageData)!!)
                    bitmap = BitmapFactory.decodeStream(input)
                    input!!.close()
                    if (bitmap != null) addElement(
                        bitmap,
                        resources.getDimension(R.dimen.sign_field_default_height),
                        resources.getDimension(R.dimen.sign_field_default_height)
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.com_bk_signer_main, menu)
        mmenu = menu
        val saveItem: MenuItem = mmenu!!.findItem(R.id.action_save)
        saveItem.icon?.alpha = 130
        val signItem: MenuItem = mmenu!!.findItem(R.id.action_sign)
        signItem.icon?.alpha = 255
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_sign) {
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

            val dialogView = ComBkSignerOptiondialogBinding.inflate(layoutInflater)

            dialogBuilder.setView(dialogView.root)


            dialogView.fromCollection.setOnClickListener {
                val intent = Intent(applicationContext, SignatureActivity::class.java)
                intent.putExtra(ACTIVITY_ACTION, true)
                startActivityForResult(intent, SIGNATURE_Request_CODE)
                signatureOptionDialog?.dismiss()
            }

            dialogView.fromImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/jpeg"
                val mimetypes: Array<String> = arrayOf("image/jpeg", "image/png")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                startActivityForResult(intent, IMAGE_REQUEST_CODE)
                signatureOptionDialog?.dismiss()
            }
            signatureOptionDialog = dialogBuilder.create()
            signatureOptionDialog?.show()
            return true
        }
        if (id == R.id.action_save) {
            savePDFDocument()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (isSigned) {
            AlertDialog.Builder(this).setTitle("Save Document")
                .setMessage("Want to save your changes to PDF document?").setPositiveButton(
                    "Save"
                ) { dialog, which -> savePDFDocument() }.setNegativeButton(
                    "Exit"
                ) { dialog, which -> finish() }.show()
        } else {
            finish()
        }
    }

    private fun openPDFViewer(pdfData: Uri?) {
        try {
            document = PDSPDFDocument(this, pdfData)
            document?.open()

            if (document == null) return

            imageAdapter = PDSPageAdapter(supportFragmentManager, document!!)
            updatePageNumber(1)
            mBinding.viewpager.adapter = imageAdapter
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this@DigitalSignatureActivity,
                "Cannot open PDF, either PDF is corrupted or password protected",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }


    private fun computeVisibleWindowHtForNonFullScreenMode(): Int {
        return findViewById<View>(R.id.docviewer).getHeight()
    }

    val visibleWindowHeight: Int
        get() {
            if (mVisibleWindowHt == 0) {
                mVisibleWindowHt = computeVisibleWindowHtForNonFullScreenMode()
            }
            return mVisibleWindowHt
        }

    private fun getFilePassword() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = getLayoutInflater()
        val dialogView: View = inflater.inflate(R.layout.com_bk_signer_passworddialog, null)
        dialogBuilder.setView(dialogView)
        val password: EditText = dialogView.findViewById(R.id.passwordText)
        val submit: Button = dialogView.findViewById(R.id.passwordSubmit)
        submit.setOnClickListener {
            if (password.length() == 0) {
                Toast.makeText(
                    this@DigitalSignatureActivity, "Password can't be blank", Toast.LENGTH_LONG
                ).show()
            } else {
                mdigitalIDPassword = password.getText().toString()
                val provider = BouncyCastleProvider()
                Security.addProvider(provider)
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(
                        (mdigitalID)!!
                    )
                    keyStore = KeyStore.getInstance("pkcs12", provider.getName())
                    keyStore?.load(inputStream, mdigitalIDPassword!!.toCharArray())
                    alises = keyStore?.aliases()?.nextElement()
                    passwordalertDialog!!.dismiss()
                    Toast.makeText(
                        this@DigitalSignatureActivity,
                        "Digital certificate is added with Signature",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    if (e.message!!.contains("wrong password")) {
                        Toast.makeText(
                            this@DigitalSignatureActivity,
                            "Password is incorrect or certificate is corrupted",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@DigitalSignatureActivity,
                            "Something went wrong while adding Digital certificate",
                            Toast.LENGTH_LONG
                        ).show()
                        passwordalertDialog!!.dismiss()
                    }
                    e.printStackTrace()
                }
            }
        }
        passwordalertDialog = dialogBuilder.create()
        passwordalertDialog?.show()
    }

    fun invokeMenuButton(disableButtonFlag: Boolean) {
        val saveItem: MenuItem = mmenu!!.findItem(R.id.action_save)
        saveItem.isEnabled = disableButtonFlag
        mmenu!!.findItem(R.id.action_sign)
        isSigned = disableButtonFlag
        if (disableButtonFlag) {
            saveItem.icon!!.alpha = 255
        } else {
            saveItem.icon!!.alpha = 130
        }
    }

    private fun addElement(file: File?, f: Float, f2: Float) {
        val focusedChild: View? = mBinding.viewpager.focusedChild
        if (focusedChild != null) {
            val fASPageViewer: PDSPageViewer? =
                (focusedChild as ViewGroup).getChildAt(0) as PDSPageViewer?
            if (fASPageViewer != null) {
                val visibleRect: RectF = fASPageViewer.visibleRect
                val width: Float = (visibleRect.left + (visibleRect.width() / 2.0f)) - (f / 2.0f)
                val height: Float = (visibleRect.top + (visibleRect.height() / 2.0f)) - (f2 / 2.0f)
                fASPageViewer.lastFocusedElementViewer


                fASPageViewer.createElement(
                    PDSElementType.PDSElementTypeSignature, file, width, height, f, f2
                )
            }
            invokeMenuButton(true)
        }
    }

    private fun addElement(bitmap: Bitmap?, f: Float, f2: Float) {
        val focusedChild: View? = mBinding.viewpager.focusedChild
        if (focusedChild != null && bitmap != null) {
            val fASPageViewer: PDSPageViewer? =
                (focusedChild as ViewGroup).getChildAt(0) as PDSPageViewer?
            if (fASPageViewer != null) {
                val visibleRect: RectF = fASPageViewer.visibleRect
                val width: Float = (visibleRect.left + (visibleRect.width() / 2.0f)) - (f / 2.0f)
                val height: Float = (visibleRect.top + (visibleRect.height() / 2.0f)) - (f2 / 2.0f)
                fASPageViewer.lastFocusedElementViewer

                fASPageViewer.createElement(
                    PDSElementType.PDSElementTypeImage, bitmap, width, height, f, f2
                )
            }
            invokeMenuButton(true)
        }
    }

    fun updatePageNumber(i: Int) {
        val textView: TextView = findViewById<View>(R.id.pageNumberTxt) as TextView
        findViewById<View>(R.id.pageNumberOverlay).setVisibility(View.VISIBLE)
        val stringBuilder = StringBuilder()
        stringBuilder.append(i)
        stringBuilder.append("/")
        stringBuilder.append(document?.numPages)
        textView.text = stringBuilder.toString()
        resetTimerHandlerForPageNumber(1000)
    }

    private fun resetTimerHandlerForPageNumber(i: Int) {
        mUIElemsHandler.removeMessages(1)
        val message = Message()
        message.what = 1
        mUIElemsHandler.sendMessageDelayed(message, i.toLong())
    }

    private fun fadePageNumberOverlay() {
        val loadAnimation: Animation =
            AnimationUtils.loadAnimation(this, R.anim.com_bk_signer_fade_out)
        val pageNumberOverlay: View = findViewById(R.id.pageNumberOverlay)
        if (pageNumberOverlay.visibility == View.VISIBLE) {
            pageNumberOverlay.startAnimation(loadAnimation)
            pageNumberOverlay.visibility = View.INVISIBLE
        }
    }

    private class UIElementsHandler constructor(fASDocumentViewer: DigitalSignatureActivity?) :
        Handler() {
        private val mActivity: WeakReference<DigitalSignatureActivity>

        init {
            mActivity = WeakReference<DigitalSignatureActivity>(fASDocumentViewer)
        }

        public override fun handleMessage(message: Message) {
            val fASDocumentViewer: DigitalSignatureActivity? = mActivity.get()
            if (fASDocumentViewer != null && message.what == 1) {
                fASDocumentViewer.fadePageNumberOverlay()
            }
            super.handleMessage(message)
        }
    }


    private fun savePDFDocument() {
        val finalFileName =
            intent.getStringExtra(SIGNER_SDK_NEW_FILE_NAME) ?: Random.nextInt().toString()

        returnSignedFile(finalFileName)

    }


    private fun returnSignedFile(mfileName: String) = CoroutineScope(Dispatchers.Default).launch {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
        withContext(Main) {
            controlProgressCallback(true)
        }

        val document: PDSPDFDocument? = document
        val root: File = filesDir
        val myDir = File("$root/DigitalSignature")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val file = File(myDir.absolutePath, mfileName)
        if (file.exists()) file.delete()
        try {
            val stream: InputStream? = document!!.stream
            val os = FileOutputStream(file)
            val reader = PdfReader(stream)
            var signer: PdfStamper? = null
            var createBitmap: Bitmap? = null
            for (i in 0 until document.numPages) {
                val mediabox: Rectangle = reader.getPageSize(i + 1)
                for (j in 0 until document.getPage(i)!!.numElements) {
                    val page: PDSPDFPage? = document.getPage(i)
                    val element: PDSElement = page!!.getElement(j)
                    val bounds: RectF? = element.rect
                    if (element.type == PDSElementType.PDSElementTypeSignature) {
                        val viewer: PDSElementViewer? = element.mElementViewer
                        val dummy: View? = viewer?.elementView
                        val view: View? = ViewUtils.createSignatureView(
                            this@DigitalSignatureActivity,
                            element,
                            viewer?.pageViewer?.toViewCoordinatesMatrix
                        )
                        createBitmap = Bitmap.createBitmap(
                            dummy!!.getWidth(), dummy.getHeight(), Bitmap.Config.ARGB_8888
                        )
                        view!!.draw(Canvas(createBitmap))
                    } else {
                        createBitmap = element.bitmap
                    }
                    val saveBitmap = ByteArrayOutputStream()
                    createBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, saveBitmap)
                    val byteArray: ByteArray = saveBitmap.toByteArray()
                    createBitmap.recycle()
                    val sigimage: Image = Image.getInstance(byteArray)
                    if ((this@DigitalSignatureActivity.alises != null) && (this@DigitalSignatureActivity.keyStore != null) && (this@DigitalSignatureActivity.mdigitalIDPassword != null)) {
                        val ks: KeyStore? = this@DigitalSignatureActivity.keyStore
                        val alias: String? = this@DigitalSignatureActivity.alises
                        val pk: PrivateKey = ks!!.getKey(
                            alias, this@DigitalSignatureActivity.mdigitalIDPassword!!.toCharArray()
                        ) as PrivateKey
                        val chain: Array<Certificate> = ks.getCertificateChain(alias)
                        if (signer == null) signer =
                            PdfStamper.createSignature(reader, os, '\u0000')
                        val appearance: PdfSignatureAppearance = signer!!.getSignatureAppearance()
                        val top: Float = mediabox.getHeight() - (bounds!!.top + bounds.height())
                        appearance.setVisibleSignature(
                            Rectangle(
                                bounds.left,
                                top,
                                bounds.left + bounds.width(),
                                top + bounds.height()
                            ), i + 1, "sig" + j
                        )
                        appearance.renderingMode = PdfSignatureAppearance.RenderingMode.GRAPHIC
                        appearance.signatureGraphic = sigimage
                        val digest: ExternalDigest = BouncyCastleDigest()
                        val signature: ExternalSignature =
                            PrivateKeySignature(pk, DigestAlgorithms.SHA256, null)
                        MakeSignature.signDetached(
                            appearance,
                            digest,
                            signature,
                            chain,
                            null,
                            null,
                            null,
                            0,
                            MakeSignature.CryptoStandard.CADES
                        )
                    } else {
                        if (signer == null) signer = PdfStamper(reader, os, '\u0000')
                        val contentByte: PdfContentByte = signer.getOverContent(i + 1)
                        sigimage.alignment = Image.ALIGN_UNDEFINED
                        sigimage.scaleToFit(bounds!!.width(), bounds.height())
                        sigimage.setAbsolutePosition(
                            bounds.left - (sigimage.scaledWidth - bounds.width()) / 2,
                            mediabox.height - (bounds.top + bounds.height())
                        )
                        contentByte.addImage(sigimage)
                    }
                }
            }
            signer?.close()
            reader.close()
            withContext(IO) {
                os.close()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            if (file.exists()) {
                file.delete()
            }
            withContext(Main) {

                setResult(RESULT_CANCELED, Intent().apply {
                    putExtra(SIGNER_SDK_NEW_FILE_ERROR, e.message)
                })
                controlProgressCallback(false)
                finish()
            }

            return@launch
        }

        withContext(Main) {

            setResult(RESULT_OK, Intent().apply {
                putExtra(SIGNER_SDK_NEW_FILE_NAME, file.path)
            })
            controlProgressCallback(false)
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        document?.close()
    }

    companion object {
        private val READ_REQUEST_CODE: Int = 42
        private val SIGNATURE_Request_CODE: Int = 43
        private val IMAGE_REQUEST_CODE: Int = 45
        private val DIGITALID_REQUEST_CODE: Int = 44
    }

    override fun controlProgressCallback(isVisible: Boolean) {
        mBinding.savingProgress.isVisible = isVisible
    }


}