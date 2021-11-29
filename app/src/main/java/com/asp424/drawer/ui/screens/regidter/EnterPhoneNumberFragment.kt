package com.asp424.drawer.ui.screens.regidter


import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.database.*
import com.asp424.drawer.databinding.FragmentEnterPhoneNumberBinding
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.ServerValue
import java.util.concurrent.TimeUnit

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {
    private lateinit var mPhoneNumber: String
    private lateinit var mPhoneNumberRegex: String
    private val mEnterPhoneFragment by viewBinding(FragmentEnterPhoneNumberBinding::bind)
    private val mViewModel = ViewModelProvider(APP_ACTIVITY).get(MainViewModel::class.java)
    override fun onResume() {
        super.onResume()
        mEnterPhoneFragment.registerInputCode.visibility = View.INVISIBLE
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).navigationIcon = null
        if (check_permissions((WRITE_FILES), this, activity as MainActivity) && check_permissions(
                (CAMERA),
                this,
                activity as MainActivity
            ) && check_permissions(
                (RECORD_AUDIO), this, activity as MainActivity
            ) && check_permissions((READ_CONTACTS), this, activity as MainActivity)
        ) {
            mEnterPhoneFragment.registerBtnNext.setOnClickListener {
                mEnterPhoneFragment.progressBarEnterCode.visibility = View.VISIBLE
                sendCode()
            }
            mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    showToast(
                        "Авторизация не удалась, попробуйте позже" + "\n" + p0.message
                    )
                    mEnterPhoneFragment.progressBarEnterCode.visibility = View.INVISIBLE

                }

                override fun onCodeSent(
                    id: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    /*  val bundle = Bundle()
                    bundle.putString("her", mPhoneNumber)
                    bundle.putString("hui", id)
                    if (findNavController().currentDestination?.displayName == "com.asp424.drawer:id/nav_enter_phone")
                        findNavController().navigate(
                            R.id.action_nav_enter_phone_to_nav_enter_code,
                            bundle
                        )*/
                    requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).title =
                        "Введите код"
                    mEnterPhoneFragment.text.visibility = View.VISIBLE
                    mEnterPhoneFragment.registerInputPhoneNumber.visibility = View.INVISIBLE
                    mEnterPhoneFragment.progressBarEnterCode.visibility = View.VISIBLE
                    mEnterPhoneFragment.registerInputCode.visibility = View.VISIBLE
                    mEnterPhoneFragment.registerInputCode.addTextChangedListener(AppTextWatcher {
                        val string = mEnterPhoneFragment.registerInputCode.text.toString()
                        if (string.length >= 6) {
                            mEnterPhoneFragment.progressBarEnterCode.visibility = View.VISIBLE
                            if (AUTH.currentUser?.uid == null)
                            enterCode(string, id)
                        }
                    })

                    mViewModel.credential.observe(viewLifecycleOwner, { credential ->
                        mEnterPhoneFragment.registerInputCode.setText(credential.smsCode.toString())
                    })
                }
            }
        }
    }

    private fun sendCode() {
        if (mEnterPhoneFragment.registerInputPhoneNumber.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber = mEnterPhoneFragment.registerInputPhoneNumber.text.toString()
        mPhoneNumberRegex = mPhoneNumber.replace(Regex("[\\s,()-]"), "")
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(mPhoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity as MainActivity)                 // Activity (for callback binding)
            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun createNodes(mPhoneNumber: String) {

        MESSAGING_TOKEN.addOnCompleteListener(OnCompleteListener { ass ->
            if (!ass.isSuccessful) {
                showToast(
                    "Ошибка сети, попробуйте ещё раз, через минуту"
                )
                mEnterPhoneFragment.progressBarEnterCode.visibility = View.GONE
                findNavController().navigate(R.id.action_nav_enter_code_to_nav_enter_phone)
                return@OnCompleteListener
            } else {
                val phoneNumberRegex = mPhoneNumber.replace(Regex("[\\s,()-]"), "").toString()
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
                                mEnterPhoneFragment.progressBarEnterCode.visibility = View.GONE

                            }
                            .addOnSuccessListener {

                                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(
                                    dateMap
                                )
                                    .addOnSuccessListener {
                                        showToast("Добро пожаловать")
                                        initUser {
                                            initContacts(APP_ACTIVITY)
                                            AppStates.updateState(AppStates.ONLINE)
                                            initHeaderDrawer(requireActivity() as MainActivity)
                                            mEnterPhoneFragment.progressBarEnterCode.visibility =
                                                View.GONE
                                            findNavController().navigate(R.id.action_nav_enter_phone_to_nav_main)
                                        }

                                    }.addOnFailureListener {
                                        mEnterPhoneFragment.progressBarEnterCode.visibility =
                                            View.GONE
                                        showToast(
                                            "Ошибка данных, попробуйте войти ещё раз, через минуту"
                                        )

                                    }
                            }
                    })
            }
        })

    }

    private fun enterCode(string: String, id: String) {
        val credential = PhoneAuthProvider.getCredential(id, string)
        AUTH.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                createNodes(mPhoneNumber)
            } else {
                showToast("Введён неверный код")
                mEnterPhoneFragment.progressBarEnterCode.visibility = View.GONE
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        AUTH.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mViewModel.credential.value = credential
                    createNodes(mPhoneNumber)
                } else {
                    showToast("Введён неверный код")
                    mEnterPhoneFragment.progressBarEnterCode.visibility = View.GONE
                }
            }
    }
}