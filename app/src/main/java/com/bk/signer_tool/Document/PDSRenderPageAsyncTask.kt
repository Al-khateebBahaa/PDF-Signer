package com.bk.signer_tool.Document

import android.util.SizeF
import android.graphics.Bitmap
import com.bk.signer_tool.PDF.PDSPDFPage
import android.os.AsyncTask
import android.content.*

class PDSRenderPageAsyncTask internal constructor(
    val page: PDSPDFPage?,
    private val mImageViewSize: SizeF?,
    val scale: Float,
    private val mListener: OnPostExecuteListener
) : AsyncTask<Any, Any, Bitmap>() {
    var bitmapSize: SizeF? = null
        private set


    interface OnPostExecuteListener {
        fun onPostExecute(fASRenderPageAsyncTask: PDSRenderPageAsyncTask, bitmap: Bitmap)
    }


    /* Access modifiers changed, original: protected|varargs */
    override fun doInBackground(vararg params: Any?): Bitmap? {
        if (isCancelled || mImageViewSize!!.width <= 0.0f) {
            return null
        }
        bitmapSize = computePageBitmapSize()
        if (isCancelled) {
            return null
        }
        var width: Float = bitmapSize!!.width * scale
        var height: Float = bitmapSize!!.height * scale
        val f: Float = width / height
        if (width > 3072.0f && width > height) {
            height = 3072.0f / f
            width = 3072.0f
        } else if (height > 3072.0f && height > width) {
            width = f * 3072.0f
            height = 3072.0f
        }
        try {
            val createBitmap: Bitmap =
                Bitmap.createBitmap(Math.round(width), Math.round(height), Bitmap.Config.ARGB_8888)
            if (isCancelled()) {
                return null
            }
            createBitmap.setHasAlpha(false)
            createBitmap.eraseColor(-1)
            if (isCancelled()) {
                createBitmap.recycle()
                return null
            }
            page!!.renderPage(createBitmap)
            if (!isCancelled()) {
                return createBitmap
            }
            createBitmap.recycle()
            return null
        } catch (unused: OutOfMemoryError) {
            return null
        }
    }

    /* Access modifiers changed, original: protected */
    public override fun onPostExecute(bitmap: Bitmap) {
        if (mListener != null) {
            mListener?.onPostExecute(this, bitmap)
        }
    }

    /* Access modifiers changed, original: protected */
    public override fun onCancelled(bitmap: Bitmap) {
        if (mListener != null) {
            //Changed this from null to bitmap
            mListener?.onPostExecute(this, bitmap)
        }
    }

    private fun computePageBitmapSize(): SizeF {
        var width: Float
        val pageSize: SizeF? = page?.pageSize
        var width2: Float = pageSize!!.getWidth() / pageSize.getHeight()
        if (width2 > mImageViewSize!!.getWidth() / mImageViewSize!!.getHeight()) {
            width = mImageViewSize!!.getWidth()
            if (width > 3072.0f) {
                width = 3072.0f
            }
            width2 = Math.round(width / width2).toFloat()
        } else {
            width = mImageViewSize.height
            if (width > 3072.0f) {
                width = 3072.0f
            }
            val f: Float = width2 * width
            width2 = width
            width = f
        }
        return SizeF(width, width2)
    }


}