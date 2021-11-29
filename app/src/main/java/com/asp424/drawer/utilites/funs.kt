package com.asp424.drawer.utilites


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.core.view.forEach
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.database.USER
import com.asp424.drawer.database.updatePhonesToDatabase
import com.asp424.drawer.models.CommonModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun log(text: Any) {
    Log.d("My", text.toString())
}

fun hideKeyboard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun ImageView.downloadAndSetImage(url: String) {
    Picasso.get()
        .load(url)
        .fit()
        .centerInside()
        .placeholder(R.drawable.x)
        .into(this)
}

fun ImageView.downloadAndSetImageWithoutPlaceholder(url: String, function: () -> Unit) {
    Picasso.get()
        .load(url)
        .fit()
        .centerInside()
        .into(this)
    function()
}

fun initContacts(context: Context?) {
    val listContacts = mutableListOf<CommonModel>()
    val cursor = context?.contentResolver?.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )
    cursor?.let { cur ->
        cur.apply {
            while (moveToNext()) {
                var phoneVal =
                    getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .isDigitsOnly().toString()
                phoneVal = "+" + "7" + phoneVal.substring(2, 11)
                if (phoneVal != USER.phone && phoneVal.length == 12) {
                    CommonModel().apply {
                        fullname = getString(getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        phone = phoneVal
                        listContacts.add(this)
                    }
                }
            }
        }

        cursor.close()

    }
}

fun initHeaderDrawer(activity: MainActivity) {
    val navView = activity.findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
    val photo = navView.findViewById<CircleImageView>(R.id.user_photo_header)
    val name = navView.findViewById<TextView>(R.id.name_header)
    photo.downloadAndSetImageWithoutPlaceholder(USER.photoUrl) {
        photo.setOnClickListener {
            val bundle1 = Bundle()
            bundle1.putString("ass", USER.photoUrl)
            val extras = FragmentNavigatorExtras(
                photo to "image"
            )
            activity.findNavController(R.id.nav_host_fragment)
                .navigate(R.id.nav_full_photo, bundle1, null, extras)
        }
        if (USER.fullname.isEmpty()) {
            name.text = USER.phone
        } else {
            name.text = USER.fullname
        }
    }
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun String.asTimeSec(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm:SS", Locale.getDefault())
    return timeFormat.format(time)
}

fun String.asDate(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("dd:MM:yy", Locale.getDefault())
    return timeFormat.format(time)
}

fun getFileNameFromUri(uri: Uri): String {
    var result = ""

    val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null)
    try {
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    } catch (e: Exception) {
        showToast(e.message.toString())
    } finally {
        cursor?.close()
        return result
    }
}

fun getRealPathFromURI(contentURI: Uri): String {
    var result = ""
    val cursor = APP_ACTIVITY.contentResolver.query(
        contentURI,
        null, null, null, null
    )
    try {
        if (cursor == null) {
            result = contentURI.path.toString()
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
    } catch (e: Exception) {

        showToast(e.message.toString())
    } finally {
        cursor?.close()
        return result
    }
}

fun getPlurals(count: Int) = APP_ACTIVITY.resources.getQuantityString(
    R.plurals.count_members, count, count
)

fun addChip(pItem: String, pChipGroup: ChipGroup?) {
    val lChip = Chip(APP_ACTIVITY)
    lChip.text = pItem
    pChipGroup?.addView(lChip, pChipGroup.childCount - 1)
}

fun deleteChip(pItem: String, pChipGroup: ChipGroup?) {
    pChipGroup?.forEach {
        val j = it as Chip
        val k = j.text
        if (pItem == k) {
            j.visibility = View.GONE
        }
    }
}

fun deleteAllChips(pChipGroup: ChipGroup?) {
    pChipGroup?.forEach {
        pChipGroup.run { removeAllViews() }
    }
}

fun String.cipherEncrypt(encryptionKey: String): String? {
    try {
        val secretKeySpec = SecretKeySpec(encryptionKey.toByteArray(), "AES")
        val iv = encryptionKey.toByteArray()
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encryptedValue = cipher.doFinal(this.toByteArray())
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(encryptedValue)
        } else {
            android.util.Base64.encodeToString(encryptedValue, android.util.Base64.DEFAULT)
        }


    } catch (e: Exception) {
        e.message?.let {}

        return null
    }
}


fun String.cipherDecrypt(encryptionKey: String): String? {
    try {

        val secretKeySpec = SecretKeySpec(encryptionKey.toByteArray(), "AES")
        val iv = encryptionKey.toByteArray()
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decodedValue = Base64.getMimeDecoder().decode(this)
            val decryptedValue = cipher.doFinal(decodedValue)
            String(decryptedValue)
        } else {
            val decodedValue = android.util.Base64.decode(this, android.util.Base64.DEFAULT)
            val decryptedValue = cipher.doFinal(decodedValue)
            String(decryptedValue)
        }

    } catch (e: Exception) {
        e.message?.let { }
        return null
    }
}





