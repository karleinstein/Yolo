package com.karl.yolosocialnetwork.view.activity

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.view.adapter.ImageAdapter
import kotlinx.android.synthetic.main.activity_list_image.*
import java.util.*

class ListImageActivity : AppCompatActivity() {

    private lateinit var imageAdapter: ImageAdapter
    private lateinit var links: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_image)

        init()
    }

    private fun init() {

        val checkReadStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (checkReadStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            val layoutManager = GridLayoutManager(this, 3)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            rcvImage.layoutManager = layoutManager

            links = getImage()
            imageAdapter = ImageAdapter(this, links)
            rcvImage.adapter = imageAdapter
            imageAdapter.notifyDataSetChanged()
        }

    }


    private fun getImage(): ArrayList<String> {
        val links = ArrayList<String>()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor.moveToFirst()

        while (!cursor.isAfterLast) {

            val link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            links.add(link)
            cursor.moveToNext()
        }

        return links
    }



}
