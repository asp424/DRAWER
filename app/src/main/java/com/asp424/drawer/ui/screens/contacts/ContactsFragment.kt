package com.asp424.drawer.ui.screens.contacts

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.R
import com.asp424.drawer.databinding.FragmentContactsBinding
import com.asp424.drawer.ui.screens.main_list.ContactsAdapter
import com.asp424.drawer.utilites.hideKeyboard
import com.asp424.drawer.viewModel.MainViewModel

class ContactsFragment : Fragment(R.layout.fragment_contacts) {
    private val mViewModel: MainViewModel by activityViewModels()
    private val mContactsFragment by viewBinding(FragmentContactsBinding::bind)

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        initRecycleView()
    }

    private fun initRecycleView() {
        val mAdapter = ContactsAdapter(mViewModel)
        mContactsFragment.contactsRecycleView.adapter = mAdapter
        mViewModel.getContacts()
        mViewModel.listContact.observe(viewLifecycleOwner, { ass ->
            mAdapter.updateListItems(ass)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.clearContactsListeners()
    }
}

