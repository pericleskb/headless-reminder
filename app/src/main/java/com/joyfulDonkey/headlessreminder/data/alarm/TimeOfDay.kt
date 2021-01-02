package com.joyfulDonkey.headlessreminder.data.alarm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeOfDay(
    val hour: Int,
    val minute: Int
): Parcelable {

    fun isEarlierThanOrSameTo(other: TimeOfDay): Boolean {
        if (hour < other.hour) {
            return true
        } else if (hour == other.hour && minute <= other.minute) {
            return true
        }
        return false
    }
}