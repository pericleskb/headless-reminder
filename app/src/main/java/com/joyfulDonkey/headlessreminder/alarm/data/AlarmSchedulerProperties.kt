package com.joyfulDonkey.headlessreminder.alarm.data

data class AlarmSchedulerProperties(
    var numberOfAlarms: Int = 3,
    var earliestAlarmAt: TimeOfDay = TimeOfDay(18,0),
    var latestAlarmAt: TimeOfDay = TimeOfDay(18, 15)
)