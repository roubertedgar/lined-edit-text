package com.downstairs.components

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val drawView = DrawView(this)
        drawView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN

        setContentView(drawView)
    }
}