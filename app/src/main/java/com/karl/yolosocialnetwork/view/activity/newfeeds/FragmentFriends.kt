package com.karl.yolosocialnetwork.view.activity.newfeeds

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.karl.yolosocialnetwork.view.adapter.FriendsAdapter
import com.karl.yolosocialnetwork.view.activity.newfeeds.FragmentMessage
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_friends.*


class FragmentFriends : Fragment(), FriendsAdapter.OnItemClickMessageListenner {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var currentUserUid: String
    private lateinit var listFollow: MutableList<String>
    private lateinit var listUserFollow: MutableList<User>
    private lateinit var listUserOffline: MutableList<User>
    private lateinit var listUserOnline: MutableList<User>
    private lateinit var followRef: DatabaseReference
    private var friendsAdapter: FriendsAdapter? = null
    private var fragmentMessage: FragmentMessage? = null
    override fun onItemClickedMessage(friend: User, position: Int) {
        fragmentMessage = FragmentMessage()
        activity!!.supportFragmentManager.beginTransaction().add(R.id.contentsM, fragmentMessage!!)
                .addToBackStack(null)
                .commit()
        val bundle = Bundle()
        bundle.putString("user_want_to_chat", friend.uid)
        bundle.putString("name_user_rcv", friend.nameUser)
        fragmentMessage!!.arguments = bundle
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        listFollow = ArrayList()
        listUserFollow = ArrayList()
        listUserOnline = ArrayList()
        listUserOffline = ArrayList()
//        if (activity!!.btmNavigation.visibility == View.VISIBLE) {
//            activity!!.btmNavigation.visibility = View.GONE
//        }
        databaseReference = firebaseDatabase.reference
        userRef = databaseReference.child("User")
        currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        followRef = databaseReference.child("Follow").child(currentUserUid)
        followRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if (pgbLoadFrd != null) {
                        pgbLoadFrd.visibility = View.GONE
                    }
                    if (tvOnline != null) {
                        tvOnline.visibility = View.VISIBLE
                    }
                    if (tvOffline != null) {
                        tvOffline.visibility = View.VISIBLE
                    }


                    for (data in p0.children) {
                        val userFollowing: String = data.key.toString()
                        listFollow.add(userFollowing)
                    }
                    for (i in listFollow.indices) {
                        userRef.child(listFollow[i]).addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {
                                    listUserFollow.clear()
                                    val userTemp: User = p0.getValue(User::class.java)!!
                                    listUserFollow.add(userTemp)
                                    for (j in listUserFollow.indices) {
                                        if (listUserFollow[j].online == 0.toLong()) {

                                            listUserOnline.add(listUserFollow[j])
                                            friendsAdapter = FriendsAdapter(context!!, listUserOnline)
                                            if (rcvFriendsOnline != null) {
                                                rcvFriendsOnline.layoutManager = LinearLayoutManager(context)
                                                rcvFriendsOnline.adapter = friendsAdapter
                                                //friendsAdapter!!.notifyDataSetChanged()
                                            }
                                        } else {
                                            if (!listUserOffline.contains(listUserFollow[j])) {
                                                listUserOffline.add(listUserFollow[j])
                                            }
                                            if (context != null) {
                                                friendsAdapter = FriendsAdapter(context!!, listUserOffline)
                                                if (rcvFriendsOffline != null) {
                                                    rcvFriendsOffline.layoutManager = LinearLayoutManager(context)
                                                    rcvFriendsOffline.adapter = friendsAdapter
                                                    //friendsAdapter!!.notifyDataSetChanged()
                                                }
                                            }


                                        }

                                    }
                                    friendsAdapter!!.setOnItemClickMessageListenner(this@FragmentFriends)
                                }

                            }

                        })
                    }
                }


            }

        })

    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//            if (activity != null) {
//
//            }
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        if (activity!!.btmNavigation.visibility == View.GONE) {
//            activity!!.btmNavigation.visibility = View.VISIBLE
//        }

    }

}
