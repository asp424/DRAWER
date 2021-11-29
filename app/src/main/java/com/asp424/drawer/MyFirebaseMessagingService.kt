package com.asp424.drawer;


import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val g = remoteMessage.data.get("userUid")

           /* object : Thread() {
                override fun run() {
                    APP_ACTIVITY.runOnUiThread(Runnable {

                    })
                }
            }.start()
            remoteMessage.notification?.let {
            }*/
        }
    }

}

