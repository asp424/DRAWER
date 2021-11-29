package com.asp424.drawer.ui.message_recycler_view.views

interface MessageView {
    var id: String
    val from: String
    val timeStamp: String
    val type: String
    val fileUrl: String
    val fullname: String
    val pathStorage: String
    var text: String
    val counter: Int
    val who: String

    companion object {
        val MESSAGE_IMAGE: Int
            get() = 0
        val MESSAGE_TEXT: Int
            get() = 1
        val MESSAGE_VOICE: Int
            get() = 2
        val MESSAGE_FILE: Int
            get() = 3
    }

    fun getTypeView(): Int
}