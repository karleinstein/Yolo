package com.karl.yolosocialnetwork.view.activity.createnewposts

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.Post
import com.google.firebase.database.*
import com.google.firebase.storage.StorageMetadata
import kotlinx.android.synthetic.main.activity_post_new.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import kotlin.math.sign

class ActivityNewPost : AppCompatActivity(), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseStorage: FirebaseStorage
    private var pictureImagePath: String? = null
    private var videoImagePath: String? = null
    private lateinit var currentDateTime: String
    private lateinit var idPost: String
    private lateinit var pathImage: Uri
    private var isCameraOpen = false
    private var isLibraryOpen = false
    private var isTextPost = false
    private var isOpenVideo = false
    private var isOpenLibraryVideo = false
    private lateinit var pathVideo: Uri
    private lateinit var builder: AlertDialog.Builder
    private lateinit var inflter: LayoutInflater
    private lateinit var dialog: View
    private var alertDialog: AlertDialog? = null
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_new)
        initComponents()
        btnLibrary.setOnClickListener(this)
        btnCamera.setOnClickListener(this)
        vvShowVideo.setOnClickListener(this)
        txtPost.setOnClickListener(this)
        ivLeft.setOnClickListener(this)
        builder = AlertDialog.Builder(this)
        inflter = this.layoutInflater
        dialog = inflter.inflate(R.layout.dialog_submitting_post, null)
        builder.setView(dialog)
        alertDialog = builder.create()
        val tf = Typeface.createFromAsset(assets, "fonts/Roboto-Medium.ttf")
        txtPost.typeface = tf
        titleToolbar.typeface = tf
        setSupportActionBar(findViewById(R.id.my_toolbar))
        checkSignal()

    }


    private fun initComponents() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference


    }


    private fun processingPost() {
        val title = tvTitle.text.toString()
        val userId = firebaseAuth.currentUser!!.uid
        idPost = databaseReference.child("post").push().key!!
        if (title.isNotEmpty()) {
            if (tvContentStatus.visibility == View.VISIBLE) {
                isTextPost = true
                val content = tvContentStatus.text.toString()
                if (content.isNotEmpty()) {
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val calendar = Calendar.getInstance()
                            val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm")
                            currentDateTime = currentDate.format(calendar.time)


                            val newPost = Post(idPost, title, content, userId, null, currentDateTime, null)
                            databaseReference.child("post").child(idPost).setValue(newPost).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    alertDialog!!.dismiss()
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
                }

            } else {
                if (isCameraOpen) {
                    processingCamera(pictureImagePath, null, userId, title)
                }
                if (isLibraryOpen) {
                    processingLibrary(pictureImagePath, videoImagePath, userId, title)
                }
                if (isOpenVideo) {
                    processingCamera(null, videoImagePath, userId, title)
                }
                if (isOpenLibraryVideo) {
                    processingLibrary(null, videoImagePath, userId, title)
                }
            }
        }


    }

    private fun processingLibrary(pictureImagePath: String?, videoImagePath: String?, userId: String, title: String) {
        //upload from choose image from gallery
        if (pictureImagePath == null && isOpenLibraryVideo) {
            pathImage = pathVideo
        }
        val riverRef = storageReference.child("posts/" + idPost + "/" + userId + "/"
                + pathImage.lastPathSegment)
        var uploadTask = riverRef.putFile(pathImage)
        if (pictureImagePath == null) {
            val metadata = StorageMetadata.Builder()
                    .setContentType("audio/mp4")
                    .build()
            uploadTask = riverRef.putFile(pathImage, metadata)

        }
        uploadTask.addOnFailureListener { Toast.makeText(applicationContext, "Ban da upload file that bai", Toast.LENGTH_SHORT).show() }
        uploadTask.addOnSuccessListener {
            Log.d("fuck", "Ban da upload thanh cong")
            storageReference.child(riverRef.path).downloadUrl.addOnSuccessListener { uri ->
                Log.d("fuck", uri.toString())
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val calendar = Calendar.getInstance()
                        val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm")
                        currentDateTime = currentDate.format(calendar.time)
                        var newPost = Post(idPost, title, userId, uri.toString(), currentDateTime)
                        if (pictureImagePath == null && isOpenLibraryVideo) {
                            newPost = Post(idPost, title, null, userId, null, currentDateTime, uri.toString())
                        }

                        databaseReference.child("post").child(idPost).setValue(newPost)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
            }.addOnFailureListener { Log.d("fuck", "Ban da get download link that bai") }
            Toast.makeText(applicationContext, "Ban da upload file thanh cong", Toast.LENGTH_SHORT).show()
            alertDialog!!.dismiss()
            this.finish()
            Log.d("test", "upload from galley")
        }
    }

    private fun processingCamera(pictureImagePath: String?, videoImagePath: String?, userId: String, title: String) {
        var file = File("test")
        if (pictureImagePath != null) {
            file = File(pictureImagePath)
        }
        if (videoImagePath != null) {
            file = File(videoImagePath)
        }

        val fileImage = Uri.fromFile(file)
        if (file.exists()) {
            //upload from take photo from camera
            //idPost = UUID.randomUUID().toString().replace("-", "");
            val riverRef = storageReference.child("posts/" + idPost
                    + "/" + userId + "/" + fileImage.lastPathSegment)
            val uploadTask = riverRef.putFile(fileImage)

            uploadTask.addOnFailureListener { Toast.makeText(applicationContext, "Ban da upload file that bai", Toast.LENGTH_SHORT).show() }
            uploadTask.addOnSuccessListener {
                Log.d("fuck", "Ban da upload thanh cong")

                storageReference.child(riverRef.path).downloadUrl.addOnSuccessListener { uri ->
                    Log.d("fuck", uri.toString())
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val calendar = Calendar.getInstance()
                            val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm")
                            currentDateTime = currentDate.format(calendar.time)
                            var newPost = Post(idPost, title, userId, uri.toString(), currentDateTime)
                            if (videoImagePath != null) {
                                newPost = Post(idPost, title, null, userId, null, currentDateTime, uri.toString())
                            }
                            databaseReference.child("post").child(idPost).setValue(newPost)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
                }.addOnFailureListener { Log.d("fuck", "Ban da get download link that bai") }
                Toast.makeText(applicationContext, "Ban da upload file thanh cong", Toast.LENGTH_SHORT).show()
                alertDialog!!.dismiss()
                finish()
            }
            uploadTask.addOnFailureListener { Log.d("fuck", "Ban da upload that bai") }
        }
    }

    private fun checkSignal() {
        val signal = intent.getIntExtra("signal", 0)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        when (signal) {
            1 -> {
                layoutImage.visibility = View.GONE
                titleToolbar.text = "Status post"
            }
            2 -> {
                tvContentStatus.visibility = View.GONE
                titleToolbar.text = "Image post"
            }
            3 -> {
                tvContentStatus.visibility = View.GONE
                titleToolbar.text = "Video post"
            }
            else -> {
            }
        }
    }

    override fun onClick(view: View) {
        val signal = intent.getIntExtra("signal", 0)

        when (view.id) {
            R.id.btnCamera -> {
                if (signal == 2) {
                    openCamera()
                    isCameraOpen = true
                } else if (signal == 3) {
                    openVideo()
                    isOpenVideo = true
                }


            }
            R.id.btnLibrary -> {
                if (signal == 2) {
                    val intent1 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent1.type = "image/*"
                    startActivityForResult(intent1, REQUEST_IMAGE_GALLERY)
                    isLibraryOpen = true
                } else if (signal == 3) {
                    openLibraryVideo()
                    isOpenLibraryVideo = true
                }

            }
            R.id.txtPost -> {
                alertDialog!!.setCancelable(false)
                alertDialog!!.setCanceledOnTouchOutside(false)
                alertDialog!!.show()

                processingPost()

            }
            R.id.ivLeft -> {
                onBackPressed()
            }
//            R.id.vvShowVideo -> {
//                vvShowVideo.start()
//            }
        }

    }

    private fun openLibraryVideo() {

        val intent1 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent1.type = "video/*"
        startActivityForResult(intent1, REQUEST_VIDEO_GALLERY)
    }

    private fun openVideo() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val videoFileName = "$timeStamp.mp4"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        try {
            val video = File(storageDir, videoFileName)
            video.mkdir()
            videoImagePath = video.absolutePath
            Log.d("justlol", videoImagePath)
            val uri = Uri.fromFile(video)
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            if (takeVideoIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun openCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_" + ".jpg"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        try {
            val image = File(storageDir, imageFileName)
            image.createNewFile()
            pictureImagePath = image.absolutePath

            val uri = Uri.fromFile(image)
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            if (takePicture.resolveActivity(packageManager) != null) {
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("justlol", requestCode.toString())
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imgFile = File(pictureImagePath)
            if (imgFile.exists()) {
                layoutImage.visibility = View.GONE
                imgShowImage.visibility = View.VISIBLE
                val matrix = Matrix()
                matrix.postRotate(90f)
                //Bundle bundle = data.getExtras();
                val image = BitmapFactory.decodeFile(pictureImagePath)
                val rotateImage = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
                imgShowImage.setImageBitmap(rotateImage)
            }

        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
//            val videoFile = File(videoImagePath)
//            if (videoFile.exists()) {
            val uri = data.data
            layoutImage.visibility = View.GONE
            layoutVideo.visibility = View.VISIBLE
            vvShowVideo.setVideoURI(Uri.parse(videoImagePath))
            vvShowVideo.setMediaController(uMediaController)
            vvShowVideo.start()
            //}

        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            pathImage = data.data!!
            layoutImage.visibility = View.GONE
            imgShowImage.visibility = View.VISIBLE
            imgShowImage.setImageURI(pathImage)

        }
        if (requestCode == REQUEST_VIDEO_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            pathVideo = data.data!!
            layoutImage.visibility = View.GONE
            layoutVideo.visibility = View.VISIBLE
            vvShowVideo.setVideoURI(pathVideo)
            vvShowVideo.setMediaController(uMediaController)
            vvShowVideo.start()
        }

    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_GALLERY = 2
        private const val REQUEST_VIDEO_CAPTURE = 8080
        private const val REQUEST_VIDEO_GALLERY = 4
        private const val TAG = "ActivityNewPost"
    }
}
