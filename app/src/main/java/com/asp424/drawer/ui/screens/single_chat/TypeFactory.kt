package com.asp424.drawer.ui.screens.single_chat

import android.view.View
import com.asp424.drawer.database.CURRENT_UID
import com.asp424.drawer.databinding.MessageItemFileBinding
import com.asp424.drawer.databinding.MessageItemImageBinding
import com.asp424.drawer.databinding.MessageItemTextBinding
import com.asp424.drawer.databinding.MessageItemVoiceBinding
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.utilites.TYPE_MESSAGE_FILE
import com.asp424.drawer.utilites.TYPE_MESSAGE_IMAGE
import com.asp424.drawer.utilites.TYPE_MESSAGE_TEXT
import com.asp424.drawer.utilites.TYPE_MESSAGE_VOICE

class TypeFactory {
    fun getView(itemView: View, item: MessageView): View {
        when (item.type) {
            TYPE_MESSAGE_FILE -> {
                val binding = MessageItemFileBinding.bind(itemView)
                return if (item.from == CURRENT_UID) binding.blockUserFile
                else binding.blockRecievedFile
            }
            TYPE_MESSAGE_IMAGE -> {
                val binding = MessageItemImageBinding.bind(itemView)
                return if (item.from == CURRENT_UID) binding.chatUserImage
                else binding.photo
            }
            TYPE_MESSAGE_TEXT -> {
                val binding = MessageItemTextBinding.bind(itemView)
                return if (item.text.startsWith("https")){
                    if (item.from == CURRENT_UID) binding.chatUserMessageLink
                    else binding.chatReceivedMessageLink
                } else{
                    if (item.from == CURRENT_UID) binding.chatUserMessageText
                    else binding.chatReceivedMessageText
                }
            }
            else -> {
                //TYPE_VOICE
               val binding = MessageItemVoiceBinding.bind(itemView)
               return if (item.from == CURRENT_UID) binding.blockUserVoice
               else binding.blockRecievedVoice
           }
       }
    }
}