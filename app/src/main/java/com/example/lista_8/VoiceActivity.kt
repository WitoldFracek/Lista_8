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
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import java.io.File
import java.io.IOException
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*


lateinit var mediaRecorder: MediaRecorder

lateinit var timeText: TextView
lateinit var recordButton: Button
lateinit var pauseRecordButton: Button
lateinit var stopRecordButton: Button
lateinit var playButton: Button
lateinit var pausePlayButton: Button
lateinit var recordProgress: SeekBar

class VoiceActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null
    var mediaRecorder: MediaRecorder? = null

    private var isRecording = false
    private var fileName = ""
    private var userStart = 0.0
    private var isTrackingDisabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice)

        ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
        10)

        fileName = "${externalCacheDir?.absolutePath}/test_nagrania.3gp"

        mediaPlayer = MediaPlayer()
        


        timeText = findViewById(R.id.record_time_text)

        recordButton = findViewById(R.id.start_record_button)
        pauseRecordButton = findViewById(R.id.pause_record_button)
        pauseRecordButton.isEnabled = false
        stopRecordButton = findViewById(R.id.stop_record_button)
        stopRecordButton.isEnabled = false

        playButton = findViewById(R.id.play_record_button)
        pausePlayButton = findViewById(R.id.play_pause_button)
        pausePlayButton.isEnabled = false

        recordProgress = findViewById(R.id.record_progress_bar)
        recordProgress.max = 100

        setupListeners()

        timeText.text = "$fileName\n\n0:00"

    }

    private fun startRecording() {
//        val filePath = Environment.getExternalStorageDirectory().toString() + File.separator + getHash() + ".3gpp"
        if(mediaRecorder == null){
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
        }
        isRecording = true
    }

    private fun stopPlaying() {
        mediaPlayer!!.stop()
        mediaPlayer!!.reset()
        mediaPlayer!!.release()
        mediaPlayer = null

        recordButton.isEnabled = true
        pauseRecordButton.isEnabled = false
        stopRecordButton.isEnabled = false
        playButton.isEnabled = true
        pausePlayButton.isEnabled = true
        isRecording = false
    }

    @SuppressLint("RestrictedApi")
    private fun setupListeners() {
        recordProgress.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {

            var wasPlaying = false

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(!fromUser && !isTrackingDisabled){
                    val minutes:Int = (mediaPlayer!!.currentPosition.toDouble() / 60000).toInt()
                    val seconds: Int = (mediaPlayer!!.currentPosition.toDouble() / 1000 - minutes.toDouble() * 60).toInt()
                    timeText.text = if(seconds < 10){
                        "$fileName\n\n$minutes:0$seconds"
                    } else {
                        "$fileName\n\n$minutes:$seconds"
                    }
                } else {
                    userStart = progress.toDouble() / 100
                }
                if(progress == 100){
                    recordButton.isEnabled = true
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTrackingDisabled = true
                wasPlaying = mediaPlayer!!.isPlaying
                mediaPlayer!!.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer!!.seekTo((userStart * mediaPlayer!!.duration).toInt())
                isTrackingDisabled = false
                if(wasPlaying){
                    mediaPlayer!!.start()
                }
                startThread()
            }

        })

        recordButton.setOnClickListener {
            startRecording()
            it.isEnabled = false
            pauseRecordButton.isEnabled = true
            stopRecordButton.isEnabled = true
            playButton.isEnabled = false
            pausePlayButton.isEnabled = false
        }

        pauseRecordButton.setOnClickListener {
            isRecording = if(isRecording) {
                mediaRecorder!!.pause()
                false
            } else {
                mediaRecorder!!.resume()
                true
            }
            recordButton.isEnabled = false
            stopRecordButton.isEnabled = true
            playButton.isEnabled = false
            pausePlayButton.isEnabled = false
        }

        stopRecordButton.setOnClickListener {
            if(mediaRecorder != null){
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                mediaRecorder = null
            }
            isRecording = false
            recordButton.isEnabled = true
            it.isEnabled = false
            playButton.isEnabled = true
            pausePlayButton.isEnabled = true
        }

        playButton.setOnClickListener {
            if(mediaPlayer != null && !mediaPlayer!!.isPlaying){
                mediaPlayer = MediaPlayer().apply {
                    try{
                        setDataSource(fileName)
                        prepare()
                        seekTo((userStart * mediaPlayer!!.duration).toInt())
                        start()
                    } catch (e: IOException) {
                        Log.e(LOG_TAG, e.message as String)
                    }
                }
                isRecording = false
                recordButton.isEnabled = false
                pauseRecordButton.isEnabled = false
                stopRecordButton.isEnabled = false
                playButton.isEnabled = true
                pausePlayButton.isEnabled = true

                startThread()
            }

        }

        pausePlayButton.setOnClickListener {
            stopPlaying()
        }


    }

    @SuppressLint("RestrictedApi")
    fun startThread() {
        Thread {
            var position: Int
            try {
                while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    position = ((mediaPlayer!!.currentPosition.toDouble() / mediaPlayer!!.duration.toDouble()) * 100).toInt()
                    recordProgress.progress = position
                }
            } catch (e:IllegalStateException) {
                Log.e(LOG_TAG, "Message")
            } finally {
                userStart = 0.0
                recordProgress.progress = 0
            }
        }.start()
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

    override fun onBackPressed() {
        mediaPlayer!!.stop()
        super.onBackPressed()
    }
}