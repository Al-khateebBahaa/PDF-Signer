package com.bk.signer_tool.core.signer

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bk.signer_tool.Signature.SignatureActivity
import com.bk.signer_tool.core.Errors.SignError
import com.bk.signer_tool.core.callbacks.SignerCallback
import com.bk.signer_tool.digital_signer.DigitalSignatureActivity
import com.bk.signer_tool.digital_signer.SIGNER_SDK_FILE_URI
import com.bk.signer_tool.digital_signer.SIGNER_SDK_NEW_FILE_NAME
import java.io.File

public class SignManager internal constructor(
    private val delegate: SignerCallback,
    private val mFileCallbackIntent: ActivityResultLauncher<Intent>
) {


    public fun startSigning(
        inputFile: File?,
        returnedFileName: String? = null, // optional return file name
        activity: AppCompatActivity
    ) {

        if (inputFile == null) {
            delegate.onSignFailed(SignError.FILE_NOT_FOUND.error)
            return
        }
        if (!inputFile.exists()) {
            delegate.onSignFailed(SignError.FILE_NOT_FOUND.error)
            return
        }

        if (!inputFile.name.endsWith(".pdf")) {
            delegate.onSignFailed(SignError.INVALID_FILE_TYPE.error)
            return
        }


        val intent = Intent(activity, DigitalSignatureActivity::class.java)
        intent.putExtra(SIGNER_SDK_FILE_URI, inputFile.toUri().toString())


        val finalFilaName = if (returnedFileName.isNullOrBlank()) null else
            if (returnedFileName.endsWith(".pdf")) returnedFileName else returnedFileName.plus(
                ".pdf"
            )

        intent.putExtra(SIGNER_SDK_NEW_FILE_NAME, finalFilaName)

        mFileCallbackIntent.launch(intent)

    }


    public fun openSigningLibrary(context: Context) {
        context.startActivity(
            Intent(
                context,
                SignatureActivity::class.java
            )
        )
    }


}