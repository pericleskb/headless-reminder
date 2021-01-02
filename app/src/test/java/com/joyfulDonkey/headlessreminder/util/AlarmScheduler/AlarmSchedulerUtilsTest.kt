package com.joyfulDonkey.headlessreminder.util.AlarmScheduler

import com.google.common.truth.Truth.assertThat
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.data.alarm.TimeOfDay
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils.HOUR_IN_MS
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils.SALT_PERCENTAGE
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AlarmSchedulerUtilsTest {

    //TODO add extra case for last and first alarm of the next day
    @Test
    fun getAlarmIntervals_whenNoTimeLimit_thenIntervalsCorrect() {
        val properties = AlarmSchedulerProperties(
            numberOfAlarms = 10,
            earliestAlarmAt = TimeOfDay(0, 0),
            latestAlarmAt = TimeOfDay(0, 0)
        )
        val intervalsList: ArrayList<Long> = AlarmSchedulerUtils.getAlarmIntervals(properties)
        intervalsList.sort()

        assertThat(properties.numberOfAlarms).isEqualTo(intervalsList.size)

        val evenDistributionMilliseconds = 24 * HOUR_IN_MS / 10
        // Two consecutive interval will have at least 0.33 * evenDistributionMinutes between them
        val minInterval = (evenDistributionMilliseconds * SALT_PERCENTAGE).toInt()
        for (i in 0 until intervalsList.size - 1) {
            assertThat(intervalsList[i+1] - intervalsList[i]).isAtLeast(minInterval)
        }
    }

    @Test
    fun getAlarmIntervals_whenEarliestAlarmAtFuture_thenAllIntervalsInTimeLimit() {
        val calendarNow = Calendar.getInstance()
        val hourNow = calendarNow.get(Calendar.HOUR_OF_DAY)
        val minutesNow = calendarNow.get(Calendar.MINUTE)
        val properties = AlarmSchedulerProperties(
            numberOfAlarms = 10,
            earliestAlarmAt = TimeOfDay(hourNow + 1, minutesNow),
            latestAlarmAt = TimeOfDay(hourNow + 12, minutesNow)
        )
        val intervalsList: ArrayList<Long> = AlarmSchedulerUtils.getAlarmIntervals(properties)
        intervalsList.sort()

        assertThat(properties.numberOfAlarms).isEqualTo(intervalsList.size)

        val evenDistributionMilliseconds = 12 * HOUR_IN_MS / 10
        val minInterval = (evenDistributionMilliseconds * SALT_PERCENTAGE).toInt()
        for (i in 0 until intervalsList.size - 1) {
            assertThat(intervalsList[i+1] - intervalsList[i]).isAtLeast(minInterval)
        }

        assertThat(intervalsList[0]).isAtLeast(HOUR_IN_MS)
        assertThat(intervalsList[0]).isAtMost(HOUR_IN_MS + (4/3) * evenDistributionMilliseconds)

        assertThat(intervalsList[intervalsList.size -1])
            .isAtMost(HOUR_IN_MS + ((intervalsList.size -1 + 0.5).roundToInt()) * evenDistributionMilliseconds)
    }

    @Test
    fun getAlarmIntervals_whenEarliestAlarmTimePassed_thenFirstAlarmNotImmediate() {
        val calendarNow = Calendar.getInstance()
        val hourNow = calendarNow.get(Calendar.HOUR_OF_DAY)
        val minutesNow = calendarNow.get(Calendar.MINUTE)
        val properties = AlarmSchedulerProperties(
            numberOfAlarms = 10,
            earliestAlarmAt = TimeOfDay(hourNow - 6, minutesNow),
            latestAlarmAt = TimeOfDay(hourNow + 6, minutesNow)
        )
        val intervalsList: ArrayList<Long> = AlarmSchedulerUtils.getAlarmIntervals(properties)
        intervalsList.sort()

        val minStartInterval = AlarmSchedulerUtils.START_NOW_OFFSET

        for (i in 0 until intervalsList.size - 1) {
            assertThat(intervalsList[i+1] - intervalsList[i]).isAtLeast(minStartInterval)
        }
        assertThat(intervalsList[0]).isAtLeast(minStartInterval)
    }
}