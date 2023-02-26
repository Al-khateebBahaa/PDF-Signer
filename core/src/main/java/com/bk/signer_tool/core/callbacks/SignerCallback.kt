package com.bk.signer_tool.core.callbacks

import androidx.annotation.UiThread
import java.io.File

public interface SignerCallback {

    //This will return when adding signature success
    @UiThread
    public fun onSignResult(isSuccess: Boolean, resultFile: File?)


    //This will return when adding signature failed
    @UiThread
    public fun onSignFailed(error: String)

}