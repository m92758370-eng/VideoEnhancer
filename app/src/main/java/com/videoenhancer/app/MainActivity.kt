package com.videoenhancer.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnEnhanceQuality).setOnClickListener {
            startActivity(Intent(this, EnhanceQualityActivity::class.java))
        }

        findViewById<Button>(R.id.btnRemoveText).setOnClickListener {
            startActivity(Intent(this, RemoveTextActivity::class.java))
        }
    }
}
