package com.bk.signer_tool.Signature

import com.bk.signer_tool.R
import android.widget.RelativeLayout
import android.content.*
import android.os.*
import android.util.AttributeSet

class SignatureLayout : RelativeLayout {
    private var mHeight: Int = 0
    var mRequestLayoutHandler: Handler = object : Handler() {
        public override fun handleMessage(message: Message) {
            if (message.what == 1) {
                resizeDrawingView()
            }
            super.handleMessage(message)
        }
    }
    private var mWidth: Int = 0

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {}
    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context,
        attributeSet,
        i
    ) {
    }

    /* Access modifiers changed, original: protected */
    public override fun onSizeChanged(i: Int, i2: Int, i3: Int, i4: Int) {
        var i2: Int = i2
        super.onSizeChanged(i, i2, i3, i4)
        mWidth = i - ((getResources().getDimension(R.dimen.signature_panel_horizontal_margin)
            .toInt()) * 2)
        mHeight = mWidth / 2
        //if (!SignatureUtils.isTablet((Activity) getContext())) {
        i2 -= (getResources().getDimension(R.dimen.signature_view_header) * 2.0f).toInt()
        if (mHeight > i2) {
            mHeight = i2
            mWidth = mHeight * 2
        }
        // }
        mRequestLayoutHandler.removeMessages(1)
        val obtain: Message = Message.obtain()
        obtain.what = 1
        mRequestLayoutHandler.sendMessage(obtain)
    }

    private fun resizeDrawingView() {
        /* ViewGroup.LayoutParams layoutParams =findViewById(R.id.drawingView).getLayoutParams();
        layoutParams.height = this.mHeight;
        layoutParams.width = this.mWidth;
        findViewById(R.id.signature_top_bar).getLayoutParams().height = (int) getResources().getDimension(R.dimen.signature_view_header);
        findViewById(R.id.signature_bottom_bar).getLayoutParams().height = (int) getResources().getDimension(R.dimen.signature_view_header);
        findViewById(R.id.signature_panel_layout).requestLayout();*/
    }

    companion object {
        private val REQUEST_LAYOUT_MSG: Int = 1
    }
}