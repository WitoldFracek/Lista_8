package com.example.lista_8

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

lateinit var EXTERNAL_STORAGE: String
lateinit var mediaPlayer: MediaPlayer
lateinit var mediaRecorder: MediaRecorder

class VoiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice)

        ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
        10)

        val idList = mutableListOf<Long>()
        val uri: Uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        mediaPlayer = MediaPlayer()
        mediaRecorder = MediaRecorder()
        setupMediaRecorder(mediaRecorder)
    }

    fun setupMediaRecorder(mr: MediaRecorder){
//        val filePath = Environment.getExternalStorageDirectory().toString() + File.separator + getHash() + ".3gpp"
        with(mr) {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }
        mr.prepare()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getHash(): String{
        val dateFormat = SimpleDateFormat("yyMMddHHmmss")
        return dateFormat.format(Calendar.getInstance().time)
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