package com.downstairs.linededittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

    private val lines = mutableListOf<Line>()
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

        val lineSpacing = 20
        val lineSize = (availableLineSize - lineSpacing).toFloat()

        val yPoint = (height - paddingBottom).toFloat() - dpToPixel(3f)
        var xPoint = INITIAL_X + paddingStart + lineSpacing / 2f

        for (i in 0 until TEXT_LENGTH) {
            val stopX = xPoint + lineSize

            lines.add(Line(xPoint, yPoint, stopX))
            xPoint += lineSize + lineSpacing
        }
    }

    override fun onDraw(canvas: Canvas) {

        lines.forEach {
            canvas.drawLine(it.startX, it.startY, it.stopX, it.stopY, linePaint)
        }
    }
}

class Line(val startX: Float, val startY: Float, val stopX: Float, val stopY: Float = startY) {

    val center: Float
        get() = (stopX - startX) / 2
}