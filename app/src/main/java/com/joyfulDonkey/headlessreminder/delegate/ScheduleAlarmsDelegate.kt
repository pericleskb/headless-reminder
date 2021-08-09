package com.joyfulDonkey.headlessreminder.delegate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.joyfulDonkey.headlessreminder.alarm.broadcastReceiver.RingAlarmReceiver
import com.joyfulDonkey.headlessreminder.alarm.data.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.alarm.util.AlarmSchedulerUtils

class ScheduleAlarmsDelegate(
    private val context: Context,
    private val alarmProperties: AlarmSchedulerProperties
) {
    fun scheduleAlarms() {
        AlarmSchedulerUtils.getAlarmIntervals(alarmProperties).forEachIndexed { index, interval ->
            setUpAlarm(context, interval, index)
        }
    }

    private fun setUpAlarm(context: Context, triggerAtMillis: Long, index: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, RingAlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, index, intent, 0)
        }
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + triggerAtMillis,
            alarmIntent
        )
    }
}