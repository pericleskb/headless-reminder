package com.joyfulDonkey.headlessreminder.worker

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.joyfulDonkey.headlessreminder.R
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils

class PlaySoundWorker(private val appContext: Context, workerParameters: WorkerParameters)
    : Worker(appContext, workerParameters) {

    companion object {
        const val TIME_INTERVAL_DATA = "HR_TIME_INTERVAL_DATA"
        const val DEFAULT_INTERVAL = 5400 * 1000
    }

    private var mediaPlayer: MediaPlayer? = null

    override fun doWork(): Result {
        val soundUri: Uri =
            Uri.parse("android.resource://com.joyfulDonkey.headlessreminder/" + R.raw.ting)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            appContext.applicationContext?.let { setDataSource(it, soundUri) }
            setOnCompletionListener {
                it.reset()
                it.release()
                mediaPlayer = null
            }
            prepare()
            start()
        }
        return Result.success()
    }
}