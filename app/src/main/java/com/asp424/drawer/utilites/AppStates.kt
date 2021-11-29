package com.asp424.drawer.utilites

import com.asp424.drawer.database.*

enum class AppStates(val state: String) {
    ONLINE("в сети");
    companion object {
        fun updateState(appStates: AppStates) {
            if (AUTH.currentUser != null) {
                CURRENT_UID = AUTH.currentUser?.uid.toString()
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_EXIT_TIMESTAMP)
                    .setValue(appStates.state)
                    .addOnSuccessListener {
                        USER.timeExitStamp = appStates.state }
                    .addOnFailureListener { showToast(
                        it.message.toString()
                    ) }
            }
        }
    }
}





