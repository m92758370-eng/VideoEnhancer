package com.videoenhancer.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class EnhanceQualityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "افزایش کیفیت ویدیو - به زودی"
        textView.textSize = 18f
        textView.setPadding(32, 32, 32, 32)
        setContentView(textView)
    }
}
