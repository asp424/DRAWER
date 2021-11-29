package com.asp424.drawer.ui.screens.single_chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.asp424.drawer.DashboardActivity
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.database.*
import com.asp424.drawer.databinding.ChangeKeyBinding
import com.asp424.drawer.databinding.CreateKeyBinding
import com.asp424.drawer.databinding.EnterKeyBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.asp424.drawer.utilites.TYPE_CHAT
import com.asp424.drawer.utilites.TYPE_MESSAGE_TEXT
import com.asp424.drawer.utilites.showToast
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Windows(
    mViewModel: MainViewModel,
    mainActivity: MainActivity,
  viewLifecycleOwner: LifecycleOwner
) {
    private val mSingleChatViewModel = mViewModel
    private val mViewLifecycleOwner = viewLifecycleOwner
    private val mContext = mainActivity
    private lateinit var mAlertDialogEnter: AlertDialog
    private lateinit var mAlertDialogCreate: AlertDialog
    private lateinit var mAlertDialogChange: AlertDialog
    private lateinit var mAlertDialogSee: AlertDialog
    private lateinit var mJEnter: EnterKeyBinding
    private lateinit var mJCreate: CreateKeyBinding
    private lateinit var mJChange: ChangeKeyBinding
    private var mKeyFlag: Any? = null
    private var mKey: String? = null
    private lateinit var mKeySee: String
    private val mNavController =
        Navigation.findNavController(mContext, viewId = R.id.nav_host_fragment)

  fun initWindows(mContact: CommonModel) = CoroutineScope(Dispatchers.Unconfined).launch {
      mKeySee = if (existFile(mContact.id)) {
            readTXT(mContact.id).substring(0, 6)
        } else "Ключ не создан"
        mContext.apply {
            mJEnter = EnterKeyBinding.inflate(LayoutInflater.from(this))
            mJCreate = CreateKeyBinding.inflate(LayoutInflater.from(this))
            mJChange = ChangeKeyBinding.inflate(LayoutInflater.from(this))
            mAlertDialogCreate = MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.key_chat_text))
                .setMessage("Придумайте и введите шестизначный ключ, запомните его и передайте второму участнику чата.")
                .setView(mJCreate.root)
                .setNegativeButton(resources.getString(R.string.decline)) { _, _ ->
                    mNavController.navigate(R.id.action_nav_single_to_nav_main)
                }
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    val key = mJCreate.editTextCreateKey.text.toString()
                    if (key.isEmpty()) {
                        Toast.makeText(mContext, "Введите ключ",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        if (key.length == 6) {
                            val p = key + 1467890983
                            saveToTXT(mContact.id, p)
                            mKey = p
                            mKeySee = key
                            mSingleChatViewModel.setValueKey(mContact)
                            val k = "Я создал ключ, давай общаться!"
                            mSingleChatViewModel.sendMessage(
                                k,
                                mContact.id,
                                TYPE_MESSAGE_TEXT
                            ) {
                                mSingleChatViewModel.saveToMainList(
                                    mContact.id,
                                    TYPE_CHAT
                                )
                            }
                        } else {
                            Toast.makeText(mContext, "Ключ должен быть шестизначным",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }.create()
            mAlertDialogChange = MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.change_key_chat_text))
                .setMessage("Введите ключ, который предоставил Вам второй участник чата.")
                .setView(mJChange.root)
                .setNegativeButton(resources.getString(R.string.decline)) { _, _ ->
                    mNavController.navigate(R.id.action_nav_single_to_nav_main)
                }
                .setPositiveButton(getString(R.string.accept)) { _, _ ->
                    val key = mJChange.editTextChangeKey.text.toString()
                    if (key.isEmpty()) {
                        Toast.makeText(mContext, "Введите ключ", Toast.LENGTH_SHORT).show()
                    } else {
                        if (key.length == 6) {
                            val p = key + 1467890983
                            saveToTXT(mContact.id, p)
                            mKey = p
                            mKeySee = key
                            mSingleChatViewModel.setValueKey(mContact)

                        } else {
                            Toast.makeText(mContext, "Ключ должен быть шестизначным",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }.create()
            mAlertDialogSee = MaterialAlertDialogBuilder(this)
                .setTitle(mKeySee)
                .setPositiveButton(getString(R.string.accept)) { _, _ ->
                }.create()
            mAlertDialogEnter = MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.enter_key_chat_text))
                .setMessage("Введите ключ, который предоставил Вам второй участник чата.")
                .setView(mJEnter.root)
                .setNegativeButton(resources.getString(R.string.decline)) { _, _ ->
                    mNavController.navigate(R.id.action_nav_single_to_nav_main)
                }
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    val key = mJEnter.editTextEnterKey.text.toString()
                    if (key.isEmpty()) {
                        Toast.makeText(APP_ACTIVITY, "Введите ключ",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        if (key.length == 6) {
                            mKey = key + 1467890983
                            mKeySee = key
                            saveToTXT(mContact.id, mKey!!)
                            mSingleChatViewModel.setValueKey(mContact)
                            if (mKeyFlag == "key")
                                findViewById<ImageView>(R.id.icon_connect).visibility =
                                    View.VISIBLE
                            mSingleChatViewModel.sendMessage(
                                text = "Я ввёл ключ, можем общаться!",
                                mContact.id,
                                TYPE_MESSAGE_TEXT
                            ) {
                                mSingleChatViewModel.saveToMainList(
                                    mContact.id,
                                    TYPE_CHAT
                                )
                            }
                        } else {
                            Toast.makeText(mContext, "Ключ должен быть шестизначным",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }.create()
        }

        mAlertDialogCreate.setOnCancelListener {
            mAlertDialogEnter.dismiss()
            mAlertDialogChange.dismiss()
            mAlertDialogSee.dismiss()
            mAlertDialogCreate.dismiss()
        }
        mAlertDialogEnter.setOnCancelListener {
            mAlertDialogEnter.dismiss()
            mAlertDialogChange.dismiss()
            mAlertDialogSee.dismiss()
            mAlertDialogCreate.dismiss()
        }
        mAlertDialogSee.setOnCancelListener {
            mAlertDialogEnter.dismiss()
            mAlertDialogChange.dismiss()
            mAlertDialogSee.dismiss()
            mAlertDialogCreate.dismiss()
        }
        mAlertDialogChange.setOnCancelListener {
            mAlertDialogEnter.dismiss()
            mAlertDialogChange.dismiss()
            mAlertDialogSee.dismiss()
            mAlertDialogCreate.dismiss()
        }
        windowCrypt(mContact)
    }

    private fun windowCrypt(mContact: CommonModel) = CoroutineScope(Dispatchers.Unconfined).launch {
        val mToolbarIcon = mContext.findViewById<ImageView>(R.id.icon_connect)
        mSingleChatViewModel.apply {
            kryptKeys(mContact.id)
            mToolbarIcon?.visibility = View.GONE
            keyFlag.observe(mViewLifecycleOwner, { keyFlag ->
                mKeyFlag = keyFlag
                if (mKeyFlag == "key" && existFile(mContact.id))
                    mToolbarIcon?.visibility = View.VISIBLE
                if (mKeyFlag == "key" && !existFile(mContact.id))
                    mAlertDialogEnter.show()
                if (mKeyFlag != "key" && !existFile(mContact.id))
                    mAlertDialogCreate.show()
                if (existFile(mContact.id))
                    mKey = readTXT(mContact.id)
            })
        }
    }

    fun menu(item: MenuItem, mContact: CommonModel) {
        when (item.itemId) {
            R.id.menu_clear_chat -> {
                mNavController.navigate(R.id.nav_main)
                clearSingleChat(mContact.id) {

                    Toast.makeText(mContext, "Чат очищен",
                        Toast.LENGTH_SHORT).show()
                }
            }
            R.id.menu_delete_chat -> {
                mNavController.navigate(R.id.nav_main)
                deleteChat(mContact.id) {
                    Toast.makeText(mContext, "Чат удалён",
                        Toast.LENGTH_SHORT).show()
                }
            }
            R.id.menu_create_key ->
                if (!existFile(mContact.id)) {
                    mAlertDialogCreate.show()
                } else {
                    Toast.makeText(mContext, "Ключ уже создан", Toast.LENGTH_SHORT).show()
                    mAlertDialogSee.show()
                }
            R.id.menu_see_key ->
                if (existFile(mContact.id)) {
                    mKeySee = readTXT(mContact.id).substring(0, 6)
                    mAlertDialogSee.setTitle(mKeySee)
                    mAlertDialogSee.show()
                } else {
                    if (mKeyFlag == "key") {
                        mJEnter.editTextEnterKey.setText("")
                        mAlertDialogEnter.show()
                    } else {
                        mAlertDialogCreate.show()
                    }
                }
            R.id.menu_change_key -> {
                if (existFile(mContact.id)) {
                    mAlertDialogChange.show()
                } else {
                    if (mKeyFlag == "key") {
                        mJEnter.editTextEnterKey.setText("")
                        mAlertDialogEnter.show()
                    } else {
                        mAlertDialogCreate.show()
                    }
                }
            }
            R.id.menu_enter_key -> {
                if (existFile(mContact.id)) {
                    Toast.makeText(mContext, "Ключ уже введён",
                        Toast.LENGTH_SHORT).show()
                    mAlertDialogSee.show()
                } else {
                    if (mKeyFlag == "key") {
                        mJEnter.editTextEnterKey.setText("")
                        mAlertDialogEnter.show()
                    } else {
                        mAlertDialogCreate.show()
                    }
                }
            }
            R.id.menu_delete_key ->
                if (existFile(mContact.id)) {
                    deleteTXT(mContact.id) {
                        Toast.makeText(mContext, "Ключ удален",
                            Toast.LENGTH_SHORT).show()
                        mSingleChatViewModel.removeValueKey(mContact)
                        mContext.findViewById<ImageView>(R.id.icon_connect).visibility =
                            View.GONE
                        if (mKeyFlag == "key") mAlertDialogEnter.show()
                        else mAlertDialogCreate.show()
                    }
                } else {
                    if (mKeyFlag == "key") {
                        mAlertDialogEnter.show()
                        Toast.makeText(mContext, "Вы ещё не ввели ключ",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(mContext, "Ключ ещё не создан",
                            Toast.LENGTH_SHORT).show()
                        mAlertDialogCreate.show()
                    }

                }
            R.id.menu_call -> {
                val intent = Intent(APP_ACTIVITY, DashboardActivity::class.java)
                APP_ACTIVITY.startActivity(intent)
                showToast("Вы совершаете защищённый звонок")
            }
        }
    }
}