package com.karl.yolosocialnetwork.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class MyService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val temp: String = user.uid
            FirebaseDatabase.getInstance().reference.child("User").child(temp).child("online")
                    .setValue(ServerValue.TIMESTAMP)
        }
        stopSelf()
    }
}