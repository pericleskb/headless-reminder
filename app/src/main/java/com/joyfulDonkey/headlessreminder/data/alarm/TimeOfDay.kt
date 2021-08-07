package com.joyfulDonkey.headlessreminder.data.alarm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TimeOfDay(
    val hour: Int,
    val minute: Int
): Parcelable {

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