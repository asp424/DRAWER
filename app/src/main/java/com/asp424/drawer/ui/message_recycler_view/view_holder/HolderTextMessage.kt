package com.asp424.drawer.ui.message_recycler_view.view_holder

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.database.*
import com.asp424.drawer.databinding.MessageItemTextBinding
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import de.hdodenhof.circleimageview.CircleImageView
import io.github.ponnamkarthik.richlinkpreview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HolderTextMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val mBinding = MessageItemTextBinding.bind(view)
    private val blockUserMessage: ConstraintLayout = mBinding.blockUserMessage
    private val chatUserMessageLink: RichLinkView = mBinding.chatUserMessageLink
    private val chatUserMessageText: TextView = mBinding.chatUserMessageText
    private val chatUserMessageTime: TextView = mBinding.chatUserMessageTime
    private val blocReceivedMessage: ConstraintLayout = mBinding.blockReceivedMessage
    private val chatReceivedMessageLink: RichLinkView = mBinding.chatReceivedMessageLink
    private val chatReceivedMessageText: TextView = mBinding.chatReceivedMessageText
    private val chatReceivedMessageTime: TextView = mBinding.chatReceivedMessageTime
    private val chatReceivedUserImage: CircleImageView = mBinding.contactSingleChatReceivedPhoto
    private val chatReceivedDeleted: TextView = mBinding.chatReceivedMessageDeleted
    private val chatRead: TextView = mBinding.chatUserRead
    private var mKey: String? = null

    override fun drawMessage(
        view: MessageView,
        mViewModel: MainViewModel
    ) {
        chatReceivedDeleted.visibility = View.GONE
        if (view.from == CURRENT_UID) {
            current(view, mViewModel)
        } else {
            user(view, mViewModel)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun user(view: MessageView, mViewModel: MainViewModel) =
        CoroutineScope(Dispatchers.Unconfined).launch {
            if (existFile(view.from)) {
                mKey = readTXT(view.from)
            }
            chatRead.visibility = View.INVISIBLE
            if (view.counter != 0) {
                mViewModel.setValueReaded(view.from, view.id, 0)
            }
            blockUserMessage.visibility = View.GONE
            if (view.timeStamp != "")
                chatReceivedMessageTime.text = view.timeStamp.asTime()
            chatUserMessageTime.visibility = View.GONE
            if (view.who.isEmpty()) {
                chatReceivedUserImage.visibility = View.VISIBLE
                REF_DATABASE_ROOT.child(NODE_USERS).child(view.from)
                    .addValueEventListener(AppValueEventListener {
                        val her = it.getCommonModel()

                        if (her.photoUrl.isNotEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                chatReceivedUserImage.downloadAndSetImage(her.photoUrl)
                            }
                        }
                    })

            } else {
                chatReceivedUserImage.visibility = View.GONE
            }

            if (view.text.startsWith("http")) {
                chatReceivedMessageText.visibility = View.GONE
                CoroutineScope(Dispatchers.IO).launch {
                    chatReceivedMessageLink.setLink(view.text, object : ViewListener {
                        override fun onSuccess(status: Boolean) {
                            blocReceivedMessage.visibility = View.VISIBLE
                            chatReceivedMessageTime.visibility = View.VISIBLE
                            chatReceivedMessageText.visibility = View.GONE
                            chatReceivedMessageLink.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception) {
                        }
                    })
                }
            } else {
                blocReceivedMessage.visibility = View.VISIBLE
                chatReceivedMessageTime.visibility = View.VISIBLE
                chatReceivedMessageText.visibility = View.VISIBLE
                chatReceivedMessageLink.visibility = View.GONE
                if (view.text.startsWith("Я создал ключ, давай общаться!") ||
                    view.text.startsWith("Я ввёл ключ, можем общаться!") || view.text == "null" && view.who != ""
                ) {
                    chatReceivedMessageText.text = view.text
                } else {
                    val o = mKey?.let { view.text.cipherDecrypt(it) }
                    if (o == null) {
                        chatReceivedMessageText.text = "*****"
                    } else {
                        chatReceivedMessageText.text = o.toString()
                    }
                }
                if (view.who == "") {
                    chatReceivedMessageText.text = view.text
                }
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    private fun current(view: MessageView, mViewModel: MainViewModel) {
        blockUserMessage.visibility = View.GONE
        chatUserMessageText.visibility = View.GONE
        chatUserMessageLink.visibility = View.GONE
        if (view.who.isNotEmpty()) {
            if (existFile(view.who)) {
                mKey = readTXT(view.who)
            }
        }
        mViewModel.getReaded(view.who, view.id).observe(APP_ACTIVITY, {
            if (it == 0) {
                chatRead.visibility = View.VISIBLE
            } else {
                chatRead.visibility = View.INVISIBLE
            }
        })
        chatReceivedMessageTime.visibility = View.GONE
        blocReceivedMessage.visibility = View.GONE
        if (view.timeStamp != "")
            chatUserMessageTime.text = view.timeStamp.asTime()
        if (view.text.startsWith("http")) {
            CoroutineScope(Dispatchers.Main).launch {
                chatUserMessageText.visibility = View.GONE
                chatUserMessageLink.setLink(view.text, object : ViewListener {
                    override fun onSuccess(status: Boolean) {
                        blockUserMessage.visibility = View.VISIBLE
                        chatUserMessageTime.visibility = View.VISIBLE
                        chatUserMessageLink.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception) {
                    }
                })
            }
        } else {
            blockUserMessage.visibility = View.VISIBLE
            chatUserMessageLink.visibility = View.GONE
            chatUserMessageText.visibility = View.VISIBLE
            chatUserMessageTime.visibility = View.VISIBLE
            if (view.text.startsWith("Я создал ключ, давай общаться!") ||
                view.text.startsWith("Я ввёл ключ, можем общаться!") && view.who != ""
            ) {
                chatUserMessageText.text = view.text
            } else {
                val o = mKey?.let {
                    view.text.cipherDecrypt(it)
                }
                if (o == null) {
                    chatUserMessageText.text = "*****"
                } else {
                    chatUserMessageText.text = o.toString()
                }
            }
            if (view.who == "") {
                chatUserMessageText.text = view.text
            }
        }
    }

    override fun onAttach(view: MessageView, mViewModel: MainViewModel) {

    }

    override fun onDetach() {

    }

}
