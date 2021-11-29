package com.asp424.drawer.ui.screens.groups

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.R
import com.asp424.drawer.database.createGroupToDatabase
import com.asp424.drawer.databinding.FragmentCreateGroupBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.asp424.drawer.utilites.getPlurals
import com.asp424.drawer.utilites.hideKeyboard
import com.asp424.drawer.utilites.showToast
import com.asp424.drawer.viewModel.MainViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class CreateGroupFragment :
    Fragment(R.layout.fragment_create_group) {
    private lateinit var listContacts: List<CommonModel>
    private var mUri = Uri.EMPTY
    private val mCreateGroupFragment by viewBinding(FragmentCreateGroupBinding::bind)
    private val mViewModel: MainViewModel by activityViewModels()
    private var launcherImageChoose: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultPhoto ->
            if (resultPhoto.resultCode == RESULT_OK && resultPhoto.data != null) {
                mUri = CropImage.getActivityResult(resultPhoto.data!!).uri
                mCreateGroupFragment.createGropPhoto.setImageURI(mUri)
            }
        }

    override fun onResume() {
        super.onResume()
        requireView().f
        mCreateGroupFragment.apply {
            mViewModel.listContacts.observe(viewLifecycleOwner, {
                listContacts = it
            })
            APP_ACTIVITY.title = getString(R.string.create_group)
            hideKeyboard()
            initRecycleView()
            createGropPhoto.setOnClickListener { addPhoto() }
            createGroupBtnComplete.setOnClickListener {
                val nameGroup = createGroupInputName.text.toString()
                if (nameGroup.isEmpty()) {
                    showToast("Назови как-нибудь группу")
                } else {
                    createGroupToDatabase(nameGroup, mUri, listContacts) {
                        findNavController().navigate(R.id.action_nav_create_group_to_nav_main)
                        showToast("Группа $nameGroup создана")
                    }
                }
            }
            createGroupInputName.requestFocus()
            createGroupCounts.text = getPlurals(listContacts.size)
        }
    }

    private fun addPhoto() {
        launcherImageChoose.launch(
            CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(250, 250)
                .setCropShape(CropImageView.CropShape.OVAL)
                .getIntent(APP_ACTIVITY)
        )
    }

    private fun initRecycleView() {
        val mAdapter = AddContactsAdapter()
        mCreateGroupFragment.createGroupRecycleView.adapter = mAdapter
        listContacts.forEach {
            mAdapter.updateListItems(it)
        }
    }






}







