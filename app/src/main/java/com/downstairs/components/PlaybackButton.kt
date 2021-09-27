package com.downstairs.components

import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.downstairs.AnimationConfig
import com.downstairs.animateIntValue
import com.downstairs.dpToPixel
import com.downstairs.measureDimension
import kotlin.math.sqrt

class PlaybackButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val size = context.resources.getDimension(R.dimen.default_progress_size)
    private val stroke = context.resources.getDimension(R.dimen.default_progress_stroke)

    private var progressBox = RectF(0f, 0f, 0f, 0f)
    private var playbackBox = RectF(0f, 0f, 0f, 0f)
    private var playbackPath = Path()
    private var startAngle = 0.0f
    private var sweepAngle = 0.0f
    private var centerX = 0.0f
    private var centerY = 0.0f

    private var progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = stroke
        strokeCap = Paint.Cap.ROUND
        color = resources.getColor(R.color.colorAccent)
    }

    private var playbackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = stroke
        color = resources.getColor(R.color.colorAccent)
    }

    init {
        if (isInEditMode) sweepAngle = 270f

        isClickable = true
        isFocusable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = size.toInt()
        val desiredWidth = size.toInt()

        val measuredWidth = measureDimension(desiredWidth, widthMeasureSpec)
        val measuredHeight = measureDimension(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        centerX = (width / 2f)
        centerY = height / 2f
        updateProgressBox(width, height)
        updatePlaybackBox(progressBox)
        toPlayState()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.save()
        canvas.rotate(-90f, centerX, centerY)
        canvas.drawArc(progressBox, startAngle, sweepAngle, false, progressPaint)
        canvas.restore()

        canvas.drawPath(playbackPath, playbackPaint)
    }

    fun setProgress(progress: Int, duration: Long = 100) {
        val angle = progress * 360 / 100
        val config = AnimationConfig(angle, duration)

        animateIntValue(config) { animated, _ ->
            sweepAngle = animated.toFloat()
            postInvalidate()
        }
    }

    private fun animateInfinitely() {
        val config = AnimationConfig(200, 1200, INFINITE, AccelerateInterpolator())

        animateIntValue(config) { _, fraction ->
            if (fraction < 0.5) sweepAngle = 360 * fraction
            else if (fraction >= 0.5) sweepAngle = 360 - (360 * fraction)

            if (fraction >= 0.25) {
                startAngle = (480 * fraction) - 120
            }
            postInvalidate()
        }
    }

    private fun updateProgressBox(width: Int, height: Int) {
        val halfStroke = stroke / 2

        val left = halfStroke + paddingLeft
        val right = (width - halfStroke) - paddingRight
        val bottom = (height - halfStroke) - paddingBottom
        val top = halfStroke + paddingTop
        progressBox = RectF(left, top, right, bottom)
    }

    private fun updatePlaybackBox(progressBox: RectF) {
        val playbackPadding = dpToPixel(6f)
        val halfStroke = stroke / 2
        val centerX = progressBox.centerX()
        val centerY = progressBox.centerY()
        val x = (progressBox.right - centerX) / sqrt(2.0).toFloat()
        val y = (progressBox.bottom - centerY) / sqrt(2.0).toFloat()

        val left = centerX - x + (halfStroke + playbackPadding * 2)
        val right = centerX + x - halfStroke - playbackPadding
        val top = centerY - y + halfStroke + playbackPadding
        val bottom = centerY + y - halfStroke - playbackPadding
        playbackBox = RectF(left, top, right, bottom)
    }

    private fun toPlayState() {
        playbackPath.moveTo(playbackBox.left, playbackBox.top)
        playbackPath.lineTo(playbackBox.left, playbackBox.bottom)
        playbackPath.lineTo(playbackBox.right, playbackBox.centerY())
        playbackPath.lineTo(playbackBox.left, playbackBox.top)
        playbackPath.close()
    }
}