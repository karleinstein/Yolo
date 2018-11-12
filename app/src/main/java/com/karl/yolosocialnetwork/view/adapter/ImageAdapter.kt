package com.karl.yolosocialnetwork.view.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karl.socialnetwork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_image.view.*

class ImageAdapter(val context: Context, val links: ArrayList<String>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    private val firebaseStorage: StorageReference = FirebaseStorage.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_image, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return links.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val s = links[p1]
        val uri: Uri = Uri.parse(s)
        p0.image.setImageURI(uri)

        p0.itemView.setOnClickListener { v ->


        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.itemImage
    }


}