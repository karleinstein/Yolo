package com.karl.yolosocialnetwork.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import kotlinx.android.synthetic.main.item_searching.view.*

class SearchingAdapter(val context: Context, private val listUsers: List<User>) : RecyclerView.Adapter<SearchingAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(container: ViewGroup, position: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_searching, container, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = listUsers[position]
        if (user.avatar == "none") {
            holder.circleAvtSearching.setImageResource(R.drawable.user_default)
        } else {
            Glide.with(context).load(user.avatar).into(holder.circleAvtSearching)
        }
        holder.txtNameSearching.text = user.nameUser
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleAvtSearching: ImageView = itemView.circleAvtSearching
        val txtNameSearching: TextView = itemView.tvNameSearching
    }
}