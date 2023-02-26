package com.bk.signer_tool.Signature

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bk.signer_tool.R
import java.io.File

const val NEW_SIGN_URI = "new_sign_uri"

class FreeHandActivity : AppCompatActivity() {
    private var isFreeHandCreated: Boolean = false
    private var signatureView: SignatureView? = null
    private var inkWidth: SeekBar? = null
    private var menu: Menu? = null
    var saveItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.com_bk_signer_activity_free_hand)
        val ab: ActionBar? = getSupportActionBar()
        ab!!.setDisplayHomeAsUpEnabled(true)
        signatureView = findViewById(R.id.inkSignatureOverlayView)
        inkWidth = findViewById(R.id.seekBar)
        inkWidth?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            public override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                signatureView?.strokeWidth = progress.toFloat()
            }

            public override fun onStartTrackingTouch(seekBar: SeekBar) {}
            public override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        findViewById<View>(R.id.action_clear).setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                clearSignature()
                enableClear(false)
                enableSave(false)
            }
        })
    }

    public override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.com_bk_signer_freehandmenu, menu)
        this.menu = menu
        saveItem = menu.findItem(R.id.signature_save)
        saveItem?.setEnabled(false)
        saveItem?.getIcon()!!.setAlpha(130)
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        if (id == R.id.signature_save) {
            saveFreeHand()
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun onRadioButtonClicked(view: View) {
        // Is the button now checked?
        val checked: Boolean = (view as RadioButton).isChecked()
        when (view.getId()) {
            R.id.radioBlack -> if (checked) {
                signatureView!!.setStrokeColor(
                    ContextCompat.getColor(
                        this@FreeHandActivity,
                        R.color.inkblack
                    )
                )
            }
            R.id.radioRed -> if (checked) signatureView!!.setStrokeColor(
                ContextCompat.getColor(
                    this@FreeHandActivity,
                    R.color.inkred
                )
            )
            R.id.radioBlue -> if (checked) signatureView!!.setStrokeColor(
                ContextCompat.getColor(
                    this@FreeHandActivity,
                    R.color.inkblue
                )
            )
            R.id.radiogreen -> if (checked) signatureView!!.setStrokeColor(
                ContextCompat.getColor(
                    this@FreeHandActivity,
                    R.color.inkgreen
                )
            )
        }
    }

    public override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
    }

    fun clearSignature() {
        signatureView!!.clear()
        signatureView!!.setEditable(true)
    }

    fun enableClear(z: Boolean) {
        val button: ImageButton = findViewById(R.id.action_clear)
        button.setEnabled(z)
        if (z) {
            button.setAlpha(1.0f)
        } else {
            button.setAlpha(0.5f)
        }
    }

    fun enableSave(z: Boolean) {
        if (z) {
            saveItem!!.getIcon()!!.setAlpha(255)
        } else {
            saveItem!!.getIcon()!!.setAlpha(130)
        }
        saveItem!!.setEnabled(z)
    }

    private fun saveFreeHand() {
        val localSignatureView: SignatureView = findViewById(R.id.inkSignatureOverlayView)
        val localArrayList: ArrayList<*>? = localSignatureView.inkList
        if ((localArrayList != null) && (localArrayList.size > 0)) {
            isFreeHandCreated = true
        }
        SignatureUtils.saveSignature(
            applicationContext,
            localSignatureView,
            object : CreateSignatureCallback {
                override fun onSignatureCreatedResult(isSuccess: Boolean, signFile: File?) {
                    val data = Intent()

                    if (isSuccess)
                        data.putExtra(NEW_SIGN_URI, signFile?.toUri().toString())

                    data.action = "Result OK"
                    setResult(RESULT_OK, data)
                    finish()
                }

            })
    }
}