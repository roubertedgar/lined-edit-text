package com.downstairs.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
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

    private var startAngle = 270f
    private var sweepAngle: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

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
        canvas.drawArc(progressBox, startAngle, sweepAngle, false, progressPaint)
    }

    private fun sweepAngle(progress: Int) {
        sweepAngle = if (progress > 0) (360f * progress / 100) else 0f
    }

    fun animateProgress(duration: Long) {
        animateFloatRange(to = 180f, durationMs = duration) { sweepAngle = it }
    }
}


fun animateFloatRange(
    from: Float = 0f,
    to: Float = 100f,
    durationMs: Long = 600,
    repeat: Int = 0,
    onUpdate: (Float) -> Unit
) {
    ValueAnimator.ofFloat(from, to).apply {
        duration = durationMs
        repeatCount = repeat
        interpolator = LinearInterpolator()
        addUpdateListener { valueAnimator ->
            onUpdate(valueAnimator.animatedValue as Float)
        }
    }.start()
}