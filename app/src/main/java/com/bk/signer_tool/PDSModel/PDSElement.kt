package com.bk.signer_tool.PDSModel

import android.graphics.Bitmap
import android.graphics.RectF
import com.bk.signer_tool.Document.PDSElementViewer
import java.io.File


class PDSElement {
    var horizontalPadding: Float = 0.0f
    var letterSpace: Float = 0.0f
    var maxWidth: Float = 0.0f
    var minWidth: Float = 0.0f
    var rect: RectF? = null
    var size: Float = 0.0f
    var strokeWidth: Float = 0.0f
    var type: PDSElementType = PDSElementType.PDSElementTypeSignature
        private set
    var mElementViewer: PDSElementViewer? = null
    var file: File? = null
        private set
    var bitmap: Bitmap? = null
        private set
    var verticalPadding: Float = 0.0f
    var alises: String? = null

    enum class PDSElementType {
        PDSElementTypeImage, PDSElementTypeSignature
    }

    constructor(fASElementType: PDSElementType, file: File?) {
        type = fASElementType
        this.file = file
    }

    constructor(fASElementType: PDSElementType, file: Bitmap?) {
        type = fASElementType
        bitmap = file
    }
}