package com.karl.yolosocialnetwork.view.activity.newfeeds

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.Post
import com.karl.yolosocialnetwork.view.activity.FragmentProfile
import com.karl.yolosocialnetwork.view.adapter.NewFeedsApdater
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_new_feeds.*
import kotlinx.android.synthetic.main.item_new_feeds_image_post.*
import kotlinx.android.synthetic.main.item_new_feeds_status_post.*
import java.util.*

class FragmentNewFeeds : Fragment(), NewFeedsApdater.OnItemClickListener,
        NewFeedsApdater.OnItemClickImageViewListener, NewFeedsApdater.OnItemClickAvtListener, SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        rcvNewFeeds.scrollToPosition(0)
        swipeRefresh.isRefreshing = false
    }


    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private lateinit var lists: MutableList<Post>
    private var newFeedsApdater: NewFeedsApdater? = null
    override fun onItemImageViewClicked(position: Int, post: Post) {
        val fragmentImagePostClick = FragmentImagePostClick()
        val changeTransform: Transition = TransitionInflater.from(context)
                .inflateTransition(R.transition.change_image_transform)
        val exploreTransform: Transition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.explode)
        this.sharedElementReturnTransition = changeTransform
        this.exitTransition = exploreTransform
        fragmentImagePostClick.sharedElementEnterTransition = changeTransform
        fragmentImagePostClick.enterTransition = exploreTransform

        activity!!.supportFragmentManager.beginTransaction()
                .add(R.id.contentsM, fragmentImagePostClick)
                .addToBackStack(null)
                .addSharedElement(imvImagePost2, "imagePost")
                .commit()
        val bundle = Bundle()
        bundle.putString("idPost", post.idPost)
        bundle.putString("linkImage", post.image)
        bundle.putString("title", post.title)
        bundle.putString("timeStamp", post.timeStamp)
        bundle.putString("nameUser", post.userUid)

        fragmentImagePostClick.arguments = bundle
    }

    override fun onItemClickedAvt(position: Int, post: Post) {
        val fragmentProfile = FragmentProfile()
        val changeTransform: Transition = TransitionInflater.from(context)
                .inflateTransition(R.transition.change_image_transform)
        val exploreTransform: Transition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.explode)
        this.sharedElementEnterTransition = changeTransform
        this.enterTransition = Fade()
        this.exitTransition = Fade()
//        this.sharedElementReturnTransition = changeTransform
        //fragmentProfile.sharedElementEnterTransition = changeTransform
        //fragmentProfile.enterTransition = exploreTransform
        if (activity!!.imvAvatar != null) {
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.contentsM, fragmentProfile)
                    .addSharedElement(imvAvatar, "avatar")
                    .addToBackStack(null)
                    .commit()
        }
        if (activity!!.imvAvatar2 != null) {
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.contentsM, fragmentProfile)
                    .addSharedElement(imvAvatar2, "avatar")
                    .addToBackStack(null)
                    .commit()
        }

        val bundle = Bundle()
        bundle.putString("uid", post.userUid)
        fragmentProfile.arguments = bundle
    }

    override fun onItemClicked(position: Int, post: Post) {
        userRef = databaseReference.child("User").child(post.userUid!!).child("avatar")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val avatarLink: String = p0.value.toString()
                Log.d("justtest", avatarLink)
                val fragmentDetailPost = FragmentDetailPost()
                val fragmentManager = activity!!.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.contentsM, fragmentDetailPost)
                        .addToBackStack(null)
                        .commit()
                val bundle = Bundle()
                bundle.putString("idPost", post.idPost)
                bundle.putString("linkImage", post.image)
                bundle.putString("title", post.title)
                bundle.putString("timeStamp", post.timeStamp)
                bundle.putString("content", post.content)
                bundle.putString("nameUser", post.userUid)
                bundle.putString("avatar", avatarLink)
                fragmentDetailPost.arguments = bundle


            }

        })


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_feeds, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserUid: String = firebaseAuth.currentUser!!.uid
        swipeRefresh.setOnRefreshListener(this)

        lists = ArrayList()
        val linearLayoutManager = LinearLayoutManager(context)
        rcvNewFeeds.layoutManager = linearLayoutManager
        val divider = DividerItemDecoration(rcvNewFeeds.context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.cutm_dvdr)!!)
        rcvNewFeeds.addItemDecoration(divider)
        rcvNewFeeds.setHasFixedSize(true)

        takePostFromDatabase()


    }

    private fun takePostFromDatabase() {

        databaseReference.child("post").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (pgbNewFeeds != null) {
                        pgbNewFeeds.visibility = View.GONE
                    }

                    lists.clear()
                    for (snapshot in dataSnapshot.children) {
                        val newPost = snapshot.getValue<Post>(Post::class.java)
                        lists.add(newPost!!)
                        if (activity != null) {
                            newFeedsApdater = NewFeedsApdater(activity!!, lists)
                            newFeedsApdater!!.setOnItemClickListener(this@FragmentNewFeeds)
                            newFeedsApdater!!.setOnItemClickImageViewListener(this@FragmentNewFeeds)
                            newFeedsApdater!!.setOnItemClickAvtListener(this@FragmentNewFeeds)
                        }

                        if (rcvNewFeeds != null) {
                            rcvNewFeeds.adapter = newFeedsApdater
                        }


                    }
                    lists.reverse()
                    newFeedsApdater!!.notifyDataSetChanged()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


}

