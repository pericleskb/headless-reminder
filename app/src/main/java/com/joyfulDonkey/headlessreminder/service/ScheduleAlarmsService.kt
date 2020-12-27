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
import com.joyfulDonkey.headlessreminder.R
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils

class ScheduleAlarmsService: Service() {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    companion object {
        val ALARM_PROPERTIES_BUNDLE = "HR_ALARM_PROPERTIES_BUNDLE"
    }

    override fun onCreate() {
        super.onCreate()
        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        println("@@@ ScheduleAlarmsService - " + SystemClock.elapsedRealtime())
        val alarmProperties: AlarmSchedulerProperties = intent?.getParcelableExtra(
            ALARM_PROPERTIES_BUNDLE) ?: AlarmSchedulerProperties()
        val alarmIntervals = AlarmSchedulerUtils.getAlarmIntervals(alarmProperties)
        var code: Int = 0
        for (alarmInterval in alarmIntervals) {
            setUpAlarm(alarmInterval, code++)
        }
        stopSelf()
        return START_REDELIVER_INTENT
    }

    private fun setUpAlarm(triggerAtMillis: Long, requestCode: Int) {
        //TODO can we use same intent and set up multiple alarms?
        alarmIntent = Intent(this, SoundPlayingService::class.java).let { intent ->
            PendingIntent.getService(this, requestCode, intent, 0)
        }
        alarmMgr?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis,
            alarmIntent
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}