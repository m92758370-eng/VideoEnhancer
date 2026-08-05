package com.videoenhancer.app

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Contrast
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File

@UnstableApi
class EnhanceQualityActivity : AppCompatActivity() {

    private lateinit var txtStatus: TextView
    private lateinit var progressBar: ProgressBar
    private var selectedUri: Uri? = null
    private lateinit var transformer: Transformer

    private val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            txtStatus.text = "ویدیو انتخاب شد"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enhance_quality)

        txtStatus = findViewById(R.id.txtStatus)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        findViewById<Button>(R.id.btnPickVideo).setOnClickListener {
            pickVideoLauncher.launch("video/*")
        }

        findViewById<Button>(R.id.btnStartProcess).setOnClickListener {
            val uri = selectedUri
            if (uri == null) {
                Toast.makeText(this, "اول یه ویدیو انتخاب کن", Toast.LENGTH_SHORT).show()
            } else {
                startProcessing(uri)
            }
        }
    }

    private fun startProcessing(uri: Uri) {
        val outputDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val outputFile = File(outputDir, "enhanced_${System.currentTimeMillis()}.mp4")

        progressBar.visibility = View.VISIBLE
        txtStatus.text = "در حال پردازش..."

        val videoEffects = listOf(
            ScaleAndRotateTransformation.Builder().setScale(1.5f, 1.5f).build(),
            Contrast(0.1f)
        )

        val editedMediaItem = EditedMediaItem.Builder(MediaItem.fromUri(uri))
            .setEffects(Effects(emptyList(), videoEffects))
            .build()

        transformer = Transformer.Builder(this)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        txtStatus.text = "پردازش تمام شد:\n${outputFile.absolutePath}"
                        Toast.makeText(this@EnhanceQualityActivity, "ویدیو با کیفیت بهتر ذخیره شد", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        txtStatus.text = "پردازش ناموفق بود"
                        Toast.makeText(this@EnhanceQualityActivity, "خطا: ${exportException.message}", Toast.LENGTH_LONG).show()
                    }
                }
            })
            .build()

        transformer.start(editedMediaItem, outputFile.absolutePath)
    }
}
