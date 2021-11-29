package com.asp424.drawer.ui.screens.main_list


import android.annotation.SuppressLint
import android.app.Fragment
import android.view.View
import androidx.lifecycle.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.R
import com.asp424.drawer.databinding.FragmentMainListBinding
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.asp424.drawer.utilites.hideKeyboard
import com.asp424.drawer.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainListFragment : Fragment(R.layout.fragment_main_list) {
    private val mViewModel = ViewModelProvider(APP_ACTIVITY).get(MainViewModel::class.java)
    private lateinit var mAdapter: MainListAdapter
    private val mMainListFragment by viewBinding(FragmentMainListBinding::bind)

    override fun onResume() {
        super.onResume()

        mAdapter = MainListAdapter(mViewModel)
        mMainListFragment.progressBar.visibility = View.VISIBLE
        mMainListFragment.mainListRecycleView.adapter = mAdapter
        hideKeyboard()
        initRecycleView()
    }

    private fun initRecycleView() = CoroutineScope(Dispatchers.Main).launch {
        delay(400L)
        initRV()
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun initRV() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.modelItem.collect { modelItem ->
                    mMainListFragment.progressBar.visibility = View.INVISIBLE
                    if (modelItem.id != "1") {
                        mAdapter.updateListItems(modelItem)
                    }
                }
            }
        }
        mViewModel.getMainListData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.clearMainListListeners()
    }
}






























