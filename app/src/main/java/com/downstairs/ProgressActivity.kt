package com.downstairs

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.downstairs.components.R
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.progress_activity.*

class ProgressActivity : AppCompatActivity(R.layout.progress_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playbackButton.animateProgress(5000)
    }
}
