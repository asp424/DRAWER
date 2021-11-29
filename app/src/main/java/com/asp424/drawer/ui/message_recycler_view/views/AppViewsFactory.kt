package com.asp424.drawer.ui.message_recycler_view.views

import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.TYPE_MESSAGE_FILE
import com.asp424.drawer.utilites.TYPE_MESSAGE_IMAGE
import com.asp424.drawer.utilites.TYPE_MESSAGE_VOICE

class AppViewsFactory {
    companion object {
        fun getView(message: CommonModel): MessageView {
            return when (message.type) {
                TYPE_MESSAGE_IMAGE -> ViewImageMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.type,
                    message.fileUrl,
                    message.fullname,
                    message.pathStorage,
                    message.text,
                    message.counter,
                    message.who

                )
                TYPE_MESSAGE_VOICE -> ViewVoiceMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.type,
                    message.fileUrl,
                    message.fullname,
                    message.pathStorage,
                    message.text,
                    message.counter,
                    message.who


                )
                TYPE_MESSAGE_FILE -> ViewFileMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.type,
                    message.fileUrl,
                    message.fullname,
                    message.pathStorage,
                    message.text,
                    message.counter,
                    message.who
                )
                else -> ViewTextMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.type,
                    message.fileUrl,
                    message.fullname,
                    message.pathStorage,
                    message.text,
                    message.counter,
                    message.who

                )
            }
        }
    }
}