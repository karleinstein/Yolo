package com.karl.yolosocialnetwork.view.activity.createnewposts

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.karl.socialnetwork.R
import kotlinx.android.synthetic.main.dialog_create_new_post.*

class BottomSheetDialogCreatePost : BottomSheetDialogFragment(), View.OnClickListener {
    private var signal: Int = 0
    private lateinit var intent: Intent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_create_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnStatus.setOnClickListener(this)
        btnImage.setOnClickListener(this)
        btnVideo.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        intent = Intent(context, ActivityNewPost::class.java)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnStatus -> {
                signal = 1
                intent.putExtra("signal", signal)
                startActivity(intent)
                dismiss()
            }
            R.id.btnImage -> {
                signal = 2
                intent.putExtra("signal", signal)
                startActivity(intent)
                dismiss()
            }
            R.id.btnVideo -> {
                signal = 3
                intent.putExtra("signal", signal)
                startActivity(intent)
                dismiss()
            }
            R.id.btnCancel -> dismiss()
            else -> {
            }
        }
    }
}
