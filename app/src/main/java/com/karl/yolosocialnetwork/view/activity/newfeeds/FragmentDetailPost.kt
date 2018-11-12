package com.karl.yolosocialnetwork.view.activity.newfeeds

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.karl.yolosocialnetwork.model.Comment
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.view.adapter.CommentAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail_post.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentDetailPost : Fragment(), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var idPost: String
    private var userRef = FirebaseDatabase.getInstance().reference.child("User")
    private lateinit var listComments: MutableList<Comment>
    private lateinit var cmtAdapter: CommentAdapter
    private var isLiked = true
    private var countLoves: Long = 0

    companion object {
        const val TAG = "FragmentDetailPost"
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btnSend -> {
                generateCmt()
            }
            R.id.layoutLoveDetail -> {
                val likeRef: DatabaseReference = databaseReference.child("Likes")
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                isLiked = true
                likeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        isLiked = if (dataSnapshot.child(idPost).hasChild(currentUserId)) {
                            likeRef.child(idPost).child(currentUserId).removeValue()
                            tvLoveDetail.setTextColor(Color.BLACK)
                            imvLoveDetail.setImageResource(R.drawable.like)
                            false
                        } else {
                            likeRef.child(idPost).child(currentUserId).setValue(true)
                            tvLoveDetail.setTextColor(Color.RED)
                            imvLoveDetail.setImageResource(R.drawable.liked)
                            false
                        }
                    }

                })

            }
        }
    }

    private fun generateCmt() {
        val userId = firebaseAuth.currentUser!!.uid

        userRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val nameUser: String = p0.child("nameUser").getValue(String::class.java)!!
                    val avatarLink: String = p0.child("avatar").getValue(String::class.java)!!
                    displayComment(nameUser, avatarLink)
                }
            }

            fun displayComment(nameUser: String, avatar: String) {
                if (context != null) {
                    if (edtComment != null && edtComment.text.isNotEmpty()) {
                        val calendar = Calendar.getInstance()
                        val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm")
                        val currentDateTime = currentDate.format(calendar.time)
                        val contentCmt = edtComment.text.toString()
                        val comment = Comment(contentCmt, currentDateTime, nameUser, idPost, avatar)
                        val postRef = FirebaseDatabase.getInstance().reference.child("post").child(idPost).child("comments")
                        val idComment: String = postRef.push().key!!
                        postRef.child(idComment).setValue(comment)
                        listComments.clear()
                        edtComment.setText("")
                    } else {
                        Toast.makeText(context, "What are you doing????", Toast.LENGTH_SHORT).show()
                    }
                }


            }


        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.btmNavigation.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_detail_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle: Bundle = this.arguments!!
        tvContentDetail.visibility = View.GONE
        imvImagePostDetail.visibility = View.GONE
        listComments = ArrayList()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        checkNumberLoves()
        idPost = bundle.getString("idPost", "fail")
        val linkImage: String = bundle.getString("linkImage", "gg")
        val title: String = bundle.getString("title", "trash")
        val content = bundle.getString("content", "fail")
        val nameUserTemp = bundle.getString("nameUser", "cant")
        val timeStamp = bundle.getString("timeStamp")
        val avatarLink = bundle.getString("avatar", "wp")
        btnSend.setOnClickListener(this)
        layoutLoveDetail.setOnClickListener(this)
        val postRef = FirebaseDatabase.getInstance().reference.child("post")
                .child(idPost).child("comments")
        postRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (dataSnapshot in p0.children) {
                            val comment: Comment = dataSnapshot.getValue(Comment::class.java)!!
                            listComments.add(comment)
                            if (context != null) {
                                cmtAdapter = CommentAdapter(context!!, listComments)
                                val layoutManager = LinearLayoutManager(context)
                                layoutManager.stackFromEnd = true
                                rcvShowComment.layoutManager = layoutManager
                                rcvShowComment.adapter = cmtAdapter
                                cmtAdapter.notifyDataSetChanged()
                            }


                        }
                    }

                }
        )
        if (content != "fail") {
            tvContentDetail.visibility = View.VISIBLE
            tvContentDetail.text = content
        }
        if (linkImage != "gg") {
            imvImagePostDetail.visibility = View.VISIBLE
            Glide.with(context!!)
                    .load(linkImage)
                    .into(imvImagePostDetail)
        }
        if (avatarLink != "wp") {
            Glide.with(context!!)
                    .load(avatarLink)
                    .into(imvAvatarDetail)

        }
        databaseReference.child("User").child(nameUserTemp).child("nameUser").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val realUser = p0.value.toString()
                tvNameUserDetail.text = realUser
                tvTitleDetail.text = title
                tvTimeDetail.text = timeStamp
            }

        })
    }

    private fun checkNumberLoves() {
        val likeRef: DatabaseReference = databaseReference.child("Likes")
        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                countLoves = p0.child(idPost).childrenCount
                if (tvLoveDetail != null) {
                    tvLoveDetail.text = countLoves.toString()
                }

            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (activity!!.btmNavigation.visibility == View.GONE) {
            activity!!.btmNavigation.visibility = View.VISIBLE
        }
    }
}