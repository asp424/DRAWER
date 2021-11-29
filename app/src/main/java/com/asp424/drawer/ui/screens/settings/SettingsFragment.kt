package com.asp424.drawer.ui.screens.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.database.*
import com.asp424.drawer.databinding.FragmentSettingsBinding
import com.asp424.drawer.utilites.downloadAndSetImage
import com.asp424.drawer.utilites.hideKeyboard
import com.asp424.drawer.utilites.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val settingsFragment by viewBinding(FragmentSettingsBinding::bind)
    private var launcherImageChoose: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultPhoto ->
            if (resultPhoto.resultCode == RESULT_OK && resultPhoto.data != null) {
                val uri = CropImage.getActivityResult(resultPhoto.data).uri
                val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                    .child(CURRENT_UID)
                putFileToStorage(uri, path) {
                    getUrlFromStorage(path) {
                        putUrlToDatabase(it) {
                            settingsFragment.settingsUserPhoto.downloadAndSetImage(it)
                            showToast(
                                getString(R.string.toast_data_update)
                            )
                            USER.photoUrl = it
                        }
                    }
                }
            }
        }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() = CoroutineScope(Dispatchers.Main).launch {
        settingsFragment.apply {
            if (USER.fullname.isEmpty()) {
                settingsFullName.text = USER.phone
            } else {
                settingsFullName.text = USER.fullname
            }
            settingsPhoneNumber.text = USER.phone
            settingsUsername.text = USER.username
            settingsBtnChangeUsername.setOnClickListener {
                findNavController().navigate(R.id.action_nav_settings_to_nav_change_username)
            }
            settingsChangePhoto.setOnClickListener { changePhotoUser() }
            settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
            settingsUserPhoto.setOnClickListener {
                val bundle1 = Bundle()
                bundle1.putString("ass", USER.photoUrl)
                findNavController().navigate(R.id.action_nav_settings_to_nav_full_photo, bundle1)
            }
        }
    }

    private fun changePhotoUser() = CoroutineScope(Dispatchers.IO).launch {
        launcherImageChoose.launch(
            CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(600, 600)
                .setCropShape(CropImageView.CropShape.OVAL)
                .getIntent(activity as MainActivity)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                updateStateExit()
                AUTH.signOut()
                CURRENT_UID = AUTH.currentUser?.uid.toString()
                findNavController().navigate(R.id.action_nav_settings_to_nav_enter_phone)
            }
            R.id.settings_menu_change_name -> findNavController().navigate(R.id.action_nav_settings_to_nav_change_name)
        }
        return true
    }
}

