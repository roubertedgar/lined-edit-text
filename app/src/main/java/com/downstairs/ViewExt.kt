package com.downstairs

import android.view.View

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