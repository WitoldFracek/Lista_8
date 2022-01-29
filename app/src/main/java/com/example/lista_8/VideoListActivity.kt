package com.example.lista_8

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.app.ActivityCompat

val PERMISIONS = 11

class VideoListActivity : AppCompatActivity() {

    lateinit var adapter: ArrayAdapter<String>
    lateinit var titles: MutableList<String>
    lateinit var ids: MutableList<Long>
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISIONS)

        listView = findViewById(R.id.title_list)
        seedLists()
        adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, titles)
        listView.adapter = adapter

        listView.setOnItemClickListener {_, _, position, _ ->
            val intent = Intent(this, PlayVideoActivity::class.java)
            val bundle = Bundle()
            bundle.putLong(TITLE, ids[position])
            intent.putExtras(bundle)
            startActivity(intent)
        }
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

    fun seedLists() {
        titles = mutableListOf()
        ids = mutableListOf()
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        Log.println(Log.INFO, "check", MediaStore.Video.Media.EXTERNAL_CONTENT_URI.path.toString())
        val contentResolver = contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        if(cursor == null){
            // err
        } else if (!cursor.moveToFirst()) {
            Log.println(Log.INFO, "no files", "No files in that storage")
        } else {
            val id = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val title = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
            do {
               val itemId = cursor.getLong(id)
               val itemTitle = cursor.getString(title)
               titles.add(itemTitle)
               ids.add(itemId)
            } while(cursor.moveToNext())
        }
        cursor?.close()
    }

    companion object {
        val TITLE = "title"
    }

}