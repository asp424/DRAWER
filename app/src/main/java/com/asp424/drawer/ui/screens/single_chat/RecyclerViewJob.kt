package com.asp424.drawer.ui.screens.single_chat

import android.content.Context
import android.graphics.Color
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.asp424.drawer.MainActivity
import com.asp424.drawer.database.CURRENT_UID
import com.asp424.drawer.databinding.FragmentSingleChatBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.ui.message_recycler_view.views.AppViewsFactory
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecyclerViewJob(
    viewModel: MainViewModel,
    binding: FragmentSingleChatBinding?,
    context: Context,
    contact: CommonModel,
    activity: MainActivity,
    lifecycleScope: LifecycleCoroutineScope,
    lifecycle: Lifecycle
) : LifecycleObserver {

    private val mActivity = activity
    private val mLifeCycleScope = lifecycleScope
    private val mLifeCycle = lifecycle
    private val mViewModel = viewModel
    private var mBinding = binding
    private val mContext = context
    private val mContact = contact
    private var mCountMessages = 10
    private var mCount = 0
    private var mIsScrolling = false
    private val mAdapter = SingleChatAdapter(mViewModel, mActivity)
    private val mRecyclerView = binding?.chatRecycleView

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun initRV() {
        mBinding?.apply {
            val mSwipeRefreshLayout = charSwipeRefresh
            val mLayoutManager = LinearLayoutManager(mContext)
            mRecyclerView?.apply {
                adapter = mAdapter
                layoutManager = mLayoutManager
                mLayoutManager.stackFromEnd = true
                CoroutineScope(Dispatchers.IO).launch {
                    delay(400L)
                    setHasFixedSize(true)
                    isNestedScrollingEnabled = false
                    addContactsBtnToBottom.setOnClickListener {
                        mBinding.apply {
                            animButtonToBottomInvisible(mBinding!!)
                            mCount = 0
                            smoothScrollToPosition(mAdapter.itemCount)
                        }
                    }
                    rVListener(mAdapter, mSwipeRefreshLayout)
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView,
                            newState: Int,
                        ) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                mIsScrolling = true
                            }
                        }

                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            if (mIsScrolling && dy < 0) {
                                if (mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                                    updateData()
                                }
                            }
                        }
                    })
                    mSwipeRefreshLayout.setOnRefreshListener {
                        updateData()
                    }
                }
            }
        }
    }

    private fun rVListener(
        mAdapter: SingleChatAdapter,
        mSwipeRefreshLayout: SwipeRefreshLayout
    ){
        mLifeCycleScope.launch {
            mLifeCycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.message.collect { message ->
                    if (message.id != "1") {
                        mAdapter.addItemToTop(AppViewsFactory.getView(message)) {
                            mSwipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            }
        }
        mViewModel.getRecyclerData(mContact.id, mCountMessages)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(400L)
            getLastMessage()
        }
    }

    private fun getLastMessage(){

            mViewModel.getRecyclerDataSCFLastMessage(mContact.id)
            mLifeCycleScope.launch {
                mLifeCycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mViewModel.lastMessage.collect { lastMessage ->
                        if (lastMessage.id != "1") {
                            if (lastMessage.from != CURRENT_UID) {
                                if (mCount == 0)
                                    mAdapter.addItemToBottom(AppViewsFactory.getView(lastMessage)) {
                                        mRecyclerView?.smoothScrollToPosition(mAdapter.itemCount)
                                    }
                                else mAdapter.addItemToBottom(AppViewsFactory.getView(lastMessage)) {}
                            } else mAdapter.addItemToBottom(AppViewsFactory.getView(lastMessage)) {
                                mRecyclerView?.smoothScrollToPosition(mAdapter.itemCount)
                            }
                        }
                    }
                }
            }
        }

    private fun updateData() {
        mIsScrolling = false
        mBinding?.apply {
            if (!addContactsBtnToBottom.isVisible && mCount == 1){

                animButtonToBottomVisible(mBinding!!)
            }
        }
        mCount = 1
        mCountMessages += 10
        mViewModel.getRecyclerData(mContact.id, mCountMessages)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        mViewModel.apply {
            clearSCFListener()
            valueWriting(0, mContact.id)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        mViewModel.apply { mAdapter.destroy()
            mRecyclerView?.adapter = null
        }
        mBinding = null
    }
}