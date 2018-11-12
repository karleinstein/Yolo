package com.karl.yolosocialnetwork.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.view.activity.loginandregister.LoginAndRegisterActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_splash)
        val thread = Thread {
            kotlin.run {
                var time = 0
                while (time < 2000) {
                    Thread.sleep(1000)
                    time += 1000
                }
                val intent = Intent(this, LoginAndRegisterActivity::class.java)
                startActivity(intent)
                this.finish()
            }
        }
        thread.start()

    }

}