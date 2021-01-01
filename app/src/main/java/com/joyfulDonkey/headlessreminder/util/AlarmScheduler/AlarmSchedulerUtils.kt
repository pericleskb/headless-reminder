package com.joyfulDonkey.headlessreminder.util.AlarmScheduler

import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.data.alarm.TimeOfDay
import java.util.*
import kotlin.collections.ArrayList


/**********************************************************************
 * Alarm Scheduling Model
 * Alarms should be spaced out between them but not coming in exact times.
 * To accomplish this we space them out evenly and then add some salt.
 * We have 2 variables. EvenDistributionMinutes (ED) and salt.
 * ED   = minutesAvailableForAlarm / numberOfAlarms
 * salt = 0.33 * ED
 * The first interval (i = 0) can be from 0 -> (4/3) * ED
 * The following ones follow this model: For interval i,
 * delay = (i + 0,5) * ED +/- ED/3
 * And the last one can be till the end of the time limit.
 **********************************************************************/
object AlarmSchedulerUtils {

    const val SECOND_IN_MS = 1000
    const val MINUTE_IN_MS = 60 * SECOND_IN_MS
    const val HOUR_IN_MS = 60 * MINUTE_IN_MS
    const val SALT_PERCENTAGE = 0.33

    fun getAlarmIntervals(properties: AlarmSchedulerProperties): ArrayList<Int> {
        //TODO implement rules

        val firstAlarmDelay = calculateFirstAlarmDelay(properties)


        return arrayListOf(
            5000,
            10000,
            15000
        )
    }

    private fun calculateFirstAlarmDelay(properties: AlarmSchedulerProperties): Int {
        var delay = 0
        val calendarNow = Calendar.getInstance()
        var startTimeStamp = 0L
        var endTimeStamp = 0L

        //TODO I need to pair the logic of the following if statements.
        //TODO Calculate if start and end times are in past or future and today or tomorrow
        //TODO Then create calendar instances with these dates and find diff in ms
        if (properties.earliestAlarmAt.isEarlierThan(properties.latestAlarmAt)) {
            val calendarStartTime = Calendar.
        } else {

        }

        val startTimeInPast = isTimeInPast(properties.earliestAlarmAt, calendarNow)
        val endTimeInPast = isTimeInPast(properties.latestAlarmAt, calendarNow)

        if (startTimeInPast && !endTimeInPast)
        {
            //between
            return delay
        } else if (startTimeInPast && endTimeInPast)
        {
            //alarm tomorrow
        } else if (!startTimeInPast && !endTimeInPast)
        {
            //alarm today
        } else if (!startTimeInPast && endTimeInPast)
        {

        }

        if () {
            if (!)
            {
            } else {
                delay += (23 - calendarNow.get(Calendar.HOUR_OF_DAY)) * HOUR_IN_MS + (60 - calendarNow.get(Calendar.MINUTE)) * MINUTE_IN_MS
            }
        }
        delay += calendarNow.get(Calendar.HOUR_OF_DAY)
    }

    private fun isTimeInPast(timeOfDay: TimeOfDay, calendarNow: Calendar): Boolean {
        val hourNow = calendarNow.get(Calendar.HOUR_OF_DAY)
        val minuteNow = calendarNow.get(Calendar.MINUTE)

        if (hourNow > timeOfDay.hour) {
            return true
        } else if (hourNow == timeOfDay.hour && minuteNow >= timeOfDay.minute) {
            return true
        }
        return false
    }
}