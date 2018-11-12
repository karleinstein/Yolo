package com.karl.yolosocialnetwork.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentActivity
import android.view.View
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import com.karl.yolosocialnetwork.view.activity.loginandregister.LoginAndRegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.confirm_logout_dialog.*

class DialogConfirmLogout(context: Context, theme: Int, val activity: FragmentActivity) : BottomSheetDialog(context, theme), View.OnClickListener {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_logout_dialog)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        userRef = firebaseDatabase.reference.child("User")
        val currentUserUId: String = firebaseAuth.currentUser!!.uid
        btnCancelL.setOnClickListener(this)
        layoutConfirmLogout.setOnClickListener(this)
        setCanceledOnTouchOutside(true)
        userRef.child(currentUserUId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val currentUser: User = p0.getValue(User::class.java)!!
                val nameUser: String = currentUser.nameUser!!
                txtNameUserC.text = nameUser
            }

        })
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btnCancelL -> {
                dismiss()
            }
            R.id.layoutConfirmLogout -> {
                processingLogout()
            }
        }
    }

    private fun processingLogout() {
        val currentUserUid = firebaseAuth.currentUser!!.uid
        userRef.child(currentUserUid)
                .child("online").setValue(ServerValue.TIMESTAMP)
        activity.finish()
        val intent = Intent(context, LoginAndRegisterActivity::class.java)
        context.startActivity(intent)
        firebaseAuth.signOut()
    }
}