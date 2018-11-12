package com.karl.yolosocialnetwork.view.activity.newfeeds

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.karl.socialnetwork.R
import com.karl.yolosocialnetwork.model.Message
import com.karl.yolosocialnetwork.view.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_message.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FragmentMessage : Fragment(), View.OnClickListener {
    private lateinit var listMessage: MutableList<Message>
    private var currentUserUid: String? = null
    private var uidUserRcv: String? = null
    private lateinit var messageRef: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter

    companion object {
        const val TAG = "FragmentMessage"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity!!.btmNavigation.visibility == View.VISIBLE) {
            activity!!.btmNavigation.visibility = View.GONE
        }
        listMessage = ArrayList()
        currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        uidUserRcv = arguments!!.getString("user_want_to_chat")!!
        messageRef = FirebaseDatabase.getInstance().reference.child("Messages").child(currentUserUid!!).child(uidUserRcv!!)
        messageRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                listMessage.add(p0.getValue(Message::class.java)!!)
                if (context != null) {
                    messageAdapter = MessageAdapter(context!!, listMessage)
                    val layoutManager = LinearLayoutManager(context)
                    layoutManager.stackFromEnd = true
                    rcvMessage.layoutManager = layoutManager
                    rcvMessage.adapter = messageAdapter
                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
        imvSendMessage.setOnClickListener(this)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.imvSendMessage -> {

                val contentMessage: String = edtMessage.text.toString()
                if (contentMessage.isNotEmpty()) {
                    val calendar = Calendar.getInstance()
                    val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    val dateTime = currentDate.format(calendar.time)
                    //val message = Message(dateTime, currentUserUid!!, contentMessage, "text")
                    val messageSendRef = "Messages/$currentUserUid/$uidUserRcv"
                    val messageRcvRef = "Messages/$uidUserRcv/$currentUserUid"
                    val userMessageKey: DatabaseReference = FirebaseDatabase.getInstance().reference.child(messageSendRef)
                            .child(messageRcvRef).push()
                    val messageId: String = userMessageKey.key!!
                    //FirebaseDatabase.getInstance().reference.child(messageRcvRef).setValue(message)
                    //messageRef.child(uidMessage!!).setValue(message)
                    val messageTextBody = HashMap<String, Any>()
                    messageTextBody["contents"] = contentMessage
                    messageTextBody["from"] = currentUserUid!!
                    val messageBodyDetail = HashMap<String, Any>()
                    messageBodyDetail["$messageSendRef/$messageId"] = messageTextBody
                    messageBodyDetail["$messageRcvRef/$messageId"] = messageTextBody
                    FirebaseDatabase.getInstance().reference.updateChildren(messageBodyDetail)
                    //listMessage.add(message)
//                    messageAdapter = MessageAdapter(context!!, listMessage)
//                    rcvMessage.layoutManager = LinearLayoutManager(context)
//                    rcvMessage.adapter = messageAdapter
                    edtMessage.setText("")
                } else {
                    Toast.makeText(context, "?", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity!!.btmNavigation.visibility == View.GONE) {
            activity!!.btmNavigation.visibility = View.VISIBLE
        }
    }
}