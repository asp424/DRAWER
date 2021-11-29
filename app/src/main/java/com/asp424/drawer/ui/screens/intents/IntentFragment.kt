package com.asp424.drawer.ui.screens.intents

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.database.sendMessageToGroup
import com.asp424.drawer.database.uploadFileToStorage
import com.asp424.drawer.database.uploadFileToStorageToGroup
import com.asp424.drawer.databinding.FragmentAddContactsBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class IntentFragment : Fragment(R.layout.fragment_add_contacts) {
    private val mViewModel = ViewModelProvider(APP_ACTIVITY).get(MainViewModel::class.java)
    private val mAddContactsBinding by viewBinding(FragmentAddContactsBinding::bind)
    private lateinit var mRecyclerView: RecyclerView
    override fun onResume() {
        listContacts.clear()
        super.onResume()
        val mMessage = arguments?.getString("shi").toString()
        val image = arguments?.getInt("Image")
        ActivityCompat.requireViewById<MaterialToolbar>(
            requireActivity(),
            R.id.topAppBar
        ).navigationIcon = null
        hideKeyboard()
        initRecycleView()
        mAddContactsBinding.addContactsBtnNext.setOnClickListener {
            if (listContacts.isEmpty()) showToast(
                "Добавьте хотя бы одного индюка"
            )
            else {
                listContacts.forEach {
                    if (image == 0){
                        if (it.type == TYPE_CHAT) {
                            mViewModel.sendMessage(
                                mMessage,
                                it.id,
                                TYPE_MESSAGE_TEXT
                            ) {}
                        }
                        if (it.type == TYPE_GROUP) {
                            sendMessageToGroup(
                                mMessage,
                                it.id,
                                TYPE_MESSAGE_TEXT
                            ) {}
                        }
                    }
                    else{
                        val mUri = mMessage.toUri()
                        val messageKey = mViewModel.getMessageKey(it.id)
                        if (it.type == TYPE_CHAT) {
                            uploadFileToStorage(
                                mUri,
                                messageKey,
                                it.id,
                                TYPE_MESSAGE_IMAGE,
                                pathStorage = getRealPathFromURI(mUri),
                                filename = getFileNameFromUri(mUri), TYPE_MANY
                            )
                        }
                        if (it.type == TYPE_GROUP) {
                            uploadFileToStorageToGroup(
                                mUri,
                                messageKey,
                                TYPE_MESSAGE_IMAGE,
                                it.id,
                                pathStorage = getRealPathFromURI(mUri),
                                filename = getFileNameFromUri(mUri)
                            )
                        }
                    }
                }
                showToast("Ссылка отправлена")
                findNavController().navigate(R.id.action_nav_intent_to_nav_main)
            }
        }
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun initRecycleView() {
        mRecyclerView =  mAddContactsBinding.addContactsRecycleView
        val mAdapterText = IntentAdapter()
        mRecyclerView.adapter = mAdapterText
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.modelItem.collect {
                    mAdapterText.updateListItems(it)
                }
            }
        }
        mViewModel.getMainListData()
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.clearMainListListeners()
        mRecyclerView.adapter = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                (requireActivity() as MainActivity).finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }
}