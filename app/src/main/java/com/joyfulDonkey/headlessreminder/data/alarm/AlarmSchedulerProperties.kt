package com.joyfulDonkey.headlessreminder.data.alarm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlarmSchedulerProperties(
    val numberOfAlarms: Int = 3,
    val minIntervalBetweenAlarms: Int = DEFAULT_MIN_INTERVAL_MS,
    val earliestAlarmAt: TimeOfDay = TimeOfDay(9,0),
    val latestAlarmAt: TimeOfDay = TimeOfDay(22, 0)
): Parcelable
{
    companion object {
        val DEFAULT_MIN_INTERVAL_MS: Int = 3600 * 1000
    }
}