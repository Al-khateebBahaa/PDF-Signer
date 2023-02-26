package com.bk.signer_tool.Document

import android.view.ViewGroup
import fr.castorflex.android.verticalviewpager.VerticalViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import android.view.MotionEvent
import android.content.*
import android.util.AttributeSet
import com.bk.signer_tool.digital_signer.DigitalSignatureActivity

class PDSViewPager : VerticalViewPager {
    private var mActivityContext: Context? = null
    private var mDownReceieved = true

    constructor(context: Context?) : super(context) {
        mActivityContext = context
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        mActivityContext = context
        init()
    }

    private fun init() {
        setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(i: Int) {}
            override fun onPageScrolled(i: Int, f: Float, i2: Int) {}
            override fun onPageSelected(i: Int) {
                val focusedChild = this@PDSViewPager.focusedChild
                if (focusedChild != null) {
                    val pDSPageViewer = (focusedChild as ViewGroup).getChildAt(0) as PDSPageViewer
                    pDSPageViewer?.resetScale()
                }
                if (mActivityContext != null) {
                    (mActivityContext as DigitalSignatureActivity?)!!.updatePageNumber(i + 1)
                }
            }
        })
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
            mDownReceieved = true
        }
        if (motionEvent.pointerCount <= 1 && mDownReceieved) {
            return super.onInterceptTouchEvent(motionEvent)
        }
        mDownReceieved = false
        return false
    }
}