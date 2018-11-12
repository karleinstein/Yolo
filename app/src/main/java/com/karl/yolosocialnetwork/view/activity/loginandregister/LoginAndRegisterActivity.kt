package com.karl.yolosocialnetwork.view.activity.loginandregister

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.karl.socialnetwork.R
import com.google.firebase.auth.FirebaseAuth
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class LoginAndRegisterActivity : AppCompatActivity() {

    private lateinit var loginFragment: LoginFragment
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_register)
        loginFragment = LoginFragment()
        newFragment(loginFragment)

    }


    fun newFragment(f: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.layoutFragment, f)
                .show(f)
                .addToBackStack(null)
                .commit()
    }


}
