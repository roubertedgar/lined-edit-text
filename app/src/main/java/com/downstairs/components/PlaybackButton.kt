package com.downstairs.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange
import androidx.core.animation.addListener
import androidx.core.animation.doOnRepeat
import com.downstairs.measureDimension

class PlaybackButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val size = context.resources.getDimension(R.dimen.default_progress_size)
    private val stroke = context.resources.getDimension(R.dimen.default_progress_stroke)

    private var progressBox = RectF(0f, 0f, 0f, 0f)

    private var startAngle = 270
    private var sweepAngle = 0

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
        canvas.drawArc(
            progressBox,
            startAngle.toFloat(),
            sweepAngle.toFloat(),
            false,
            progressPaint
        )
    }

    private fun sweepAngle(progress: Int) {
        sweepAngle = if (progress > 0) (360 * progress / 100) else 0
    }

    fun animateProgress(duration: Long) {
        startAngle = 0
        sweepAngle = 0

        animateIntValue(to = 450, durationMs = duration,
            onUpdate = { angle ->
                if (angle <= 180) sweepAngle = angle
                else if (startAngle >= 180) sweepAngle = 360 - startAngle

                if (angle >= 90) {
                    startAngle = angle - 90
                } else if (angle == 0) {
                    startAngle = 0
                    sweepAngle = 1
                }

                print(
                    "ANGLES:\n" +
                            "Sweep: $sweepAngle\n" +
                            "Start: $startAngle\n" +
                            "Animation: $angle\n\n"
                )

                invalidate()
            })
    }
}

fun animateIntValue(
    to: Int = 100,
    durationMs: Long = 600,
    onComplete: () -> Unit = {},
    onUpdate: (Int) -> Unit
) {
    ValueAnimator.ofInt(0, to).apply {
        duration = durationMs
        repeatCount = ValueAnimator.INFINITE
        interpolator = AccelerateInterpolator()
        addUpdateListener { valueAnimator -> onUpdate(valueAnimator.animatedValue as Int) }
        doOnRepeat { onComplete() }
    }.start()
}