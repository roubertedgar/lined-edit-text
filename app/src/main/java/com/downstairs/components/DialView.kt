package com.downstairs.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when(this){
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35
private const val RADIAN_160_DEGREES = 3.53429173529
private const val RADIAN_45_DEGREES = 0.785398

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius = 0.0f
    private var centerY = 0.0f
    private var centerX = 0.0f
    private var fanSpeed = FanSpeed.OFF
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init{
        isClickable = true
        isFocusable = true
    }

    override fun performClick(): Boolean {
        fanSpeed = fanSpeed.next()
        invalidate()

        return super.performClick()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN
        canvas.drawCircle(centerX, centerY, radius, paint)

        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)

        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
            val label = resources.getString(i.label)
            canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        val startAngle = RADIAN_160_DEGREES
        val angle = startAngle + pos.ordinal * RADIAN_45_DEGREES
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }
}

