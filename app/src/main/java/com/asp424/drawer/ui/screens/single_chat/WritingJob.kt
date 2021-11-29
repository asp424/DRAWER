package com.asp424.drawer.ui.screens.single_chat

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.utilites.AppTextWatcher
import com.asp424.drawer.utilites.addChip
import com.asp424.drawer.utilites.deleteAllChips
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WritingJob(
    _id: String,
    SingleChatViewModel: MainViewModel,
    mainActivity: MainActivity,
    mViewLifecycleOwner: LifecycleOwner
) {
    private val mSingleChatViewModel = SingleChatViewModel
    private val mContext = mainActivity
    private val id = _id

    init {
        CoroutineScope(Dispatchers.Unconfined).launch {
            val mChips = mContext.findViewById<ChipGroup>(R.id.chip_writing)
            mSingleChatViewModel.apply {
                getWritingInt(id).observe(mViewLifecycleOwner, {
                    if (it == 1) {
                        deleteAllChips(mChips)
                        addChip(" печатает...", mChips)
                    } else {
                        if (it == 0) {
                            deleteAllChips(mChips)
                        }
                    }
                })
                valueWriting(0, id)
                val mChatInputMessage = mContext.findViewById<EditText>(R.id.chat_input_message)
                val mWritingListener = AppTextWatcher {
                    val string = mChatInputMessage.text.toString()
                    if (string.isNotEmpty()) {
                        valueWriting(1, id)
                    }
                    if (string.isEmpty()) {
                        valueWriting(0, id)
                    }
                }
                mChatInputMessage.addTextChangedListener(mWritingListener)
            }
        }
    }
}