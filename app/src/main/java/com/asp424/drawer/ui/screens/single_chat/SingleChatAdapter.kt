package com.asp424.drawer.ui.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.MainActivity
import com.asp424.drawer.ui.message_recycler_view.view_holder.AppHolderFactory
import com.asp424.drawer.ui.message_recycler_view.view_holder.MessageHolder
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.utilites.showToast
import com.asp424.drawer.viewModel.MainViewModel

class SingleChatAdapter(
    SingleChatViewModel: MainViewModel,
    activity: MainActivity,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mViewModel = SingleChatViewModel
    private val mActivity = activity
    private var mListMessagesCache = mutableListOf<MessageView>()
    private var mListHolder = mutableListOf<MessageHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getTypeView()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemPosition = holder.adapterPosition
        val item = mListMessagesCache[itemPosition]
        val view = TypeFactory().getView(holder.itemView, item)
        (holder as MessageHolder).drawMessage(
            mListMessagesCache[position],
            mViewModel
        )
        view.setOnLongClickListener {
            PopUpMenu().popUpMenu(it, item, mViewModel, mActivity) {
                showToast("Сообщение удалено")
                if (mListMessagesCache.contains(mListMessagesCache[itemPosition]))
                mListMessagesCache.removeAt(itemPosition)
                notifyItemRemoved(itemPosition)
            }
            true
        }

    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(mListMessagesCache[holder.adapterPosition], mViewModel)
        mListHolder.add((holder as MessageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()

        mListHolder.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    fun addItemToBottom(
        item: MessageView,
        onSuccess: () -> Unit,
    ) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(
        item: MessageView,
        onSuccess: () -> Unit,
    ) {

        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun destroy() {
        mListHolder.forEach {
            it.onDetach()
        }
    }
}


