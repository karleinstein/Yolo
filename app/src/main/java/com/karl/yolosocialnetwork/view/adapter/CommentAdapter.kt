package com.karl.yolosocialnetwork.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.karl.yolosocialnetwork.model.Comment
import com.karl.socialnetwork.R
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentAdapter(val context: Context, private val listCmts: List<Comment>)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_comment, p0, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listCmts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNameUserCmt.text = listCmts[position].userName
        holder.tvContentCmt.text = listCmts[position].content
        holder.tvTimeStampCmt.text = listCmts[position].timeStamp
        if (listCmts[position].avatar == "none") {
            holder.imvAvatarCmt.setImageResource(R.drawable.user_default)
        } else {
            Glide.with(context)
                    .load(listCmts[position].avatar)
                    .into(holder.imvAvatarCmt)
        }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameUserCmt: TextView = itemView.tvNameUserCmt
        val tvTimeStampCmt: TextView = itemView.tvTimeCmt
        val tvContentCmt: TextView = itemView.tvContentCmt
        val imvAvatarCmt: ImageView = itemView.imvAvatarCmt

    }
}