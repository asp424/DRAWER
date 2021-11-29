package com.asp424.drawer.ui.screens.single_chat

import android.content.Context
import android.widget.Toast
import com.asp424.drawer.MainActivity
import com.asp424.drawer.database.existFile
import com.asp424.drawer.database.microsoftApi
import com.asp424.drawer.database.readTXT
import com.asp424.drawer.databinding.FragmentSingleChatBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendMessage(mViewModel: MainViewModel) {
    private val mSingleChatViewModel = mViewModel
    private val mSendVoice = AppVoicePlayer()
    fun sendJob(
        mContact: CommonModel,
        mSingleChatFragment: FragmentSingleChatBinding?,
        mContext: MainActivity
    ) {
        mSendVoice.init()
        mSingleChatFragment?.apply {
            mSingleChatViewModel.apply {
                chatBtnSendMessageEng.setOnClickListener {
                    val message = chatInputMessage.text.toString()
                    if (message.isEmpty()) {
                        Toast.makeText(mContext, "Введи сообщение", Toast.LENGTH_SHORT).show()
                    } else {
                        chatInputMessage.setText("Идёт перевод...")
                        CoroutineScope(Dispatchers.IO).launch {
                            val mText = microsoftApi(message)
                            if (existFile(mContact.id)) {
                                val k = readTXT(mContact.id).let { it1 ->
                                    mText.cipherEncrypt(it1).toString()
                                }
                                mSendVoice.playSound()
                                sendMessage(
                                    k,
                                    mContact.id,
                                    TYPE_MESSAGE_TEXT
                                ) {
                                    saveToMainList(mContact.id, TYPE_CHAT)
                                    chatInputMessage.setText("")
                                }
                            } else {
                                Toast.makeText(mContext, "Ключ ещё не создан", Toast.LENGTH_SHORT).show()
                                chatInputMessage.setText("")
                            }
                        }
                    }
                }

                chatBtnSendMessage.setOnClickListener {
                    val message = chatInputMessage.text.toString()
                    if (message.isEmpty()) {
                        showToast("Введи сообщение")
                    } else {
                        if (message.startsWith("https://")) {
                            chatInputMessage.setText("")
                            mSendVoice.playSound()
                            sendMessage(
                                message,
                                mContact.id,
                                TYPE_MESSAGE_TEXT
                            ) {
                                saveToMainList(mContact.id, TYPE_CHAT)
                            }

                        } else {
                            if (existFile(mContact.id)) {
                                val k = readTXT(mContact.id).let { it1 ->
                                    message.cipherEncrypt(it1).toString()
                                }
                                chatInputMessage.setText("")
                                mSendVoice.playSound()
                                sendMessage(
                                    k,
                                    mContact.id,
                                    TYPE_MESSAGE_TEXT
                                ) {
                                    saveToMainList(mContact.id, TYPE_CHAT)
                                }
                            } else {
                                Toast.makeText(mContext, "Ключ ещё не создан", Toast.LENGTH_SHORT).show()
                                chatInputMessage.setText("")
                            }
                        }
                    }
                }
            }
        }
    }
}