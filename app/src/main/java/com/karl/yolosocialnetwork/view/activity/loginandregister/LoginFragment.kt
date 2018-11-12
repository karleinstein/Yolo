package com.karl.yolosocialnetwork.view.activity.loginandregister

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.view.activity.DialogForgotPwd
import com.karl.yolosocialnetwork.view.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import android.util.DisplayMetrics
import com.karl.yolosocialnetwork.service.MyService
import com.karl.yolosocialnetwork.view.activity.newfeeds.FragmentFriends
import com.karl.yolosocialnetwork.view.activity.newfeeds.FragmentSuggestFriends


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LoginFragment : Fragment(), View.OnClickListener, FirebaseAuth.AuthStateListener {

    private lateinit var rootView: View
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var registerFragment: RegisterFragment
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private var isLogin = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_login, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        userRef = firebaseDatabase.reference.child("User")
        firebaseAuth.addAuthStateListener(this)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerFragment = RegisterFragment()
        val mHandler = Handler()
        if (!this.isRemoving) {
            activity!!.runOnUiThread(object : Runnable {
                override fun run() {
                    if (edtEmailL != null && edtPassL != null) {
                        val email: String = edtEmailL.text.toString()
                        val pass: String = edtPassL.text.toString()
                        if (email.isNotEmpty() && pass.isNotEmpty()) {
                            btnLoginL.setBackgroundResource(R.drawable.btn_background_clicked)
                            btnLoginL.setTextColor(Color.parseColor("#FFFFFF"))
                            btnLoginL.isEnabled = true
                        } else {
                            btnLoginL.isEnabled = false
                        }

                        mHandler.postDelayed(this, 1000)
                    }

                }
            })
        }


        init()

    }

    fun init() {

        btnLoginL.setOnClickListener(this)
        tvSigUp.setOnClickListener(this)
        tvForgot.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnLoginL -> {
                btnLoginL.isEnabled = false
                btnLoginL.setBackgroundResource(R.drawable.btn_background_click)
                if (pgbLogin != null) {
                    pgbLogin.visibility = View.VISIBLE
                }
                loginEmail()
            }
            R.id.tvSigUp -> {
                (activity as LoginAndRegisterActivity).newFragment(registerFragment)

            }
            R.id.tvForgot -> {
                val dialog = DialogForgotPwd(context!!)
                val metrics = resources.displayMetrics
                val width = metrics.widthPixels
                val height = metrics.heightPixels
                dialog.show()
                //dialog.window.setLayout((6 * width) / 7, (5 * height) / 9)


            }


        }

    }

    private fun loginEmail() {
        val email: String = edtEmailL.text.toString()
        val pass: String = edtPassL.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                isLogin = true
                Toast.makeText(activity, "Login successfully ", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(activity, "Please check again your email or password ", Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        val user = firebaseAuth.currentUser

        if (user != null) {
            val temp: String = firebaseAuth.currentUser!!.uid
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("isRegister", false)
            startActivity(intent)

//            val fragmentFriends = FragmentSuggestFriends()
//            activity!!.supportFragmentManager.beginTransaction().add(R.id.layoutFragment, fragmentFriends)
//                    .commit()
            //activity!!.startService(Intent(context, MyService::class.java))
            userRef.child(temp).child("online").setValue(0.0)
            //activity!!.finish()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        firebaseAuth.removeAuthStateListener(this)
    }

}