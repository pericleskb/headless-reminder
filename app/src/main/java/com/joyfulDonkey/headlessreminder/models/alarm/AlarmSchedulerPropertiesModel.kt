package com.joyfulDonkey.headlessreminder.models.alarm

import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

/**********************************************************************
 * Alarm Scheduling Model
 * Alarms should be spaced out between them but not coming in exact times.
 * To accomplish this we space them out evenly and then add some salt.
 * We have 2 variables. EvenDistributionMinutes (ED) and salt.
 * ED   = minutesAvailableForAlarm / numberOfAlarms
 * salt = 0.33 * ED
 * The first interval (i = 0) can be from 0 -> (5/6) * ED
 * The following ones follow this model: For interval i,
 * delay = (i + 0,5) * ED +/- ED/3
 * And the last one can be till the end of the time limit.
 **********************************************************************/
data class AlarmSchedulerPropertiesModel(
    var numberOfAlarms: Int,
    var earliestAlarmAt: TimeOfDayModel,
    var latestAlarmAt: TimeOfDayModel
) {
    companion object {
        private const val SECOND_IN_MS = 1000L
        const val SALT_PERCENTAGE = 0.33
        //    const val START_NOW_OFFSET = (MINUTE_IN_MS * 2) TODO causes crashes on sort windows
        const val START_NOW_OFFSET = (SECOND_IN_MS * 10)
    }

    fun getAlarmIntervals(): ArrayList<Long> {
        val firstAlarmDelay = calculateFirstAlarmDelay()
        val evenDistributionMs = calculateEvenDistributionMs()

        val intervalsList = arrayListOf<Long>()
        intervalsList.add(firstAlarmDelay + generateFirstInterval(
                evenDistributionMs,
                firstAlarmDelay
            )
        )
        for (i in 1 until this.numberOfAlarms) {
            val interval = firstAlarmDelay +
                    ((i + 0.5) * evenDistributionMs +
                            (evenDistributionMs * generateRandomSalt())).toLong()
            intervalsList.add(
                interval
            )
        }
        return intervalsList
    }

    /* Here we need to find out if alarms should be activated right now or the next day.
     * To do this we need to find out if we are between the selected start and end time.
     * If we are, start right away. If we are not, start on the next start time.
     */
    private fun calculateFirstAlarmDelay(): Long {
        val betweenAlarms = TimeOfDayModel.timeOfDayNow().isBetweenAlarms(this)
        if (betweenAlarms) {
            return 0
        }

        val calendarStartTime = Calendar.getInstance()
        calendarStartTime.set(Calendar.HOUR_OF_DAY, this.earliestAlarmAt.hour)
        calendarStartTime.set(Calendar.MINUTE, this.earliestAlarmAt.minute)

        if (this.earliestAlarmAt.isEarlierThanOrSameTo(TimeOfDayModel.timeOfDayNow())) {
            calendarStartTime.add(Calendar.DATE, 1)
        }
        return calendarStartTime.timeInMillis - System.currentTimeMillis()
    }

    private fun calculateEvenDistributionMs(): Long {
        val calendarStartTime = Calendar.getInstance()
        calendarStartTime.set(Calendar.HOUR_OF_DAY, this.earliestAlarmAt.hour)
        calendarStartTime.set(Calendar.MINUTE, this.earliestAlarmAt.minute)
        calendarStartTime.set(Calendar.SECOND, 0)

        val calendarEndTime = Calendar.getInstance()
        calendarEndTime.set(Calendar.HOUR_OF_DAY, this.latestAlarmAt.hour)
        calendarEndTime.set(Calendar.MINUTE, this.latestAlarmAt.minute)
        calendarEndTime.set(Calendar.SECOND, 59)

        if (this.latestAlarmAt.isEarlierThanOrSameTo(this.earliestAlarmAt)) {
            calendarEndTime.add(Calendar.DATE, 1)
        }
        return (calendarEndTime.timeInMillis - calendarStartTime.timeInMillis) / this.numberOfAlarms
    }

    private fun generateRandomSalt(): Double {
        return ThreadLocalRandom.current().nextDouble(-SALT_PERCENTAGE, SALT_PERCENTAGE)
    }

    private fun generateFirstInterval(evenDistribution: Long, firstAlarmDelay: Long): Long {
        val minValue = if (firstAlarmDelay == 0L) START_NOW_OFFSET else 0
        return ThreadLocalRandom.current().nextLong(minValue, (0.83 * evenDistribution).toLong())
    }
}