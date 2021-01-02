package com.joyfulDonkey.headlessreminder.data.alarm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlarmSchedulerProperties(
    val numberOfAlarms: Int = 3,
    val earliestAlarmAt: TimeOfDay = TimeOfDay(18,0),
    val latestAlarmAt: TimeOfDay = TimeOfDay(18, 15)
): Parcelable