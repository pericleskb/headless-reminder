package com.joyfulDonkey.headlessreminder.worker

import android.content.Context
import androidx.work.*
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils
import java.util.concurrent.TimeUnit

//TODO if we need this to run at a specific time then we should use ScheduleAlarmService
class ScheduleAlarmsWorker(private val appContext: Context, workerParameters: WorkerParameters)
    : Worker(appContext, workerParameters) {

    companion object {
        const val ALARM_PROPERTIES_BUNDLE = "HR_ALARM_PROPERTIES_BUNDLE"
    }

    override fun doWork(): Result {
        var alarmProperties: AlarmSchedulerProperties = AlarmSchedulerProperties()
        if (!inputData.keyValueMap.containsKey(ALARM_PROPERTIES_BUNDLE)) {
            //TODO remove else and testing case
//            return Result.failure()
        } else {
            alarmProperties =
                inputData.keyValueMap[ALARM_PROPERTIES_BUNDLE] as AlarmSchedulerProperties
        }
        val alarmIntervals = AlarmSchedulerUtils.getAlarmIntervals(alarmProperties)
        for (alarmInterval in alarmIntervals) {
            setUpAlarm(alarmInterval)
        }
        return Result.success()
    }


    private fun setUpAlarm(triggerAtMillis: Long) {
        //TODO set retry and backoff policy
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<PlaySoundWorker>()
            .setInitialDelay(triggerAtMillis.toLong(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(appContext).enqueue(workRequest)
    }
}