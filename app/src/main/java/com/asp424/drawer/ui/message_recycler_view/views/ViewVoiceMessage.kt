package com.asp424.drawer.ui.message_recycler_view.views

data class ViewVoiceMessage(
    override var id: String,
    override val from: String,
    override val timeStamp: String,
    override val type: String,
    override val fileUrl: String = "",
    override val fullname: String,
    override val pathStorage: String,
    override var text: String,
    override var counter: Int,
    override val who: String
) : MessageView {
    override fun getTypeView(): Int {
    return MessageView.MESSAGE_VOICE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}