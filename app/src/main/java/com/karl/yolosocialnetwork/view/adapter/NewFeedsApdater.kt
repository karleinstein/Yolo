package com.karl.yolosocialnetwork.view.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.Post
import com.google.firebase.database.*
import com.universalvideoview.UniversalMediaController
import com.universalvideoview.UniversalVideoView
import kotlinx.android.synthetic.main.activity_post_new.*
import kotlinx.android.synthetic.main.activity_post_new.view.*
import kotlinx.android.synthetic.main.item_new_feeds_image_post.view.*
import kotlinx.android.synthetic.main.item_new_feeds_status_post.view.*
import kotlinx.android.synthetic.main.item_new_feeds_video_post.view.*
import java.io.File
import kotlin.collections.ArrayList


class NewFeedsApdater(private val context: Context, private val listPosts: List<Post>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storageReference: StorageReference
    private val firebaseStorage: FirebaseStorage
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemClickAvtListener: OnItemClickAvtListener? = null
    private var onItemClickImageViewListener: OnItemClickImageViewListener? = null
    private var isLiked = false
    private var listLoves1: MutableList<String>
    private var listLoves2: MutableList<String>
    private var listTemps: MutableList<String>
    private var likeRef: DatabaseReference
    private var postRef: DatabaseReference
    private var countLoves: Long = 0
    private var countCommnts: Long = 0
    private var currentUserId: String
    private lateinit var storageRef: StorageReference

    private companion object {
        const val TAG = "NewFeedsAdapter"
    }

    init {
        databaseReference = firebaseDatabase.reference
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        likeRef = firebaseDatabase.reference.child("Likes")
        postRef = firebaseDatabase.reference.child("post")
        currentUserId = firebaseAuth.currentUser!!.uid
        listLoves1 = ArrayList()
        listLoves2 = ArrayList()
        listTemps = ArrayList()
        setHasStableIds(true)

    }

    override fun getItemCount(): Int {
        return listPosts.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = listPosts[holder.adapterPosition]
        val currentUserId = firebaseAuth.currentUser!!.uid
        when (holder.itemViewType) {
            1 -> {
                val holderStatus = holder as ViewHolderStatusPost
                postRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children) {
                            postRef.child(post.idPost).addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    countCommnts = p0.child("comments").childrenCount
                                    holderStatus.tvComment.text = countCommnts.toString()
                                }

                            })
                        }
                    }

                })
                likeRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        countLoves = p0.child(post.idPost).childrenCount
                        holderStatus.tvLove.text = countLoves.toString()
                    }

                })

                likeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        isLiked = if (dataSnapshot.child(post.idPost).hasChild(currentUserId)) {
                            holderStatus.tvLove.setTextColor(Color.RED)
                            holderStatus.imvLove.setImageResource(R.drawable.liked)
                            false
                        } else {

                            holderStatus.tvLove.setTextColor(Color.BLACK)
                            holderStatus.imvLove.setImageResource(R.drawable.like)
                            false
                        }
                    }

                })

                databaseReference.child("User").child(post.userUid!!).child("nameUser").addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        val userName = dataSnapshot.value.toString()
                        holderStatus.tvTime.text = post.timeStamp
                        holderStatus.tvTitle.text = post.title
                        holderStatus.tvName.text = userName
                        holderStatus.tvContent.text = post.content

                        databaseReference.child("User").child(post.userUid!!).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val backgroundImage: String = p0.child("avatar").value.toString()
                                if (context.applicationContext != null && backgroundImage != "none") {
                                    Glide.with(context.applicationContext).load(backgroundImage).into(holderStatus.imvAvatar)
                                }


                            }

                        })

                    }


                    override fun onCancelled(@NonNull databaseError: DatabaseError) {

                    }
                })

                holderStatus.itemView.setOnClickListener {
                    //onItemClickListener!!.onItemClicked(position)

                }
                holderStatus.layoutComment.setOnClickListener {
                    onItemClickListener!!.onItemClicked(position, post)
                }
                holderStatus.tvOptions.visibility = View.GONE
                if (post.userUid == currentUserId) {
                    holderStatus.tvOptions.visibility = View.VISIBLE
                }
                holderStatus.tvOptions.setOnClickListener {
                    val popupMenu = PopupMenu(context, holderStatus.tvOptions)
                    popupMenu.inflate(R.menu.menu_modify)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.btnDelete -> {
                                postRef.child(post.idPost).removeValue()
                                notifyDataSetChanged()
                            }
                            R.id.btnUpdate -> {
                                val builder = AlertDialog.Builder(context)
                                val editTitle = EditText(context)
                                builder.setTitle("Update Post:")
                                builder.setView(editTitle)
                                editTitle.setText(post.title)
                                builder.setPositiveButton("Save") { dialogInterface, _ ->
                                    postRef.child(post.idPost).child("title").setValue(editTitle.text.toString())
                                    Toast.makeText(context.applicationContext, "Updated post successfully", Toast.LENGTH_SHORT).show()
                                    dialogInterface.dismiss()
                                }
                                builder.setNegativeButton("Cancel") { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                }
                                val dialog = builder.create()

                                dialog.show()
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
                holderStatus.imvAvatar.setOnClickListener {
                    onItemClickAvtListener!!.onItemClickedAvt(position, post)

                }
                holderStatus.layoutLove.setOnClickListener {
                    isLiked = true
                    likeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            isLiked = if (dataSnapshot.child(post.idPost).hasChild(currentUserId)) {
                                likeRef.child(post.idPost).child(currentUserId).removeValue()
                                holderStatus.tvLove.setTextColor(Color.BLACK)
                                holderStatus.imvLove.setImageResource(R.drawable.like)
                                false
                            } else {
                                likeRef.child(post.idPost).child(currentUserId).setValue(true)
                                holderStatus.tvLove.setTextColor(Color.RED)
                                holderStatus.imvLove.setImageResource(R.drawable.liked)
                                false
                            }
                        }

                    })


                    //notifyItemChanged(holderStatus.adapterPosition)

                }
            }
            2 -> {

                val holderImagePost = holder as ViewHolderImagePost
                postRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        //val idPost = p0.value.toString()
                        postRef.child(post.idPost).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                countCommnts = p0.child("comments").childrenCount
                                holderImagePost.tvComment.text = countCommnts.toString()
                            }

                        })
                    }

                })
                likeRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        countLoves = p0.child(post.idPost).childrenCount
                        holderImagePost.tvLove.text = countLoves.toString()
                    }

                })
                likeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        isLiked = if (dataSnapshot.child(post.idPost).hasChild(currentUserId)) {
                            holderImagePost.tvLove.setTextColor(Color.RED)
                            holderImagePost.imvLove.setImageResource(R.drawable.liked)
                            false
                        } else {
                            holderImagePost.tvLove.setTextColor(Color.BLACK)
                            holderImagePost.imvLove.setImageResource(R.drawable.like)
                            false

                        }
                    }

                })

                databaseReference.child("User").child(post.userUid!!).child("nameUser").addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        val userName = dataSnapshot.value.toString()
                        holderImagePost.tvTime.text = post.timeStamp
                        holderImagePost.tvTitle.text = post.title
                        holderImagePost.tvName.text = userName
                        //Glide
                        //Glide.with(context).clear(holderImagePost.imvImagePost)
                        holderImagePost.imvImagePost.layout(0, 0, 0, 0)
                        databaseReference.child("User").child(post.userUid!!).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val backgroundImage: String = p0.child("avatar").value.toString()
                                if (context.applicationContext != null && backgroundImage != "none") {
                                    Glide.with(context.applicationContext).load(backgroundImage).into(holderImagePost.imvAvatar)
                                }


                            }

                        })
                        Glide.with(context.applicationContext)
                                .load(post.image)
                                .into(holderImagePost.imvImagePost)

                        //Picasso
