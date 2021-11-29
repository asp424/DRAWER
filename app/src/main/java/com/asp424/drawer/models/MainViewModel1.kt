package com.asp424.drawer.models

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asp424.drawer.database.firebase.AppFirebaseRepository

class MainViewModel1 : ViewModel() {
    var listItem: MutableLiveData<CommonModel> = MutableLiveData()
    var listContacts: MutableLiveData<MutableList<CommonModel>> = MutableLiveData()
    var contact: MutableLiveData<CommonModel> = MutableLiveData()
    var uri: MutableLiveData<Uri> = MutableLiveData()
}
