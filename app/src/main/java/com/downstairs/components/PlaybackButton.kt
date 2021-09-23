package com.downstairs.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
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

    @IntRange(from = 0, to = 100) var progress = 0
        set(value) {
            field = value
            sweepAngle(value)
        }

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
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.save()
        canvas.rotate(-90f, (width / 2f), height / 2f)
        canvas.drawArc(progressBox, startAngle, sweepAngle, false, progressPaint)
        canvas.restore()
    }

    private fun sweepAngle(progress: Int) {
        sweepAngle = if (progress > 0) (360 * progress / 100.0f) else 0.0f
    }

    fun animateProgress(duration: Long) {
        var previousAngle = 0
        animateIntValue(to = 200, durationMs = duration,
            onUpdate = { animated, fraction ->
                if (fraction < 0.5) sweepAngle = 360 * fraction
                else if (fraction >= 0.5) sweepAngle = 360 - (360 * fraction)


                if (fraction >= 0.25) {
                    startAngle = (480 * fraction) - 120
                }

                if (previousAngle != animated) {
                    print(
                        "ANGLES:\n" +
                                "Sweep: $sweepAngle\n" +
                                "Start: $startAngle\n" +
                                "Fraction: $fraction\n" +
                                "Animation:$animated\n\n"
                    )

                    previousAngle = animated
                    invalidate()
                }
            })
    }
}

fun animateIntValue(
    to: Int = 100,
    durationMs: Long = 600,
    onUpdate: (animated: Int, fraction: Float) -> Unit
) {
    ValueAnimator.ofInt(0, to).apply {
        duration = durationMs
        repeatCount = ValueAnimator.INFINITE
        interpolator = AccelerateInterpolator()
        addUpdateListener { valueAnimator ->
            val animated = valueAnimator.animatedValue as Int
            onUpdate(animated, valueAnimator.animatedFraction)
        }
    }.start()
}