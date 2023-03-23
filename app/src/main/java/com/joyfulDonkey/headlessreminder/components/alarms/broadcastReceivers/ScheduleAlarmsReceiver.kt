package com.joyfulDonkey.headlessreminder.components.alarms.broadcastReceivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import com.joyfulDonkey.headlessreminder.definitions.PreferenceDefinitions
import com.joyfulDonkey.headlessreminder.models.alarm.AlarmSchedulerPropertiesModel
import com.joyfulDonkey.headlessreminder.models.alarm.TimeOfDayModel
import com.joyfulDonkey.headlessreminder.components.alarms.delegates.ScheduleAlarmsDelegate
import com.joyfulDonkey.headlessreminder.components.files.delegates.WriteFileDelegate
import java.util.*

class ScheduleAlarmsReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        //why do we need to reschedule ourselves everyday?
        scheduleSelf(context)
        ScheduleAlarmsDelegate(context).scheduleAlarms()
    }

    private fun scheduleSelf(context: Context) {
        val alarmProperties = getAlarmProperties(context)

        val timeToStart = Calendar.getInstance()
        timeToStart.set(Calendar.HOUR_OF_DAY, alarmProperties.earliestAlarmAt.hour)
        timeToStart.set(Calendar.MINUTE, alarmProperties.earliestAlarmAt.minute)
        timeToStart.add(Calendar.DAY_OF_MONTH, 1)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ScheduleAlarmsReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val delay = timeToStart.timeInMillis - System.currentTimeMillis()
        val triggerAt = SystemClock.elapsedRealtime() + delay
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME,
            triggerAt,
            alarmIntent
        )
        logNextScheduleTime(context, timeToStart)
    }

    private fun getAlarmProperties(context: Context): AlarmSchedulerPropertiesModel {
        val prefSettings = context.getSharedPreferences(
            PreferenceDefinitions.preferencesName,
            Context.MODE_PRIVATE
        )

        return AlarmSchedulerPropertiesModel(
            prefSettings.getInt(PreferenceDefinitions.numOfAlarms, 5),
            TimeOfDayModel(prefSettings.getInt(PreferenceDefinitions.hour,0), prefSettings.getInt(PreferenceDefinitions.minute, 0)),
            TimeOfDayModel(prefSettings.getInt(PreferenceDefinitions.endHour,0), prefSettings.getInt(PreferenceDefinitions.endMinute, 0))
        )
    }

    private fun logNextScheduleTime(context: Context, timeToStart: Calendar) {
        val triggerTimeOfDay = TimeOfDayModel(
            timeToStart.get(Calendar.HOUR_OF_DAY),
            timeToStart.get(Calendar.MINUTE))
        val content = "Time now = ${TimeOfDayModel.timeOfDayNow()} - Next schedule time: $triggerTimeOfDay ${timeToStart.get(Calendar.DAY_OF_MONTH)}\\${timeToStart.get(Calendar.MONTH) + 1} \n"
        val prefSettings = context.getSharedPreferences(
            PreferenceDefinitions.preferencesName,
            Context.MODE_PRIVATE
        )
        val uri = Uri.parse(prefSettings.getString(PreferenceDefinitions.logFileUri, ""))
        WriteFileDelegate(context).appendToFile(uri, content)
    }
}