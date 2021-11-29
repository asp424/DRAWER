package com.asp424.drawer.ui.message_recycler_view.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.viewModel.MainViewModel

interface MessageHolder {
    fun drawMessage(
        view: MessageView,
        mViewModel: MainViewModel
    )

    fun onAttach(view: MessageView, mViewModel: MainViewModel)
    fun onDetach()
}