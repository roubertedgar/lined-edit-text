package com.downstairs.components

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

private const val STROKE_WIDTH = 12f

class DrawView(context: Context) : View(context) {

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private lateinit var frame: Rect
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitMap: Bitmap

    val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
    }

    val path = Path()

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::extraBitMap.isInitialized) extraBitMap.recycle()

        extraBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitMap)
        extraCanvas.drawColor(backgroundColor)

        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)

        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)

        if (dx >= touchTolerance || dy >= touchTolerance) {

            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            extraCanvas.drawPath(path, paint)
        }

        invalidate()
    }

    private fun touchUp() {
        path.reset()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitMap, 0f, 0f, null)
        canvas.drawRect(frame, paint)
    }
}
