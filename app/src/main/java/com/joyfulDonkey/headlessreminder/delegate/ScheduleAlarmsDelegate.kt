package com.joyfulDonkey.headlessreminder.delegate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import com.joyfulDonkey.headlessreminder.alarm.broadcastReceiver.RingAlarmReceiver
import com.joyfulDonkey.headlessreminder.alarm.data.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.alarm.data.TimeOfDay
import com.joyfulDonkey.headlessreminder.alarm.util.AlarmSchedulerUtils
import com.joyfulDonkey.headlessreminder.dashboard.fragment.selectTime.SelectTimeFragment
import com.joyfulDonkey.headlessreminder.delegate.files.WriteFileDelegateImpl
import java.util.*

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
        val triggerAt = SystemClock.elapsedRealtime() + triggerAtMillis
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME,
            triggerAt,
            alarmIntent
        )
        logAlarmTime(context, triggerAt)
    }

    private fun logAlarmTime(context: Context, triggerAt: Long) {
        val triggerDate = Calendar.getInstance()
        triggerDate.timeInMillis = triggerAt
        val triggerTimeOfDay = TimeOfDay(
            triggerDate.get(Calendar.HOUR_OF_DAY),
            triggerDate.get(Calendar.MINUTE))
        val content = "Alarm scheduled for $triggerTimeOfDay\n"
        val prefSettings = context.getSharedPreferences(
            SelectTimeFragment.DEFINITIONS.prefs,
            Context.MODE_PRIVATE
        )
        val uri = Uri.Builder().path(prefSettings.getString("hrLogFileUri", "")).build()
        WriteFileDelegateImpl(context).appendToFile(uri, content)
    }
}