package com.karl.yolosocialnetwork.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karl.yolosocialnetwork.App
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import kotlinx.android.synthetic.main.item_friends.view.*

class FriendsAdapter(val context: Context, private val listFriends: List<User>) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    val inflater: LayoutInflater = LayoutInflater.from(context)
    private var onItemClickMessage: OnItemClickMessageListenner? = null
    private var userRef = FirebaseDatabase.getInstance().reference.child("User")
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_friends, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listFriends.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = listFriends[position]
        holder.tvNameFrd.text = friend.nameUser
        userRef.child(friend.uid!!).child("avatar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val avatarLink = p0.value.toString()
                if (avatarLink != "none") {
                    Glide.with(context)
                            .load(avatarLink)
                            .into(holder.imvAvtFrd)
                }

            }

        })
        if (friend.online == 0.toLong()) {
            holder.tvStatusFrd.text = "Online"
            holder.tvStatusFrd.setTextColor(Color.GREEN)
        } else {
            val lastSeen: String? = App.getTimeAgo(friend.online!!, context)
            if (lastSeen != null) {
                holder.tvStatusFrd.text = "Last Online: $lastSeen"
            }


        }
        holder.imvMessage.setOnClickListener {
            onItemClickMessage!!.onItemClickedMessage(friend, position)

        }
    }

    fun setOnItemClickMessageListenner(onItemClickMessageListenner: OnItemClickMessageListenner) {
        this.onItemClickMessage = onItemClickMessageListenner
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameFrd: TextView = itemView.tvNameFrd
        val imvAvtFrd: ImageView = itemView.imvAvtFrd
        val tvStatusFrd: TextView = itemView.tvStatusFrd
        val imvMessage: ImageView = itemView.imvMessage

    }

    interface OnItemClickMessageListenner {
        fun onItemClickedMessage(friend: User, position: Int)
    }
}