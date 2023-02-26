package com.bk.signer_tool.Signature

import java.io.File

interface CreateSignatureCallback {

    fun onSignatureCreatedResult(isSuccess: Boolean, signFile: File? = null)

}