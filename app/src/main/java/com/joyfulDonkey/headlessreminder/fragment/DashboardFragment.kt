package com.joyfulDonkey.headlessreminder.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joyfulDonkey.headlessreminder.broadcastReceiver.ScheduleAlarmsReceiver
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.data.alarm.TimeOfDay
import com.joyfulDonkey.headlessreminder.databinding.FragmentDashboardBinding
import com.joyfulDonkey.headlessreminder.delegate.ScheduleAlarmsDelegate
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils
import com.joyfulDonkey.headlessreminder.util.AlarmScheduler.AlarmSchedulerUtils.HOUR_IN_MS
import java.util.*

class DashboardFragment: Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    object DEFINITIONS {
        const val prefs = "DonkeyMonkey"
    }

    /*****************************************************
     * No arguments here but following this practice everywhere
     * https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
     *****************************************************/
    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initResources()
    }

    private fun initResources() {
        binding.setAlarmsButton.setOnClickListener {
            val prefSettings = activity?.getSharedPreferences(DEFINITIONS.prefs, MODE_PRIVATE)
            val prefsEditor = prefSettings?.edit()
            prefsEditor?.putInt("numOfAlarms", binding.numOfAlarmsPicker.value)
            prefsEditor?.putInt("hour", binding.hourPicker.value)
            prefsEditor?.putInt("minute", binding.minutePicker.value)
            prefsEditor?.putInt("endHour", binding.endHourPicker.value)
            prefsEditor?.putInt("endMinute", binding.endMinutePicker.value)
            prefsEditor?.apply()
            setUpAlarms(
                AlarmSchedulerProperties(
                    binding.numOfAlarmsPicker.value,
                    TimeOfDay(binding.hourPicker.value, binding.minutePicker.value),
                    TimeOfDay(binding.endHourPicker.value, binding.endMinutePicker.value)
                )
            )
        }
        binding.numOfAlarmsPicker.minValue = 1
        binding.numOfAlarmsPicker.maxValue = 10
        binding.numOfAlarmsPicker.wrapSelectorWheel = false

//
////        binding.hourPicker.displayedValues = arrayOf(
////            "00","01","02","03","04","05","06",
////            "07","08","09","10","11","12","13",
////            "13","14","15","16","17","18","19",
////            "20","21","22","23","24"
//        )
        val now = Calendar.getInstance()
        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23
        binding.hourPicker.value = now.get(Calendar.HOUR_OF_DAY)
        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 59
        binding.minutePicker.value = now.get(Calendar.MINUTE)
        binding.endHourPicker.minValue = 0
        binding.endHourPicker.maxValue = 23
        binding.endHourPicker.value = 0
        binding.endMinutePicker.minValue = 0
        binding.endMinutePicker.maxValue = 59
        binding.endMinutePicker.value = 0
    }

    private fun setUpAlarms(properties: AlarmSchedulerProperties) {
        if (AlarmSchedulerUtils.isBetweenAlarms(properties)) {
            Log.i("@@@", "is between alarms. schedule now")
            scheduleTodaysAlarms(properties)
            val timeToStart = Calendar.getInstance()
            timeToStart.set(Calendar.HOUR_OF_DAY, properties.earliestAlarmAt.hour)
            timeToStart.set(Calendar.MINUTE, properties.earliestAlarmAt.minute)
            timeToStart.add(Calendar.DAY_OF_MONTH, 1)
            val delay = timeToStart.timeInMillis - System.currentTimeMillis()
            Log.i("@@@", "start alarm scheduler tomorrow")
            setUpAlarmScheduler(properties, delay)
        } else {
            Log.i("@@@", "is NOT between alarms")
            setUpAlarmScheduler(properties, 0)
        }
    }

    private fun setUpAlarmScheduler(properties: AlarmSchedulerProperties, delay: Long) {
        assert(properties.earliestAlarmAt.hour in 0..23 && properties.earliestAlarmAt.minute in 0..59)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ScheduleAlarmsReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + delay,
            alarmIntent
        )
    }

    private fun scheduleTodaysAlarms(properties: AlarmSchedulerProperties) {
        context?.let { ScheduleAlarmsDelegate(it, properties).scheduleAlarms() }
    }
}