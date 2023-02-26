package com.bk.signer_tool.core.signer

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bk.signer_tool.core.Errors.PdfSignerInitialingException
import com.bk.signer_tool.core.Errors.SignError
import com.bk.signer_tool.core.callbacks.SignerCallback
import com.bk.signer_tool.digital_signer.SIGNER_SDK_NEW_FILE_ERROR
import com.bk.signer_tool.digital_signer.SIGNER_SDK_NEW_FILE_NAME
import java.io.File

public object SignBuilder {


    private var mDelegate: SignerCallback? = null
    private var mFileCallbackIntent: ActivityResultLauncher<Intent>? = null

    public fun setCallback(delegate: SignerCallback): SignBuilder {
        mDelegate = delegate
        return this
    }


    @Throws(PdfSignerInitialingException::class)
    public fun build(): SignManager {

        val validationResult = validateMembers()
        if (validationResult != null)
            throw validationResult


        return SignManager(
            delegate = mDelegate!!,
            mFileCallbackIntent!!
        )


    }

    private fun validateMembers(): PdfSignerInitialingException? {

        if (mDelegate == null)
            return PdfSignerInitialingException(SignError.DELEGATE_NOT_FOUND.error)

        if (mFileCallbackIntent == null)
            return PdfSignerInitialingException(SignError.ON_CREATE_ERROR.error)

        return null

    }


    public fun onCreate(activity: AppCompatActivity) {
        mFileCallbackIntent =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                Log.e("AAA", "onCreate: result is ${it.resultCode}", )

                if (it.resultCode == Activity.RESULT_OK) {
                    mDelegate?.onSignResult(
                        true,
                        File(it.data?.getStringExtra(SIGNER_SDK_NEW_FILE_NAME)!!)
                    )
                } else {
                    mDelegate?.onSignFailed(it.data?.getStringExtra(SIGNER_SDK_NEW_FILE_ERROR) ?: SignError.FAILED_TO_SIGN.error)
                }
            }
    }


    public fun destroy() {
        mFileCallbackIntent?.unregister()
        mDelegate = null

    }
}