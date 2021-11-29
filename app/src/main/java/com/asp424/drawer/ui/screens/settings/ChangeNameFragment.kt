package com.asp424.drawer.ui.screens.settings

import android.view.*
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.R
import com.asp424.drawer.database.USER
import com.asp424.drawer.database.setNameToDatabase
import com.asp424.drawer.databinding.FragmentChangeNameBinding
import com.asp424.drawer.utilites.showToast

class ChangeNameFragment : Fragment(R.layout.fragment_change_name) {
    private val mSettingsFragment by viewBinding(FragmentChangeNameBinding::bind)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings_confirm_change -> change()
        }
        return true
    }
    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        mSettingsFragment.settingsInputName.setText(USER.fullname)
    }

  private fun change() {
        val name = mSettingsFragment.settingsInputName.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.toast_name_empty))
        } else {
            setNameToDatabase(name)
        }
    }
}
