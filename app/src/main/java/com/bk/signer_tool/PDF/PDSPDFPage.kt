package com.bk.signer_tool.PDF

import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.pdf.PdfRenderer
import android.util.SizeF
import com.bk.signer_tool.Document.PDSPageViewer
import com.bk.signer_tool.PDSModel.PDSElement


class PDSPDFPage(val number: Int, val document: PDSPDFDocument) {
    private val elements: ArrayList<PDSElement> = arrayListOf()
    private var mPageSize: SizeF? = null
    var pageViewer: PDSPageViewer? = null


    val pageSize: SizeF?
        get() {
            if (mPageSize == null) {
                synchronized(PDSPDFDocument.lockObject) {
                    synchronized(document) {
                        val openPage = document.renderer?.openPage(
                            number
                        )
                        mPageSize = SizeF(openPage?.width!!.toFloat(), openPage.height.toFloat())
                        openPage.close()
                    }
                }
            }
            return mPageSize
        }

    fun renderPage(bitmap: Bitmap?) {
        synchronized(PDSPDFDocument.lockObject) {
            synchronized(document) {
                val openPage = document.renderer?.openPage(
                    number
                )
                mPageSize = SizeF(openPage?.width!!.toFloat(), openPage.height.toFloat())
                openPage.render(bitmap!!, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                openPage.close()
            }
        }
    }

    fun removeElement(fASElement: PDSElement?) {
        elements.remove(fASElement)
    }

    fun addElement(fASElement: PDSElement) {
        elements.add(fASElement)
    }

    val numElements: Int
        get() = elements.size

    fun getElement(i: Int): PDSElement {
        return elements[i]
    }

    fun updateElement(
        fASElement: PDSElement,
        rectF: RectF?,
        f: Float,
        f2: Float,
        f3: Float,
        f4: Float
    ) {
        if (rectF != fASElement.rect) {
            fASElement.rect = rectF
        }
        if (!(f == 0.0f || f == fASElement.size)) {
            fASElement.size = f
        }
        if (!(f2 == 0.0f || f2 == fASElement.maxWidth)) {
            fASElement.maxWidth = f2
        }
        if (!(f3 == 0.0f || f3 == fASElement.strokeWidth)) {
            fASElement.strokeWidth = f3
        }
        if (!(f4 == 0.0f || f4 == fASElement.letterSpace)) {
            fASElement.letterSpace = f4
        }
    }

}