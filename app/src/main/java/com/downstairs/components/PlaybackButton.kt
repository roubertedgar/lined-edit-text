package com.downstairs.components

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange
import com.downstairs.measureDimension

class PlaybackButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val size = context.resources.getDimension(R.dimen.default_progress_size)
    private val stroke = context.resources.getDimension(R.dimen.default_progress_stroke)

    private var progressBox = RectF(0f, 0f, 0f, 0f)
    private var startAngle = 0.0f
    private var sweepAngle = 0.0f
    private var centerX = 0.0f
    private var centerY = 0.0f

    private var progress = 0

    private var progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = stroke
        strokeCap = Paint.Cap.ROUND
        color = resources.getColor(R.color.colorAccent)
    }

    init {
        if (isInEditMode) progress = 75
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
        val halfStrokeTop = stroke / 2
        val halfStrokeBottom = stroke / 2

        val left = halfStrokeTop + paddingLeft
        val right = (width - halfStrokeBottom) - paddingRight
        val bottom = (height - halfStrokeBottom) - paddingBottom
        val top = halfStrokeTop + paddingTop
        progressBox = RectF(left, top, right, bottom)
        centerX = (width / 2f)
        centerY = height / 2f
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.save()
        canvas.rotate(-90f, centerX, centerY)
        canvas.drawArc(progressBox, startAngle, sweepAngle, false, progressPaint)
        canvas.restore()
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
}

fun animateIntValue(
    config: AnimationConfig,
    onUpdate: (value: Int, fraction: Float) -> Unit
) {
    ValueAnimator.ofInt(0, config.value).apply {
        duration = config.duration
        repeatCount = config.repeat
        interpolator = config.interpolator
        addUpdateListener { valueAnimator ->
            onUpdate(
                valueAnimator.animatedValue as Int,
                valueAnimator.animatedFraction
            )
        }
    }.start()
}

data class AnimationConfig(
    val value: Int = 100,
    val duration: Long = 600,
    val repeat: Int = 0,
    val interpolator: Interpolator = LinearInterpolator()
)