package com.asp424.drawer.database

import android.net.Uri
import android.util.Log
import android.view.View
import com.asp424.drawer.R
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.models.UserModel
import com.asp424.drawer.ui.message_recycler_view.views.MessageView
import com.asp424.drawer.utilites.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import java.io.File
import java.io.FileInputStream
import java.util.*
inline fun initUser(crossinline function: () -> Unit) {
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}
inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            Log.d("My", it.message.toString())
            showToast(it.message.toString())

        }
}

fun updatePhonesToDatabase(listContacts: MutableList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES)
            .addListenerForSingleValueEvent(AppValueEventListener { DataSnapshot ->
                DataSnapshot.children.forEach { snapshot ->
                    listContacts.forEach { contact ->
                        if (snapshot.key == contact.phone) {
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                                .child(snapshot.value.toString()).child(CHILD_ID)
                                .setValue(snapshot.value.toString())
                                .addOnFailureListener { showToast(
                                    it.message.toString()
                                ) }
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                                .child(snapshot.value.toString()).child(CHILD_FULLNAME)
                                .setValue(contact.fullname)
                                .addOnFailureListener { showToast(
                                    it.message.toString()
                                ) }
                        }
                    }
                }
            })
    }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun updateCurrentUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
        .setValue(newUserName)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            deleteOldUsernames(newUserName)
        }
}
private fun deleteOldUsernames(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES)
        .child(USER.username)
        .removeValue()
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            APP_ACTIVITY.navController.navigate(R.id.action_nav_change_username_to_nav_settings)
            USER.username = newUserName
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun setNameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname).addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            USER.fullname = fullname
            APP_ACTIVITY.navController.navigate(R.id.action_nav_change_name_to_nav_settings)
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun getFileFromStorage(
    mFile: File,
    fileUrl: String,
    btn_received: View,
    progressBar_received: View,
    function: () -> Unit,
) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.metadata.addOnSuccessListener {
        mFile.createNewFile()
        path.getFile(mFile)
            .addOnSuccessListener {
                function()
                path.delete()
            }
            .addOnFailureListener {
                showToast(it.message.toString())
                function()
            }
    }.addOnFailureListener {
        btn_received.visibility = View.VISIBLE
        progressBar_received.visibility = View.INVISIBLE
        showToast("Файл удалён с сервера")
    }
}
fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}
fun getVoiceFromStorage(mFile: File, view: MessageView, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(view.fileUrl)
    path.metadata.addOnSuccessListener {
        mFile.createNewFile()
        path.getFile(mFile)
            .addOnSuccessListener {
                function()
                if (view.text != "many")
                    path.delete()
            }
            .addOnFailureListener {
                showToast(it.message.toString())
                function()
            }
    }.addOnFailureListener {
        showToast("Файл удалён с сервера")
    }
}

fun getImageFromStorage(
    mFile: File,
    view: MessageView,
    block_received: View,
    function: () -> Unit,
) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(view.fileUrl)
    path.metadata.addOnSuccessListener {
        mFile.createNewFile()
        path.getFile(mFile)
            .addOnSuccessListener {
                if (view.text != TYPE_MANY)
                    path.delete()
                function()
            }
            .addOnFailureListener {
                showToast(it.message.toString())
                function()
            }
    }.addOnFailureListener {
        block_received.visibility = View.INVISIBLE
    }
}

fun updateStateExit() {
    val uid = AUTH.currentUser?.uid.toString()
    val dateMap = mutableMapOf<String, Any>()
    dateMap[CHILD_EXIT_TIMESTAMP] = ServerValue.TIMESTAMP
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
    }
}

fun deleteChat(id: String, function: () -> Unit) = CoroutineScope(Dispatchers.IO).launch{
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}

fun deleteGroupChat(id: String, function: () -> Unit) {
    val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { DataSnapshot ->
        DataSnapshot.children.forEach { DataSnapshot1 ->
            val l = DataSnapshot1.key.toString()
            DataSnapshot1.children.forEach {
                val h = it.key.toString()
                val map = it.getCommonModel()
                if (h == id) {
                    if (map.type == TYPE_GROUP) {
                        mRefMainList.child(l).child(h).removeValue()
                    }
                }
            }
        }
    })
    REF_DATABASE_ROOT.child(NODE_GROUPS).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}

fun clearSingleChat(id: String, function: () -> Unit) = CoroutineScope(Dispatchers.IO).launch{
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(CURRENT_UID).removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
}

fun leaveGroupChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
    REF_DATABASE_ROOT.child(NODE_GROUPS).child(id).child(NODE_MEMBERS).child(CURRENT_UID)
        .removeValue()
}

fun clearGroupChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_GROUPS).child(id).child(NODE_MESSAGES).removeValue()
        .addOnFailureListener { }
        .addOnSuccessListener { function() }
}

