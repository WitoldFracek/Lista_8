package com.example.lista_8

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.MediaController
import android.widget.VideoView

class PlayVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        val videoView: VideoView = findViewById(R.id.video_view)

        val passedIntent = intent
        val passedBundle = passedIntent.extras
        val title = passedBundle?.getLong(VideoListActivity.TITLE)
        if(title == null) {
            videoView.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.ahsoka_vs_maul}"))
        } else {
            val newUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, title.toString())
            videoView.setVideoURI(newUri)
        }

        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)

        videoView.setOnPreparedListener{
            videoView.start()
        }
    }
}