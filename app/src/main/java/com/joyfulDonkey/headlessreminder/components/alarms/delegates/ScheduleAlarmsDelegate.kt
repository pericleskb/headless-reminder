package com.joyfulDonkey.headlessreminder.components.alarms.delegates

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import com.joyfulDonkey.headlessreminder.components.alarms.broadcastReceivers.RingAlarmReceiver
import com.joyfulDonkey.headlessreminder.definitions.PreferenceDefinitions
import com.joyfulDonkey.headlessreminder.models.alarm.AlarmSchedulerPropertiesModel
import com.joyfulDonkey.headlessreminder.models.alarm.TimeOfDayModel
import com.joyfulDonkey.headlessreminder.components.files.delegates.WriteFileDelegate
import java.util.*

class ScheduleAlarmsDelegate(
    private val context: Context,
    private val alarmProperties: AlarmSchedulerPropertiesModel
) {
    private lateinit var uri: Uri
    private lateinit var alarmManager: AlarmManager

    fun scheduleAlarms() {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val prefSettings = context.getSharedPreferences(
            PreferenceDefinitions.preferencesName,
            Context.MODE_PRIVATE
        )
        uri = Uri.parse(prefSettings.getString(PreferenceDefinitions.logFileUri, ""))
        alarmProperties.getAlarmIntervals().forEachIndexed { index, interval ->
            if (timeInBoundaries(alarmProperties, interval)) {
                WriteFileDelegate(context).appendToFile(uri, "$interval - ")
                setUpAlarm(context, interval, index)
            }
        }
    }

    private fun setUpAlarm(context: Context, triggerAfterMillis: Long, index: Int) {
        val alarmIntent = Intent(context, RingAlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, index, intent, 0)
        }
        val triggerAt = SystemClock.elapsedRealtime() + triggerAfterMillis
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME,
            triggerAt,
            alarmIntent
        )
        logAlarmTime(context, triggerAfterMillis)
    }

    private fun logAlarmTime(context: Context, triggerAt: Long) {
        val triggerDate = Calendar.getInstance()
        triggerDate.timeInMillis = System.currentTimeMillis() + triggerAt
        val triggerTimeOfDay = TimeOfDayModel(
            triggerDate.get(Calendar.HOUR_OF_DAY),
            triggerDate.get(Calendar.MINUTE))
        val content = "Alarm scheduled for $triggerTimeOfDay\n"
        WriteFileDelegate(context).appendToFile(uri, content)
    }

    private fun timeInBoundaries(alarmSchedulerProperties: AlarmSchedulerPropertiesModel, triggerAfterMillis: Long): Boolean {
        val triggerDate = Calendar.getInstance()
        triggerDate.timeInMillis = System.currentTimeMillis() + triggerAfterMillis
        val triggerTimeOfDay = TimeOfDayModel(
            triggerDate.get(Calendar.HOUR_OF_DAY),
            triggerDate.get(Calendar.MINUTE))
        return triggerTimeOfDay.isBetweenAlarms(alarmSchedulerProperties)
    }
}