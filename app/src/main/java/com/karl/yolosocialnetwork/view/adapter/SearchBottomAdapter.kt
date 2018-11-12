package com.karl.yolosocialnetwork.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.item_rcv_suggest_people.view.*

class SearchBottomAdapter(val context: Context, private val listUserSearchSg: MutableList<User>)
    : RecyclerView.Adapter<SearchBottomAdapter.ViewHolder>() {
    private var onItemClickListenner: OnItemClickListenner? = null
    private val inflater = LayoutInflater.from(context)
    private var followRef = FirebaseDatabase.getInstance().reference.child("Follow")
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_rcv_suggest_people, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listUserSearchSg.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = listUserSearchSg[position]
        holder.itemView.setOnClickListener {
            onItemClickListenner!!.onItemClicked(user, position)
        }
        val currentUserUId = FirebaseAuth.getInstance().currentUser!!.uid
        followRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(currentUserUId).hasChild(user.uid!!)) {
                    //followRef.child(currentUserUId).child(user.uid!!).removeValue()
                    holder.btnFollowSearch.text = "FOLLOWED"
                } else {
                    //followRef.child(currentUserUId).child(user.uid!!).setValue(true)
                    holder.btnFollowSearch.text = "FOLLOW"
                }
            }

        })
        holder.tvDescription.text = user.description
        holder.tvNameUserSearch.text = user.nameUser
        when (user.background) {
            "none" -> {
                holder.imvBackgroundSearch.setImageResource(R.drawable.background_default)
            }
            else -> {
                Glide.with(context)
                        .load(user.background)
                        .into(holder.imvBackgroundSearch)
            }
        }
        when (user.avatar) {
            "none" -> {
                holder.imvAvatarSearch.setImageResource(R.drawable.user_default_big)
            }
            else -> {
                Glide.with(context)
                        .load(user.avatar)
                        .into(holder.imvAvatarSearch)
            }

        }

    }

    fun setOnItemClickListenner(onItemClickListenner: OnItemClickListenner) {
        this.onItemClickListenner = onItemClickListenner
    }

    interface OnItemClickListenner {
        fun onItemClicked(user: User, position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDescription: TextView = itemView.tvDescription
        var tvNameUserSearch: TextView = itemView.tvNameUserSearch
        var imvAvatarSearch: ImageView = itemView.ivAvatar
        var imvBackgroundSearch: ImageView = itemView.ivBackground
        var btnFollowSearch: Button = itemView.btnFollowSearch
        var imvRemoveSearch: ImageView = itemView.imvRemoveSearch
    }
}