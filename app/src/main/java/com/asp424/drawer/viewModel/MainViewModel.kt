package com.asp424.drawer.viewModel

import androidx.lifecycle.*
import com.asp424.drawer.database.firebase.AppFirebaseRepository
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.AppVoicePlayer
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val mFBRepo = AppFirebaseRepository()

    //MainList

    var _modelItem = MutableStateFlow(CommonModel(id = "1"))
    val modelItem: StateFlow<CommonModel> = _modelItem.asStateFlow()


    fun getMainListData() = viewModelScope.launch {
        mFBRepo.getMainListData(this@MainViewModel)
    }

    fun clearMainListListeners() = viewModelScope.launch {
        _modelItem.value = CommonModel(id = "1")
        mFBRepo.clearMainListListeners()
    }

    fun saveToMainList(id: String, typeMessage: String) {
        mFBRepo.saveToMainList(id, typeMessage)
    }


    //SCF

   var _message = MutableStateFlow(CommonModel(id = "1"))
    val message: StateFlow<CommonModel> = _message.asStateFlow()
   var _lastMessage = MutableStateFlow(CommonModel(id = "1"))
    val lastMessage: StateFlow<CommonModel> = _lastMessage.asStateFlow()
    val recivingUserToolbar = MutableLiveData<CommonModel>()

    fun getRecyclerData(id: String, mCountMessages: Int) = viewModelScope.launch {
        mFBRepo.getDataForRecyclerViewSingleChat(id, mCountMessages, this@MainViewModel)
        delay(1000L)
        mFBRepo.clearChildListenersSCF()
    }

    fun getRecyclerDataSCFLastMessage(id: String) = viewModelScope.launch {
        mFBRepo.startLastMessageListenersForSCF(id, this@MainViewModel)
    }

    fun clearSCFListener() = viewModelScope.launch {
        _lastMessage.value.id = "1"
        _message.value.id = "1"
        mFBRepo.clearSCFListeners()
    }

    fun toolbarListenerSCF(id: String) = viewModelScope.launch {
        recivingUserToolbar.value = CommonModel(id = "1")
        mFBRepo.sCFToolbarListener(id, this@MainViewModel)
    }


    //Contacts
    val listContact = MutableLiveData<CommonModel>()

    fun getContacts() {
        mFBRepo.getContacts(this)

    }

    fun clearContactsListeners() {
        mFBRepo.clearContactsListeners()
    }

    fun valueWritingGroup(i: Int, id: String) {

        mFBRepo.valueWritingGroup(i, id)
    }

    //CreateGroup

    var listContacts: MutableLiveData<MutableList<CommonModel>> = MutableLiveData()


    //Notification

    fun getContaktForUfterNotif(id: String, function: () -> Unit) {
        mFBRepo.getContactAfterNotification(id, this) {
            function()
        }
    }


    //BeetwinMainListAdapter and SCF (GCF)

    val contact: MutableLiveData<CommonModel> by lazy {
        MutableLiveData<CommonModel>()
    }

    //Writing

    fun valueWriting(value: Int, id: String) {
        mFBRepo.valueWritingChat(value, id)
    }

    fun getWritingInt(id: String): MutableLiveData<Int> {
        val h = mFBRepo.writingData(id)
        return h
    }

    fun createNodeWritingGroup(id: String) {
        mFBRepo.createNodeWritingGroup(id)
    }

    //Keys
    val keyFlag = MutableLiveData<String>()

    fun setValueKey(contact: CommonModel) {
        mFBRepo.setValueKey(contact)
    }

    fun removeValueKey(contact: CommonModel) {
        mFBRepo.removeValueKey(contact)
    }

    fun kryptKeys(id: String) {
        mFBRepo.keyListenerForChat(id, this)

    }


    //ReadJob


    fun getReaded(who: String, id: String): MutableLiveData<Int> {
        val k = mFBRepo.getRead(who, id)
        return k
    }

    fun setValueReaded(from: String, id: String, value: Int) {
        mFBRepo.setValueRead(from, id, value)
    }


    //Service


    fun sendMessage(text: String, id: String, typeMessage: String, function: () -> Unit) {
        mFBRepo.sendMessage(text, id, typeMessage) {
            function()
        }
    }

    fun removeMessage(from: String, id: String, function: () -> Unit) {
        mFBRepo.removeMessage(from, id) {
            function()
        }
    }

    fun getMessageKey(id: String): String {
        val h = mFBRepo.getMessageKey(id)
        return h
    }


    //Registration


    var credential = MutableLiveData<PhoneAuthCredential>()


    //voice

    val idVoice = MutableLiveData<String>()
    val flag = MutableLiveData(0)
    val player = MutableLiveData<AppVoicePlayer>()
    val hashMap = MutableLiveData(hashMapOf<String, AppVoicePlayer>())
}