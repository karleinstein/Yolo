package com.karl.yolosocialnetwork.view.activity.newfeeds

import android.app.ActionBar
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.bumptech.glide.Glide
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.Comment
import com.karl.yolosocialnetwork.model.Post
import com.karl.yolosocialnetwork.view.adapter.CommentAdapter
import com.karl.yolosocialnetwork.view.adapter.NewFeedsApdater
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail_post.*
import kotlinx.android.synthetic.main.fragment_show_full_image_post.*

class FragmentImagePostClick : Fragment(), View.OnClickListener {


    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var countLoves: Long = 0
    //share element transistion
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val window = activity!!.window
        window.statusBarColor = Color.BLACK

        return inflater.inflate(R.layout.fragment_show_full_image_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        activity!!.btmNavigation.visibility = View.GONE
        receiveData()
        imvBack.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.imvBack -> {
                activity!!.supportFragmentManager.popBackStack()
            }
        }
    }

    private fun receiveData() {
        val bundle: Bundle = this.arguments!!
        val idPost = bundle.getString("idPost")
        val linkImage = bundle.getString("linkImage")
        val title = bundle.getString("title")
        val time = bundle.getString("timeStamp")
        val nameUserTemp = bundle.getString("nameUser")
        databaseReference.child("User").child(nameUserTemp).child("nameUser").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val realUser = p0.value.toString()
                tvTitle.text = title
                tvNameUser.text = realUser
                tvTimeStamp.text = time

                Glide.with(this@FragmentImagePostClick)
                        .load(linkImage)
                        .into(pvImagePost)
            }

        })
        databaseReference.child("Likes").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                countLoves = p0.child(idPost).childrenCount
                if (tvNumberLove != null) {
                    tvNumberLove.text = countLoves.toString()
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