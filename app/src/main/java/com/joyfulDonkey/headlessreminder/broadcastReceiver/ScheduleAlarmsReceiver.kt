package com.joyfulDonkey.headlessreminder.broadcastReceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.data.alarm.TimeOfDay
import com.joyfulDonkey.headlessreminder.delegate.ScheduleAlarmsDelegate
import com.joyfulDonkey.headlessreminder.fragment.DashboardFragment
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils.HOUR_IN_MS
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils.MINUTE_IN_MS
import java.util.*

class ScheduleAlarmsReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("@@@- scheduler", "alarmScheduler onReceive")
        val prefSettings = context?.getSharedPreferences(
            DashboardFragment.DEFINITIONS.prefs,
            Context.MODE_PRIVATE
        ) ?: return
        val alarmProperties = AlarmSchedulerProperties(
            prefSettings.getInt("numOfAlarms", 5),
            TimeOfDay(prefSettings.getInt("hour",0),prefSettings.getInt("minute", 0)),
            TimeOfDay(prefSettings.getInt("endHour",0),prefSettings.getInt("endMinute", 0))
        )
        scheduleSelf(context, alarmProperties)
        Log.i("@@@ - scheduler", "schedule today's alarms")
        ScheduleAlarmsDelegate(context, alarmProperties).scheduleAlarms()
    }

    private fun scheduleSelf(context: Context, alarmProperties: AlarmSchedulerProperties) {
        Log.i("@@@ - scheduler", "schedule self before next alarm tomorrow")

        val timeToStart = Calendar.getInstance()
        timeToStart.set(Calendar.HOUR_OF_DAY, alarmProperties.earliestAlarmAt.hour)
        timeToStart.set(Calendar.MINUTE, alarmProperties.earliestAlarmAt.minute)
        timeToStart.add(Calendar.DAY_OF_MONTH, 1)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ScheduleAlarmsReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        val delay = timeToStart.timeInMillis - System.currentTimeMillis()
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + delay,
            alarmIntent
        )
    }
}