package com.joyfulDonkey.headlessreminder.components.workers

import android.content.Context
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.joyfulDonkey.headlessreminder.components.alarms.delegates.ScheduleAlarmsDelegate
import com.joyfulDonkey.headlessreminder.components.files.delegates.WriteFileDelegate
import com.joyfulDonkey.headlessreminder.definitions.PreferenceDefinitions
import com.joyfulDonkey.headlessreminder.models.alarm.TimeOfDayModel

class ScheduleAlarmsWorker(private val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        logNowTime(appContext)
        ScheduleAlarmsDelegate(appContext).scheduleAlarms()
        return Result.success()
    }

    private fun logNowTime(context: Context) {
        val content = "Time now = ${TimeOfDayModel.timeOfDayNow()}\n"
        val prefSettings = context.getSharedPreferences(
            PreferenceDefinitions.preferencesName,
            Context.MODE_PRIVATE
        )
        val uri = Uri.parse(prefSettings.getString(PreferenceDefinitions.logFileUri, ""))
        WriteFileDelegate(context).appendToFile(uri, content)
    }
}