package com.example.tiptime

import android.content.Context
import android.graphics.*
import android.text.Spannable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView



internal class GuideMessageView(context: Context) :
    LinearLayout(context) {
    private val mPaint: Paint
    private val mRect: RectF
    private val mTitleTextView: TextView
    private val mContentTextView: TextView
    var location = IntArray(2)
    fun setTitle(title: String?) {
        if (title == null) {
            removeView(mTitleTextView)
            return
        }
        mTitleTextView.text = title
    }

    fun setContentText(content: String?) {
        mContentTextView.text = content
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        getLocationOnScreen(location)
        mRect[paddingLeft.toFloat(), paddingTop.toFloat(), (
                width - paddingRight).toFloat()] = (
                height - paddingBottom
                ).toFloat()
        val density = resources.displayMetrics.density.toInt()
        val radiusSize = RADIUS_SIZE * density
        canvas.drawRoundRect(mRect, radiusSize.toFloat(), radiusSize.toFloat(), mPaint)
    }

    companion object {
        private const val RADIUS_SIZE = 5
        private const val PADDING_SIZE = 10
        private const val BOTTOM_PADDING_SIZE = 5
        private const val DEFAULT_TITLE_TEXT_SIZE = 18
        private const val DEFAULT_CONTENT_TEXT_SIZE = 14
    }

    init {
        val density = context.resources.displayMetrics.density
        setWillNotDraw(false)
        orientation = VERTICAL

        mRect = RectF()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.strokeCap = Paint.Cap.ROUND
        val padding = (PADDING_SIZE * density).toInt()
        val paddingBottom = (BOTTOM_PADDING_SIZE * density).toInt()
        mTitleTextView = TextView(context)
        mTitleTextView.setPadding(padding, padding, padding, paddingBottom)

        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TITLE_TEXT_SIZE.toFloat())
        mTitleTextView.setTextColor(Color.BLACK)
        addView(
            mTitleTextView,
            LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        mContentTextView = TextView(context)
        mContentTextView.setTextColor(Color.BLACK)
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_CONTENT_TEXT_SIZE.toFloat())
        mContentTextView.setPadding(padding, paddingBottom, padding, padding)
        mContentTextView.gravity = Gravity.CENTER
        addView(
            mContentTextView,
            LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }
}