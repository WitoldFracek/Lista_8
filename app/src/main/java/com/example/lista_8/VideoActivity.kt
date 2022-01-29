package com.example.lista_8

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.VideoView
import androidx.core.app.ActivityCompat

val REQUEST_VIDEO_CAPTURE = 1

class VideoActivity : AppCompatActivity() {

    lateinit var videoView: VideoView
    lateinit var recordButton: Button
    lateinit var playPauseButton: Button

    private var isRecording = false
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            100)

        videoView = findViewById(R.id.camera_view)
        recordButton = findViewById(R.id.record_video_camera_button)

        recordButton.setOnClickListener {
            startRecording()
        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startRecording() {
        intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.resolveActivity(packageManager)
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val videoUri = intent.data
        videoView.setVideoURI(videoUri)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val req = mutableListOf<Boolean>()
        for(grant in grantResults) {
            req.add(grant == PackageManager.PERMISSION_GRANTED)
        }
        if(req.any { !it }) {
            onBackPressed()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}