fun createGroupToDatabase(
    nameGroup: String,
    uri: Uri,
    listContacts: List<CommonModel>,
    function: () -> Unit,
) {
    val keyGroup = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val pathStorage = REF_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(keyGroup)
    val mapData = hashMapOf<String, Any>()
    mapData[CHILD_ID] = keyGroup
    mapData[CHILD_FULLNAME] = nameGroup
    mapData[CHILD_PHOTO_URL] = "empty"
    val mapMembers = hashMapOf<String, Any>()
    listContacts.forEach {
        mapMembers[it.id] = USER_MEMBER
    }
    mapMembers[CURRENT_UID] = USER_CREATOR
    mapData[NODE_MEMBERS] = mapMembers
    path.updateChildren(mapData)
        .addOnSuccessListener {
            if (uri != Uri.EMPTY) {
                putFileToStorage(uri, pathStorage) {
                    getUrlFromStorage(pathStorage) {
                        path.child(CHILD_PHOTO_URL).setValue(it)
                        addGroupToMainList(mapData, listContacts) {
                            function()
                        }
                    }
                }
            } else {
                addGroupToMainList(mapData, listContacts) {
                    function()
                }
            }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun addGroupToMainList(
    mapData: HashMap<String, Any>,
    listContacts: List<CommonModel>,
    function: () -> Unit,
) {
    val path = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()
    map[CHILD_ID] = mapData[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP
    listContacts.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    path.child(CURRENT_UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageToGroup(message: String, groupID: String, typeText: String, function: () -> Unit) {
    val refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_USERNAME] = USER.fullname
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    typeMessage: String,
    pathStorage: String = "",
    filename: String = "",
    typeMany: String = "",
) {
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(receivedID,
                it,
                messageKey,
                typeMessage,
                pathStorage,
                filename,
                typeMany)
        }
    }
}

fun uploadFileToStorageToGroup(
    uri: Uri,
    messageKey: String,
    typeMessage: String,
    groupID: String,
    pathStorage: String = "",
    filename: String = "",
) {
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFileToGroup(it, messageKey, typeMessage, groupID, filename, pathStorage)
        }
    }
}

fun sendMessageAsFileToGroup(
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    groupID: String,
    filename: String,
    pathStorage: String,
) {
    val refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_USERNAME] = USER.fullname
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TEXT] = "many"
    mapMessage[CHILD_FULLNAME] = filename
    mapMessage[CHILD_STORAGE_PATH] = pathStorage
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    REF_DATABASE_ROOT.child(refMessages).child(messageKey)
        .updateChildren(mapMessage)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    storage_path: String,
    filename: String,
    typeMany: String,
) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(receivingUserID).child(CURRENT_UID).limitToLast(1)
        .addListenerForSingleValueEvent(AppValueEventListener { Data ->
            val tempList = Data.children.map { it.getCommonModel() }
            val mapMessage = hashMapOf<String, Any>()
            mapMessage[CHILD_ID] = messageKey
            mapMessage[CHILD_FROM] = CURRENT_UID
            mapMessage[CHILD_WHO] = receivingUserID
            mapMessage[CHILD_TYPE] = typeMessage
            mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
            mapMessage[CHILD_FILE_URL] = fileUrl
            mapMessage[CHILD_FULLNAME] = filename
            mapMessage[CHILD_STORAGE_PATH] = storage_path
            if (typeMany.isNotEmpty()) mapMessage[CHILD_TEXT] = typeMany
            val mapMessageRes = hashMapOf<String, Any>()
            mapMessageRes[CHILD_ID] = messageKey
            mapMessageRes[CHILD_FROM] = CURRENT_UID
            mapMessageRes[CHILD_WHO] = receivingUserID
            mapMessageRes[CHILD_TYPE] = typeMessage
            mapMessageRes[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
            mapMessageRes[CHILD_FILE_URL] = fileUrl
            mapMessageRes[CHILD_FULLNAME] = filename
            mapMessageRes[CHILD_STORAGE_PATH] = storage_path
            if (typeMany.isNotEmpty()) mapMessageRes[CHILD_TEXT] = typeMany
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
                .addOnFailureListener { showToast(it.message.toString()) }
        })
}

fun saveToTXT(user_id: String, key: String) {
    val path = APP_ACTIVITY.filesDir
    val letDirectory = File(path, user_id)
    letDirectory.mkdirs()
    val file = File(letDirectory, CURRENT_UID)
    file.delete()
    file.appendText(key)
}

fun deleteTXT(user_id: String, function: () -> Unit) {
    val path = APP_ACTIVITY.filesDir
    val letDirectory = File(path, user_id)
    val file = File(letDirectory, CURRENT_UID)
    file.delete()
    letDirectory.delete()
    function()
}

fun readTXT(user_id: String): String {
    val path = APP_ACTIVITY.filesDir
    val letDirectory = File(path, user_id)
    val file = File(letDirectory, CURRENT_UID)
    return FileInputStream(file).bufferedReader().use { it.readText() }
}
fun existFile(id: String): Boolean{
    val path = APP_ACTIVITY.filesDir
    val letDirectory = File(path, id)
    val file = File(letDirectory, CURRENT_UID)
    return file.exists()
}

fun microsoftApi(message: String): String {
    val client = OkHttpClient()
    val text: String = message
    val mediaType = MediaType.parse("application/json")
    val body = RequestBody.create(mediaType,
        "[\r\n    {\r\n        \"Text\": \"${text}\"\r\n    }\r\n]")
    val request = Request.Builder()
        .url("https://microsoft-translator-text.p.rapidapi.com/translate?to=en&api-version=3.0&profanityAction=NoAction&textType=plain")
        .post(body)
        .addHeader("content-type", "application/json")
        .addHeader("x-rapidapi-key", "18cac4b499msh13077a2901b6138p1f2e80jsn29f5ad0bad17")
        .addHeader("x-rapidapi-host", "microsoft-translator-text.p.rapidapi.com")
        .build()
    val response = client.newCall(request).execute()
    val hui = response.body()?.string()
    return JSONArray(hui).getJSONObject(0).getJSONArray("translations").getJSONObject(0)
        .get("text").toString()
}









