package com.asp424.drawer.ui.screens.groups

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.AbsListView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.asp424.drawer.R
import com.asp424.drawer.database.*
import com.asp424.drawer.databinding.ChouseUploadBinding
import com.asp424.drawer.databinding.FragmentSingleChatBinding
import com.asp424.drawer.databinding.ToolbarGroupInfoBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.models.UserModel
import com.asp424.drawer.ui.message_recycler_view.views.AppViewsFactory
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class GroupChatFragment :
    Fragment() {
    private lateinit var mAppVoicePlayer: AppVoicePlayer
    private lateinit var mToolbarInfo: View
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: CommonModel
    private lateinit var mReceivingUserStatus: UserModel
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mRefWriting: DatabaseReference
    private lateinit var mRefWriter: DatabaseReference
    private lateinit var mAdapter: GroupChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private lateinit var mMessagesListener1: AppChildEventListener
    private lateinit var mWritingListener: AppValueEventListener
    private var mCountMessages = 10
    private var mIsScrolling = false
    private lateinit var mImagePath: Uri
    private lateinit var mPhotoUri: Uri
    private lateinit var currentPhotoPath: String
    private lateinit var mFilename: String
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var group: CommonModel
    private lateinit var mBottomShit: View
    private var mGroupChatFragment: FragmentSingleChatBinding? = null
    private val binding get() = mGroupChatFragment
    private val mViewModel = ViewModelProvider(APP_ACTIVITY).get(MainViewModel::class.java)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        mGroupChatFragment = FragmentSingleChatBinding.inflate(inflater, container, false)
        return binding?.root
    }

    private var mToolbarBinding: ToolbarGroupInfoBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mGroupChatFragment = FragmentSingleChatBinding.bind(view)

    }

    override fun onStart() {
        super.onStart()
        mRecycler = requireActivity().findViewById<RecyclerView>(R.id....)
        val mInflater = LayoutInflater.from(APP_ACTIVITY)
        mToolbarInfo = APP_ACTIVITY.toolbar
        mToolbarInfo = mInflater.inflate(
            R.layout.toolbar_group_info,
            mToolbarInfo as Toolbar
        )
        mToolbarBinding = ToolbarGroupInfoBinding.bind(mToolbarInfo)
    }

    override fun onResume() {
        super.onResume()
        mAppVoicePlayer = AppVoicePlayer()
        mAppVoicePlayer.init()
        initFields()
        initRecyclerView()
        initToolBar()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        setHasOptionsMenu(true)
        mBottomShit = APP_ACTIVITY.findViewById(R.id.bottom_sheet_choice)
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomShit)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = mGroupChatFragment!!.charSwipeRefresh
        mLayoutManager = LinearLayoutManager(this.context)
        mGroupChatFragment?.chatInputMessage?.addTextChangedListener(AppTextWatcher {
            val string = mGroupChatFragment?.chatInputMessage?.text.toString()
            if (string.isEmpty() || string == "     Идёт запись голоса...") {
                mGroupChatFragment?.chatBtnSendMessage?.visibility = View.GONE
                mGroupChatFragment?.chatBtnAttach?.visibility = View.VISIBLE
                mGroupChatFragment?.chatBtnVoice?.visibility = View.VISIBLE
            } else {
                mGroupChatFragment?.chatBtnSendMessage?.visibility = View.VISIBLE
                mGroupChatFragment?.chatBtnAttach?.visibility = View.GONE
                mGroupChatFragment?.chatBtnVoice?.visibility = View.GONE
            }
        })
        mViewModel.createNodeWritingGroup(group.id)
        mGroupChatFragment?.chatInputMessage?.addTextChangedListener(AppTextWatcher {
            val string = mGroupChatFragment!!.chatInputMessage.text.toString()
            if (string.isNotEmpty()) {
                mViewModel.valueWriting(1, group.id)
            }
            if (string.isEmpty()) {
                mViewModel.valueWriting(0, group.id)
            }
        })
        //mGroupChatFragment?.chatBtnAttach?.setOnClickListener { attach() }
        CoroutineScope(Dispatchers.IO).launch {
            mGroupChatFragment?.chatBtnVoice?.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    mGroupChatFragment?.chatInputMessage?.setText("     Идёт запись голоса...")
                    mGroupChatFragment!!.chatBtnVoice.setColorFilter(
                        ContextCompat.getColor(
                            APP_ACTIVITY,
                            R.color.md_red_400
                        )
                    )
                    val messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
                        .child(group.id).push().key.toString()
                    mAppVoiceRecorder.startRecord(messageKey)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    //TODO stop record
                    mGroupChatFragment?.chatInputMessage?.setText("")
                    mGroupChatFragment!!.chatBtnVoice.colorFilter = null
                    mAppVoiceRecorder.stopRecord { file, messageKey ->
                        uploadFileToStorageToGroup(
                            Uri.fromFile(file),
                            messageKey,
                            TYPE_MESSAGE_VOICE,
                            group.id
                        )
                    }
                }
                true
            }
        }
    }

    fun writingJob() {
        mRefWriting = REF_DATABASE_ROOT.child(NODE_WRITING).child(group.id)
        mWritingListener = AppValueEventListener { Data ->
            val her = Data.children.map { it.getCommonModel() }
            her.forEach { hui ->
                mRefWriter = REF_DATABASE_ROOT.child(NODE_USERS).child(hui.id)
                mRefWriter.addListenerForSingleValueEvent(AppValueEventListener {
                    val p = it.getCommonModel().fullname
                    REF_DATABASE_ROOT.child(NODE_WRITING).child(group.id).child(hui.id)
                        .addValueEventListener(AppValueEventListener {
                            val get = it.getCommonModel()
                            deleteAllChips(mGroupChatFragment?.chipWriting)
                            if (get.writing == 1 && mGroupChatFragment?.chipWriting != null && hui.id != CURRENT_UID) {
                                addChip("$p печатает...", mGroupChatFragment?.chipWriting)
                            } else {
                                if (get.writing == 0 && mGroupChatFragment?.chipWriting != null) {
                                    deleteChip("$p печатает...", mGroupChatFragment?.chipWriting)
                                }
                            }

                        })
                })
            }
        }
        mRefWriting.addValueEventListener(mWritingListener)

    }

    //private fun attach() {
        //val bindingContactItem = ChouseUploadBinding.bind(mBottomShit)
        //mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        //bindingContactItem.btnAttachFile.setOnClickListener { attachFile() }
        //bindingContactItem.btnAttachImage.setOnClickListener { startImageChooser() }
      //  bindingContactItem.btnAttachCamera.setOnClickListener { startCameraChooser() }
    //}

    fun startImageChooser() {
        val i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_PICK)
        startActivityForResult(Intent.createChooser(i, "Выбери фото"), 111)
    }

    fun startCameraChooser() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        if (photoFile != null) {
            mPhotoUri = FileProvider.getUriForFile(
                APP_ACTIVITY,
                "com.asp424.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                mPhotoUri
            )

            startActivityForResult(
                takePictureIntent,
                112
            )
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            mFilename = name
            currentPhotoPath = absolutePath
        }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun initRecyclerView() = CoroutineScope(Dispatchers.Main).launch {
        launch {
            delay(400L)
            init()

        }
    }

    private fun init() {
        mRecyclerView = mGroupChatFragment?.chatRecycleView!!
        mAdapter = GroupChatAdapter(mViewModel)
        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_GROUPS)
            .child(group.id)
            .child(NODE_MESSAGES)

        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager
        mLayoutManager.stackFromEnd = true
        mGroupChatFragment?.addContactsBtnToBottom?.setOnClickListener {
            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
            mGroupChatFragment?.addContactsBtnToBottom?.visibility = View.GONE
            mRefMessages.removeEventListener(mMessagesListener1)
            mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        }
        mMessagesListener1 = AppChildEventListener {
            val message = it.getCommonModel()
            val currentTimestamp = System.currentTimeMillis().toString().asTime()
            val messTime = message.timeStamp.toString().asTime()

            if (messTime == currentTimestamp) {
                mAdapter.addItemToBottom(AppViewsFactory.getView(message)) {}
                mGroupChatFragment?.addContactsBtnToBottom?.visibility = View.VISIBLE
            } else {
                mAdapter.addItemToTop(AppViewsFactory.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }
        mMessagesListener = AppChildEventListener {
            mRefMessages.removeEventListener(mMessagesListener1)
            val message = it.getCommonModel()
            mAdapter.addItemToBottom(AppViewsFactory.getView(message))
            {
                mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
            }
        }
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0) {
                    if (mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                        updateData()
                    }
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener {
            updateData()
        }

    }

    private fun updateData() {
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener1)
        mGroupChatFragment?.addContactsBtnToBottom?.visibility = View.GONE
    }

    private fun initToolBar() {
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getCommonModel()
            mReceivingUserStatus = it.getUserModel()
            initInfoToolbar()
        }
        mRefUser = REF_DATABASE_ROOT.child(NODE_GROUPS).child(group.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        mGroupChatFragment?.chatBtnSendMessageEng?.setOnClickListener {
            mRefMessages.removeEventListener(mMessagesListener1)
            mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
            val message = mGroupChatFragment?.chatInputMessage?.text.toString()

            if (message.isEmpty()) {
                showToast("Введи сообщение")
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    mGroupChatFragment?.chatInputMessage?.setText("Идёт перевод...")
                    val text: String = microsoftApi(message)
                    mAppVoicePlayer.playSound()
                    mViewModel.valueWriting(0, group.id)
                    sendMessageToGroup(
                        text,
                        group.id,
                        TYPE_MESSAGE_TEXT
                    ) {
                        mGroupChatFragment?.chatInputMessage?.setText("")

                    }
                }
            }
        }
        mGroupChatFragment?.chatBtnSendMessage?.setOnClickListener {
            mRefMessages.removeEventListener(mMessagesListener1)
            mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
            val message = mGroupChatFragment?.chatInputMessage?.text.toString()
            if (message.isEmpty()) {
                showToast("Введи сообщение")
            } else {
                mAppVoicePlayer.playSound()
                mViewModel.valueWriting(0, group.id)
                sendMessageToGroup(
                    message,
                    group.id,
                    TYPE_MESSAGE_TEXT
                ) {
                    mGroupChatFragment?.chatInputMessage?.setText("")

                }
            }
        }
    }

    private fun initInfoToolbar() {
        var count = 0
        REF_DATABASE_ROOT.child(NODE_GROUPS).child(group.id).child(NODE_MEMBERS)
            .addListenerForSingleValueEvent(AppValueEventListener {

                it.children.forEach {
                    count++
                }
                val u = "Участников: $count"
                mToolbarBinding?.toolbarGroupUsers?.text = u
            })
        val p = group.fullname + " (группа)"
        mToolbarBinding?.toolbarGroupFullname?.text = p
        mToolbarBinding?.toolbarGroupImage?.downloadAndSetImage(mReceivingUser.photoUrl)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            112 -> {
                mRefMessages.removeEventListener(mMessagesListener1)
                mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
                mGroupChatFragment?.addContactsBtnToBottom?.visibility = View.GONE
                val filename = mFilename
                val messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
                    .child(group.id).push().key.toString()
                uploadFileToStorageToGroup(
                    mPhotoUri,
                    messageKey,
                    TYPE_MESSAGE_IMAGE,
                    group.id,
                    currentPhotoPath, filename
                )
                mRefMessages.removeEventListener(mMessagesListener1)
                mRefMessages.limitToLast(mCountMessages)
                    .addChildEventListener(mMessagesListener)
            }
            111 -> {
                mRefMessages.removeEventListener(mMessagesListener1)
                mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
                mGroupChatFragment?.addContactsBtnToBottom?.visibility = View.GONE
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mImagePath = data.data!!
                    val path = getRealPathFromURI(mImagePath)
                    if (path != "") {
                        val messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(
                            CURRENT_UID
                        )
                            .child(group.id).push().key.toString()
                        val filename = getFileNameFromUri(mImagePath)
                        mRefMessages.removeEventListener(mMessagesListener1)
                        mRefMessages.limitToLast(mCountMessages)
                            .addChildEventListener(mMessagesListener)
                        uploadFileToStorageToGroup(
                            mImagePath,
                            messageKey,
                            TYPE_MESSAGE_IMAGE,
                            group.id,
                            path, filename
                        )
                    }
                }
            }
            PICK_FILE_REQUEST_CODE -> {
                val uri = data?.data
                val messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
                    .child(group.id).push().key.toString()
                val filename = getFileNameFromUri(uri!!)
                uploadFileToStorageToGroup(
                    uri,
                    messageKey,
                    TYPE_MESSAGE_FILE,
                    group.id,
                    filename
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.removeEventListener(mMessagesListener1)
        mRefMessages.limitToLast(mCountMessages).removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mGroupChatFragment = null
        mToolbarBinding = null
        mAdapter.destroy()
        mViewModel.valueWriting(0, group.id)
    }

    override fun onStop() {
        super.onStop()
        val u = hashMapOf<View, ViewGroup>()
        APP_ACTIVITY.toolbar.forEach {
            if (it.id != -1) {
                u[it] = APP_ACTIVITY.toolbar
            }
        }
        u.forEach {
            it.value.removeView(it.key)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        REF_DATABASE_ROOT.child(NODE_GROUPS).child(group.id).child(NODE_MEMBERS).child(CURRENT_UID)
            .addListenerForSingleValueEvent(AppValueEventListener {
                val mMemberStatus = it.getValue().toString()

                if (mMemberStatus == USER_CREATOR) {
                    activity?.menuInflater?.inflate(R.menu.group_chat_action_menu_creator, menu)
                } else {
                    activity?.menuInflater?.inflate(R.menu.group_chat_action_menu_member, menu)
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_group_chat_member -> leaveGroupChat(group.id) {
                showToast("Ты ушёл из группы")

                APP_ACTIVITY.navController.navigate(R.id.action_nav_group_to_nav_main)
            }
            R.id.menu_delete_group_chat_creator -> deleteGroupChat(group.id) {
                showToast("Группа удалена")

                APP_ACTIVITY.navController.navigate(R.id.action_nav_group_to_nav_main)
            }
            R.id.menu_delete_my_messages_group_chat_creator -> clearGroupChat(group.id) {
                showToast("Группа была очищена создателем")

                APP_ACTIVITY.navController.navigate(R.id.action_nav_group_to_nav_main)
            }
        }

        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_nav_group_to_nav_main)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }
}


