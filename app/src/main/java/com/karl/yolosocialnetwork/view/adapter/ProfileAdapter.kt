package com.karl.yolosocialnetwork.view.adapter

import android.content.Context
import android.net.Uri
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.universalvideoview.UniversalMediaController
import com.universalvideoview.UniversalVideoView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_new_feeds_image_post.view.*
import kotlinx.android.synthetic.main.item_new_feeds_status_post.view.*
import kotlinx.android.synthetic.main.item_new_feeds_video_post.view.*

class ProfileAdapter(val context: Context, private val listPostsByUser: MutableList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = listPostsByUser[position]
        when (holder.itemViewType) {
            1 -> {
                val holderStatusPost = holder as ViewHolderStatusPost

                FirebaseDatabase.getInstance().reference.child("User")
                        .child(post.userUid!!).child("nameUser").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val realName: String = p0.value.toString()
                                holderStatusPost.tvContent.text = post.content
                                holderStatusPost.tvTitle.text = post.title
                                holderStatusPost.tvName.text = realName
                                holderStatusPost.tvTime.text = post.timeStamp
                                FirebaseDatabase.getInstance().reference.child("User").child(post.userUid!!)
                                        .child("avatar").addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot) {
                                                val avatar: String = p0.value.toString()
                                                if (avatar != "none") {
                                                    Glide.with(context).load(avatar).into(holderStatusPost.imvAvatar)
                                                }

                                            }

                                        })
                            }

                        })


            }
            2 -> {
                val holderImagePost = holder as ViewHolderImagePost
                FirebaseDatabase.getInstance().reference.child("User")
                        .child(post.userUid!!).child("nameUser").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val realName: String = p0.value.toString()
                                holderImagePost.itemView.layout(0, 0, 0, 0)
                                holderImagePost.tvTitle.text = post.title
                                holderImagePost.tvName.text = realName
                                holderImagePost.tvTime.text = post.timeStamp
                                Glide.with(context).load(post.image).into(holderImagePost.imvImagePost)
                                FirebaseDatabase.getInstance().reference.child("User").child(post.userUid!!)
                                        .child("avatar").addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot) {
                                                val avatar: String = p0.value.toString()
                                                if (avatar != "none") {
                                                    Glide.with(context).load(avatar).into(holderImagePost.imvAvatar)
                                                }

                                            }

                                        })

                            }

                        })


            }
            3 -> {
                val holderVideoPost = holder as ViewHolderVideoPost
                FirebaseDatabase.getInstance().reference.child("User")
                        .child(post.userUid!!).child("nameUser").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val realName: String = p0.value.toString()
                                holderVideoPost.itemView.layout(0, 0, 0, 0)
                                holderVideoPost.tvTitle.text = post.title
                                holderVideoPost.tvName.text = realName
                                holderVideoPost.tvTime.text = post.timeStamp
                                holderVideoPost.videoView.setVideoURI(Uri.parse(post.video))
                                holderVideoPost.videoView.setMediaController(holderVideoPost.videoMediaController)
                                holderVideoPost.videoView.start()
                                FirebaseDatabase.getInstance().reference.child("User").child(post.userUid!!)
                                        .child("avatar").addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot) {
                                                val avatar: String = p0.value.toString()
                                                if (avatar != "none") {
                                                    Glide.with(context).load(avatar).into(holderVideoPost.imvAvatar)
                                                }

                                            }

                                        })

                            }

                        })


            }
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        var view = inflater.inflate(R.layout.item_new_feeds_status_post, p0, false)
        when (p1) {
            1 -> {
                view = inflater.inflate(R.layout.item_new_feeds_status_post, p0, false)
                return ViewHolderStatusPost(view)
            }
            2 -> {
                view = inflater.inflate(R.layout.item_new_feeds_image_post, p0, false)
                return ViewHolderImagePost(view)
            }
            3 -> {
                view = inflater.inflate(R.layout.item_new_feeds_video_post, p0, false)
                return ViewHolderVideoPost(view)
            }
            else -> {

            }
        }
        return ViewHolderStatusPost(view)
    }

    override fun getItemCount(): Int {
        return listPostsByUser.size
    }

    override fun getItemViewType(position: Int): Int {
        if (listPostsByUser[position].content != null) {
            return 1
        } else if (listPostsByUser[position].image != null) {
            return 2
        } else if (listPostsByUser[position].video != null) {
            return 3
        } else {
            return 4
        }
    }

    inner class ViewHolderStatusPost internal constructor(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.tvTitleStatus
        var tvTime: TextView = itemView.tvTime
        var tvName: TextView = itemView.tvNameUser
        var tvLove: TextView = itemView.tvLove
        var tvComment: TextView = itemView.tvComment
        var tvContent: TextView = itemView.tvContent
        var layoutLove: LinearLayout = itemView.layoutLove
        var imvLove: ImageView = itemView.imvLove
        var cvReact: LinearLayout = itemView.cvReact
        var layoutComment: LinearLayout = itemView.layoutComment
        var tvOptions: TextView = itemView.tvOptions
        var imvAvatar: ImageView = itemView.imvAvatar


    }

    inner class ViewHolderImagePost internal constructor(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.tvTitle2
        var tvTime: TextView = itemView.tvTime2
        var tvName: TextView = itemView.tvNameUser2
        var tvLove: TextView = itemView.tvLove2
        var tvComment: TextView = itemView.tvComment2
        var imvImagePost: ImageView = itemView.imvImagePost2
        var layoutLove: LinearLayout = itemView.layoutLove2
        var imvLove: ImageView = itemView.imvLove2
        var cvReact: LinearLayout = itemView.cvReact2
        var layoutComment: LinearLayout = itemView.layoutComment2
        var tvOptions: TextView = itemView.tvOptions2
        var imvAvatar: ImageView = itemView.imvAvatar2
    }

    inner class ViewHolderVideoPost internal constructor(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.tvTitle3
        var tvTime: TextView = itemView.tvTime3
        var tvName: TextView = itemView.tvNameUser3
        var tvLove: TextView = itemView.tvLove3
        var tvComment: TextView = itemView.tvComment3
        var layoutVideo2: FrameLayout = itemView.layoutVideo2
        var videoView: UniversalVideoView = itemView.vvShowVideo2
        var layoutLove: LinearLayout = itemView.layoutLove3
        var imvLove: ImageView = itemView.imvLove3
        var cvReact: LinearLayout = itemView.cvReact3
        var layoutComment: LinearLayout = itemView.layoutComment3
        var tvOptions: TextView = itemView.tvOptions3
        var imvAvatar: CircleImageView = itemView.imvAvatar3
        var videoMediaController: UniversalMediaController = itemView.uMediaController2
    }
}