package com.example.lista_8

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recordVoiceButton: Button = findViewById(R.id.record_voice_button)
        recordVoiceButton.setOnClickListener {
            val intent = Intent(this, VoiceActivity::class.java)
            startActivity(intent)
        }

        val recordVideoButton: Button = findViewById(R.id.record_video_button)
        recordVideoButton.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }

        val playVideoButton: Button = findViewById(R.id.play_video_button)
        playVideoButton.setOnClickListener {
            val intent = Intent(this, VideoListActivity::class.java)
            startActivity(intent)
        }

        val testButton: Button = findViewById(R.id.test_button)
        testButton.setOnClickListener {
            val intent = Intent(this, PlayVideoActivity::class.java)
            startActivity(intent)
        }
    }
}