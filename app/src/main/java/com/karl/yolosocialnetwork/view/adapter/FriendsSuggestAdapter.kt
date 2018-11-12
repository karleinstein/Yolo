package com.karl.yolosocialnetwork.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_suggesstion_friends.view.*
import kotlinx.android.synthetic.main.item_suggest_friends.view.*

class FriendsSuggestAdapter(val context: Context, private val listSuggestFrds: MutableList<User>)
    : RecyclerView.Adapter<FriendsSuggestAdapter.ViewHolder>() {
    private lateinit var folowPref: DatabaseReference
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userRef: DatabaseReference
    private var currentUserUid: String
    private var onItemClickListener: OnItemClickListener? = null
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var suggestFollow: DatabaseReference

    init {

        userRef = firebaseDatabase.reference.child("User")
        currentUserUid = firebaseAuth.currentUser!!.uid

    }

    val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_suggest_friends, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listSuggestFrds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = listSuggestFrds[position]
        folowPref = firebaseDatabase.reference.child("Follow")
        suggestFollow = firebaseDatabase.reference.child("User").child(currentUserUid).child("suggestFollow")
        holder.btnAddFrdSg.setOnClickListener { _ ->
            folowPref.child(currentUserUid).child(user.uid!!).setValue(true)
                    .addOnSuccessListener {
                        Toast.makeText(context, "you followed ${user.nameUser}", Toast.LENGTH_SHORT).show()
                        listSuggestFrds.remove(user)
                        Log.d("testfck1", user.uid!!)
                        suggestFollow.child(user.uid!!).removeValue()
                        notifyDataSetChanged()
                    }
        }
        holder.btnRemoveSg.setOnClickListener {
            listSuggestFrds.remove(user)
            Log.d("testfck1", user.uid!!)
            suggestFollow.child(user.uid!!).removeValue()
            notifyDataSetChanged()
        }



        holder.tvFrdSuggest.text = user.nameUser
        if (user.avatar == "none") {
            holder.imvAvatarFrdSg.setImageResource(R.drawable.user_default_big)
        } else {
            Glide.with(context).load(user.avatar).into(holder.imvAvatarFrdSg)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener!!.onItemClicked(user, position)
        }

    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imvAvatarFrdSg: ImageView = itemView.imvAvtFrdSuggest
        val btnAddFrdSg: Button = itemView.btnAddFriends
        val btnRemoveSg: Button = itemView.btnRemoveSuggest
        val tvFrdSuggest: TextView = itemView.tvNameFrdSuggest
    }


    interface OnItemClickListener {
        fun onItemClicked(user: User, position: Int)
    }
}