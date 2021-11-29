package com.asp424.drawer.database.firebase

import android.util.Log
import androidx.lifecycle.*
import com.asp424.drawer.database.*
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import com.asp424.drawer.viewModel.Mapper

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.set

class AppFirebaseRepository {
    companion object {
        private var mListenersListMainList =
            hashMapOf<DatabaseReference, AppValueEventListener>()
        private var mListenersListSCF =
            hashMapOf<Query, AppChildEventListener>()
        private var mListenersListSCFLastMessage =
            hashMapOf<Query, AppChildEventListener>()
        private var mListenersLastMessageForMainList =
            hashMapOf<Query, AppValueEventListener>()
        private var mMapOfContactsListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
        private var mSCFListenerRef = hashMapOf<DatabaseReference, AppValueEventListener>()
    }

    fun getMainListData(ViewModel: MainViewModel) =
        CoroutineScope(Dispatchers.IO).launch {
            mListenersListMainList.clear()
            mListenersLastMessageForMainList.clear()
            val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
            val mLMainListListener = AppValueEventListener { mainList ->
                makeList(mainList.children.map {
                    it.getCommonModel() }, ViewModel)
            }
            mRefMainList.addValueEventListener(mLMainListListener)
            mListenersListMainList[mRefMainList] = mLMainListListener
        }

    fun clearMainListListeners() = CoroutineScope(Dispatchers.Main).launch {
        if (mListenersListMainList.isNotEmpty())
        mListenersListMainList.forEach {
            it.key.removeEventListener(it.value)
        }
        if (mListenersLastMessageForMainList.isNotEmpty())
        mListenersLastMessageForMainList.forEach {
            it.key.removeEventListener(it.value)
        }
    }

