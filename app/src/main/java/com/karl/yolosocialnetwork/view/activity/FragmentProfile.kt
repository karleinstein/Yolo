package com.karl.yolosocialnetwork.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.karl.yolosocialnetwork.App
import com.karl.yolosocialnetwork.model.User
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import com.karl.yolosocialnetwork.view.activity.profile.BottomSheetProfile
import com.karl.yolosocialnetwork.view.adapter.ProfileAdapter
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FragmentProfile : Fragment(), View.OnClickListener, BottomSheetProfile.OnClickOpenItemListener, ValueEventListener {

    companion object {
        const val TAG = "FragmentProfile"
        const val BACKGROUND = "background"
        const val AVATAR = "avatar"
        const val REQUEST_IMAGE = 100
        const val REQUEST_CAMERA = 200
    }

    private lateinit var firebaseStore: FirebaseStorage
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var addPhotoBottomDialogFragment: BottomSheetProfile
    private lateinit var userRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var followRef: DatabaseReference
    private lateinit var mCurrenrPhotoPath: String
    private lateinit var file: File
    private lateinit var listPosts: MutableList<Post>
    private lateinit var listPostsTemp: MutableList<Post>
    private lateinit var profileAdapter: ProfileAdapter
    private var uidUser: String = ""
    private var handler: Handler = Handler()
    private var check = false
    private var isSignedOut = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        check = true
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbarProfile)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbarProfile.setNavigationIcon(R.drawable.ic_arro_24dp)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        followRef = databaseReference.child("Follow")
        userRef = databaseReference.child("User")
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        listPosts = ArrayList()
        listPostsTemp = ArrayList()
        storageReference = firebaseStore.reference
        storageRef = storageReference.child("ProfilesImage").child(firebaseAuth.uid!!)
        toolbarProfile.setNavigationOnClickListener {
            //activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
            activity!!.supportFragmentManager.popBackStack()
        }

        addPhotoBottomDialogFragment = BottomSheetProfile()
        btnFollow.setOnClickListener(this)
        tvEditProfile.setOnClickListener(this)
        tvSaveProfile.setOnClickListener(this)
        checkInforUser()
        //dang ky
        databaseReference.child("User").addValueEventListener(this)
        imgBackgroundProfile.setOnClickListener(this)
        imgAvatarProfile.setOnClickListener(this)
        addPhotoBottomDialogFragment.setOnClickOpenLixtener(this)
        checkFollow()
    }

    private fun checkFollow() {
        val currentUserUId: String = firebaseAuth.currentUser!!.uid
        followRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (btnFollow != null) {
                    if (btnFollow.visibility == View.VISIBLE) {
                        if (p0.child(currentUserUId).hasChild(uidUser)) {
                            btnFollow.text = "FOLLOWED"
                        } else {
                            btnFollow.text = "FOLLOW"
                        }
                    }
                }


            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun checkInforUser() {

        if (arguments != null) {
            uidUser = arguments!!.getString("uid")!!
            Toast.makeText(context, uidUser, Toast.LENGTH_SHORT).show()
            when (uidUser) {
                firebaseAuth.currentUser!!.uid -> {
                    btnFollow.visibility = View.GONE
                    tvEditProfile.visibility = View.VISIBLE

                }
                else -> {
                    tvEditProfile.visibility = View.GONE
                    btnFollow.visibility = View.VISIBLE

                }
            }
        } else {
            uidUser = firebaseAuth.currentUser!!.uid
        }

        databaseReference.child("User").child(uidUser).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists() && pgbAvatar != null && pgbBackground != null) {
                    pgbBackground.visibility = View.GONE
                    pgbAvatar.visibility = View.GONE
                    val backgroudImage: String = p0.child("background").value.toString()
                    val avatarImage: String = p0.child("avatar").value.toString()
                    if (imgAvatarProfile != null) {
                        if (avatarImage == "none") {
                            imgAvatarProfile.setImageResource(R.drawable.user_default_big)
                        } else {
                            if (activity != null) {
                                Glide.with(activity!!.applicationContext).load(backgroudImage).into(imgBackgroundProfile)
                                Glide.with(activity!!.applicationContext).load(avatarImage).into(imgAvatarProfile)
                            }
                        }
                    }
                }


            }

        })
        databaseReference.child("post").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if (pgbProfile != null) {
                        pgbProfile.visibility = View.GONE
                    }

                    for (data in p0.children) {
                        val post: Post = data.getValue(Post::class.java)!!

                        listPosts.add(post)
                    }

                    for (i in listPosts.indices) {
                        if (listPosts[i].userUid == uidUser) {
                            listPostsTemp.add(listPosts[i])

                        }
                    }
                    if (rcvLoadPostUser != null) {
                        if (listPostsTemp.size == 0) {
                            rcvLoadPostUser.visibility = View.GONE
                        } else {
                            rcvLoadPostUser.visibility = View.VISIBLE
                            tvEmpty.visibility = View.GONE
                        }
                        Log.d("just test", listPostsTemp.size.toString())
                        profileAdapter = ProfileAdapter(context!!, listPostsTemp)
                        rcvLoadPostUser.layoutManager = LinearLayoutManager(context)
                        val divider = DividerItemDecoration(rcvLoadPostUser.context, DividerItemDecoration.VERTICAL)
                        divider.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.cutm_dvdr)!!)
                        rcvLoadPostUser.addItemDecoration(divider)
                        rcvLoadPostUser.adapter = profileAdapter
                        listPostsTemp.reverse()
                        profileAdapter.notifyDataSetChanged()
                    }
                }


            }

        })

    }

    override fun onCancelled(p0: DatabaseError) {

    }

    override fun onDataChange(p0: DataSnapshot) {
        if (firebaseAuth != null) {
            val user: User? = p0.child(uidUser).getValue(User::class.java)
            if (user != null) {
                val name = user.nameUser
                val gender = user.gender
                val description = user.description
                if (description != null && txtDescription != null) {
                    txtDescription.setText(description)
                }

                if (txtName != null) {
                    txtName.setText(name)

                }

                if (gender != null) {

                }
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        val currentUserUId: String = firebaseAuth.currentUser!!.uid

        when (v!!.id) {
            R.id.imgBackgroundProfile -> {
                App.getInstance().image = BACKGROUND
                addPhotoBottomDialogFragment.show(activity!!.supportFragmentManager,
                        "add_photo_dialog_fragment")
            }

            R.id.imgAvatarProfile
            -> {
                App.getInstance().image = AVATAR
                addPhotoBottomDialogFragment.show(activity!!.supportFragmentManager,
                        "add_photo_dialog_fragment")
            }
            R.id.btnFollow -> {
                followRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.child(currentUserUId).hasChild(uidUser)) {
                            followRef.child(currentUserUId).child(uidUser).removeValue()
                            btnFollow.text = "FOLLOW"
                        } else {
                            followRef.child(currentUserUId).child(uidUser).setValue(true)
                            btnFollow.text = "FOLLOWED"
                        }
                    }

                })

            }
            R.id.tvEditProfile -> {
                txtName.requestFocus()
                txtName.isClickable = true
                txtDescription.isClickable = true

                tvEditProfile.visibility = View.GONE
                tvSaveProfile.visibility = View.VISIBLE
            }
            R.id.tvSaveProfile -> {
                userRef.child(uidUser).child("nameUser").setValue(txtName.text.toString())
                userRef.child(uidUser).child("description").setValue(txtDescription.text.toString())
                txtName.isClickable = false
                txtName.isFocusableInTouchMode = false
                txtDescription.isFocusableInTouchMode = false
                txtDescription.isClickable = false
                txtName.clearFocus()
                activity!!.window.decorView.clearFocus()
                tvSaveProfile.visibility = View.GONE
                tvEditProfile.visibility = View.VISIBLE
                Toast.makeText(context, "You updated your information successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClickOpenCameraListener() {

        if (App.getInstance().image === BACKGROUND) {
            openCamera()
        }

        if (App.getInstance().image === AVATAR) {
            openCamera()
        }
    }

    override fun onClickOpenImageListener() {

        if (App.getInstance().image === BACKGROUND) {
            openImage()
        }

        if (App.getInstance().image === AVATAR) {
            openImage()
        }
    }

    private fun openCamera() {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        try {
            val image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            )

            mCurrenrPhotoPath = image.absolutePath
            val uri = Uri.fromFile(image)
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            if (takePicture.resolveActivity(activity!!.packageManager) != null) {
                startActivityForResult(takePicture, REQUEST_CAMERA)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun openImage() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        val view: ImageView = if (App.getInstance().image == BACKGROUND) {
            imgBackgroundProfile

        } else {
            imgAvatarProfile

        }

        if (requestCode === REQUEST_CAMERA) {

            if (resultCode === Activity.RESULT_OK) {
                file = File(mCurrenrPhotoPath)//load
                uploadPhoto(file)
            }
        }
        if (requestCode === REQUEST_IMAGE) {
            if (resultCode === Activity.RESULT_OK && data != null) {
//                Toast.makeText(this@FragmentProfile, "ok...........", Toast.LENGTH_LONG).show()
                val image: Uri = data.data!!
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                Toast.makeText(context, image.toString(), Toast.LENGTH_LONG).show()
                uploadPhotoFromLibrary(image)

            }
        }
    }

    private fun uploadPhotoFromLibrary(image: Uri) {
        var imageView: String = ""
        imageView = if (App.getInstance().image == AVATAR) {
            "avatar"
        } else {
            "background"
        }
        val storageTemp = storageRef.child(image.lastPathSegment)
        val uploadTask: UploadTask = storageTemp.putFile(image)
        when (imageView) {
            "avatar" -> {
                if (pgbAvatar != null && pgbAvatar.visibility == View.GONE) {
                    pgbAvatar.visibility = View.VISIBLE
                }
            }
            "background" -> {
                if (pgbBackground != null && pgbBackground.visibility == View.GONE) {
                    pgbBackground.visibility = View.VISIBLE
                }
            }
        }
        uploadTask.addOnSuccessListener {
            storageTemp.downloadUrl.addOnSuccessListener { uri ->
                databaseReference.child("User").child(firebaseAuth.uid!!).child(imageView).setValue(uri.toString())
                when (imageView) {
                    "avatar" -> {
                        Glide.with(this).load(uri).into(imgAvatarProfile)
                    }
                    "background" -> {
                        Glide.with(this).load(uri).into(imgBackgroundProfile)
                    }
                }

            }


        }
        uploadTask.addOnCompleteListener { task ->
            if (task.isComplete) {
                when (imageView) {
                    "avatar" -> {
                        if (pgbAvatar != null) {
                            pgbAvatar.visibility = View.GONE
                        }

                    }
                    "background" -> {
                        if (pgbBackground != null) {
                            pgbBackground.visibility = View.GONE
                        }
                    }
                }

            }
        }
        uploadTask.addOnFailureListener {
            Toast.makeText(context, "Upload failure", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadPhoto(file: File) {
        val filePhoto = Uri.fromFile(file)
        var imageView: String = ""
        imageView = if (App.getInstance().image == AVATAR) {
            "avatar"
        } else {
            "background"
        }
        if (file.exists()) {
            val storageTemp = storageRef.child(filePhoto.lastPathSegment)
            val uploadFile: UploadTask = storageTemp.putFile(filePhoto)
            when (imageView) {
                "avatar" -> {
                    if (pgbAvatar != null && pgbAvatar.visibility == View.GONE) {
                        pgbAvatar.visibility = View.VISIBLE
                    }
                }
                "background" -> {
                    if (pgbBackground != null && pgbBackground.visibility == View.GONE) {
                        pgbBackground.visibility = View.VISIBLE
                    }
                }
            }
            uploadFile.addOnSuccessListener {

                storageTemp.downloadUrl.addOnSuccessListener { uri ->
                    val user = User()
                    databaseReference.child("User").child(firebaseAuth.currentUser!!.uid).child(imageView).setValue(uri.toString())
                    Toast.makeText(context, "Upload Image Success", Toast.LENGTH_LONG).show()
                    when (imageView) {
                        "avatar" -> {
                            Glide.with(this).load(uri).into(imgAvatarProfile)

                        }
                        "background" -> {
                            Glide.with(this).load(uri).into(imgBackgroundProfile)
                        }
                    }

                }
            }
            uploadFile.addOnCompleteListener { task ->
                if (task.isComplete) {
                    when (imageView) {
                        "avatar" -> {
                            if (pgbAvatar != null) {
                                pgbAvatar.visibility = View.GONE
                            }

                        }
                        "background" -> {
                            if (pgbBackground != null) {
                                pgbBackground.visibility = View.GONE
                            }
                        }
                    }

                }
            }
            uploadFile.addOnFailureListener {
                Toast.makeText(context, "Load Failure", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.set_info, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.menu_set_info -> {
//                val view: View = activity!!.findViewById(R.id.menu_set_info)
//                val popupMenu = PopupMenu(context, view)
//                popupMenu.inflate(R.menu.menu_sign_out)
//                popupMenu.setOnMenuItemClickListener { item2 ->
//                    when (item2.itemId) {
//                        R.id.menu_sign_out -> {
//

//                        }
//
//                    }
//                    true
//                }
//                popupMenu.show()
                val dialog = DialogSetting(context!!, R.style.YourTheme, activity!!)
                dialog.show()


            }

        }
        return false

    }
}
