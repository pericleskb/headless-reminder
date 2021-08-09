package com.joyfulDonkey.headlessreminder.dashboard.fragment.selectTime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.joyfulDonkey.headlessreminder.alarm.broadcastReceiver.ScheduleAlarmsReceiver
import com.joyfulDonkey.headlessreminder.alarm.data.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.alarm.data.TimeOfDay
import com.joyfulDonkey.headlessreminder.databinding.FragmentDashboardBinding
import com.joyfulDonkey.headlessreminder.delegate.ScheduleAlarmsDelegate
import com.joyfulDonkey.headlessreminder.alarm.util.AlarmSchedulerUtils
import com.joyfulDonkey.headlessreminder.dashboard.viewModel.DashboardViewModel
import java.util.*

class SelectTimeFragment: Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel

    object DEFINITIONS {
        const val prefs = "DonkeyMonkey"
    }

    /*****************************************************
     * No arguments here but following this practice everywhere
     * https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
     *****************************************************/
    companion object {
        fun newInstance(): SelectTimeFragment {
            return SelectTimeFragment()
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
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
//
////        binding.hourPicker.displayedValues = arrayOf(
////            "00","01","02","03","04","05","06",
////            "07","08","09","10","11","12","13",
////            "13","14","15","16","17","18","19",
////            "20","21","22","23","24"
//        )
        binding.numOfAlarmsPicker.minValue = 1
        binding.numOfAlarmsPicker.maxValue = 10
        binding.numOfAlarmsPicker.wrapSelectorWheel = false
        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23
        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 59
        binding.endHourPicker.minValue = 0
        binding.endHourPicker.maxValue = 23
        binding.endMinutePicker.minValue = 0
        binding.endMinutePicker.maxValue = 59

        binding.hourPicker.value = dashboardViewModel.getAlarmProperties().earliestAlarmAt.hour
        binding.minutePicker.value = dashboardViewModel.getAlarmProperties().earliestAlarmAt.minute
        binding.endHourPicker.value = dashboardViewModel.getAlarmProperties().latestAlarmAt.hour
        binding.endMinutePicker.value = dashboardViewModel.getAlarmProperties().latestAlarmAt.minute
        binding.numOfAlarmsPicker.value = dashboardViewModel.getAlarmProperties().numberOfAlarms
        binding.setAlarmsButton.setOnClickListener {
            setUpAlarms(dashboardViewModel.getAlarmProperties())
            dashboardViewModel.storePreferences()
        }
        //TODO update view model's ui values when ui changes
        binding.hourPicker.setOnValueChangedListener{ picker, oldVal, newVal ->
            val newTime = TimeOfDay(newVal, dashboardViewModel.getAlarmProperties().earliestAlarmAt.minute)
            dashboardViewModel.updateStartTime(newTime)
        }
        binding.minutePicker.setOnValueChangedListener{ picker, oldVal, newVal ->
            val newTime = TimeOfDay(dashboardViewModel.getAlarmProperties().earliestAlarmAt.hour, newVal)
            dashboardViewModel.updateStartTime(newTime)
        }
        binding.endHourPicker.setOnValueChangedListener{ picker, oldVal, newVal ->
            val newTime = TimeOfDay(newVal, dashboardViewModel.getAlarmProperties().latestAlarmAt.minute)
            dashboardViewModel.updateEndTime(newTime)
        }
        binding.endMinutePicker.setOnValueChangedListener{ picker, oldVal, newVal ->
            val newTime = TimeOfDay(dashboardViewModel.getAlarmProperties().latestAlarmAt.hour, newVal)
            dashboardViewModel.updateEndTime(newTime)
        }
        binding.numOfAlarmsPicker.setOnValueChangedListener{ picker, oldVal, newVal ->
            dashboardViewModel.updateRemindersPerDay(newVal)
        }
    }

    private fun setUpAlarms(properties: AlarmSchedulerProperties) {
        if (AlarmSchedulerUtils.isBetweenAlarms(properties)) {
            scheduleTodaysAlarms(properties)
            val timeToStart = Calendar.getInstance()
            timeToStart.set(Calendar.HOUR_OF_DAY, properties.earliestAlarmAt.hour)
            timeToStart.set(Calendar.MINUTE, properties.earliestAlarmAt.minute)
            timeToStart.add(Calendar.DAY_OF_MONTH, 1)
            val delay = timeToStart.timeInMillis - System.currentTimeMillis()
            setUpAlarmScheduler(properties, delay)
        } else {
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