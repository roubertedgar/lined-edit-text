package com.downstairs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.downstairs.components.R
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawButton.setOnClickListener {
            startActivity(Intent(this, DrawActivity::class.java))
        }

        progressButton.setOnClickListener {
            startActivity(Intent(this, ProgressActivity::class.java))
        }
    }
}