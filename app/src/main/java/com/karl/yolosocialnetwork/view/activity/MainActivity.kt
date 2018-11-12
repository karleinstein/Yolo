package com.karl.yolosocialnetwork.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.bottomnavigation.LabelVisibilityMode
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.karl.yolosocialnetwork.model.Post

import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.service.MyService
import com.karl.yolosocialnetwork.view.activity.createnewposts.BottomSheetDialogCreatePost
import com.karl.yolosocialnetwork.view.activity.newfeeds.FragmentFriends
import com.karl.yolosocialnetwork.view.activity.newfeeds.FragmentNewFeeds
import com.karl.yolosocialnetwork.view.activity.newfeeds.FragmentSuggestFriends
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceOnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.time.LocalDate

import java.util.ArrayList


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.btnSearch -> {
                searchUser()
            }
            R.id.btnHome -> {
                showNewFeeds()
            }
            R.id.btnNewPost -> {
                dialogNewPost()
            }
            R.id.btnFriends -> {
                showFriends()
            }
            R.id.btnWall -> {
                showWall()
            }
        }
        return true
    }


    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var posts: List<Post>
    private var fragmentNewFeeds: FragmentNewFeeds? = null
    private var fragmentSearch: FragmentSearchPeople? = null
    private var fragmentWall: FragmentProfile? = null
    private var fragmentSuggestFriends: FragmentSuggestFriends? = null
    private var fragmentFriends: FragmentFriends? = null

    private fun searchUser() {
        if (fragmentSearch == null) {
            fragmentSearch = FragmentSearchPeople()
        }
        supportFragmentManager.beginTransaction().replace(R.id.contentsM, fragmentSearch!!, "searchs")
                .addToBackStack(null)
                .commit()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseMessaging.getInstance().isAutoInitEnabled
        btmNavigation.setOnNavigationItemSelectedListener(this)
        //btmNavigation.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        userRef = databaseReference.child("User")
        posts = ArrayList()
        val isRegister: Boolean = intent.getBooleanExtra("isRegister", false)
        if (isRegister) {
            val fragmentSuggestFriends = FragmentSuggestFriends()
            supportFragmentManager.beginTransaction().add(R.id.contentsM, fragmentSuggestFriends)
                    .commit()
        } else {
            showNewFeeds()
        }

    }


    private fun showFriends() {
        if (fragmentFriends == null) {
            fragmentFriends = FragmentFriends()
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentsM, fragmentFriends!!, "friends")
                .addToBackStack(null)
                .commit()


    }

    private fun showWall() {
        if (fragmentWall == null) {
            fragmentWall = FragmentProfile()
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentsM, fragmentWall!!, "walls")
                .addToBackStack(null)
                .commit()


    }

    private fun showNewFeeds() {
        if (fragmentNewFeeds == null) {
            fragmentNewFeeds = FragmentNewFeeds()
        }
        if (!fragmentNewFeeds!!.isInLayout) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contentsM, fragmentNewFeeds!!, "newFeeds")
                    .commit()
        }

        //Toast.makeText(this, supportFragmentManager.backStackEntryCount.toString(), Toast.LENGTH_SHORT).show()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFragmentManager.popBackStack()
    }

    private fun dialogNewPost() {
        BottomSheetDialogCreatePost().show(supportFragmentManager, "Dialog")
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onDestroy() {
        super.onDestroy()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val temp: String = user.uid
            FirebaseDatabase.getInstance().reference.child("User").child(temp).child("online")
                    .setValue(ServerValue.TIMESTAMP)
        }

    }

    override fun onPause() {
        super.onPause()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val temp: String = user.uid
            FirebaseDatabase.getInstance().reference.child("User").child(temp).child("online")
                    .setValue(ServerValue.TIMESTAMP)
        }
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val temp: String = user.uid
            FirebaseDatabase.getInstance().reference.child("User").child(temp).child("online")
                    .setValue(0.0)
        }
    }


}
