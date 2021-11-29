package com.asp424.drawer.ui.screens.single_chat
import android.graphics.Color
import android.transition.TransitionManager
import android.view.View
import com.asp424.drawer.databinding.FragmentSingleChatBinding
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun animButtonToBottomInvisible(mBinding: FragmentSingleChatBinding) =
    CoroutineScope(Dispatchers.Main).launch {
        mBinding.apply {
            val transform = MaterialContainerTransform().apply {
                startView = addContactsBtnToBottom
                endView = addContactsBtnToBottomNull
                addTarget(addContactsBtnToBottomNull)
                scrimColor = Color.TRANSPARENT
                pathMotion = MaterialArcMotion()
                duration = 500L
            }
            TransitionManager.beginDelayedTransition(mBinding.root, transform)
            addContactsBtnToBottom.visibility = View.INVISIBLE
            addContactsBtnToBottomNull.visibility = View.VISIBLE
        }

    }

fun animButtonToBottomVisible(
    mBinding: FragmentSingleChatBinding
) =
    CoroutineScope(Dispatchers.Main).launch {
    mBinding.apply {
        val transform = MaterialContainerTransform().apply {
            startView = addContactsBtnToBottomNull
            endView = addContactsBtnToBottom
            addTarget(addContactsBtnToBottom)
            scrimColor = Color.TRANSPARENT
            pathMotion = MaterialArcMotion()
            duration = 500L
        }

        TransitionManager.beginDelayedTransition(mBinding.root, transform)
        addContactsBtnToBottom.visibility = View.VISIBLE
        addContactsBtnToBottomNull.visibility = View.INVISIBLE
    }
}

