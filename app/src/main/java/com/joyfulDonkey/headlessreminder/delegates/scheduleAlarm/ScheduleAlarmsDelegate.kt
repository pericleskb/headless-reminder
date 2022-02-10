package com.joyfulDonkey.headlessreminder.delegates.scheduleAlarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import com.joyfulDonkey.headlessreminder.broadcastReceivers.RingAlarmReceiver
import com.joyfulDonkey.headlessreminder.models.alarm.AlarmSchedulerPropertiesModel
import com.joyfulDonkey.headlessreminder.models.alarm.TimeOfDayModel
import com.joyfulDonkey.headlessreminder.ui.dashboard.fragments.selectTime.SelectTimeFragment
import com.joyfulDonkey.headlessreminder.delegates.files.WriteFileDelegateImpl
import java.util.*

class ScheduleAlarmsDelegate(
    private val context: Context,
    private val alarmProperties: AlarmSchedulerPropertiesModel
) {
    fun scheduleAlarms() {
        alarmProperties.getAlarmIntervals().forEachIndexed { index, interval ->
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
        val triggerTimeOfDay = TimeOfDayModel(
            triggerDate.get(Calendar.HOUR_OF_DAY),
            triggerDate.get(Calendar.MINUTE))
        val content = "Alarm scheduled for $triggerTimeOfDay\n"
        val prefSettings = context.getSharedPreferences(
            SelectTimeFragment.DEFINITIONS.prefs,
            Context.MODE_PRIVATE
        )
        val uri = Uri.parse(prefSettings.getString("hrLogFileUri", ""))
        WriteFileDelegateImpl(context).appendToFile(uri, content)
    }
}