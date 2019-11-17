package com.downstairs.linededittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.TextView
import kotlin.math.roundToInt


class LinedEditText(context: Context, attributes: AttributeSet) : TextView(context, attributes) {

    private val linePath = Path()
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)

    companion object {
        const val MIN_LAYOUT_SIZE_DP = 50f
        const val INITIAL_X = 0
        const val TEXT_LENGTH = 6
    }

    init {
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dpToPixel(2f)
        linePaint.color = Color.GRAY

        configureText()
    }

    private fun configureText() {
        textPaint.color = Color.BLACK
        textPaint.textSize = dpToPixel(28f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val fontMetrics = textPaint.fontMetrics

        val desiredHeight =
            (dpToPixel(MIN_LAYOUT_SIZE_DP) + paddingTop + paddingBottom).roundToInt()
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec) + paddingLeft + paddingRight

        val measuredWidth = measureDimension(desiredWidth, widthMeasureSpec)
        val measuredHeight = measureDimension(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.also {
            drawPath(it)
            drawText(it)
        }
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText("A", getPathLineX().toFloat(), getPathLineY().toFloat(), textPaint)
    }

    private fun drawPath(canvas: Canvas) {
        var pathX = getPathLineX().toFloat()
        val pathY = getPathLineY().toFloat()
        val drawableWidth = getDrawableWidht()

        val lineSpacing = 20
        val lineSize = measureLineSize(drawableWidth, lineSpacing)

        for (i in 1..TEXT_LENGTH) {
            linePath.moveTo(pathX + lineSpacing, pathY)
            pathX += lineSize

            linePath.lineTo(pathX, pathY)
            pathX += lineSpacing
        }

        canvas.drawPath(linePath, linePaint)
    }

    private fun measureLineSize(drawableWidth: Int, lineSpacing: Int): Float {
        val maxLineSize = drawableWidth / TEXT_LENGTH
        val remainWidth = drawableWidth % TEXT_LENGTH
        val minLineSpace = remainWidth.toFloat() / (TEXT_LENGTH - 1).toFloat()

        return maxLineSize - (lineSpacing - minLineSpace)
    }

    private fun getCharWidth() = textPaint.measureText("A", 0, 1)

    private fun getPathLineX(): Int = INITIAL_X + paddingStart

    private fun getPathLineY(): Int = height - paddingBottom - 20

    private fun getDrawableWidht(): Int = width - paddingStart - paddingEnd

}
