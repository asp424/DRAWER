package com.asp424.drawer.database

import com.asp424.drawer.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

var AUTH = FirebaseAuth.getInstance()
var MESSAGING_TOKEN = FirebaseMessaging.getInstance().token
lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
var CURRENT_UID = AUTH.currentUser?.uid.toString()
var REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
var REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference

var USER = UserModel()

const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_FILES = "messages_files"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_TOKEN = "token"
const val CHILD_FULLNAME = "fullname"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_TEXT = "text"
const val CHILD_IMAGE = "image"
const val CHILD_VOICE = "voice"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_WHO = "who"
const val CHILD_TIMESTAMP = "timeStamp"
const val CHILD_COUNT = "counter"
const val CHILD_STORAGE_PATH = "pathStorage"
const val CHILD_REGISTRATION_TIMESTAMP = "timeRegistrationStamp"
const val CHILD_EXIT_TIMESTAMP = "timeExitStamp"
const val CHILD_FILE_URL = "fileUrl"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_MESSAGES = "messages"
const val NODE_MAIN_LIST = "main_list"
const val NODE_GROUPS = "groups"
const val NODE_MEMBERS = "members"
const val NODE_WRITING = "writing"
const val FOLDER_GROUPS_IMAGE = "groups_image"
const val USER_CREATOR = "creator"
const val USER_MEMBER = "member"
const val NODE_KEY = "key"
const val CHILD_KEY = "key"


