package com.joyfulDonkey.headlessreminder.alarm.broadcastReceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.joyfulDonkey.headlessreminder.alarm.data.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.alarm.data.TimeOfDay
import com.joyfulDonkey.headlessreminder.delegate.ScheduleAlarmsDelegate
import com.joyfulDonkey.headlessreminder.dashboard.fragment.selectTime.SelectTimeFragment
import com.joyfulDonkey.headlessreminder.delegate.files.WriteFileDelegateImpl
import java.util.*

class ScheduleAlarmsReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val prefSettings = context?.getSharedPreferences(
            SelectTimeFragment.DEFINITIONS.prefs,
            Context.MODE_PRIVATE
        ) ?: return
        val alarmProperties = AlarmSchedulerProperties(
            prefSettings.getInt("numOfAlarms", 5),
            TimeOfDay(prefSettings.getInt("hour",0),prefSettings.getInt("minute", 0)),
            TimeOfDay(prefSettings.getInt("endHour",0),prefSettings.getInt("endMinute", 0))
        )
        scheduleSelf(context, alarmProperties)
        ScheduleAlarmsDelegate(context, alarmProperties).scheduleAlarms()
    }

    private fun scheduleSelf(context: Context, alarmProperties: AlarmSchedulerProperties) {

        val timeToStart = Calendar.getInstance()
        timeToStart.set(Calendar.HOUR_OF_DAY, alarmProperties.earliestAlarmAt.hour)
        timeToStart.set(Calendar.MINUTE, alarmProperties.earliestAlarmAt.minute)
        timeToStart.add(Calendar.DAY_OF_MONTH, 1)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ScheduleAlarmsReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        val delay = timeToStart.timeInMillis - System.currentTimeMillis()
        val triggerAt = SystemClock.elapsedRealtime() + delay
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME,
            triggerAt,
            alarmIntent
        )
        logNextScheduleTime(context, triggerAt)
    }

    private fun logNextScheduleTime(context: Context, triggerAt: Long) {
        val triggerDate = Calendar.getInstance()
        triggerDate.timeInMillis = triggerAt
        val triggerTimeOfDay = TimeOfDay(
            triggerDate.get(Calendar.HOUR_OF_DAY),
            triggerDate.get(Calendar.MINUTE))
        val content = "Time now = ${TimeOfDay.TimeOfDayNow()} - Next alarm scheduled for ${triggerTimeOfDay.toString()}\n"
        val prefSettings = context.getSharedPreferences(
            SelectTimeFragment.DEFINITIONS.prefs,
            Context.MODE_PRIVATE
        )
        val uri = Uri.Builder().path(prefSettings.getString("hrLogFileUri", "")).build()
        WriteFileDelegateImpl(context).appendToFile(uri, content)
    }
}