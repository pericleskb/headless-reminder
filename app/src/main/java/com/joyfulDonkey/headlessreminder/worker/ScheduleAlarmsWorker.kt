package com.joyfulDonkey.headlessreminder.worker

import android.content.Context
import androidx.work.*
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.data.alarm.TimeOfDay
import com.joyfulDonkey.headlessreminder.fragment.DashboardFragment
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils
import java.util.concurrent.TimeUnit

//TODO if we need this to run at a specific time then we should use ScheduleAlarmService
class ScheduleAlarmsWorker(private val appContext: Context, workerParameters: WorkerParameters)
    : Worker(appContext, workerParameters) {

    companion object {
        const val ALARM_PROPERTIES_BUNDLE = "HR_ALARM_PROPERTIES_BUNDLE"
    }

    override fun doWork(): Result {

        val prefSettings = appContext.getSharedPreferences(
            DashboardFragment.DEFINITIONS.prefs,
            Context.MODE_PRIVATE
        )
        var alarmProperties: AlarmSchedulerProperties = AlarmSchedulerProperties(
            prefSettings.getInt("numOfAlarms", 5),
            TimeOfDay(prefSettings.getInt("hour",0),prefSettings.getInt("minute", 0)),
            TimeOfDay(prefSettings.getInt("endHour",0),prefSettings.getInt("endMinute", 0))
        )
        if (!inputData.keyValueMap.containsKey(ALARM_PROPERTIES_BUNDLE)) {
            //TODO remove else and testing case
//            return Result.failure()
        } else {
            alarmProperties =
                inputData.keyValueMap[ALARM_PROPERTIES_BUNDLE] as AlarmSchedulerProperties
        }

        AlarmSchedulerUtils.getAlarmIntervals(alarmProperties).forEach { interval ->
            println("@@@ - interval - $interval")
            setUpAlarm(interval)
        }
        return Result.success()
    }


    private fun setUpAlarm(triggerAtMillis: Long) {
        //TODO set retry and backoff policy
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<PlaySoundWorker>()
            .setInitialDelay(triggerAtMillis, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(appContext).enqueue(workRequest)
    }
}