package com.karl.yolosocialnetwork.view.activity.profile

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karl.socialnetwork.R
import kotlinx.android.synthetic.main.layout_bottom_sheet_profile.*


class BottomSheetProfile : BottomSheetDialogFragment() {

    private lateinit var onClickOpenItemListener: OnClickOpenItemListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnOpenCamera.setOnClickListener {
            onClickOpenItemListener.onClickOpenCameraListener()
            dismiss()
        }
        btnOpenImage.setOnClickListener {
            onClickOpenItemListener.onClickOpenImageListener()
            dismiss()
        }
    }

    fun setOnClickOpenLixtener(onClickOpenItemListener: OnClickOpenItemListener) {
        this.onClickOpenItemListener = onClickOpenItemListener

    }

    interface OnClickOpenItemListener {
        fun onClickOpenCameraListener()
        fun onClickOpenImageListener()
    }

}