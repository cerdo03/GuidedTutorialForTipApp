package com.example.tiptime

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout


class GuideView(context: Context, view: View?) :
    FrameLayout(context) {
    private val selfPaint = Paint()
    private val paintLine = Paint()
    private val paintCircle = Paint()
    private val paintCircleInner = Paint()
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val X_FER_MODE_CLEAR: Xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private val target: View?
    private var targetRect: RectF? = null
    private val selfRect = Rect()
    private val density: Float
    private var stopY = 0f
    private var isTop = false
    var isShowing = false
        private set
    private var yMessageView = 0
    private var startYLineAndCircle = 0f
    private var lineIndicatorWidthSize = 0f
    private var messageViewPadding = 0
    private var marginGuide = 0f
    private var strokeCircleWidth = 0f
    private var indicatorHeight = 0f
    private var isPerformedAnimationSize = false
    private var mGuideListener: GuideListener? = null
    private val mMessageView: GuideMessageView
    private fun startAnimationSize() {
        if (!isPerformedAnimationSize) {
            val linePositionAnimator = ValueAnimator.ofFloat(
                stopY,
                startYLineAndCircle
            )
            linePositionAnimator.addUpdateListener {
                startYLineAndCircle = linePositionAnimator.animatedValue as Float
                postInvalidate()
            }
            linePositionAnimator.start()
            linePositionAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    isPerformedAnimationSize = true
                }
                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
        }
    }

    private fun init() {
        lineIndicatorWidthSize = LINE_INDICATOR_WIDTH_SIZE * density
        marginGuide = MARGIN_INDICATOR * density
        indicatorHeight = INDICATOR_HEIGHT * density
        messageViewPadding = (MESSAGE_VIEW_PADDING * density).toInt()
        strokeCircleWidth = STROKE_CIRCLE_INDICATOR_SIZE * density
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (target != null) {
            selfPaint.color = BACKGROUND_COLOR
            selfPaint.style = Paint.Style.FILL
            selfPaint.isAntiAlias = true
            canvas.drawRect(selfRect, selfPaint)
            paintLine.style = Paint.Style.FILL
            paintLine.color = LINE_INDICATOR_COLOR
            paintLine.strokeWidth = lineIndicatorWidthSize
            paintLine.isAntiAlias = true
            paintCircle.style = Paint.Style.STROKE
            paintCircle.color = CIRCLE_INDICATOR_COLOR
            paintCircle.strokeCap = Paint.Cap.ROUND
            paintCircle.strokeWidth = strokeCircleWidth
            paintCircle.isAntiAlias = true
            paintCircleInner.style = Paint.Style.FILL
            paintCircleInner.color = CIRCLE_INNER_INDICATOR_COLOR
            paintCircleInner.isAntiAlias = true
            val x = targetRect!!.left / 2 + targetRect!!.right / 2
            targetPaint.xfermode = X_FER_MODE_CLEAR
            targetPaint.isAntiAlias = true
            if (target is Targetable) {
                (target as Targetable).guidePath()?.let { canvas.drawPath(it, targetPaint) }
            } else {
                canvas.drawRoundRect(
                    targetRect!!,
                    RADIUS_SIZE_TARGET_RECT.toFloat(),
                    RADIUS_SIZE_TARGET_RECT.toFloat(),
                    targetPaint
                )
            }
        }
    }

    fun dismiss() {
        ((context as Activity).window.decorView as ViewGroup).removeView(this)
        isShowing = false
        if (mGuideListener != null) {
            mGuideListener!!.onDismiss(target)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
               dismiss()
            return true
        }
        return false
    }



    private fun setMessageLocation(p: Point) {
        mMessageView.x = p.x.toFloat()
        mMessageView.y = p.y.toFloat()
        postInvalidate()
    }

    private fun resolveMessageViewLocation(): Point {
        var xMessageView: Int
        xMessageView = targetRect!!.right.toInt() - mMessageView.width

        if (xMessageView + mMessageView.width > width) {
            xMessageView = width - mMessageView.width
        }
        if (xMessageView < 0) {
            xMessageView = 0
        }

        if (targetRect!!.top + indicatorHeight > height / 2f) {
            isTop = false
            yMessageView = (targetRect!!.top - mMessageView.height - indicatorHeight).toInt()
        } else {
            isTop = true
            yMessageView = (targetRect!!.top + target!!.height + indicatorHeight).toInt()
        }
        if (yMessageView < 0) {
            yMessageView = 0
        }
        return Point(xMessageView, yMessageView)
    }

    fun show() {
        this.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.isClickable = false
        ((context as Activity).window.decorView as ViewGroup).addView(this)
        val startAnimation = AlphaAnimation(0.0f, 1.0f)
        startAnimation.duration = APPEARING_ANIMATION_DURATION.toLong()
        startAnimation.fillAfter = true
        startAnimation(startAnimation)
        isShowing = true
    }

    fun setTitle(str: String?) {
        mMessageView.setTitle(str)
    }

    fun setContentText(str: String?) {
        mMessageView.setContentText(str)
    }


    companion object {
        private const val INDICATOR_HEIGHT = 40
        private const val MESSAGE_VIEW_PADDING = 5
        private const val APPEARING_ANIMATION_DURATION = 400
        private const val LINE_INDICATOR_WIDTH_SIZE = 3
        private const val STROKE_CIRCLE_INDICATOR_SIZE = 3
        private const val RADIUS_SIZE_TARGET_RECT = 15
        private const val MARGIN_INDICATOR = 15
        private const val BACKGROUND_COLOR = -0x67000000
        private const val CIRCLE_INNER_INDICATOR_COLOR = -0x333334
        private const val CIRCLE_INDICATOR_COLOR = Color.WHITE
        private const val LINE_INDICATOR_COLOR = Color.WHITE
    }

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        target = view
        density = context.resources.displayMetrics.density
        init()
        targetRect = if (view is Targetable) {
            (view as Targetable?)?.boundingRect()
        } else {
            val locationTarget = IntArray(2)
            target!!.getLocationOnScreen(locationTarget)
            RectF(
                locationTarget[0].toFloat(),
                locationTarget[1].toFloat(),
                (locationTarget[0] + target.width).toFloat(),
                (locationTarget[1] + target.height).toFloat()
            )
        }
        mMessageView = GuideMessageView(getContext())
        mMessageView.setPadding(
            messageViewPadding,
            messageViewPadding,
            messageViewPadding,
            messageViewPadding
        )
        mMessageView.setColor(Color.WHITE)
        addView(
            mMessageView,
            LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        setMessageLocation(resolveMessageViewLocation())
        val layoutListener: OnGlobalLayoutListener = object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                setMessageLocation(resolveMessageViewLocation())
                targetRect = if (target is Targetable) {
                    (target as Targetable?)!!.boundingRect()
                } else {
                    val locationTarget = IntArray(2)
                    target!!.getLocationOnScreen(locationTarget)
                    RectF(
                        locationTarget[0].toFloat(),
                        locationTarget[1].toFloat(),
                        (locationTarget[0] + target.width).toFloat(),
                        (locationTarget[1] + target.height).toFloat()
                    )
                }
                selfRect[paddingLeft, paddingTop, width - paddingRight] = height - paddingBottom

                marginGuide = ((if (isTop) marginGuide else -marginGuide)).toFloat()
                startYLineAndCircle =
                    (if (isTop) targetRect!!.bottom else targetRect!!.top) + marginGuide
                stopY = yMessageView + indicatorHeight
                startAnimationSize()
                viewTreeObserver.addOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }
}