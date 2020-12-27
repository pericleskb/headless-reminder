package com.joyfulDonkey.headlessreminder.worker

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils

class ScheduleAlarmsWorker(appContext: Context, workerParameters: WorkerParameters)
    : Worker(appContext, workerParameters) {

    companion object {
        const val ALARM_PROPERTIES_BUNDLE = "HR_ALARM_PROPERTIES_BUNDLE"
    }

    override fun doWork(): Result {
        val alarmProperties: AlarmSchedulerProperties =
            inputData.keyValueMap[ALARM_PROPERTIES_BUNDLE] as AlarmSchedulerProperties
        val alarmIntervals = AlarmSchedulerUtils.getAlarmIntervals(alarmProperties)
        for ((code, alarmInterval) in alarmIntervals.withIndex()) {
            setUpAlarm(alarmInterval, code)
        }
        return Result.success()
    }


    private fun setUpAlarm(triggerAtMillis: Long, requestCode: Int) {
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<PlaySoundWorker>().build()
    }
}