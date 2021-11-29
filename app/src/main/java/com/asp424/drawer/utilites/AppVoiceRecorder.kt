package com.asp424.drawer.utilites

import android.media.MediaRecorder
import android.util.Log
import java.io.File

class AppVoiceRecorder {
    private val mMediaRecorder = MediaRecorder()
    private lateinit var mFile: File
    private lateinit var mMessageKey: String
    fun startRecord(messageKey: String) {
        try {
            mMessageKey = messageKey
            createFileForRecord()
            prepareMediaRecorder()
            Log.d("My", "ass")
            mMediaRecorder.start()
        } catch (e: Exception) {
            Log.d("My", e.toString())
            showToast(e.message.toString())
        }
    }

    private fun prepareMediaRecorder() {
        mMediaRecorder.apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setOutputFile(mFile.absolutePath)
            prepare()
        }
    }

    private fun createFileForRecord() {
        mFile = File(APP_ACTIVITY.filesDir, mMessageKey)
        mFile.createNewFile()
    }

    fun stopRecord(onSuccess: (file: File, messageKey: String) -> Unit) {
        try {
            mMediaRecorder.stop()
            onSuccess(mFile, mMessageKey)
        } catch (e: Exception) {
            showToast("Держите кнопку дольше")
            mFile.delete()
        }
    }

    fun releaseRecorder() {
        try {
            mMediaRecorder.release()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }
}


