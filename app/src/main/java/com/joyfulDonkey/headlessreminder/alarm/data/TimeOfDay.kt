package com.joyfulDonkey.headlessreminder.alarm.data

import java.util.*

data class TimeOfDay(
    var hour: Int,
    var minute: Int
) {

    companion object {
        fun TimeOfDayNow(): TimeOfDay {
            val calendarNow = Calendar.getInstance()
            return TimeOfDay(
                calendarNow.get(Calendar.HOUR_OF_DAY),
                calendarNow.get(Calendar.MINUTE))
        }
    }

    fun isEarlierThanOrSameTo(other: TimeOfDay): Boolean {
        if (hour < other.hour) {
            return true
        } else if (hour == other.hour && minute <= other.minute) {
            return true
        }
        return false
    }
}