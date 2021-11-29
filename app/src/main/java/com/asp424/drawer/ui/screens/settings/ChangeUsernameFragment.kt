package com.asp424.drawer.ui.screens.settings

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.R
import com.asp424.drawer.database.CURRENT_UID
import com.asp424.drawer.database.NODE_USERNAMES
import com.asp424.drawer.database.REF_DATABASE_ROOT
import com.asp424.drawer.database.updateCurrentUsername
import com.asp424.drawer.databinding.FragmentChangeUsernameBinding
import com.asp424.drawer.utilites.AppValueEventListener
import com.asp424.drawer.utilites.showToast
import java.util.*

class ChangeUsernameFragment : Fragment(R.layout.fragment_change_username) {
    private lateinit var mNewUsernameRegex2: String
    private val mChangeUsernameFragment by viewBinding(FragmentChangeUsernameBinding::bind)

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_confirm_change -> change()
        }
        return true
    }

    private fun change() {
        val mNewUsername = mChangeUsernameFragment.settingsInputUsername.text.toString()
            .lowercase(Locale.getDefault())
        val mNewUsernameRegex = mNewUsername.replace(Regex("[\\s.,#$]"), "")
        val mNewUsernameRegex1 = mNewUsernameRegex.replace("[", "")
        mNewUsernameRegex2 = mNewUsernameRegex1.replace("]", "")
        if (mNewUsernameRegex.isEmpty()) {
            showToast(getString(R.string.toast_name_empty))
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUsernameRegex2)) {
                        showToast(getString(R.string.toast_takoy_uze_est))
                    } else {
                        changeUsername()
                    }
                })
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsernameRegex2).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername(mNewUsernameRegex2)
                }
            }
    }
}








