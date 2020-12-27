package com.joyfulDonkey.headlessreminder.util.AlarmScheduler

import android.os.SystemClock
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties

object AlarmSchedulerUtils {
    fun getAlarmIntervals(properties: AlarmSchedulerProperties): ArrayList<Long> {
        //TODO implement rules
        return arrayListOf(
            SystemClock.elapsedRealtime() + 5000,
            SystemClock.elapsedRealtime() + 10000,
            SystemClock.elapsedRealtime() + 15000
        )
    }
}