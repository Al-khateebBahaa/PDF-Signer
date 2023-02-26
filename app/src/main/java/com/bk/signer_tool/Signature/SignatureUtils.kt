package com.bk.signer_tool.Signature

import android.graphics.Bitmap
import android.graphics.RectF
import kotlin.Throws
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.*
import android.widget.*
import com.bk.signer_tool.utils.FREE_HAND_FOLDER_NAME
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

object SignatureUtils {



    private var EXTRA_WIDTH_PADDING: Int = 0
    fun saveSignature(
        context: Context,
        signatureView: SignatureView,
        callback: CreateSignatureCallback
    ) {
        val arrayList: ArrayList<ArrayList<Float>>? = signatureView.inkList
        val rectF: RectF? = signatureView.boundingBox
        if (arrayList!!.size != 0) {
            val openFileOutput: OutputStream
            val viewHolder: ViewHolder = ViewHolder()
            viewHolder.inkList = arrayList
            viewHolder.boundingBox = rectF
            viewHolder.inkColor = signatureView.mStrokeColor
            viewHolder.strokeWidth = signatureView.strokeWidth
            val myDir = File("${context.filesDir}/$FREE_HAND_FOLDER_NAME")
            val uniqueString: String = UUID.randomUUID().toString()
            val file = File(myDir.absolutePath, uniqueString)
            val gson = Gson()
            try {
                openFileOutput = FileOutputStream(file)
                writeToStream(openFileOutput, gson.toJson(viewHolder))
                openFileOutput.close()
                callback.onSignatureCreatedResult(true, file)
            } catch (e: IOException) {
                e.printStackTrace()
                callback.onSignatureCreatedResult(false)

            }
        }
    }

