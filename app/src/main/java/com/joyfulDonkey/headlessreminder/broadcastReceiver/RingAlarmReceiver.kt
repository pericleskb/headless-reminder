package com.joyfulDonkey.headlessreminder.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.work.ListenableWorker
import com.joyfulDonkey.headlessreminder.R
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.data.alarm.TimeOfDay
import com.joyfulDonkey.headlessreminder.fragment.DashboardFragment
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils

class RingAlarmReceiver: BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.applicationContext?.let {
            mediaPlayer = MediaPlayer.create(it, R.raw.ting).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnCompletionListener {
                    it.reset()
                    it.release()
                    mediaPlayer = null
                }
                start()
            }
        }
    }
}