package com.bk.signer_tool.Signature

import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import android.graphics.Paint.Cap
import android.content.*
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import java.lang.Exception
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

class SignatureView : RelativeLayout {
    var image: Bitmap? private set
    private var mBitmapPaint: Paint?
    private var mCanvas: Canvas?
    private var mGestureInkList: ArrayList<Float>?
    var inkList: ArrayList<ArrayList<Float>>?
    private var mIsEditable: Boolean
    private var mIsFirstBoundingRect: Boolean
    var signatureViewHeight: Int
        private set
    var signatureViewWidth: Int
        private set
    private var mPath: Path?
    private var mQuadEndPointX: Float
    private var mQuadEndPointY: Float
    var mRectBottom: Float
    var mRectLeft: Float
    var mRectRight: Float
    var mRectTop: Float
    var mRedoInkList: ArrayList<ArrayList<Float>?>?
    private var mSignatureCreationMode: Boolean
    var mStrokeColor: Int
    var actualColor: Int = 0
    var mStrokeWidthInDocSpace: Float
    private var mTouchDownPointX: Float
    private var mTouchDownPointY: Float
    private var mX: Float
    private var mY: Float

    constructor(context: Context?) : super(context) {
        mX = 0.0f
        mY = 0.0f
        mQuadEndPointX = 0.0f
        mQuadEndPointY = 0.0f
        mTouchDownPointX = 0.0f
        mTouchDownPointY = 0.0f
        mStrokeWidthInDocSpace = 0.0f
        mStrokeColor = 0
        mIsFirstBoundingRect = true
        mRectLeft = 0.0f
        mRectTop = 0.0f
        mRectRight = 0.0f
        mRectBottom = 0.0f
        image = null
        mCanvas = null
        mPath = null
        mBitmapPaint = null
        mGestureInkList = null
        inkList = null
        mRedoInkList = null
        mSignatureCreationMode = true
        mIsEditable = false
        signatureViewHeight = -1
        signatureViewWidth = -1
        initializeOverlayView()
    }

