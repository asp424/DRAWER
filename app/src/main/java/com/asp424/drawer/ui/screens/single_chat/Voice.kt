package com.asp424.drawer.ui.screens.single_chat

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.asp424.drawer.R
import com.asp424.drawer.database.uploadFileToStorage
import com.asp424.drawer.databinding.FragmentSingleChatBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.asp424.drawer.utilites.AppTextWatcher
import com.asp424.drawer.utilites.AppVoiceRecorder
import com.asp424.drawer.utilites.TYPE_MESSAGE_VOICE
import com.asp424.drawer.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Voice(
    mSingleChatFragment: FragmentSingleChatBinding?,
    viewModel: MainViewModel,
    contact: CommonModel
) {
    private val mBinding = mSingleChatFragment
    private val mViewModel = viewModel
    private val mContact = contact

    companion object {
        val mAppVoiceRecorder = AppVoiceRecorder()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun voiceJob() = CoroutineScope(Dispatchers.Unconfined).launch {
        mBinding?.apply {
            val mTextListener = AppTextWatcher {
                val string = chatInputMessage.text.toString()
                if (string.isEmpty() || string == "     Идёт запись голоса...") {
                    chatBtnSendMessage.visibility = View.GONE
                    chatBtnAttach.visibility = View.VISIBLE
                    chatBtnVoice.visibility = View.VISIBLE
                } else {
                    chatBtnSendMessageEng.visibility = View.VISIBLE
                    chatBtnSendMessage.visibility = View.VISIBLE
                    chatBtnAttach.visibility = View.GONE
                    chatBtnVoice.visibility = View.GONE
                }
            }
            chatInputMessage.addTextChangedListener(mTextListener)
            CoroutineScope(Dispatchers.IO).launch {
                chatBtnVoice.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        charSwipeRefresh.visibility = View.INVISIBLE
                        chatInputMessage.setText("     Идёт запись голоса...")
                        chatBtnVoice.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.md_red_400
                            )
                        )
                        val messageKey = mViewModel.getMessageKey(mContact.id)
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        charSwipeRefresh.visibility = View.VISIBLE
                        chatInputMessage.setText("")
                        chatBtnVoice.colorFilter = null
                        mAppVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                mContact.id,
                                TYPE_MESSAGE_VOICE
                            )
                        }
                    }
                    true
                }
            }
        }
    }

}