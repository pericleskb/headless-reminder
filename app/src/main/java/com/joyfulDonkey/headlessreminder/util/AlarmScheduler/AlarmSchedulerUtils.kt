package com.joyfulDonkey.headlessreminder.util.AlarmScheduler

import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties


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
    const val DAY_IN_MS = 24 * HOUR_IN_MS
    const val SALT_PERCENTAGE = 0.33

    /*

        val calendarNow = Calendar.getInstance()
        val hourNow = calendarNow.get(Calendar.HOUR_OF_DAY)

        this is BS
        val earliestTimeCalendar = GregorianCalendar()
            .set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH,
            Calendar.HOUR_OF_DAY + 1, Calendar.MINUTE)
        val latestTimeCalender = GregorianCalendar()
            .set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH,
                Calendar.HOUR_OF_DAY + 9, Calendar.MINUTE)
     */

    fun getAlarmIntervals(properties: AlarmSchedulerProperties): ArrayList<Int> {
        //TODO implement rules
        return arrayListOf(
            5000,
            10000,
            15000
        )
    }
}