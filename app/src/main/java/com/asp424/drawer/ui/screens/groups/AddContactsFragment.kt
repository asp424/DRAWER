package com.asp424.drawer.ui.screens.groups


import android.annotation.SuppressLint
import android.app.Fragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.R
import com.asp424.drawer.databinding.FragmentAddContactsBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.hideKeyboard
import com.asp424.drawer.utilites.showToast
import com.asp424.drawer.viewModel.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AddContactsFragment : Fragment(R.layout.fragment_add_contacts) {
    private val mAddContactsFragment by viewBinding(FragmentAddContactsBinding::bind)
    private val mViewModel: MainViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        mRe
        hideKeyboard()
        initRecycleView()
        mAddContactsFragment.addContactsBtnNext.setOnClickListener {
            mViewModel.listContacts.value = listContacts
            if (listContacts.isEmpty()) showToast(
                "Добавьте хотя бы одного участника"
            )
            else findNavController().navigate(R.id.action_nav_add_contacts_to_nav_create_group)
        }
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun initRecycleView() {
        val mAdapter = AddContactsAdapter()
        mAddContactsFragment.addContactsRecycleView.adapter = mAdapter
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.modelItem.collect {
                    mAdapter.updateListItems(it)
                }
            }
        }
        mViewModel.getMainListData()
    }


    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.clearMainListListeners()
    }
}