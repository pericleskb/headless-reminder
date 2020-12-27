package com.joyfulDonkey.headlessreminder.data.alarm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeOfDay(
    val hour: Int,
    val minute: Int
): Parcelable