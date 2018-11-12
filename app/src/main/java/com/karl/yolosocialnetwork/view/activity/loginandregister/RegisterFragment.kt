package com.karl.yolosocialnetwork.view.activity.loginandregister

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.karl.yolosocialnetwork.model.User
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.service.MyService
import com.karl.yolosocialnetwork.view.activity.FragmentProfile
import com.karl.yolosocialnetwork.view.activity.MainActivity
import com.karl.yolosocialnetwork.view.activity.newfeeds.FragmentSuggestFriends
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(), View.OnClickListener, FirebaseAuth.AuthStateListener {

    private lateinit var rootView: View
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var dataReference: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private var isRegisted = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_register, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.addAuthStateListener(this)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseDatabase = FirebaseDatabase.getInstance()
        dataReference = firebaseDatabase.reference
        userRef = dataReference.child("User")


        val handler = Handler()
        if (!this.isRemoving) {
            activity!!.runOnUiThread(object : Runnable {
                override fun run() {
                    if (edtEmailR != null) {
                        val email = edtEmailR.text.toString()
                        val pass = edtPassR.text.toString()
                        val name = edtNameR.text.toString()
                        var gender = ""
                        gender = if (rbnMale.isSelected) {
                            "male"
                        } else {
                            "female"
                        }
                        if (email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty() && gender.isNotEmpty()) {
                            btnRegisterR.setBackgroundResource(R.drawable.btn_background_clicked)
                            btnRegisterR.setTextColor(Color.parseColor("#FFFFFF"))
                            btnRegisterR.isEnabled = true
                        } else {
                            btnRegisterR.isEnabled = false

                        }
                    }

                    handler.postDelayed(this, 1000)
                }

            })
        }


//        (activity as AppCompatActivity).setSupportActionBar(toobarR)
//        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
//        toobarR.navigationIcon = (activity as AppCompatActivity).resources.getDrawable(R.drawable.ic_arro_24dp, (activity as AppCompatActivity).theme)
//        toobarR.setNavigationOnClickListener { v: View? ->
//            (activity as LoginAndRegisterActivity).backTask()
//        }

        init()
    }

    private fun init() {
        tvLoginR.setOnClickListener(this)
        btnRegisterR.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnRegisterR -> {
                if (btnRegisterR != null) {
                    btnRegisterR.visibility = View.VISIBLE
                }

                val email = edtEmailR.text.toString()
                val pass = edtPassR.text.toString()
                val name = edtNameR.text.toString()
                var gender = ""
                val idChecked = rgGioiTinh.checkedRadioButtonId
                when (idChecked) {
                    R.id.rbnFemale -> {
                        gender = "female"
                    }
                    R.id.rbnMale -> {
                        gender = "male"
                    }
                }

                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid: String = FirebaseAuth.getInstance().currentUser!!.uid
                        val user = User(name, gender, uid, "none", "none")
                        dataReference.child("User").child(firebaseAuth.uid!!).setValue(user)
                        isRegisted = true
                        Toast.makeText(activity, "You registed successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Please check again your email", Toast.LENGTH_LONG).show()
                    }
                }


            }
            R.id.tvLoginR -> {
                activity!!.onBackPressed()
            }

        }

    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("isRegister", true)
            startActivity(intent)
            val currentUserUid = firebaseAuth.currentUser!!.uid
            //activity!!.startService(Intent(context, MyService::class.java))
            userRef.child(currentUserUid).child("online").setValue(0.0)
            activity!!.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firebaseAuth.removeAuthStateListener(this)
    }
}