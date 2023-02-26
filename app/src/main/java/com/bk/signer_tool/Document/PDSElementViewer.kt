package com.bk.signer_tool.Document

import android.annotation.SuppressLint
import com.bk.signer_tool.PDSModel.PDSElement
import com.bk.signer_tool.Signature.SignatureView
import com.bk.signer_tool.R
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import androidx.core.view.ViewCompat
import com.bk.signer_tool.PDSModel.PDSElement.PDSElementType
import android.view.View.DragShadowBuilder
import android.view.View.OnFocusChangeListener
import android.graphics.drawable.GradientDrawable
import android.content.*
import android.graphics.*
import android.util.Half.toFloat
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.bk.signer_tool.utils.ViewUtils
import kotlin.math.roundToInt

class PDSElementViewer constructor(
    context: Context?,
    fASPageViewer: PDSPageViewer?,
    fASElement: PDSElement?
) {
    var isBorderShown: Boolean = false
        private set
    var containerView: RelativeLayout? = null
        private set
    private var mContext: Context? = null
    var element: PDSElement? = null
    var elementView: View? = null
        private set
    private var mHasDragStarted: Boolean = false
    var imageButton: ImageButton? = null
        private set
    private var mLastMotionX: Float = 0.0f
    private var mLastMotionY: Float = 0.0f
    private var mLongPress: Boolean = false
    var pageViewer: PDSPageViewer? = null
    private var mResizeInitialPos: Float = 0.0f

    internal inner class CustomDragShadowBuilder constructor(
        view: View?,
        var mX: Int,
        var mY: Int
    ) : DragShadowBuilder(view) {
        public override fun onDrawShadow(canvas: Canvas) {}
        public override fun onProvideShadowMetrics(point: Point, point2: Point) {
            super.onProvideShadowMetrics(point, point2)
            point2.set(
                ((mX.toFloat()) * pageViewer!!.scaleFactor).toInt(),
                ((mY.toFloat()) * pageViewer!!.scaleFactor).toInt()
            )
            point.set(
                ((getView().getWidth().toFloat()) * pageViewer!!.scaleFactor).toInt(),
                ((getView().getHeight().toFloat()) * pageViewer!!.scaleFactor).toInt()
            )
        }
    }

    internal inner class DragEventData constructor(
        var viewer: PDSElementViewer,
        var x: Float,
        var y: Float
    )

    init {
        mContext = context
        pageViewer = fASPageViewer
        element = fASElement
        fASElement!!.mElementViewer = this
        createElement(fASElement)
    }

    private fun createElement(fASElement: PDSElement) {
        elementView = createElementView(fASElement)
        pageViewer?.pageView?.addView(elementView)
        elementView!!.setTag(fASElement)
        if (!isElementInModel) {
            addElementInModel(fASElement)
        }
        setListeners()
    }

    fun removeElement() {
        if (elementView!!.getParent() != null) {
            pageViewer?.page?.removeElement(elementView!!.getTag() as PDSElement?)
            pageViewer!!.hideElementPropMenu()
            pageViewer?.pageView?.removeView(elementView)
        }
    }

    private fun createElementView(fASElement: PDSElement?): View? {
        when (fASElement?.type) {
            PDSElementType.PDSElementTypeSignature -> {
                val createSignatureView: SignatureView? = ViewUtils.createSignatureView(
                    mContext, fASElement, pageViewer?.toViewCoordinatesMatrix
                )
                fASElement.rect =
                    RectF(
                        fASElement.rect?.left ?: 0f,
                        fASElement.rect?.top ?: 0f,
                        fASElement.rect!!.left + pageViewer!!.mapLengthToPDFCoordinates(
                            createSignatureView!!.signatureViewWidth.toFloat()
                        ),
                        fASElement.rect!!.bottom
                    )

                fASElement.strokeWidth =
                    pageViewer!!.mapLengthToPDFCoordinates(createSignatureView.strokeWidth)
                createSignatureView.apply {
                    isFocusable = true
                    isFocusableInTouchMode = true
                    isClickable = true
                    isLongClickable = true
                }

                createResizeButton(createSignatureView)
                return createSignatureView
            }
            PDSElementType.PDSElementTypeImage -> {
                val imageView: ImageView? = ViewUtils.createImageView(
                    mContext, fASElement, pageViewer?.toViewCoordinatesMatrix
                )
                imageView!!.setImageBitmap(fASElement.bitmap)
                fASElement.rect =
                    RectF(
                        fASElement.rect!!.left,
                        fASElement.rect!!.top,
                        fASElement.rect!!.left + pageViewer!!.mapLengthToPDFCoordinates(
                            imageView.getWidth().toFloat()
                        ),
                        fASElement.rect!!.bottom
                    )

                imageView.apply {
                    isFocusable = true
                    isFocusableInTouchMode = true
                    isClickable = true
                    isLongClickable = true
                }

                imageView.invalidate()
                createResizeButton(imageView)
                return imageView
            }
            else -> return null
        }
    }

    private fun addElementInModel(fASElement: PDSElement) {
        pageViewer?.page?.addElement(fASElement)
    }

    private val isElementInModel: Boolean
        get() {
            for (i in 0 until pageViewer?.page!!.numElements) {
                if (pageViewer?.page!!.getElement(i) === elementView!!.tag) {
                    return true
                }
            }
            return false
        }

    private fun setListeners() {
        setTouchListener()
        setFocusListener()
    }

    private fun setTouchListener() {
        elementView?.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(view: View): Boolean {
                view.requestFocus()
                mLongPress = true
                return true
            }
        })
        elementView?.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                var action: Int = motionEvent.getAction()
                when (action and 255) {
                    0 -> {
                        mHasDragStarted = false
                        mLongPress = false
                        mLastMotionX = motionEvent.getX()
                        mLastMotionY = motionEvent.getY()
                    }
                    1 -> {
                        mHasDragStarted = false
                        pageViewer!!.setElementAlreadyPresentOnTap(true)
                        if (!(view is SignatureView)) {
                            view.setVisibility(View.VISIBLE)
                        }
                        containerView!!.setVisibility(View.VISIBLE)
                    }
                    2 -> if (!mHasDragStarted) {
                        action = Math.abs((motionEvent.getX() - mLastMotionX).toInt())
                        val abs: Int = Math.abs((motionEvent.getY() - mLastMotionY).toInt())
                        val access: Int
                        if (mLongPress) {
                            access = MOTION_THRESHOLD_LONG_PRESS
                        } else {
                            access = MOTION_THRESHOLD
                        }
                        if ((motionEvent.x >= 0.0f) && (motionEvent.getY() >= 0.0f) && isBorderShown && (action > access || abs > access)) {
                            val x: Float = motionEvent.getX()
                            val y: Float = motionEvent.getY()
                            view.startDrag(
                                ClipData.newPlainText(
                                    "pos", String.format(
                                        "%d %d", *arrayOf<Any>(
                                            Integer.valueOf(Math.round(x)),
                                            Integer.valueOf(Math.round(y))
                                        )
                                    )
                                ),
                                CustomDragShadowBuilder(view, Math.round(x), Math.round(y)),
                                DragEventData(this@PDSElementViewer, x, y),
                                0
                            )
                            mHasDragStarted = true
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun setFocusListener() {
        elementView!!.setOnFocusChangeListener(object : OnFocusChangeListener {
            public override fun onFocusChange(view: View, z: Boolean) {
                if (z) {
                    assignFocus()
                }
            }
        })
    }

    fun assignFocus() {
        pageViewer!!.showElementPropMenu(this)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun createResizeButton(view: View?) {
        imageButton = ImageButton(mContext)
        imageButton!!.setImageResource(R.drawable.ic_resize)
        imageButton!!.setBackgroundColor(0)
        imageButton!!.setPadding(0, 0, 0, 0)
        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(-2, -2)
        layoutParams.addRule(11)
        layoutParams.addRule(15)
        imageButton!!.layoutParams = layoutParams
        imageButton!!.measure((-2.0f).roundToInt(), (-2.0f).roundToInt())
        imageButton!!.layout(
            0,
            0,
            imageButton!!.measuredWidth,
            imageButton!!.measuredHeight
        )
        containerView = RelativeLayout(mContext)
        containerView!!.isFocusable = false
        containerView!!.isFocusableInTouchMode = false


        imageButton!!.setOnTouchListener { view, motionEvent ->
            when (motionEvent.getAction()) {
                0 -> {
                    mResizeInitialPos = motionEvent.getRawX()
                    pageViewer?.resizeInOperation = true
                }
                1, 3 -> pageViewer?.resizeInOperation = false

                2 -> if (pageViewer!!.resizeInOperation) {
                    val rawX: Float = (motionEvent.getRawX() - mResizeInitialPos) / 2.0f
                    if ((rawX <= (-mContext!!.getResources()
                            .getDimension(R.dimen.sign_field_step_size)) || rawX >= mContext!!.getResources()
                            .getDimension(R.dimen.sign_field_step_size)) && ((elementView?.height!!
                            .toFloat()) + rawX >= mContext!!.getResources()
                            .getDimension(R.dimen.sign_field_min_height)) && ((elementView?.height!!
                            .toFloat()) + rawX <= mContext!!.getResources()
                            .getDimension(R.dimen.sign_field_max_height))
                    ) {
                        mResizeInitialPos = motionEvent.getRawX()
                        pageViewer!!.modifyElementSignatureSize(
                            elementView!!.tag as PDSElement,
                            elementView,
                            containerView,
                            (((elementView!!.width
                                .toFloat()) * rawX) / (elementView!!.getHeight()
                                .toFloat())).toInt(),
                            rawX.toInt()
                        )
                    }
                }
            }
            true
        }
    }

    fun showBorder() {
        changeColor()
        if (containerView?.parent == null) {
            val signatureViewWidth: Int
            val signatureViewHeight: Int
            if (elementView?.parent === pageViewer?.pageView) {
                elementView?.onFocusChangeListener = null
                pageViewer?.pageView?.removeView(elementView)
                containerView?.addView(elementView)
            }
            containerView?.addView(imageButton)
            containerView?.x = elementView!!.getX()
            containerView?.y = elementView!!.getY()
            elementView?.x = 0.0f
            elementView?.y = 0.0f
            if (elementView is SignatureView) {
                signatureViewWidth =
                    (elementView as SignatureView?)!!.signatureViewWidth + (imageButton!!.measuredWidth / 2)
                signatureViewHeight = (elementView as SignatureView?)!!.signatureViewWidth
            } else {

                signatureViewWidth =
                    elementView!!.layoutParams.width + (imageButton!!.measuredWidth / 2)
                signatureViewHeight = elementView!!.getLayoutParams().height
            }
            containerView!!.layoutParams = RelativeLayout.LayoutParams(
                signatureViewWidth,
                signatureViewHeight
            )
            pageViewer?.pageView?.addView(containerView)
        }
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setStroke(2, ContextCompat.getColor(mContext!!, R.color.colorAccent))
        elementView!!.background = gradientDrawable
        isBorderShown = true
    }

    fun hideBorder() {
        changeColor()
        if (containerView!!.parent === pageViewer?.pageView) {
            elementView!!.x = containerView!!.x
            elementView!!.y = containerView!!.y
            pageViewer?.pageView?.removeView(containerView)
            containerView!!.removeView(imageButton)
            if (elementView!!.parent === containerView) {
                containerView!!.removeView(elementView)
                pageViewer?.pageView?.addView(elementView)
                setFocusListener()
            }
        }
        elementView!!.setBackground(null)
        isBorderShown = false
    }

    private fun changeColor() {
        if (elementView is SignatureView) {
            val color = (elementView as SignatureView?)!!.actualColor
            (elementView as SignatureView?)!!.setStrokeColor(color)
        }
    }

    companion object {
        private val MOTION_THRESHOLD: Int = 3
        private val MOTION_THRESHOLD_LONG_PRESS: Int = 12
    }
}