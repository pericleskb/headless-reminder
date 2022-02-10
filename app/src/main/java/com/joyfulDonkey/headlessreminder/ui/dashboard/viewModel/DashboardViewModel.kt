package com.joyfulDonkey.headlessreminder.ui.dashboard.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.joyfulDonkey.headlessreminder.models.alarm.AlarmSchedulerPropertiesModel
import com.joyfulDonkey.headlessreminder.models.alarm.TimeOfDayModel
import com.joyfulDonkey.headlessreminder.ui.dashboard.fragments.selectTime.SelectTimeFragment
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
    private var alarmProperties: AlarmSchedulerPropertiesModel = AlarmSchedulerPropertiesModel(
        prefSettings.getInt("numOfAlarms", 5),
        TimeOfDayModel(prefSettings.getInt("hour",now.get(Calendar.HOUR_OF_DAY)),prefSettings.getInt("minute", now.get(Calendar.MINUTE))),
        TimeOfDayModel(prefSettings.getInt("endHour",0),prefSettings.getInt("endMinute", 0))
    )

    fun getAlarmProperties(): AlarmSchedulerPropertiesModel {
        return alarmProperties
    }

    fun updateStartTime(newTime: TimeOfDayModel) {
        this.alarmProperties.earliestAlarmAt = newTime
    }

    fun updateEndTime(newTime: TimeOfDayModel) {
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

    fun getFile(): Uri {
        return Uri.Builder().path(prefSettings.getString("hrLogFileUri", "")).build()
    }
}