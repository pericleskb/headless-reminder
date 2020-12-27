package com.joyfulDonkey.headlessreminder.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.os.SystemClock
import com.joyfulDonkey.headlessreminder.R

class SoundPlayingService: Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        val soundUri: Uri =
            Uri.parse("android.resource://com.joyfulDonkey.headlessreminder/" + R.raw.ting)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            this@SoundPlayingService.applicationContext?.let { setDataSource(it, soundUri) }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        println("@@@ SoundPlayingService - " + SystemClock.elapsedRealtime())
        mediaPlayer?.apply {
            prepare()
            start()
            setOnCompletionListener {
                it.release()
                stopSelf()
            }
        }
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer = null
    }

}