    private fun makeList(p: List<CommonModel>, mainListViewModel: MainViewModel) {
        val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
        val mRefUsers1 = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS)
        CoroutineScope(Dispatchers.IO).launch {
            p.forEach { model ->
                when (model.type) {
                    TYPE_CHAT -> {
                        mRefUsers.child(model.id)
                            .addListenerForSingleValueEvent(AppValueEventListener { DataSnapshot1 ->
                                mRefUsers1.child(CURRENT_UID).child(model.id)
                                    .addListenerForSingleValueEvent(AppValueEventListener { DataSnapshot11 ->
                                        startListenForChat(
                                            model.id,
                                            DataSnapshot1.getCommonModel(),
                                            DataSnapshot11.getCommonModel(),
                                            mainListViewModel
                                        )
                                    })
                            })
                    }
                    TYPE_GROUP -> {
                        REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id)
                            .addListenerForSingleValueEvent(AppValueEventListener { DataSnapshot1 ->
                                startListenersForGroup(
                                    model.id,
                                    DataSnapshot1.getCommonModel(),
                                    mainListViewModel
                                )
                            })
                    }
                }
            }
        }
    }

    private fun startListenersForGroup(
        id: String,
        newModel: CommonModel,
        mMainListViewModel: MainViewModel
    ) =
        CoroutineScope(Dispatchers.IO).launch {
            val mStatusRef = REF_DATABASE_ROOT.child(NODE_USERS).child(id)
            val mRefLastMessage = REF_DATABASE_ROOT.child(NODE_GROUPS).child(id)
                .child(NODE_MESSAGES)
                .limitToLast(1)
            val mLastMessageListener = AppValueEventListener { DataSnapshot2 ->
                val tempList =
                    DataSnapshot2.children.map { it.getCommonModel() }
                val mStatusListener = AppValueEventListener { Datasnap ->
                    val statusModel = Datasnap.getCommonModel()
                    Mapper(mMainListViewModel).getDataForGroup(newModel, tempList, statusModel)
                }
                mStatusRef.addValueEventListener(mStatusListener)
                mListenersListMainList[mStatusRef] = mStatusListener
            }
            mRefLastMessage.addValueEventListener(mLastMessageListener)
            mListenersLastMessageForMainList[mRefLastMessage] = mLastMessageListener
        }

    private fun startListenForChat(
        id: String,
        newModel: CommonModel,
        newModel1: CommonModel,
        mMainListViewModel: MainViewModel,
    ) {
        val mStatusRef = REF_DATABASE_ROOT.child(NODE_USERS).child(id)
        val mLastRefMessages =
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(id).limitToLast(1)
        val mLastMessageListener = AppValueEventListener { DataSnapshot2 ->
            val tempList = DataSnapshot2.children.map {
                it.getCommonModel()
            }
            val mStatusListener = AppValueEventListener { Datasnap ->
                val statusModel = Datasnap.getCommonModel()
                Mapper(mMainListViewModel).getDataForChat(id, newModel, newModel1, tempList, statusModel)
            }
            mStatusRef.addValueEventListener(mStatusListener)
            mListenersListMainList[mStatusRef] = mStatusListener
        }
        mLastRefMessages.addValueEventListener(mLastMessageListener)
        mListenersLastMessageForMainList[mLastRefMessages] = mLastMessageListener
    }


    fun getContacts(contactsViewModel: MainViewModel) = CoroutineScope(Dispatchers.IO).launch {
        mMapOfContactsListeners.clear()
        val mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
        val mContactsListener1 = AppValueEventListener { contacts ->
            val listContacts = contacts.children.map { it.getCommonModel() }
            listContacts.forEach { contact ->
                val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
                val mContactsListener = AppValueEventListener {
                    val model = it.getCommonModel()
                    if (model.fullname.isEmpty()) {
                        if (contact.fullname.isEmpty()) model.fullname = model.phone
                        else model.fullname = contact.fullname
                    }
                    contactsViewModel.listContact.value = model
                }
                mRefUsers.addValueEventListener(mContactsListener)
                mMapOfContactsListeners[mRefUsers] = mContactsListener
            }
        }
        mRefContacts.addValueEventListener(mContactsListener1)
        mMapOfContactsListeners[mRefContacts] = mContactsListener1
    }

    fun clearContactsListeners() = CoroutineScope(Dispatchers.Main).launch {
        if (mMapOfContactsListeners.isNotEmpty())
        mMapOfContactsListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }

    fun setValueKey(contact: CommonModel) = CoroutineScope(Dispatchers.IO).launch {
        REF_DATABASE_ROOT.child(NODE_KEY).child(CURRENT_UID)
            .child(contact.id)
            .child(CHILD_KEY).setValue("key")
    }

    fun removeValueKey(contact: CommonModel) = CoroutineScope(Dispatchers.IO).launch {
        REF_DATABASE_ROOT.child(NODE_KEY).child(CURRENT_UID)
            .child(contact.id)
            .child(CHILD_KEY).removeValue()
    }

    fun sendMessage(
        message: String,
        receivingUserID: String,
        typeText: String,
        function: () -> Unit,
    ) = CoroutineScope(Dispatchers.IO).launch {
        val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
        val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
        val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(receivingUserID).child(CURRENT_UID)
            .limitToLast(1)
            .addListenerForSingleValueEvent(AppValueEventListener { Data ->
                val tempList = Data.children.map { it.getCommonModel() }
                val mapMessage = hashMapOf<String, Any>()
                mapMessage[CHILD_FROM] = CURRENT_UID
                mapMessage[CHILD_WHO] = receivingUserID
                mapMessage[CHILD_TYPE] = typeText
                mapMessage[CHILD_TEXT] = message
                mapMessage[CHILD_ID] = messageKey.toString()
                mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
                val mapMessageRes = hashMapOf<String, Any>()
                mapMessageRes[CHILD_FROM] = CURRENT_UID
                mapMessageRes[CHILD_WHO] = receivingUserID
                mapMessageRes[CHILD_TYPE] = typeText
                mapMessageRes[CHILD_TEXT] = message
                mapMessageRes[CHILD_ID] = messageKey.toString()
                mapMessageRes[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
                if (tempList.isNullOrEmpty()) {
                    mapMessageRes[CHILD_COUNT] = 1
                } else {
                    var hui = tempList[0].counter
                    hui++
                    mapMessageRes[CHILD_COUNT] = hui
                }
                val mapDialog = hashMapOf<String, Any>()
                mapDialog["$refDialogUser/$messageKey"] = mapMessage
                mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessageRes
                REF_DATABASE_ROOT
                    .updateChildren(mapDialog)
                    .addOnSuccessListener { function() }
                    .addOnFailureListener { showToast(
                        it.message.toString()
                    ) }
            })
    }

    fun saveToMainList(id: String, type: String) = CoroutineScope(Dispatchers.IO).launch {
        val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$id"
        val refReceived = "$NODE_MAIN_LIST/$id/$CURRENT_UID"
        val mapUser = hashMapOf<String, Any>()
        val mapReceived = hashMapOf<String, Any>()
        mapUser[CHILD_ID] = id
        mapUser[CHILD_TYPE] = type
        mapReceived[CHILD_ID] = CURRENT_UID
        mapReceived[CHILD_TYPE] = type
        val commonMap = hashMapOf<String, Any>()
        commonMap[refUser] = mapUser
        commonMap[refReceived] = mapReceived
        REF_DATABASE_ROOT.updateChildren(commonMap)
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    fun getContactAfterNotification(id: String, mViewModel: MainViewModel, function: () -> Unit) =
        CoroutineScope(Dispatchers.IO).launch {
            REF_DATABASE_ROOT.child(NODE_USERS).child(id)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    val g = it.getCommonModel()
                    if (g.fullname.isEmpty()) {
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID).child(id)
                            .addListenerForSingleValueEvent(AppValueEventListener { not ->
                                val c = not.getCommonModel()
                                if (c.fullname.isEmpty()) {
                                    g.fullname = g.phone
                                    mViewModel.contact.value = g
                                    function()
                                } else {
                                    g.fullname = c.fullname
                                    mViewModel.contact.value = g
                                    function()
                                }
                            })
                    } else {
                        mViewModel.contact.value = g
                        function()
                    }
                })
        }

    fun valueWritingChat(value: Int, id: String) = CoroutineScope(Dispatchers.IO).launch {
        REF_DATABASE_ROOT.child(NODE_WRITING).child(CURRENT_UID).child(id).child(
            NODE_WRITING
        ).setValue(value)
    }

    fun createNodeWritingGroup(id: String) {
        val h = hashMapOf<String, Any>()
        h[NODE_WRITING] = 0
        h[CHILD_ID] = CURRENT_UID
        REF_DATABASE_ROOT.child(NODE_WRITING).child(id).child(CURRENT_UID).setValue(h)
    }

    fun valueWritingGroup(i: Int, id: String) {
        REF_DATABASE_ROOT.child(NODE_WRITING).child(id).child(CURRENT_UID).child(
            NODE_WRITING
        ).setValue(i)
    }

    fun writingData(id: String): MutableLiveData<Int> {
        val mWritingLRef = REF_DATABASE_ROOT.child(NODE_WRITING).child(id).child(CURRENT_UID)
        val ass = MutableLiveData<Int>()
        val mWritingListener = AppValueEventListener {
            ass.value = it.getCommonModel().writing
        }
        mWritingLRef
            .addValueEventListener(mWritingListener)
        mSCFListenerRef[mWritingLRef] = mWritingListener
        return ass
    }

    fun keyListenerForChat(id: String, singleChatViewModel: MainViewModel) = CoroutineScope(Dispatchers.IO).launch {
        mListenersListSCF.clear()
        mSCFListenerRef.clear()
        val mRefKey =
            REF_DATABASE_ROOT.child(NODE_KEY).child(id).child(CURRENT_UID).child(CHILD_KEY)
        val mListenerCrypt = AppValueEventListener {
            singleChatViewModel.keyFlag.value = it.value as String?
        }
        mRefKey.addValueEventListener(mListenerCrypt)
        mSCFListenerRef[mRefKey] = mListenerCrypt
    }

    fun getMessageKey(id: String): String {
        return REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
            .child(id).push().key.toString()
    }

    fun getRead(who: String, id: String): MutableLiveData<Int> {
        val mP = MutableLiveData<Int>()
        val mRefRead =  REF_DATABASE_ROOT.child(NODE_MESSAGES).child(who).child(CURRENT_UID)
            .child(id)
        val mReadListener = AppValueEventListener {
            mP.value = it.getCommonModel().counter
        }
        mRefRead.addValueEventListener(mReadListener)
        mSCFListenerRef[mRefRead] = mReadListener
        return mP
    }

    fun setValueRead(from: String, id: String, value: Int) {
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(from)
            .child(id).child(CHILD_COUNT).setValue(value)
    }

    fun removeMessage(from: String, id: String, onSuccess: () -> Unit) {
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(from).child(id)
            .removeValue()
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(from).child(CURRENT_UID).child(id)
            .removeValue()
        onSuccess()
    }

    fun getDataForRecyclerViewSingleChat(
        id: String,
        mCountMessages: Int,
        mSingleChatViewModel: MainViewModel
    ) =
        CoroutineScope(Dispatchers.IO).launch {
            val mRefMessages = REF_DATABASE_ROOT
                .child(NODE_MESSAGES)
                .child(CURRENT_UID)
                .child(id).limitToLast(mCountMessages)
            val mMessagesListener = AppChildEventListener {
                mSingleChatViewModel._message.value = it.getCommonModel()

            }
            mRefMessages.addChildEventListener(mMessagesListener)
            mListenersListSCF[mRefMessages] = mMessagesListener
        }
     fun startLastMessageListenersForSCF(
        id: String,
        mMainViewModel: MainViewModel
    ) =
         CoroutineScope(Dispatchers.IO).launch {
             val mRefMessages = REF_DATABASE_ROOT
                 .child(NODE_MESSAGES)
                 .child(CURRENT_UID)
                 .child(id).limitToLast(1)
             val mMessagesListener = AppChildEventListener {
                 mMainViewModel._lastMessage.value = it.getCommonModel()
             }
             mRefMessages.addChildEventListener(mMessagesListener)
             mListenersListSCFLastMessage[mRefMessages] = mMessagesListener
        }
    fun clearChildListenersSCF() = CoroutineScope(Dispatchers.IO).launch {
        if (mListenersListSCF.isNotEmpty())
        mListenersListSCF.forEach {
            it.key.removeEventListener(it.value)
        }
    }
fun clearSCFListeners() = CoroutineScope(Dispatchers.Main).launch{
    if (mListenersListSCFLastMessage.isNotEmpty())
        mListenersListSCFLastMessage.forEach {
            it.key.removeEventListener(it.value)
        }
    if (mSCFListenerRef.isNotEmpty())
        mSCFListenerRef.forEach {
            it.key.removeEventListener(it.value)
        }
    if (mListenersListSCF.isNotEmpty())
        mListenersListSCF.forEach {
            it.key.removeEventListener(it.value)
        }
}
    fun sCFToolbarListener(id: String, mViewModel: MainViewModel) =
        CoroutineScope(Dispatchers.IO).launch {
            val mListenerInfoToolbar = AppValueEventListener {
                val mReceivingUser = it.getCommonModel()
                mViewModel.recivingUserToolbar.postValue(mReceivingUser)
            }
            val mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(id)
            mRefUser.addValueEventListener(mListenerInfoToolbar)
            mSCFListenerRef[mRefUser] = mListenerInfoToolbar
        }
}

