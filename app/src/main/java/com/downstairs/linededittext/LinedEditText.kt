package com.downstairs.linededittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.roundToInt


class LinedEditText @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    styleAttribute: Int = 0
) : AppCompatTextView(context, attributes, styleAttribute) {

    companion object {
        const val INITIAL_X = 0
        const val MIN_LAYOUT_SIZE_DP = 50f
        const val TEXT_LENGTH = 6
    }

    //Line
    private val lines = FloatArray(TEXT_LENGTH * 4)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dpToPixel(2f)
        linePaint.color = Color.GRAY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight =
            (dpToPixel(MIN_LAYOUT_SIZE_DP) + paddingTop + paddingBottom).roundToInt()
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec) + paddingLeft + paddingRight

        val measuredWidth = measureDimension(desiredWidth, widthMeasureSpec)
        val measuredHeight = measureDimension(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidht: Int, oldHeight: Int) {
        val availableHeight = height - paddingTop - paddingBottom
        createBottomLines(width, height)
    }

    private fun createBottomLines(width: Int, height: Int) {
        val availableWidth = width - paddingStart - paddingEnd
        val availableLineSize = availableWidth / TEXT_LENGTH
        val remainWidth = availableWidth % TEXT_LENGTH
        val minLineSpace = remainWidth.toFloat() / (TEXT_LENGTH - 1).toFloat()

        val lineSpacing = 20 + minLineSpace / 6
        val lineSize = availableLineSize - lineSpacing

        val yPoint = (height - paddingBottom).toFloat() - dpToPixel(2f)
        var xPoint = INITIAL_X + paddingStart + lineSpacing / 2

        for (i in lines.indices step 4) {
            lines[i] = xPoint
            lines[i + 1] = yPoint
            lines[i + 2] = xPoint + lineSize
            lines[i + 3] = yPoint

            xPoint += lineSize + lineSpacing
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            drawLines(lines, linePaint)
        }
    }
}
