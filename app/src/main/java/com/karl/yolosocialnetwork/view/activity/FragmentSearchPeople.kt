package com.karl.yolosocialnetwork.view.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.User
import com.karl.yolosocialnetwork.view.adapter.SearchBottomAdapter
import com.karl.yolosocialnetwork.view.adapter.SearchingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_new_feeds.*
import kotlinx.android.synthetic.main.fragment_search_people.*
import java.text.Normalizer
import java.util.regex.Pattern


class FragmentSearchPeople : Fragment(), View.OnClickListener
        , SearchBottomAdapter.OnItemClickListenner {
    override fun onItemClicked(user: User, position: Int) {
        val fragmentProfile = FragmentProfile()
        activity!!.supportFragmentManager.beginTransaction()
                .add(R.id.contentsM, fragmentProfile)
                .addToBackStack(null)
                .commit()

        val bundle = Bundle()
        bundle.putString("uid", user.uid)
        fragmentProfile.arguments = bundle

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.imgBackSearch -> {
                activity!!.supportFragmentManager.popBackStack()
            }
        }
    }

    private var searchingAdapter: SearchingAdapter? = null
    private var listUsers: MutableList<User>? = null
    private var listUsers2: MutableList<User>? = null
    private var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var searchBottomAdapter: SearchBottomAdapter
    private lateinit var followRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    private var userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("User")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listUsers = ArrayList()
        listUsers2 = ArrayList()
        firebaseDatabase = FirebaseDatabase.getInstance()
        followRef = firebaseDatabase.reference.child("User")
        imgBackSearch.setOnClickListener(this)
        processingUser()

    }


    private fun processingUser() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if (pgbSearching != null) {
                        pgbSearching.visibility = View.GONE
                    }

                    for (data in p0.children) {
                        if (firebaseAuth.currentUser != null) {
                            val currentUserUid = firebaseAuth.currentUser!!.uid
                            val user: User = data.getValue(User::class.java)!!
                            if (user.uid != currentUserUid) {
                                listUsers!!.add(user)
                            }
                        }


                    }
                    if (context != null) {
                        searchBottomAdapter = SearchBottomAdapter(context!!, listUsers!!)
                        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        if (rcvSuggestSearch != null) {
                            rcvSuggestSearch.layoutManager = layoutManager
                            rcvSuggestSearch.adapter = searchBottomAdapter
                            searchBottomAdapter.setOnItemClickListenner(this@FragmentSearchPeople)
                            val divider = DividerItemDecoration(rcvSuggestSearch.context, DividerItemDecoration.HORIZONTAL)
                            divider.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.vertical_divider)!!)
                            rcvSuggestSearch.addItemDecoration(divider)
                            rcvSuggestSearch.setHasFixedSize(true)
                        }

                    }


                    if (edtSearching != null) {
                        edtSearching.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(p0: Editable?) {

                            }

                            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                val text: String = edtSearching.text.toString()
                                listUsers2!!.clear()

                                if (text.isNotEmpty()) {
                                    for (i in listUsers!!.indices) {
                                        val temp = Normalizer.normalize(listUsers!![i].nameUser, Normalizer.Form.NFD)
                                        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                                        val userName: String = pattern.matcher(temp).replaceAll("")
                                        if (userName.toLowerCase().contains(text.toLowerCase())
                                                && !listUsers2!!.contains(listUsers!![i])) {
                                            //Toast.makeText(context, userName, Toast.LENGTH_SHORT).show()
                                            listUsers2!!.add(listUsers!![i])
                                        }
                                    }
                                    searchingAdapter = SearchingAdapter(context!!, listUsers2!!)
                                    rcvSearching.layoutManager = LinearLayoutManager(context)
                                    rcvSearching.adapter = searchingAdapter
                                    searchingAdapter!!.notifyDataSetChanged()
                                } else {
                                    listUsers2!!.clear()
                                    searchingAdapter!!.notifyDataSetChanged()
                                }
                            }

                        })
                    }


                }

            }

        })

    }
}