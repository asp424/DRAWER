package com.asp424.drawer.ui.screens.single_chat

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat.setTransitionName
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.asp424.drawer.R
import com.asp424.drawer.databinding.FragmentSingleChatBinding
import com.asp424.drawer.databinding.MessageItemImageBinding
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ClickOnImage {
    fun onClickUser(mBinding: MessageItemImageBinding, view: MessageView) = CoroutineScope(Dispatchers.IO).launch{

        mBinding.chatUserImage.setOnClickListener {
            setTransitionName(
                mBinding.chatUserImage,
                view.id
            )
            val extras = FragmentNavigatorExtras(
                mBinding.chatUserImage to "image"
            )
            val bundle = Bundle()
            bundle.putString("ass", view.pathStorage)
            mBinding.apply {
                Navigation.findNavController(it)
                 .navigate(R.id.action_nav_single_to_nav_full_photo, bundle, null, extras)
            }
        }
    }

    fun onClickReceived(mBinding: MessageItemImageBinding, mFile: File, view: MessageView) = CoroutineScope(Dispatchers.IO).launch {

        mBinding.photo.setOnClickListener {
            setTransitionName(
                mBinding.photo,
                view.id
            )
            val extras = FragmentNavigatorExtras(
                mBinding.photo to "image"
            )
            val bundle = Bundle()
            bundle.putString("ass", mFile.toString())
            Navigation.findNavController(it)
                .navigate(R.id.action_nav_single_to_nav_full_photo, bundle, null, extras)
        }
    }
}