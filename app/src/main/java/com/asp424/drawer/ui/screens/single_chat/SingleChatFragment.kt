package com.asp424.drawer.ui.screens.single_chat


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.databinding.ChouseUploadBinding
import com.asp424.drawer.databinding.FragmentSingleChatBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.set

class SingleChatFragment :
    Fragment(R.layout.fragment_single_chat) {
    private var mSingleChatFragment: FragmentSingleChatBinding? = null
    private val binding get() = checkNotNull(mSingleChatFragment)
    private lateinit var mViewModel: MainViewModel
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var mContact: CommonModel
    private lateinit var mBottomShit: View
    private lateinit var mIntents: Intents
    private lateinit var mWindows: Windows
    private lateinit var mLauncherIntent: ActivityResultLauncher<Intent>
    private lateinit var mObserverRecycler: LifecycleObserver
    private lateinit var mObserverToolbar: LifecycleObserver
    private var mChooserIntent: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mLauncherIntent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { fuckingResult ->
                when (mChooserIntent) {
                    1 -> {
                        mIntents.cameraIntent(fuckingResult, mSingleChatFragment!!, mContact.id)
                    }
                    2 -> {
                        mIntents.imageIntent(fuckingResult, mSingleChatFragment!!, mContact.id)
                    }
                   /* 3 -> {
                        mIntents.fileIntent(result, mContact.id)
                    }*/
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mSingleChatFragment = FragmentSingleChatBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(APP_ACTIVITY).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContact = mViewModel.contact.value!!
        mObserverRecycler = RecyclerViewJob(
            mViewModel,
            mSingleChatFragment,
            requireContext(),
            mContact,
            activity as MainActivity, lifecycleScope,
            lifecycle
        )
        lifecycle.addObserver(mObserverRecycler)
        mObserverToolbar = InitToolbar(
            mViewModel,
            activity as MainActivity,
            viewLifecycleOwner,
            mContact
        )
        lifecycle.addObserver(mObserverToolbar)
    }

    override fun onResume() {
        super.onResume()
        initFields()

    }

    private fun initFields() = CoroutineScope(Dispatchers.Unconfined).launch {
        mContact = mViewModel.contact.value!!
        mChooserIntent = 0
        mIntents = Intents(mViewModel, activity as MainActivity)
        mWindows = Windows(
            mViewModel, activity as MainActivity,
            viewLifecycleOwner
        )
        mWindows.initWindows(mContact)
        SendMessage(mViewModel).sendJob(mContact, mSingleChatFragment, activity as MainActivity)
        WritingJob(
            mContact.id,
            mViewModel, activity as MainActivity, viewLifecycleOwner
        )
        setHasOptionsMenu(true)
        mToolbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        mBottomShit = requireView().findViewById(R.id.bottom_sheet_choice)
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomShit)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mSingleChatFragment?.chatBtnAttach?.setOnClickListener { attach() }
        Voice(mSingleChatFragment, mViewModel, mContact).voiceJob()
    }

    private fun attach() = CoroutineScope(Dispatchers.Unconfined).launch {
        val bindingContactItem = ChouseUploadBinding.bind(mBottomShit)
        bindingContactItem.apply {
            mIntents.apply {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                btnAttachCamera.setOnClickListener {
                    mChooserIntent = 1
                    startCameraChooser(mLauncherIntent)
                }
                btnAttachImage.setOnClickListener {
                    mChooserIntent = 2
                    startImageChooser(mLauncherIntent)
                }
                /*btnAttachFile.setOnClickListener {
                    mChooserIntent = 3
                    attachFile(mLauncherIntent)
                }*/
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        CoroutineScope(Dispatchers.Main).launch {
            val u = hashMapOf<View, ViewGroup>()
            mToolbar.forEach {
                if (it.id != -1) {
                    u[it] = mToolbar
                }
            }
            u.forEach {
                it.value.removeView(it.key)
            }
        }
        lifecycle.removeObserver(mObserverRecycler)
        lifecycle.removeObserver(mObserverToolbar)
        mSingleChatFragment = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mWindows.menu(item, mContact)
        return true
    }
}