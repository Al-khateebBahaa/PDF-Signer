package com.bk.signer_tool.core.Errors

internal enum class SignError(val error:String) {

    FILE_NOT_FOUND("File not found"),
    ON_CREATE_ERROR("Please invoke SDK onCreate"),
    DELEGATE_NOT_FOUND("SignerCallback not passed"),
    INVALID_FILE_TYPE("Invalid File type"),
    FAILED_TO_SIGN("Failed to add sign")


}