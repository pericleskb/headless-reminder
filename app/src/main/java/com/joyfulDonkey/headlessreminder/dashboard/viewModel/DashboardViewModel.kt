package com.joyfulDonkey.headlessreminder.dashboard.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joyfulDonkey.headlessreminder.alarm.data.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.alarm.data.TimeOfDay
import com.joyfulDonkey.headlessreminder.dashboard.fragment.selectTime.SelectTimeFragment
import java.util.*

/*
 * The first time the app will be created we read and show current time as start time.
 * Every time a value changes in the UI we will update the ViewModel so that changes persist even
 * if the Activity is stopped and restarted.
 * When "Save" is pressed, then we update shared preferences
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val prefSettings: SharedPreferences = getApplication<Application>().getSharedPreferences(
        SelectTimeFragment.DEFINITIONS.prefs,
        Context.MODE_PRIVATE
    )
    private val now = Calendar.getInstance()
    private var alarmProperties: AlarmSchedulerProperties = AlarmSchedulerProperties(
        prefSettings.getInt("numOfAlarms", 5),
        TimeOfDay(prefSettings.getInt("hour",now.get(Calendar.HOUR_OF_DAY)),prefSettings.getInt("minute", now.get(Calendar.MINUTE))),
        TimeOfDay(prefSettings.getInt("endHour",0),prefSettings.getInt("endMinute", 0))
    )

    fun getAlarmProperties(): AlarmSchedulerProperties {
        return alarmProperties
    }

    fun updateStartTime(newTime: TimeOfDay) {
        this.alarmProperties.earliestAlarmAt = newTime
    }

    fun updateEndTime(newTime: TimeOfDay) {
        this.alarmProperties.latestAlarmAt = newTime
    }

    fun updateRemindersPerDay(numberOfReminders: Int) {
        this.alarmProperties.numberOfAlarms = numberOfReminders
    }

    fun storePreferences() {
        val prefsEditor = prefSettings.edit()
        prefsEditor?.putInt("numOfAlarms", alarmProperties.numberOfAlarms)
        prefsEditor?.putInt("hour", alarmProperties.earliestAlarmAt.hour)
        prefsEditor?.putInt("minute", alarmProperties.earliestAlarmAt.minute)
        prefsEditor?.putInt("endHour", alarmProperties.latestAlarmAt.hour)
        prefsEditor?.putInt("endMinute", alarmProperties.latestAlarmAt.minute)
        prefsEditor?.apply()
    }

    fun saveLogFileUri(logFileUri: String) {
        val prefsEditor = prefSettings.edit()
        prefsEditor.putString("hrLogFileUri", logFileUri)
        prefsEditor?.apply()
    }
}