package com.asp424.drawer.utilites

import android.graphics.Color
import android.transition.TransitionManager
import android.view.View
import com.asp424.drawer.R
import com.asp424.drawer.databinding.MessageItemVoiceBinding
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
/*
fun buttonPauseAnimUser(binding: MessageItemVoiceBinding) =
    CoroutineScope(Dispatchers.Main).launch {
        binding.apply {
            startAnim(binding, chatUserBtnPause, chatUserBtnPauseNull, 200L)
            delay(200L)
            startAnim(binding, chatUserBtnPlayNull, chatUserBtnPlay, 200L)
        }
    }

fun buttonPlayAnimUser(binding: MessageItemVoiceBinding) = CoroutineScope(Dispatchers.Main).launch {
    binding.apply {
        startAnim(binding, chatUserBtnPlay, chatUserBtnPlayNull, 200L)
        delay(300L)
        startAnim(binding, chatUserBtnPauseNull, chatUserBtnPause, 200L)
    }
}

fun textAnimUserPlay(binding: MessageItemVoiceBinding) = CoroutineScope(Dispatchers.Main).launch {
    binding.apply {
        startAnim(binding, chatUserVoiceTextVoiceMessage, chatUserVoiceTextVoiceMessageEnd, 200L)
        chatUserBtnPlay.visibility = View.INVISIBLE
        delay(300L)
        chatUserPlayingSmall.visibility = View.VISIBLE
        binding.chatUserPlayingSmall.setImageResource(R.drawable.dynamic)
        binding.chatUserPlayingLarge.setImageResource(R.drawable.dynamic)
        startAnim(binding, chatUserPlayingSmall, chatUserPlayingLarge, 200L)
    }
}

fun textAnimUserStop(binding: MessageItemVoiceBinding, function: () -> Unit) =
    CoroutineScope(Dispatchers.Main).launch {
        binding.apply {
            startAnim(binding, chatUserPlayingLarge, chatUserPlayingSmall, 200L)
            delay(300L)
            startAnim(
                binding,chatUserVoiceTextVoiceMessageEnd,
                chatUserVoiceTextVoiceMessage,200)
            function()
        }
    }

fun textAnimPause(binding: MessageItemVoiceBinding) = CoroutineScope(
    Dispatchers.Main
).launch {
    binding.apply {
        binding.chatUserPlayingSmall.setImageResource(R.drawable.dynamic)
        binding.chatUserPlayingLarge.setImageResource(R.drawable.dynamic)
        startAnim(binding, chatUserPlayingLarge, chatUserPlayingSmall, 200L)
        delay(300L)
        binding.chatUserPlayingSmall.setImageResource(R.drawable.pause)
        binding.chatUserPlayingLarge.setImageResource(R.drawable.pause)
        startAnim(binding, chatUserPlayingSmall, chatUserPlayingLarge, 200L)
    }
}

fun textAnimUserPlayAfterPause(binding: MessageItemVoiceBinding) = CoroutineScope(
    Dispatchers.Main
).launch {
    binding.apply {
        binding.chatUserPlayingSmall.setImageResource(R.drawable.pause)
        binding.chatUserPlayingLarge.setImageResource(R.drawable.pause)
        startAnim(binding, chatUserPlayingLarge, chatUserPlayingSmall, 200L)
        delay(300L)
        binding.chatUserPlayingSmall.setImageResource(R.drawable.dynamic)
        binding.chatUserPlayingLarge.setImageResource(R.drawable.dynamic)
        startAnim(binding, chatUserPlayingSmall, chatUserPlayingLarge, 200L)
    }
}

fun startAnim(binding: MessageItemVoiceBinding, start: View, end: View, duration_time: Long)
{
    CoroutineScope(Dispatchers.IO).launch {
        val transform = MaterialContainerTransform().apply {
            startView = start
            endView = end
            addTarget(end)
            scrimColor = Color.TRANSPARENT
            pathMotion = MaterialArcMotion()
            duration = duration_time
        }
        TransitionManager.beginDelayedTransition(binding.root, transform)
    }
    end.visibility = View.VISIBLE
    start.visibility = View.INVISIBLE
}

*/

