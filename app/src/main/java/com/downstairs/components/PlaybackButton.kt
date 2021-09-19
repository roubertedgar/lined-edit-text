package com.downstairs.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class PlaybackButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val size = context.resources.getDimension(R.dimen.default_progress_size)
    private val stroke = context.resources.getDimension(R.dimen.default_progress_stroke)

    private var progressBox = RectF(0f, 0f, 0f, 0f)


    private var progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = stroke
        color = resources.getColor(R.color.colorAccent)
    }

    private var progress = 50
        set(value) {
            field = if (value > 0) (360 * value / 100) else 0
            invalidate()
        }

    init {
        if (isInEditMode) progress = 100
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = size.toInt()
        val desiredWidth = size.toInt()

        val measuredWidth = measureDimension(desiredWidth, widthMeasureSpec)
        val measuredHeight = measureDimension(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        // stroke = line of 1dp + halfTop + halfBottom
        val halfStrokeTop = stroke / 2 + dpToPixel(1f)
        val halfStrokeBottom = stroke / 2

        val left = halfStrokeTop + paddingLeft
        val right = (width - halfStrokeBottom) - paddingRight
        val bottom = (height - halfStrokeBottom) - paddingBottom
        val top = halfStrokeTop + paddingTop
        progressBox = RectF(left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawArc(progressBox, START_ANGLE, progress.toFloat(), false, progressPaint)
    }

    companion object {
        const val START_ANGLE = 270f
    }
}


fun measureDimension(desiredSize: Int, measureSpec: Int): Int {

    val specMode = View.MeasureSpec.getMode(measureSpec)
    val specSize = View.MeasureSpec.getSize(measureSpec)

    if (specMode == View.MeasureSpec.EXACTLY) {
        return specSize
    } else {

        if (specMode == View.MeasureSpec.AT_MOST) {
            return desiredSize.coerceAtMost(specSize)
        }

        return desiredSize
    }
}


fun View.pixelToDp(pixel: Float): Float {
    val scale = this.resources.displayMetrics.density
    return pixel / scale - 0.5f
}

fun View.dpToPixel(dp: Float): Float {
    val scale = this.resources.displayMetrics.density
    return dp * scale + 0.5f
}