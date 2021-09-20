package com.downstairs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.downstairs.components.DrawView

class DrawActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val drawView = DrawView(this)
        drawView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setContentView(drawView)
    }
}