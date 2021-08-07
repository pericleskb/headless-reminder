package com.joyfulDonkey.headlessreminder.util.AlarmScheduler

import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.data.alarm.TimeOfDay
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
object AlarmSchedulerUtils {

    const val SECOND_IN_MS = 1000L
    const val MINUTE_IN_MS = 60 * SECOND_IN_MS
    const val HOUR_IN_MS = 60 * MINUTE_IN_MS
    const val SALT_PERCENTAGE = 0.33
//    const val START_NOW_OFFSET = (MINUTE_IN_MS * 2) TODO causes crashes on sort windows
    const val START_NOW_OFFSET = (SECOND_IN_MS * 10)

    fun getAlarmIntervals(properties: AlarmSchedulerProperties): ArrayList<Long> {

        val firstAlarmDelay = calculateFirstAlarmDelay(properties)
        val evenDistributionMs = calculateEvenDistributionMs(properties)

        val intervalsList = arrayListOf<Long>()
        intervalsList.add(firstAlarmDelay + generateFirstInterval(evenDistributionMs, firstAlarmDelay))
        for (i in 1 until properties.numberOfAlarms) {
            intervalsList.add(
                firstAlarmDelay +
                ((i + 0.5) * evenDistributionMs +
                (evenDistributionMs * generateRandomSalt(SALT_PERCENTAGE))).toLong()
            )
        }
        return intervalsList
    }

    fun isBetweenAlarms(properties: AlarmSchedulerProperties): Boolean {
        val timeOfDayNow = TimeOfDay.TimeOfDayNow()
        val startTimePassed = properties.earliestAlarmAt.isEarlierThanOrSameTo(timeOfDayNow)
        val endTimePassed = properties.latestAlarmAt.isEarlierThanOrSameTo(timeOfDayNow)

        return if (properties.earliestAlarmAt.isEarlierThanOrSameTo(properties.latestAlarmAt)) {
            startTimePassed && !endTimePassed
        } else {
            // case when end time is on the next day
            if (timeOfDayNow.isEarlierThanOrSameTo(TimeOfDay(23, 59))) {
                startTimePassed
            } else {
                !endTimePassed
            }
        }
    }

    /* Here we need to find out if alarms should be activated right now or the next day.
     * To do this we need to find out if we are between the selected start and end time.
     * If we are, start right away. If we are not, start on the next start time.
     */
    private fun calculateFirstAlarmDelay(properties: AlarmSchedulerProperties): Long {
        val betweenAlarms = isBetweenAlarms(properties)
        if (betweenAlarms) {
            return 0
        }

        val calendarStartTime = Calendar.getInstance()
        calendarStartTime.set(Calendar.HOUR_OF_DAY, properties.earliestAlarmAt.hour)
        calendarStartTime.set(Calendar.MINUTE, properties.earliestAlarmAt.minute)

        if (properties.earliestAlarmAt.isEarlierThanOrSameTo(TimeOfDay.TimeOfDayNow())) {
            calendarStartTime.add(Calendar.DATE, 1)
        }
        return calendarStartTime.timeInMillis - System.currentTimeMillis()
    }

    private fun calculateEvenDistributionMs(properties: AlarmSchedulerProperties): Long {
        val calendarStartTime = Calendar.getInstance()
        calendarStartTime.set(Calendar.HOUR_OF_DAY, properties.earliestAlarmAt.hour)
        calendarStartTime.set(Calendar.MINUTE, properties.earliestAlarmAt.minute)

        val calendarEndTime = Calendar.getInstance()
        calendarEndTime.set(Calendar.HOUR_OF_DAY, properties.latestAlarmAt.hour)
        calendarEndTime.set(Calendar.MINUTE, properties.latestAlarmAt.minute)

        if (properties.latestAlarmAt.isEarlierThanOrSameTo(properties.earliestAlarmAt)) {
            calendarEndTime.add(Calendar.DATE, 1)
        }
        return (calendarEndTime.timeInMillis - calendarStartTime.timeInMillis) / properties.numberOfAlarms
    }

    private fun generateRandomSalt(salt: Double): Double {
        return ThreadLocalRandom.current().nextDouble(-salt, salt)
    }

    private fun generateFirstInterval(evenDistribution: Long, firstAlarmDelay: Long): Long {
        val minValue = if (firstAlarmDelay == 0L) START_NOW_OFFSET else 0
        //TODO minValue > bound if too many alarms in too short a time
        return ThreadLocalRandom.current().nextLong(minValue, (0.83 * evenDistribution).toLong())
    }
}