    constructor(context: Context?, i: Int, i2: Int) : super(context) {
        mX = 0.0f
        mY = 0.0f
        mQuadEndPointX = 0.0f
        mQuadEndPointY = 0.0f
        mTouchDownPointX = 0.0f
        mTouchDownPointY = 0.0f
        mStrokeWidthInDocSpace = 0.0f
        mStrokeColor = 0
        mIsFirstBoundingRect = true
        mRectLeft = 0.0f
        mRectTop = 0.0f
        mRectRight = 0.0f
        mRectBottom = 0.0f
        image = null
        mCanvas = null
        mPath = null
        mBitmapPaint = null
        mGestureInkList = null
        inkList = null
        mRedoInkList = null
        mSignatureCreationMode = true
        mIsEditable = false
        signatureViewHeight = -1
        signatureViewWidth = -1
        mSignatureCreationMode = false
        signatureViewHeight = i2
        signatureViewWidth = i
        initializeOverlayView()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        mX = 0.0f
        mY = 0.0f
        mQuadEndPointX = 0.0f
        mQuadEndPointY = 0.0f
        mTouchDownPointX = 0.0f
        mTouchDownPointY = 0.0f
        mStrokeWidthInDocSpace = 0.0f
        mStrokeColor = 0
        mIsFirstBoundingRect = true
        mRectLeft = 0.0f
        mRectTop = 0.0f
        mRectRight = 0.0f
        mRectBottom = 0.0f
        image = null
        mCanvas = null
        mPath = null
        mBitmapPaint = null
        mGestureInkList = null
        inkList = null
        mRedoInkList = null
        mSignatureCreationMode = true
        mIsEditable = false
        signatureViewHeight = -1
        signatureViewWidth = -1
        initializeOverlayView()
    }

    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context,
        attributeSet,
        i
    ) {
        mX = 0.0f
        mY = 0.0f
        mQuadEndPointX = 0.0f
        mQuadEndPointY = 0.0f
        mTouchDownPointX = 0.0f
        mTouchDownPointY = 0.0f
        mStrokeWidthInDocSpace = 0.0f
        mStrokeColor = 0
        mIsFirstBoundingRect = true
        mRectLeft = 0.0f
        mRectTop = 0.0f
        mRectRight = 0.0f
        mRectBottom = 0.0f
        image = null
        mCanvas = null
        mPath = null
        mBitmapPaint = null
        mGestureInkList = null
        inkList = null
        mRedoInkList = null
        mSignatureCreationMode = true
        mIsEditable = false
        signatureViewHeight = -1
        signatureViewWidth = -1
        initializeOverlayView()
    }

    fun initializeOverlayView() {
        setWillNotDraw(false)
        mStrokeWidthInDocSpace = 3.0f
        mStrokeColor = ViewCompat.MEASURED_STATE_MASK
        mPath = Path()
        mBitmapPaint = Paint()
        mBitmapPaint!!.setAntiAlias(true)
        mBitmapPaint!!.setDither(true)
        mBitmapPaint!!.setColor(mStrokeColor)
        mBitmapPaint!!.setStyle(Paint.Style.STROKE)
        mBitmapPaint!!.setStrokeJoin(Paint.Join.ROUND)
        mBitmapPaint!!.setStrokeCap(Cap.ROUND)
        mBitmapPaint!!.setStrokeWidth(mStrokeWidthInDocSpace)
        inkList = arrayListOf()
        mRedoInkList = arrayListOf()
        mX = 0.0f
        mY = 0.0f
        mTouchDownPointX = 0.0f
        mTouchDownPointY = 0.0f
        mIsFirstBoundingRect = true
        mRectLeft = 0.0f
        mRectTop = 0.0f
        mRectRight = 0.0f
        mRectBottom = 0.0f
        mIsEditable = false
    }

    fun initializeInkList(arrayList: ArrayList<ArrayList<Float>>?) {
        inkList = arrayList
    }

    fun scaleAndTranslatePath(
        arrayList: ArrayList<ArrayList<Float>>?,
        rectF: RectF?,
        f: Float,
        f2: Float,
        f3: Float,
        f4: Float
    ) {
        val size: Int = arrayList!!.size
        for (i in 0 until size) {
            val arrayList2: ArrayList<Float> = arrayList.get(i)
            var i2: Int = 0
            while (i2 < arrayList2.size) {
                arrayList2.set(
                    i2,
                    java.lang.Float.valueOf(((arrayList2.get(i2) as Number).toFloat() * f) - f3)
                )
                val i3: Int = i2 + 1
                arrayList2.set(
                    i3,
                    java.lang.Float.valueOf(((arrayList2.get(i3) as Number).toFloat() * f2) - f4)
                )
                i2 += 2
            }
        }
        mRectLeft = rectF!!.left * f
        mRectTop = rectF.top * f2
        mRectRight = rectF.right * f
        mRectBottom = rectF.bottom * f2
    }

    fun redrawPath() {
        if (mCanvas != null) {
            redrawPath(mCanvas)
        }
    }

    fun drawTransparent() {
        if (mCanvas != null) {
            mCanvas!!.drawColor(0, PorterDuff.Mode.CLEAR)
        }
    }

    fun fillColor() {
        if (mCanvas != null) {
            mCanvas!!.drawColor(-16776961, PorterDuff.Mode.DARKEN)
        }
    }

    fun setStrokeColor(i: Int) {
        mStrokeColor = i
        mBitmapPaint!!.setColor(i)
        redrawPath()
    }

    fun setmActualColor(i: Int) {
        actualColor = i
    }

    var strokeWidth: Float
        get() {
            return mStrokeWidthInDocSpace
        }
        set(f) {
            var f: Float = f
            if (f <= 0.0f) {
                f = 0.5f
            }
            mStrokeWidthInDocSpace = f
            mBitmapPaint!!.setStrokeWidth(mStrokeWidthInDocSpace)
            invalidate()
            drawTransparent()
            redrawPath()
            invalidate()
        }

    fun setLayoutParams(i: Int, i2: Int) {
        image = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(image!!)
        strokeWidth = mStrokeWidthInDocSpace
        setLayoutParams(LayoutParams(i, i2))
        signatureViewHeight = i2
        signatureViewWidth = i
        val arrayList: ArrayList<ArrayList<Float>> = inkList ?: arrayListOf()
        val boundingBox: RectF = boundingBox
        clear()
        initializeInkList(arrayList)
        scaleAndTranslatePath(arrayList, boundingBox, 1.0f, 1.0f, 0.0f, 0.0f)
    }

    val boundingBox: RectF
        get() {
            return RectF(mRectLeft, mRectTop, mRectRight, mRectBottom)
        }

    fun setEditable(z: Boolean) {
        mIsEditable = z xor true
    }

    private fun redrawPath(canvas: Canvas?) {
        val size: Int = inkList!!.size
        for (i in 0 until size) {
            val arrayList: ArrayList<*>? = inkList!!.get(i)
            touch_start(
                (arrayList!!.get(0) as Float).toFloat(),
                (arrayList.get(1) as Float).toFloat()
            )
            var i2: Int = 2
            while (i2 < arrayList.size) {
                touch_move(
                    (arrayList.get(i2) as Float).toFloat(),
                    (arrayList.get(i2 + 1) as Float).toFloat()
                )
                i2 += 2
            }
            mPath!!.lineTo(mX, mY)
            canvas!!.drawPath((mPath)!!, (mBitmapPaint)!!)
            mPath!!.reset()
        }
    }

    /* Access modifiers changed, original: protected */
    public override fun onDraw(canvas: Canvas) {
        if (!mSignatureCreationMode) {
            drawTransparent()
            redrawPath(canvas)
        } else if (image != null) {
            canvas.drawBitmap(image!!, 0.0f, 0.0f, null)
        }
    }

    private fun setBoundingRect(f: Float, f2: Float) {
        if (f < mRectLeft) {
            mRectLeft = f
        } else if (f > mRectRight) {
            mRectRight = f
        }
        if (f2 < mRectTop) {
            mRectTop = f2
        } else if (f2 > mRectBottom) {
            mRectBottom = f2
        }
    }

    private fun touch_start(f: Float, f2: Float) {
        var f: Float = f
        mPath!!.reset()
        mPath!!.moveTo(f, f2)
        mQuadEndPointX = f
        mQuadEndPointY = f2
        mX = f
        mY = f2
        mTouchDownPointX = f
        mTouchDownPointY = f2
        mGestureInkList = arrayListOf()
        mGestureInkList?.add(java.lang.Float.valueOf(mX))
        mGestureInkList?.add(java.lang.Float.valueOf(mY))
        if (mIsFirstBoundingRect) {
            f = mX
            mRectRight = f
            mRectLeft = f
            f = mY
            mRectBottom = f
            mRectTop = f
            mIsFirstBoundingRect = false
            return
        }
        setBoundingRect(mX, mY)
    }

    private fun touch_move(f: Float, f2: Float) {
        val abs: Float = Math.abs(f - mX)
        val abs2: Float = Math.abs(f2 - mY)
        if (abs >= TOUCH_TOLERANCE || abs2 >= TOUCH_TOLERANCE) {
            mQuadEndPointX = (mX + f) / 2.0f
            mQuadEndPointY = (mY + f2) / 2.0f
            mPath!!.quadTo(mX, mY, mQuadEndPointX, mQuadEndPointY)
            mX = f
            mY = f2
        }
        mGestureInkList!!.add(java.lang.Float.valueOf(mX))
        mGestureInkList!!.add(java.lang.Float.valueOf(mY))
        setBoundingRect(mX, mY)
    }

    private fun touch_up(f: Float, f2: Float) {
        drawPointIfRequired(f, f2)
        mPath!!.lineTo(mX, mY)
        if (mCanvas != null) {
            mCanvas!!.drawPath((mPath)!!, (mBitmapPaint)!!)
        }
        mPath!!.reset()
        inkList!!.add(mGestureInkList!!)
        if (mRedoInkList!!.size != 0) {
            mRedoInkList!!.clear()
        }
    }

    private fun drawPointIfRequired(f: Float, f2: Float) {
        val abs: Float = Math.abs(f - mTouchDownPointX)
        val abs2: Float = Math.abs(f2 - mTouchDownPointY)
        if (abs < TOUCH_TOLERANCE && abs2 < TOUCH_TOLERANCE) {
            mX = f
            mY = f2
            if (compareDoubleValues(abs.toDouble(), 0.0) && compareDoubleValues(
                    abs2.toDouble(),
                    0.0
                )
            ) {
                mY = f2 - 1.0f
            }
            mGestureInkList!!.add(java.lang.Float.valueOf(mX))
            mGestureInkList!!.add(java.lang.Float.valueOf(mY))
            setBoundingRect(mX, mY)
        }
    }

    private fun compareDoubleValues(d: Double, d2: Double): Boolean {
        return Math.abs(d - d2) < 0.001
    }

    val statusBarHeight: Int
        get() {
            try {
                val identifier: Int =
                    getResources().getIdentifier("status_bar_height", "dimen", "android")
                if (identifier > 0) {
                    return getResources().getDimensionPixelSize(identifier)
                }
                return 0
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }
        }

    /* Access modifiers changed, original: protected */
    public override fun onSizeChanged(i: Int, i2: Int, i3: Int, i4: Int) {
        super.onSizeChanged(i, i2, i3, i4)
        try {
            image = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888)
            mCanvas = Canvas(image!!)
            scaleAndTranslatePath(
                inkList,
                RectF(mRectLeft, mRectTop, mRectRight, mRectBottom),
                if (i3 != 0) (i.toFloat()) / (i3.toFloat()) else 1.0f,
                if (i4 != 0) (i2.toFloat()) / (i4.toFloat()) else 1.0f,
                0.0f,
                0.0f
            )
            redrawPath()
        } catch (unused: IllegalArgumentException) {
        } catch (unused: OutOfMemoryError) {
        }
    }

    /* Access modifiers changed, original: protected */
    public override fun onMeasure(i: Int, i2: Int) {
        var i: Int = i
        var i2: Int = i2
        if (!(mSignatureCreationMode || (signatureViewWidth == -1) || (signatureViewHeight == -1))) {
            i = MeasureSpec.makeMeasureSpec(signatureViewWidth, MeasureSpec.EXACTLY)
            i2 = MeasureSpec.makeMeasureSpec(signatureViewHeight, MeasureSpec.EXACTLY)
        }
        super.onMeasure(i, i2)
    }

    /* Access modifiers changed, original: protected */
    public override fun onVisibilityChanged(view: View, i: Int) {
        super.onVisibilityChanged(view, i)
        if (i == 0) {
            redrawPath()
            invalidate()
        }
    }

    public override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if (mIsEditable) {
            super.onTouchEvent(motionEvent)
            return true
        }
        val x: Float = motionEvent.getX()
        val y: Float = motionEvent.getY()
        enableToolBarButton()
        when (motionEvent.getAction()) {
            0 -> {
                touch_start(x, y)
                invalidate()
            }
            1 -> {
                touch_up(x, y)
                drawTransparent()
                redrawPath()
                invalidate()
            }
            2 -> {
                val historySize: Int = motionEvent.getHistorySize()
                var i: Int = 0
                while (i < historySize) {
                    touch_move(motionEvent.getHistoricalX(i), motionEvent.getHistoricalY(i))
                    i++
                }
                touch_move(x, y)
                if (mCanvas != null) {
                    mCanvas!!.drawPath((mPath)!!, (mBitmapPaint)!!)
                    mPath!!.reset()
                    mPath!!.moveTo(mQuadEndPointX, mQuadEndPointY)
                }
                invalidate()
            }
        }
        return true
    }

    fun enableToolBarButton() {
        if (getContext() != null) {
            (getContext() as FreeHandActivity).enableClear(true)
            (getContext() as FreeHandActivity).enableSave(true)
        }
    }

    fun clear() {
        mX = 0.0f
        mY = 0.0f
        mRectLeft = 0.0f
        mRectTop = 0.0f
        mRectRight = 0.0f
        mRectBottom = 0.0f
        mIsFirstBoundingRect = true
        drawTransparent()
        mPath!!.reset()
        inkList = arrayListOf()
        invalidate()
    }

    companion object {
        private val TOUCH_TOLERANCE: Float = 0.1f
    }
}