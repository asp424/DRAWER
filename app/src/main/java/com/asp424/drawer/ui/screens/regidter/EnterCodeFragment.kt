package com.asp424.drawer.ui.screens.regidter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.database.*
import com.asp424.drawer.databinding.FragmentEnterCodeBinding
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.ServerValue

class EnterCodeFragment :
    Fragment(R.layout.fragment_enter_code) {
    private lateinit var id: String
    private lateinit var phoneNumberRegex: String
    private val mEnterCodeFragment by viewBinding(FragmentEnterCodeBinding::bind)
    private val mViewModel: MainViewModel by viewModels()
    override fun onStart() {
        super.onStart()
        val phoneNumber = arguments?.getString("her")
        id = arguments?.getString("hui").toString()
        phoneNumberRegex = phoneNumber?.replace(Regex("[\\s,()-]"), "").toString()
        mEnterCodeFragment.registerInputCode.addTextChangedListener(AppTextWatcher {
            val string = mEnterCodeFragment.registerInputCode.text.toString()
            if (string.length >= 6) {
                mEnterCodeFragment.progressBarEnterCode.visibility = View.VISIBLE
                if (AUTH.currentUser?.uid != null) createNodes()
                else enterCode(string)
            }
        })

        mViewModel.credential.observe(viewLifecycleOwner, { sms ->

        })
    }

    private fun createNodes() {
        if (findNavController().currentDestination?.displayName == "com.asp424.drawer:id/nav_enter_code") {
            MESSAGING_TOKEN.addOnCompleteListener(OnCompleteListener { ass ->
                if (!ass.isSuccessful) {
                    showToast(
                        "Ошибка сети, попробуйте ещё раз, через минуту"
                    )
                    mEnterCodeFragment.progressBarEnterCode.visibility = View.GONE
                    findNavController().navigate(R.id.action_nav_enter_code_to_nav_enter_phone)
                    return@OnCompleteListener
                } else {

                    val messagingToken = ass.result.toString()
                    val uid = AUTH.currentUser?.uid.toString()
                    val dateMap = mutableMapOf<String, Any>()
                    dateMap[CHILD_ID] = uid
                    dateMap[CHILD_PHONE] = phoneNumberRegex
                    dateMap[CHILD_REGISTRATION_TIMESTAMP] = ServerValue.TIMESTAMP
                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                        .addListenerForSingleValueEvent(AppValueEventListener { it_is ->
                            if (!it_is.hasChild(CHILD_USERNAME)) {
                                dateMap[CHILD_USERNAME] = uid
                            }
                            dateMap[CHILD_TOKEN] = messagingToken
                            REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumberRegex).setValue(
                                uid
                            ).addOnCanceledListener {

                            }
                                .addOnFailureListener {
                                    showToast(
                                        "Ошибка данных, попробуйте войти ещё раз, через минуту"
                                    )
                                    mEnterCodeFragment.progressBarEnterCode.visibility = View.GONE
                                    findNavController().navigate(R.id.action_nav_enter_code_to_nav_enter_phone)
                                }
                                .addOnSuccessListener {

                                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(
                                        dateMap
                                    )
                                        .addOnSuccessListener {
                                            showToast("Добро пожаловать")
                                            initUser {
                                                AppStates.updateState(AppStates.ONLINE)
                                                initHeaderDrawer(requireActivity() as MainActivity)
                                                mEnterCodeFragment.progressBarEnterCode.visibility =
                                                    View.GONE
                                                findNavController().navigate(R.id.action_nav_enter_code_to_nav_main)
                                            }

                                        }.addOnFailureListener {
                                            mEnterCodeFragment.progressBarEnterCode.visibility =
                                                View.GONE
                                            showToast(
                                                "Ошибка данных, попробуйте войти ещё раз, через минуту"
                                            )
                                            findNavController().navigate(R.id.action_nav_enter_code_to_nav_main)
                                        }
                                }
                        })
                }
            })
        }
    }

    private fun enterCode(string: String) {
        val credential = PhoneAuthProvider.getCredential(id, string)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                createNodes()
            } else {
                showToast("Введён неверный код")
                mEnterCodeFragment.progressBarEnterCode.visibility = View.GONE
            }
        }
    }
}

