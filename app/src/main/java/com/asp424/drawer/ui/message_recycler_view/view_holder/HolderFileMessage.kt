package com.asp424.drawer.ui.message_recycler_view.view_holder

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.database.*
import com.asp424.drawer.databinding.MessageItemFileBinding
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.asp424.drawer.utilites.asTime
import com.asp424.drawer.utilites.showToast
import com.asp424.drawer.viewModel.MainViewModel
import java.io.File

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val binding = MessageItemFileBinding.bind(view)
    private val blocRecieverFileMessage: ConstraintLayout = binding.blockRecievedFile
    private val blocUserFileMessage: ConstraintLayout = binding.blockUserFile
    private val chatUserFileMessageTime: TextView = binding.chatUserFileMessageTime
    private val chatReceivedFileMessageTime: TextView = binding.chatRecievedFileMessageTime
    private val chatUserFileName: TextView = binding.chatUserFilename
    private val chatUserBtnDownload: ImageView = binding.chatUserBtnDownload
    private val chatReceivedFilename: TextView = binding.chatReceivedFilename
    private val chatReceivedBtnDownload: ImageView = binding.chatReceivedBtnDownload
    private val chatReceivedProgressBar: ProgressBar = binding.chatReceivedProgressBar
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
            blocRecieverFileMessage.visibility = View.GONE
            blocUserFileMessage.visibility = View.VISIBLE
            if (view.timeStamp != "")
            chatUserFileMessageTime.text = view.timeStamp.asTime()
            chatUserFileName.text = view.fullname
        } else {
            chatRead.visibility = View.INVISIBLE
            if (view.counter != 0) {
                mViewModel.setValueReaded(view.from, view.id, 0)
            }
            blocRecieverFileMessage.visibility = View.VISIBLE
            blocUserFileMessage.visibility = View.GONE
            if (view.timeStamp != "")
            chatReceivedFileMessageTime.text = view.timeStamp.asTime()
            chatReceivedFilename.text = view.fullname
        }
    }
    override fun onAttach(view: MessageView, mViewModel: MainViewModel)
    {
        if (view.from != CURRENT_UID)
            chatReceivedBtnDownload.setOnClickListener { clickToBtnFile(view) }
    }

    private fun clickToBtnFile(view: MessageView) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.pathStorage
        )
        try {
            if (file.exists()) {
                showToast("У тебя уже есть такой файл в папке 'Загрузки'")
                REF_STORAGE_ROOT.child(FOLDER_FILES).child(view.id).delete()
            } else {
                chatReceivedBtnDownload.visibility = View.INVISIBLE
                chatReceivedProgressBar.visibility = View.VISIBLE
                getFileFromStorage(file,
                    view.fileUrl,
                    chatReceivedBtnDownload,
                    chatReceivedProgressBar) {
                    chatReceivedBtnDownload.visibility = View.VISIBLE
                    chatReceivedProgressBar.visibility = View.INVISIBLE
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    override fun onDetach() {
        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)
    }
}