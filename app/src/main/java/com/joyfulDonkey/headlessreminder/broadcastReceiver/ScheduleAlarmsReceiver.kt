package com.joyfulDonkey.headlessreminder.broadcastReceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.data.alarm.TimeOfDay
import com.joyfulDonkey.headlessreminder.fragment.DashboardFragment
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils

class ScheduleAlarmsReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val prefSettings = context?.getSharedPreferences(
            DashboardFragment.DEFINITIONS.prefs,
            Context.MODE_PRIVATE
        ) ?: return
        var alarmProperties = AlarmSchedulerProperties(
            prefSettings.getInt("numOfAlarms", 5),
            TimeOfDay(prefSettings.getInt("hour",0),prefSettings.getInt("minute", 0)),
            TimeOfDay(prefSettings.getInt("endHour",0),prefSettings.getInt("endMinute", 0))
        )
        AlarmSchedulerUtils.getAlarmIntervals(alarmProperties).forEach { interval ->
            println("@@@ - interval - $interval")
            setUpAlarm(context, interval)
        }
    }

    private fun setUpAlarm(context: Context, triggerAtMillis: Long) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, RingAlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME,
            triggerAtMillis,
            alarmIntent
        )
    }
}