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

    override fun toString(): String {
        val hour: String = if (this.hour != 0) this.hour.toString() else "00"
        val minute: String = if (this.minute != 0) this.minute.toString() else "00"
        return "$hour:$minute"
    }
}