package com.karl.yolosocialnetwork.view.activity.newfeeds

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import com.karl.yolosocialnetwork.view.activity.MainActivity
import com.karl.yolosocialnetwork.view.adapter.FriendsSuggestAdapter
import com.google.android.gms.common.util.ArrayUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_new_feeds.*
import kotlinx.android.synthetic.main.fragment_suggesstion_friends.*
import kotlinx.android.synthetic.main.item_friends.*

class FragmentSuggestFriends : Fragment(), View.OnClickListener, FriendsSuggestAdapter.OnItemClickListener {
    private lateinit var currentUserUid: String
    private var currentUserLocation: String = "test"
    private lateinit var listTemps: MutableList<User>
    private lateinit var listSuggestFrds: MutableList<User>
    private lateinit var listShowFrd: MutableList<User>
    private lateinit var suggestAdapter: FriendsSuggestAdapter
    private lateinit var suggestFollow: DatabaseReference
    private var fragmentNewFeeds: FragmentNewFeeds? = null
    override fun onItemClicked(user: User, position: Int) {

    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btnSkip -> {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_suggesstion_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listSuggestFrds = ArrayList()
        listTemps = ArrayList()
        listShowFrd = ArrayList()
        if (FirebaseAuth.getInstance().currentUser != null) {
            currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        }

        btnSkip.setOnClickListener(this)
        if (activity!!.btmNavigation != null && activity!!.btmNavigation.visibility == View.VISIBLE) {
            activity!!.btmNavigation.visibility = View.GONE
        }
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("User")
        suggestFollow = FirebaseDatabase.getInstance().reference.child("User").child(currentUserUid)
                .child("suggestFollow")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children) {
                    val user = dataSnapshot.getValue(User::class.java)!!
                    if (!listSuggestFrds.contains(user)) {
                        listSuggestFrds.add(user)
                    }
                    suggestFollow.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()) {
                                for (i in listSuggestFrds.indices) {
                                    if (listSuggestFrds[i].uid != currentUserUid && !listTemps.contains(listSuggestFrds[i])) {
                                        suggestFollow.child(listSuggestFrds[i].uid!!).setValue(listSuggestFrds[i])
                                    }
                                }
                            }
                        }

                    })


                }
//                for (i in listSuggestFrds.indices) {
//                    if (listSuggestFrds[i].uid != currentUserUid && !listTemps.contains(listSuggestFrds[i])
//                            && listSuggestFrds[i].uid != null) {
//                        suggestFollow.child(listSuggestFrds[i].uid!!).setValue(listSuggestFrds[i])
//                    }
//                }

//                for (i in listTemps.indices) {
//
//                }
                //Log.d("justfuck", listTemps.size.toString())


            }

        })
        suggestFollow.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                listShowFrd.clear()
                if (p0.exists()) {
                    for (data in p0.children) {
                        val user: User = data.getValue(User::class.java)!!
                        if (!listShowFrd.contains(user)) {
                            listShowFrd.add(user)
                        }

                    }
                }
                //Log.d("lol", listTemps.size.toString())
                if (context != null) {
                    suggestAdapter = FriendsSuggestAdapter(context!!, listShowFrd)
                    suggestAdapter.setOnItemClickListener(this@FragmentSuggestFriends)
                    rcvSuggestFriends.layoutManager = LinearLayoutManager(context)
                    rcvSuggestFriends.adapter = suggestAdapter
                }


            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity!!.btmNavigation != null) {
            if (activity!!.btmNavigation.visibility == View.GONE) {
                activity!!.btmNavigation.visibility = View.VISIBLE
            }
        }


    }

}