package com.bk.signer_tool.core.Errors


public class PdfSignerInitialingException internal constructor(private val errorMessage: String) :
    Throwable(errorMessage)