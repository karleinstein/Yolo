package com.karl.yolosocialnetwork.view.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentActivity
import android.view.*
import com.bumptech.glide.Glide
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import com.karl.yolosocialnetwork.view.activity.loginandregister.LoginAndRegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_sign_out.*

class DialogSetting(context: Context, theme: Int, val activity: FragmentActivity?) : BottomSheetDialog(context, theme), View.OnClickListener {
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userRef = FirebaseDatabase.getInstance().reference.child("User")

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.imvLogout -> {

                //isSignedOut = true
                this.dismiss()
                if (activity != null) {
                    val dialog = DialogConfirmLogout(context, R.style.YourTheme, activity)
                    dialog.show()
                }

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_sign_out)
        setCanceledOnTouchOutside(true)
        imvLogout.setOnClickListener(this)
        processingData()
    }

    private fun processingData() {
        val currentUserUid: String = firebaseAuth.currentUser!!.uid
        userRef.child(currentUserUid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val userSignOut: User = p0.getValue(User::class.java)!!
                val avatarUser: String = userSignOut.avatar!!
                if (avatarUser == "none") {
                    circleAvtD.setImageResource(R.drawable.user_default)
                } else {
                    if (activity != null) {
                        Glide.with(context.applicationContext)
                                .load(avatarUser)
                                .into(circleAvtD)
                    }

                }
                txtNameUserSO.text = userSignOut.nameUser

            }

        })
    }


}