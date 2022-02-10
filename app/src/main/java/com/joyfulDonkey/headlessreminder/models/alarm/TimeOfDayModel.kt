package com.joyfulDonkey.headlessreminder.models.alarm

import java.util.*

data class TimeOfDayModel(
    var hour: Int,
    var minute: Int
) {

    companion object {
        fun TimeOfDayNow(): TimeOfDayModel {
            val calendarNow = Calendar.getInstance()
            return TimeOfDayModel(
                calendarNow.get(Calendar.HOUR_OF_DAY),
                calendarNow.get(Calendar.MINUTE))
        }
    }

    fun isEarlierThanOrSameTo(other: TimeOfDayModel): Boolean {
        if (hour < other.hour) {
            return true
        } else if (hour == other.hour && minute <= other.minute) {
            return true
        }
        return false
    }

    override fun toString(): String {
        val hour: String = if (this.hour > 9) this.hour.toString() else "0${this.hour}"
        val minute: String = if (this.minute > 9) this.minute.toString() else "0${this.minute}"
        return "$hour:$minute"
    }
}