//                        Picasso.get()
//                                .load(post.image)
//                                .fit()
//                                .rotate(90F)
//                                .into(holderImagePost.imvImagePost)
//                        val uriImage = Uri.parse(post.image)
//                        val request = ImageRequest.fromUri(uriImage)
//                        val draweeController = Fresco.newDraweeControllerBuilder()
//                                .setImageRequest(request)
//                                .setOldController(holderImagePost.imvImagePost.controller).build()
//
//                        holderImagePost.imvImagePost.controller = draweeController
//                        val imageLoader = ImageLoader.getInstance()
//                        imageLoader.displayImage(post.image, holderImagePost.imvImagePost)


                    }


                    override fun onCancelled(@NonNull databaseError: DatabaseError) {

                    }
                })
                holderImagePost.itemView.setOnClickListener {


                }
                holderImagePost.imvImagePost.setOnClickListener {
                    onItemClickImageViewListener!!.onItemImageViewClicked(position, post)

                }
                holderImagePost.layoutComment.setOnClickListener {
                    onItemClickListener!!.onItemClicked(position, post)
                }
                holderImagePost.tvOptions.visibility = View.GONE
                if (post.userUid == currentUserId) {
                    holderImagePost.tvOptions.visibility = View.VISIBLE
                }


                holderImagePost.tvOptions.setOnClickListener {
                    val popupMenu = PopupMenu(context, holderImagePost.tvOptions)
                    popupMenu.inflate(R.menu.menu_modify)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.btnDelete -> {
                                postRef.child(post.idPost).removeValue()
                                storageRef = firebaseStorage.getReferenceFromUrl(post.image!!)
                                storageRef.delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "You deleted image successfully", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                notifyDataSetChanged()

                            }
                            R.id.btnUpdate -> {
                                val builder = AlertDialog.Builder(context)
                                val editTitle = EditText(context)
                                builder.setTitle("Update Post:")
                                builder.setView(editTitle)
                                editTitle.setText(post.title)
                                builder.setPositiveButton("Save") { dialogInterface, _ ->
                                    postRef.child(post.idPost).child("title").setValue(editTitle.text.toString())
                                    Toast.makeText(context.applicationContext, "Updated post successfully", Toast.LENGTH_SHORT).show()
                                    dialogInterface.dismiss()
                                }
                                builder.setNegativeButton("Cancel") { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                }
                                val dialog = builder.create()
                                dialog.show()
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
                holderImagePost.imvAvatar.setOnClickListener {
                    onItemClickAvtListener!!.onItemClickedAvt(position, post)
                }
                holderImagePost.layoutLove.setOnClickListener {
                    isLiked = true
                    likeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            isLiked = if (dataSnapshot.child(post.idPost).hasChild(currentUserId)) {
                                likeRef.child(post.idPost).child(currentUserId).removeValue()
                                holderImagePost.tvLove.setTextColor(Color.BLACK)
                                holderImagePost.imvLove.setImageResource(R.drawable.like)
                                false
                            } else {
                                likeRef.child(post.idPost).child(currentUserId).setValue(true)
                                holderImagePost.tvLove.setTextColor(Color.RED)
                                holderImagePost.imvLove.setImageResource(R.drawable.liked)
                                false

                            }
                        }

                    })


                    //notifyItemChanged(holderStatus.adapterPosition)
                }
            }
            else -> {
                val holderVideoPost = holder as ViewHolderVideoPost
                postRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        //val idPost = p0.value.toString()
                        postRef.child(post.idPost).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                countCommnts = p0.child("comments").childrenCount
                                holderVideoPost.tvComment.text = countCommnts.toString()
                            }

                        })
                    }

                })
                likeRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        countLoves = p0.child(post.idPost).childrenCount
                        holderVideoPost.tvLove.text = countLoves.toString()
                    }

                })
                likeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        isLiked = if (dataSnapshot.child(post.idPost).hasChild(currentUserId)) {
                            holderVideoPost.tvLove.setTextColor(Color.RED)
                            holderVideoPost.imvLove.setImageResource(R.drawable.liked)
                            false
                        } else {
                            holderVideoPost.tvLove.setTextColor(Color.BLACK)
                            holderVideoPost.imvLove.setImageResource(R.drawable.like)
                            false

                        }
                    }

                })

                databaseReference.child("User").child(post.userUid!!).child("nameUser").addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        val userName = dataSnapshot.value.toString()
                        holderVideoPost.tvTime.text = post.timeStamp
                        holderVideoPost.tvTitle.text = post.title
                        holderVideoPost.tvName.text = userName
                        databaseReference.child("User").child(post.userUid!!).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val backgroundImage: String = p0.child("avatar").value.toString()
                                if (context.applicationContext != null && backgroundImage != "none") {
                                    Glide.with(context.applicationContext).load(backgroundImage).into(holderVideoPost.imvAvatar)
                                }


                            }

                        })

                        holderVideoPost.videoView.setVideoURI(Uri.parse(post.video))
                        holderVideoPost.videoView.setMediaController(holderVideoPost.videoMediaController)
                        holderVideoPost.videoView.setOnPreparedListener {
                            holderVideoPost.videoView.seekTo(100)
                        }
                        holderVideoPost.videoView.start()
                    }


                    override fun onCancelled(@NonNull databaseError: DatabaseError) {

                    }
                })
                holderVideoPost.itemView.setOnClickListener {


                }
                holderVideoPost.layoutComment.setOnClickListener {
                    onItemClickListener!!.onItemClicked(position, post)
                }
                holderVideoPost.tvOptions.visibility = View.GONE
                if (post.userUid == currentUserId) {
                    holderVideoPost.tvOptions.visibility = View.VISIBLE
                }


                holderVideoPost.tvOptions.setOnClickListener {
                    val popupMenu = PopupMenu(context, holderVideoPost.tvOptions)
                    popupMenu.inflate(R.menu.menu_modify)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.btnDelete -> {
                                postRef.child(post.idPost).removeValue()
                                storageRef = firebaseStorage.getReferenceFromUrl(post.video!!)
                                storageRef.delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "You deleted image successfully", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                notifyDataSetChanged()

                            }
                            R.id.btnUpdate -> {
                                val builder = AlertDialog.Builder(context)
                                val editTitle = EditText(context)
                                builder.setTitle("Update Post:")
                                builder.setView(editTitle)
                                editTitle.setText(post.title)
                                builder.setPositiveButton("Save") { dialogInterface, _ ->
                                    postRef.child(post.idPost).child("title").setValue(editTitle.text.toString())
                                    Toast.makeText(context.applicationContext, "Updated post successfully", Toast.LENGTH_SHORT).show()
                                    dialogInterface.dismiss()
                                }
                                builder.setNegativeButton("Cancel") { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                }
                                val dialog = builder.create()
                                dialog.show()
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
                holderVideoPost.imvAvatar.setOnClickListener {
                    onItemClickAvtListener!!.onItemClickedAvt(position, post)
                }
                holderVideoPost.layoutLove.setOnClickListener {
                    isLiked = true
                    likeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            isLiked = if (dataSnapshot.child(post.idPost).hasChild(currentUserId)) {
                                likeRef.child(post.idPost).child(currentUserId).removeValue()
                                holderVideoPost.tvLove.setTextColor(Color.BLACK)
                                holderVideoPost.imvLove.setImageResource(R.drawable.like)
                                false
                            } else {
                                likeRef.child(post.idPost).child(currentUserId).setValue(true)
                                holderVideoPost.tvLove.setTextColor(Color.RED)
                                holderVideoPost.imvLove.setImageResource(R.drawable.liked)
                                false

                            }
                        }

                    })


                    //notifyItemChanged(holderStatus.adapterPosition)
                }
            }
        }

    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = inflater.inflate(R.layout.item_new_feeds_image_post, parent, false)
        when (viewType) {
            1 -> {
                view = inflater.inflate(R.layout.item_new_feeds_status_post, parent, false)
                return ViewHolderStatusPost(view)
            }
            2 -> {
                view = inflater.inflate(R.layout.item_new_feeds_image_post, parent, false)
                return ViewHolderImagePost(view)

            }
            3 -> {
                view = inflater.inflate(R.layout.item_new_feeds_video_post, parent, false)
                return ViewHolderVideoPost(view)
            }
            else -> {

            }
        }
        return ViewHolderStatusPost(view)

    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemClickImageViewListener(onItemClickImageViewListener: OnItemClickImageViewListener) {
        this.onItemClickImageViewListener = onItemClickImageViewListener
    }


    interface OnItemClickListener {
        fun onItemClicked(position: Int, post: Post)

    }

    fun setOnItemClickAvtListener(onItemClickAvtListener: OnItemClickAvtListener) {
        this.onItemClickAvtListener = onItemClickAvtListener
    }

    interface OnItemClickAvtListener {
        fun onItemClickedAvt(position: Int, post: Post)
    }

    interface OnItemClickImageViewListener {
        fun onItemImageViewClicked(position: Int, post: Post)
    }

    override fun getItemViewType(position: Int): Int {
        if (listPosts[position].content != null) {
            return 1
        } else if (listPosts[position].image != null) {
            return 2
        } else if (listPosts[position].video != null) {
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
        var imvAvatar: ImageView = itemView.imvAvatar3
        var videoMediaController: UniversalMediaController = itemView.uMediaController2
    }


}
