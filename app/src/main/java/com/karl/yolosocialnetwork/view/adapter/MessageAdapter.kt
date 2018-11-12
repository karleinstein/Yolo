package com.karl.yolosocialnetwork.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_message_rcv.view.*
import kotlinx.android.synthetic.main.item_message_send.view.*

class MessageAdapter(val context: Context, private val listMessage: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("User")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message = listMessage[position]
        val currentUserUid: String = firebaseAuth.currentUser!!.uid
        val fromUser: String = message.from!!
        when (holder.itemViewType) {
            1 -> {
                val holderSend = holder as ViewHolderSend
                if (fromUser == currentUserUid) {
                    holderSend.tvSendMessage.text = message.contents

                }

            }
            2 -> {
                val holderRcv = holder as ViewHolderRcv
                if (fromUser != currentUserUid) {
                    holderRcv.tvMessageRcv.text = message.contents
                    userRef.child(message.from!!).child("avatar").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val avatarLink: String = p0.value.toString()
                            if (avatarLink != "none") {
                                Glide.with(context)
                                        .load(avatarLink)
                                        .into(holderRcv.imvUserSend)
                            }
                        }

                    })
                }

            }
        }

    }

    private val inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = inflater.inflate(R.layout.item_message_send, parent, false)
        when (viewType) {
            1 -> {
                view = inflater.inflate(R.layout.item_message_send, parent, false)
                return ViewHolderSend(view)
            }
            2 -> {
                view = inflater.inflate(R.layout.item_message_rcv, parent, false)
                return ViewHolderRcv(view)

            }
            else -> {

            }
        }
        return ViewHolderSend(view)
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message = listMessage[position]
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid

        val fromUser = message.from
        return if (fromUser.equals(currentUserUid)) {
            1
        } else {
            2
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolderSend(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSendMessage: TextView = itemView.tvSendMessage
    }

    inner class ViewHolderRcv(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imvUserSend: ImageView = itemView.imvAvtUserSent
        val tvMessageRcv: TextView = itemView.tvRcvMessage
    }

}