    fun createFreeHandView(i: Int, file: File?, context: Context?): SignatureView? {
        val i2: Int = i - 30
        var signatureView: SignatureView? = null
        try {
            val readSignatureHolder: ViewHolder? = readSignatureHolder(context, file)
            if (readSignatureHolder != null) {
                if ((i.toFloat()) > readSignatureHolder.boundingBox!!.height()) {
                    EXTRA_WIDTH_PADDING = 30
                    return createFreeHandView(i, i, file, context)
                }
                val rectF: RectF? = readSignatureHolder.boundingBox
                val height: Float = (i2.toFloat()) / readSignatureHolder.boundingBox!!.height()
                val width: Int =
                    (((readSignatureHolder.boundingBox!!.width() * height).toInt()) + 30) + 30
                val arrayList: ArrayList<ArrayList<Float>> =
                    readSignatureHolder.inkList ?: arrayListOf()
                val f: Float = 15f
                signatureView = createFreeHandView(
                    width,
                    i,
                    arrayList,
                    rectF,
                    height,
                    height,
                    (rectF!!.left * height) - f,
                    (rectF.top * height) - f,
                    readSignatureHolder.strokeWidth,
                    readSignatureHolder.inkColor,
                    context
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return signatureView
    }

    fun createImageView(i: Int, file: Bitmap?, context: Context?): ImageView? {
        val i2: Int = i - 30
        var signatureView: ImageView? = null
        try {
            val height: Float = (i2.toFloat()) / file!!.getHeight()
            val width: Int = (((file.getWidth() * height).toInt()) + 30) + 30
            signatureView = ImageView(context)
            val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(width, i)
            signatureView.setLayoutParams(layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return signatureView
    }

    private fun createFreeHandView(
        i: Int,
        i2: Int,
        arrayList: ArrayList<ArrayList<Float>>?,
        rectF: RectF?,
        f: Float,
        f2: Float,
        f3: Float,
        f4: Float,
        f5: Float,
        strokeColor: Int,
        context: Context?
    ): SignatureView {
        val signatureView = SignatureView(context, i, i2)
        signatureView.strokeWidth = f5
        signatureView.setStrokeColor(strokeColor)
        signatureView.setmActualColor(strokeColor)
        signatureView.setEditable(false)
        val arrayList2: ArrayList<ArrayList<Float>>? = arrayList
        signatureView.initializeInkList(arrayList)
        signatureView.fillColor()
        signatureView.scaleAndTranslatePath(arrayList2, rectF, f, f2, f3, f4)
        signatureView.invalidate()
        return signatureView
    }

    fun createFreeHandView(i: Int, i2: Int, file: File?, context: Context?): SignatureView? {
        var e: Exception
        var signatureView: SignatureView? = null
        try {
            val readSignatureHolder: ViewHolder? = readSignatureHolder(context, file)
            if (readSignatureHolder != null) {
                val rectF: RectF? = readSignatureHolder.boundingBox
                val fitXYScale: Float =
                    if ((rectF!!.height() > 1.0f || rectF.width() > 1.0f)) getFitXYScale(
                        i,
                        i2,
                        file,
                        context
                    ) else 1.0f
                val f: Float = i2.toFloat()
                var i3: Int = 15
                val height: Int =
                    if (f >= readSignatureHolder.boundingBox!!.height() * fitXYScale) ((f - (readSignatureHolder.boundingBox!!.height() * fitXYScale)) / 2.0f).toInt() else 15
                val f2: Float = i.toFloat()
                if (f2 >= readSignatureHolder.boundingBox!!.width() * fitXYScale) {
                    i3 =
                        ((f2 - (readSignatureHolder.boundingBox!!.width() * fitXYScale)) / 2.0f).toInt()
                }
                val createFreeHandView: SignatureView = createFreeHandView(
                    EXTRA_WIDTH_PADDING + i,
                    i2,
                    readSignatureHolder.inkList,
                    rectF,
                    fitXYScale,
                    fitXYScale,
                    (rectF.left * fitXYScale) - (i3.toFloat()),
                    (rectF.top * fitXYScale) - (height.toFloat()),
                    readSignatureHolder.strokeWidth,
                    readSignatureHolder.inkColor,
                    context
                )
                try {
                    EXTRA_WIDTH_PADDING = 0
                    return createFreeHandView
                } catch (e2: Exception) {
                    signatureView = createFreeHandView
                    e = e2
                }
            }
        } catch (e3: Exception) {
            e = e3
            e.printStackTrace()
            return signatureView
        }
        return signatureView
    }

    @Throws(IOException::class)
    fun writeToStream(outputStream: OutputStream?, str: String?) {
        val outputStreamWriter: OutputStreamWriter = OutputStreamWriter(outputStream)
        outputStreamWriter.write(str)
        outputStreamWriter.close()
    }

    fun readSignatureHolder(context: Context?, fileStreamPath: File?): ViewHolder? {
        if (fileStreamPath!!.exists()) {
            var openFileInput: InputStream? = null
            try {
                openFileInput = FileInputStream(fileStreamPath)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            try {
                return Gson().fromJson<Any>(
                    getStringFromStream(openFileInput),
                    object : TypeToken<ViewHolder?>() {}.getType()
                ) as ViewHolder?
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun getStringFromStream(inputStream: InputStream?): String {
        val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder: StringBuilder = StringBuilder()
        while (true) {
            val readLine: String? = bufferedReader.readLine()
            if (readLine != null) {
                stringBuilder.append(readLine)
            } else {
                bufferedReader.close()
                return stringBuilder.toString()
            }
        }
    }

    private fun getFitXYScale(i: Int, i2: Int, file: File?, context: Context?): Float {
        val readSignatureHolder: ViewHolder? = readSignatureHolder(context, file)
        if (readSignatureHolder != null) {
            var f: Float = 0.0f
            if (readSignatureHolder.boundingBox!!.height() != 0.0f) {
                val width: Float =
                    readSignatureHolder.boundingBox!!.width() / readSignatureHolder.boundingBox!!.height()
                var obj: Any? = 1
                var i3: Int = i - 15
                var i4: Int = i2 - 15
                while (obj != null) {
                    if (width > ((i3 / i4).toFloat())) {
                        f = (i3.toFloat()) / readSignatureHolder.boundingBox!!.width()
                    } else {
                        f = (i4.toFloat()) / readSignatureHolder.boundingBox!!.height()
                    }
                    if ((i2.toFloat()) <= readSignatureHolder.boundingBox!!.height() * f) {
                        i4 -= 7
                    } else if ((i.toFloat()) > readSignatureHolder.boundingBox!!.width() * f) {
                        obj = null
                    } else {
                        i3 -= 7
                    }
                }
                return f
            }
        }
        return 1.0f
    }

    fun getSignatureWidth(i: Int, file: File?, context: Context?): Int {
        val i2: Int = i - 30
        try {
            val readSignatureHolder: ViewHolder? = readSignatureHolder(context, file)
            if (readSignatureHolder == null || (i.toFloat()) > readSignatureHolder.boundingBox!!.height()) {
                return i
            }
            return (((readSignatureHolder.boundingBox!!.width() * ((i2.toFloat()) / readSignatureHolder.boundingBox!!.height())).toInt()) + 30) + 30
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return i
    }

    class ViewHolder {
        var boundingBox: RectF? = null
        var inkColor: Int = 0
        var strokeWidth: Float = 0f
        var inkList: ArrayList<ArrayList<Float>>? = null
    }
}