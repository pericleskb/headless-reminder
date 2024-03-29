package com.joyfulDonkey.headlessreminder.alarm.AlarmScheduler

import com.google.common.truth.Truth.assertThat
import com.joyfulDonkey.headlessreminder.alarm.util.AlarmSchedulerUtils
import com.joyfulDonkey.headlessreminder.models.alarm.AlarmSchedulerPropertiesModel
import com.joyfulDonkey.headlessreminder.models.alarm.TimeOfDayModel
import com.joyfulDonkey.headlessreminder.alarm.util.AlarmSchedulerUtils.HOUR_IN_MS
import com.joyfulDonkey.headlessreminder.alarm.util.AlarmSchedulerUtils.SALT_PERCENTAGE
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AlarmSchedulerUtilsTest {

    //TODO add extra case for last and first alarm of the next day
    @Test
    fun getAlarmIntervals_whenNoTimeLimit_thenIntervalsCorrect() {
        val properties = AlarmSchedulerPropertiesModel(
            numberOfAlarms = 10,
            earliestAlarmAt = TimeOfDayModel(0, 0),
            latestAlarmAt = TimeOfDayModel(0, 0)
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
    fun getAlarmIntervals_whenEarliestAlarmAtFuture_thenAllIntervalsInTimeLimit_times100() {
        val calendarNow = Calendar.getInstance()
        val hourNow = calendarNow.get(Calendar.HOUR_OF_DAY)
        val minutesNow = calendarNow.get(Calendar.MINUTE)
        val properties = AlarmSchedulerPropertiesModel(
            numberOfAlarms = 10,
            earliestAlarmAt = TimeOfDayModel(hourNow + 1, minutesNow),
            latestAlarmAt = TimeOfDayModel(hourNow + 12, minutesNow)
        )

        for(i in 0..1000) {
            println(i)
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
    }

    @Test
    fun getAlarmIntervals_whenEarliestAlarmTimePassed_thenFirstAlarmNotImmediate() {
        val calendarNow = Calendar.getInstance()
        val hourNow = calendarNow.get(Calendar.HOUR_OF_DAY)
        val minutesNow = calendarNow.get(Calendar.MINUTE)
        val properties = AlarmSchedulerPropertiesModel(
            numberOfAlarms = 10,
            earliestAlarmAt = TimeOfDayModel(hourNow - 6, minutesNow),
            latestAlarmAt = TimeOfDayModel(hourNow + 6, minutesNow)
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