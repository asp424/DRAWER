package com.asp424.drawer.utilites

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.asp424.drawer.MainActivity
import com.asp424.drawer.ui.screens.regidter.EnterPhoneNumberFragment

const val READ_CONTACTS = android.Manifest.permission.READ_CONTACTS
const val RECORD_AUDIO = android.Manifest.permission.RECORD_AUDIO
const val WRITE_FILES = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
const val CAMERA = android.Manifest.permission.CAMERA
const val PERMISSION_REQUEST = 200
fun check_permissions(
    permission: String,
    context: EnterPhoneNumberFragment,
    mainActivity: MainActivity
): Boolean {
    return if (Build.VERSION.SDK_INT >= 23
        && ContextCompat.checkSelfPermission(
            context.requireContext(),
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(mainActivity, arrayOf(permission), PERMISSION_REQUEST)
        false
    } else true
}