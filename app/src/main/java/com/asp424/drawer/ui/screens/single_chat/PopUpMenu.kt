package com.asp424.drawer.ui.screens.single_chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.database.CURRENT_UID
import com.asp424.drawer.database.existFile
import com.asp424.drawer.database.readTXT
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.utilites.cipherDecrypt
import com.asp424.drawer.utilites.showToast
import com.asp424.drawer.viewModel.MainViewModel

class PopUpMenu {

    fun popUpMenu(view: View, view1: MessageView, mViewModel: MainViewModel, activity: MainActivity, function: () -> Unit) {
        val popup = PopupMenu(activity, view)
        popup.inflate(R.menu.pop_ip_menu)
        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.header1 -> {
                    if (view1.from == CURRENT_UID) {
                        mViewModel.removeMessage(view1.who, view1.id) {
                            function()
                        }

                    } else {
                        mViewModel.removeMessage(view1.from, view1.id) {
                            function()
                        }
                    }
                }
                R.id.header2 -> {
                    if (view1.from == CURRENT_UID) {
                        if (existFile(view1.who)) {
                            val mKey = readTXT(view1.who)
                            val o = mKey.let {
                                view1.text.cipherDecrypt(it)
                            }
                            writeToClipboard(activity, o)
                        }
                    } else {
                        if (view1.from != CURRENT_UID) {
                            if (existFile(view1.from)) {
                                val mKey = readTXT(view1.from)
                                val o = mKey.let {
                                    view1.text.cipherDecrypt(it)
                                }
                               writeToClipboard(activity, o)
                            }
                        } else showToast("Ключ отсутствует")
                    }
                }
                R.id.header3 -> {
                }
            }
            true
        }
        popup.show()
    }
    private fun writeToClipboard(activity: MainActivity, o: String?) {
        val myClipboard: ClipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip = ClipData.newPlainText("label", o)
        myClipboard.setPrimaryClip(myClip)
        showToast("Текст скопирован в буфер обмена")

    }
}