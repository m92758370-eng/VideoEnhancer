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
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File
import java.io.FileOutputStream

class EnhanceQualityActivity : AppCompatActivity() {

    private lateinit var txtStatus: TextView
    private lateinit var progressBar: ProgressBar
    private var selectedInputFile: File? = null

    private val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            copyUriToFile(uri)
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
            val input = selectedInputFile
            if (input == null) {
                Toast.makeText(this, "اول یه ویدیو انتخاب کن", Toast.LENGTH_SHORT).show()
            } else {
                startProcessing(input)
            }
        }
    }

    private fun copyUriToFile(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val tempFile = File(cacheDir, "input_video.mp4")
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            selectedInputFile = tempFile
            txtStatus.text = "ویدیو انتخاب شد: ${tempFile.length() / 1024} کیلوبایت"
        } catch (e: Exception) {
            Toast.makeText(this, "خطا در بارگذاری ویدیو", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startProcessing(input: File) {
        val outputDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val outputFile = File(outputDir, "enhanced_${System.currentTimeMillis()}.mp4")

        val filter = "hqdn3d=4:3:6:4,unsharp=5:5:1.0:5:5:0.0,scale=iw*1.5:ih*1.5:flags=lanczos"
        val command = "-y -i \"${input.absolutePath}\" -vf \"$filter\" -c:v libx264 -preset medium -crf 20 -c:a aac -b:a 128k \"${outputFile.absolutePath}\""

        progressBar.visibility = View.VISIBLE
        txtStatus.text = "در حال پردازش..."

        FFmpegKit.executeAsync(command) { session ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                if (ReturnCode.isSuccess(session.returnCode)) {
                    txtStatus.text = "پردازش تمام شد:\n${outputFile.absolutePath}"
                    Toast.makeText(this, "ویدیو با کیفیت بهتر ذخیره شد", Toast.LENGTH_LONG).show()
                } else {
                    txtStatus.text = "پردازش ناموفق بود"
                    Toast.makeText(this, "خطا در پردازش ویدیو", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
