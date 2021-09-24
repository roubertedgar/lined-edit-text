package com.downstairs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.downstairs.components.R
import kotlinx.android.synthetic.main.progress_activity.*

class ProgressActivity : AppCompatActivity(R.layout.progress_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playbackButton.setProgress(50, 15000)
    }
}
