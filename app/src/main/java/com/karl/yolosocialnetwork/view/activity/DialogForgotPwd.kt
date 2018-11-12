package com.karl.yolosocialnetwork.view.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.Toast
import com.karl.socialnetwork.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_forgot_password.*

class DialogForgotPwd(context: Context) : Dialog(context), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btnCancelD -> {
                this.dismiss()
            }
            R.id.btnEmailMelD -> {
                val email = edtEmail.text.toString()
                val username = edtUserName.text.toString()
                if (email.isNotEmpty() && username.isNotEmpty()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Email Sent.Please check your email !!!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Your account is not exists. Please check again !!!", Toast.LENGTH_SHORT).show()
                                }
                            }
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_forgot_password)
        btnEmailMelD.setOnClickListener(this)
        btnCancelD.setOnClickListener(this)

    }

    override fun onStart() {
        window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
        super.onStart()
    }


}