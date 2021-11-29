package com.asp424.drawer.ui.message_recycler_view.view_holder

import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.database.CURRENT_UID
import com.asp424.drawer.database.getImageFromStorage
import com.asp424.drawer.databinding.MessageItemImageBinding
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.ui.screens.single_chat.ClickOnImage
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.asp424.drawer.utilites.asTime
import com.asp424.drawer.viewModel.MainViewModel
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class HolderImageMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val binding = MessageItemImageBinding.bind(view)
    private val chatReceivedImage: SubsamplingScaleImageView = binding.photo
    private val chatUserImage: SubsamplingScaleImageView = binding.chatUserImage
    private val chatUserImageMessageTime: TextView = binding.chatUserImageMessageTime
    private val chatReceivedImageMessageTime: TextView = binding.chatRecievedImageMessageTime
    private lateinit var mFile: File
    private val chatRead: TextView = binding.chatUserRead

    override fun drawMessage(
        view: MessageView,
        mViewModel: MainViewModel
    ) {
        if (view.from == CURRENT_UID) {
            chatRead.text = "просмотрено"
            chatReceivedImage.visibility = View.GONE
            chatReceivedImageMessageTime.visibility = View.GONE
            chatRead.visibility = View.GONE
            if (File(view.pathStorage).exists()) {
                ClickOnImage().onClickUser(binding, view)
                chatUserImage.setImage(ImageSource.uri(view.pathStorage))
                chatUserImage.visibility = View.VISIBLE
                chatUserImageMessageTime.visibility = View.VISIBLE
                if (view.timeStamp != "")
                    chatUserImageMessageTime.text =
                        view.timeStamp.asTime()
                mViewModel.getReaded(view.who, view.id).observe(APP_ACTIVITY, {
                    if (it == 0) {
                        chatRead.visibility = View.VISIBLE
                    } else {
                        chatRead.visibility = View.GONE
                    }
                })
            } else {
                chatReceivedImage.visibility = View.GONE
                chatReceivedImageMessageTime.visibility = View.GONE
                chatUserImage.visibility = View.GONE
                chatUserImageMessageTime.visibility = View.GONE
                chatRead.visibility = View.VISIBLE
                chatRead.text = "фотография удалена"
            }
        } else {
            chatUserImageMessageTime.visibility = View.GONE
            chatUserImage.visibility = View.GONE
            chatRead.visibility = View.GONE
            chatReceivedImageMessageTime.visibility = View.GONE
            chatReceivedImage.visibility = View.GONE
            if (view.counter != 0) {
                mViewModel.setValueReaded(view.from, view.id, 0)
            }
            mFile = if (view.fullname != "") {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    view.fullname
                )
            } else {
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    " JPEG_${timeStamp}_.jpg"
                )
            }
            try {
                if (mFile.exists()) {
                    chatReceivedImage.visibility = View.VISIBLE
                    chatRead.visibility = View.GONE
                    if (view.timeStamp != "") {
                        chatReceivedImageMessageTime.text =
                            view.timeStamp.asTime()
                        chatReceivedImageMessageTime.visibility = View.VISIBLE
                    }
                    chatReceivedImage.setImage(ImageSource.uri(mFile.toString()))
                } else {
                    chatReceivedImage.visibility = View.VISIBLE
                    chatRead.visibility = View.GONE
                    getImageFromStorage(mFile, view, chatReceivedImage) {
                        chatReceivedImage.visibility = View.VISIBLE
                        if (view.timeStamp != "") {
                            chatReceivedImageMessageTime.text =
                                view.timeStamp.asTime()
                            chatReceivedImageMessageTime.visibility = View.VISIBLE
                        }
                        chatReceivedImage.setImage(ImageSource.uri(mFile.toString()))
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onAttach(view: MessageView, mViewModel: MainViewModel) {
        if (view.from == CURRENT_UID) {
            if (File(view.pathStorage).exists()) {
                ClickOnImage().onClickUser(binding, view)
            }
        } else {
            ClickOnImage().onClickReceived(binding, mFile, view)
        }
    }

    override fun onDetach() {
        chatUserImage.setOnClickListener(null)
        chatReceivedImage.setOnClickListener(null)
    }
}