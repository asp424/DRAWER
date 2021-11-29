package com.asp424.drawer.ui.screens.single_chat

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.Navigation
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.databinding.ToolbarSingleChatInfoBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.asDate
import com.asp424.drawer.utilites.asTime
import com.asp424.drawer.utilites.downloadAndSetImage
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InitToolbar(
    mSingleChatViewModel: MainViewModel,
    mainActivity: MainActivity,
    viewLifecycleOwner: LifecycleOwner,
    mContact: CommonModel
) : LifecycleObserver {
    private val mContext = mainActivity
    private val mViewLifecycleOwner = viewLifecycleOwner
    private val mViewModel = mSingleChatViewModel
    private var mToolbarBinding: ToolbarSingleChatInfoBinding? = null
    private var mToolbar: MaterialToolbar? = null

    init {
        CoroutineScope(Dispatchers.Main).launch {
            mToolbar = ActivityCompat.requireViewById(
                mContext,
                R.id.topAppBar
            )
            mToolbarBinding = ToolbarSingleChatInfoBinding.bind(
                LayoutInflater.from(mContext)
                    .inflate(
                        R.layout.toolbar_single_chat_info,
                        mToolbar
                    )
            )
            mViewModel.toolbarListenerSCF(mContact.id)
            mToolbarBinding?.apply {
                mViewModel.recivingUserToolbar.observe(
                    mViewLifecycleOwner,
                    { mReceivingUser ->
                        toolbarChatImage.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("ass", mReceivingUser.photoUrl)
                            Navigation.findNavController(
                                mContext,
                                viewId = R.id.nav_host_fragment
                            ).navigate(
                                R.id.action_nav_single_to_nav_full_photo,
                                bundle
                            )
                        }
                        if (mReceivingUser.id != "1") {
                            if (mReceivingUser.fullname.isEmpty()) {
                                toolbarChatFullname.text = mContact.fullname
                            } else {
                                toolbarChatFullname.text = mReceivingUser.fullname
                            }
                            toolbarChatImage.downloadAndSetImage(mReceivingUser.photoUrl)
                            if (mReceivingUser.timeExitStamp != "" && mReceivingUser.timeExitStamp != "в сети") {
                                val her =
                                    mReceivingUser.timeExitStamp.toString().asTime()
                                val hui =
                                    mReceivingUser.timeExitStamp.toString().asDate()
                                val i = "был(а) $hui в $her"
                                toolbarChatStatus.text = i
                            }
                            if (mReceivingUser.timeExitStamp == "в сети") {
                                toolbarChatStatus.text = "В сети"
                            }

                        }
                    })
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun clear() {
        mToolbarBinding = null
    }
}
