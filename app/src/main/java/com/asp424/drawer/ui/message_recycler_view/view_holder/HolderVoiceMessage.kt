package com.asp424.drawer.ui.message_recycler_view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.R
import com.asp424.drawer.database.CURRENT_UID
import com.asp424.drawer.databinding.MessageItemVoiceBinding
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HolderVoiceMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val binding = MessageItemVoiceBinding.bind(view)
    private val mAppVoicePlayer = AppVoicePlayer()
    private val blocReceivedVoiceMessage: ConstraintLayout = binding.blockRecievedVoice
    private val blocUserVoiceMessage: ConstraintLayout = binding.blockUserVoice
    private val chatUserVoiceMessageTime: TextView = binding.chatUserVoiceMessageTime
    private val chatReceivedVoiceMessageTime: TextView = binding.chatRecievedVoiceMessageTime


    private val chatReceivedBtnPlay: ImageView = binding.chatReceivedBtnPlay
    private val chatReceivedBtnStop: ImageView = binding.chatReceivedBtnStop

    private val chatUserBtnPlay: ImageView = binding.chatUserBtnPlay
    private val chatUserBtnStop: ImageView = binding.chatUserBtnStop
    private val chatRead: TextView = binding.chatUserRead


    override fun drawMessage(
        view: MessageView,
        mViewModel: MainViewModel
    ) {
        if (view.from == CURRENT_UID) {
            mViewModel.getReaded(view.who, view.id).observe(APP_ACTIVITY, {
                if (it == 0) {
                    chatRead.visibility = View.VISIBLE
                } else {
                    chatRead.visibility = View.INVISIBLE
                }
            })
            blocReceivedVoiceMessage.visibility = View.INVISIBLE
            blocUserVoiceMessage.visibility = View.VISIBLE
chatReceivedVoiceMessageTime.visibility = View.INVISIBLE
            chatUserVoiceMessageTime.visibility = View.VISIBLE
            if (view.timeStamp != "")
                chatUserVoiceMessageTime.text = view.timeStamp.asTime()
        } else {
            chatUserVoiceMessageTime.visibility = View.INVISIBLE
            chatReceivedVoiceMessageTime.visibility = View.VISIBLE
            chatRead.visibility = View.INVISIBLE
            if (view.counter != 0) {
                mViewModel.setValueReaded(view.from, view.id, 0)
            }
            blocReceivedVoiceMessage.visibility = View.VISIBLE
            blocUserVoiceMessage.visibility = View.INVISIBLE
            if (view.timeStamp != "")
                chatReceivedVoiceMessageTime.text =
                    view.timeStamp.asTime()
        }

    }

    override fun onAttach(view: MessageView, mViewModel: MainViewModel) {
    mAppVoicePlayer.init()
    if (view.from == CURRENT_UID) {
        chatUserBtnPlay.setOnClickListener {
            chatUserBtnPlay.visibility = View.INVISIBLE
            chatUserBtnStop.visibility = View.VISIBLE
            chatUserBtnStop.setOnClickListener {
                stop {
                    chatUserBtnStop.setOnClickListener(null)
                    chatUserBtnPlay.visibility = View.VISIBLE
                    chatUserBtnStop.visibility = View.INVISIBLE
                }
            }
            play(view) {
                chatUserBtnPlay.visibility = View.VISIBLE
                chatUserBtnStop.visibility = View.INVISIBLE
            }
        }
    } else {
        chatReceivedBtnPlay.setOnClickListener {
            chatReceivedBtnPlay.visibility = View.INVISIBLE
            chatReceivedBtnStop.visibility = View.VISIBLE
            chatReceivedBtnStop.setOnClickListener {
                stop {
                    chatReceivedBtnStop.setOnClickListener(null)
                    chatReceivedBtnPlay.visibility = View.VISIBLE
                    chatReceivedBtnStop.visibility = View.INVISIBLE
                }
            }
            play(view) {
                chatReceivedBtnPlay.visibility = View.VISIBLE
                chatReceivedBtnStop.visibility = View.INVISIBLE
            }
        }
    }
    }


    private fun play(
        view: MessageView,
        function: () -> Unit
    ) {
        mAppVoicePlayer.play(view.id, view.fileUrl) {
            function()
        }
    }

    fun stop(function: () -> Unit) {
        mAppVoicePlayer.stop {
            function()
        }
    }
    override fun onDetach() {
        chatReceivedBtnPlay.setOnClickListener(null)
        chatUserBtnPlay.setOnClickListener(null)
        mAppVoicePlayer.release()

    }
}