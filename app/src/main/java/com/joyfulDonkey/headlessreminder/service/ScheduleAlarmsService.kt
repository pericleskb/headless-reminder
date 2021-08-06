package com.joyfulDonkey.headlessreminder.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.os.SystemClock
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.joyfulDonkey.headlessreminder.R
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils
import com.joyfulDonkey.headlessreminder.worker.PlaySoundWorker
import java.util.concurrent.TimeUnit

class ScheduleAlarmsService: Service() {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    companion object {
        val ALARM_PROPERTIES_BUNDLE = "HR_ALARM_PROPERTIES_BUNDLE"
    }

    override fun onCreate() {
        super.onCreate()
        println("@@@ - interval")
        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val alarmProperties: AlarmSchedulerProperties = intent?.getParcelableExtra(
            ALARM_PROPERTIES_BUNDLE) ?: AlarmSchedulerProperties()
        AlarmSchedulerUtils.getAlarmIntervals(alarmProperties).forEach { interval ->
            println("@@@ - interval - $interval")
            setUpAlarm(interval)
        }

        stopSelf()
        return START_REDELIVER_INTENT
    }

    private fun setUpAlarm(triggerAtMillis: Long) {
        //TODO set retry and backoff policy
//        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<PlaySoundWorker>()
//            .setInitialDelay(triggerAtMillis, TimeUnit.MILLISECONDS)
//            .build()
//        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}