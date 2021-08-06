package com.joyfulDonkey.headlessreminder.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joyfulDonkey.headlessreminder.broadcastReceiver.ScheduleAlarmsReceiver
import com.joyfulDonkey.headlessreminder.databinding.FragmentDashboardBinding
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
            setUpAlarmScheduler(binding.hourPicker.value, binding.minutePicker.value)
            val prefSettings = activity?.getSharedPreferences(DEFINITIONS.prefs, MODE_PRIVATE)
            val prefsEditor = prefSettings?.edit()
            prefsEditor?.putInt("numOfAlarms", binding.numOfAlarmsPicker.value)
            prefsEditor?.putInt("hour", binding.hourPicker.value)
            prefsEditor?.putInt("minute", binding.minutePicker.value)
            prefsEditor?.putInt("endHour", binding.endHourPicker.value)
            prefsEditor?.putInt("endMinute", binding.endMinutePicker.value)
            prefsEditor?.apply()
            scheduleTodaysAlarms()
        }
        binding.numOfAlarmsPicker.minValue = 0
        binding.numOfAlarmsPicker.maxValue = 10
        binding.numOfAlarmsPicker.wrapSelectorWheel = false

//
////        binding.hourPicker.displayedValues = arrayOf(
////            "00","01","02","03","04","05","06",
////            "07","08","09","10","11","12","13",
////            "13","14","15","16","17","18","19",
////            "20","21","22","23","24"
//        )
        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23
        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 59
        binding.endHourPicker.minValue = 0
        binding.endHourPicker.maxValue = 23
        binding.endMinutePicker.minValue = 0
        binding.endMinutePicker.maxValue = 59
    }

    private fun setUpAlarmScheduler(startHour: Int, startMinute: Int) {
        assert(startHour in 0..23 && startMinute in 0..59)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, ScheduleAlarmsReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        //TODO better to use elapsed time in a later point. Find point before startTime and set to 24 hours
        //TODO Watch for case where we try to set the next alarms but we are also before endTime (e.g. 24 hour alarms)
        val startTime: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
        }
        //if start time in past add one day
        alarmManager.setRepeating(
            AlarmManager.RTC,
            startTime.timeInMillis,
            HOUR_IN_MS * 24,
            alarmIntent
        )
    //Work  er implementation
//        val scheduleAlarmsWork = PeriodicWorkRequestBuilder<ScheduleAlarmsWorker>(
//            10, TimeUnit.MINUTES,
//            2, TimeUnit.MINUTES)
//            .build()
//        context?.let { WorkManager.getInstance(it).enqueue(scheduleAlarmsWork) }
    }

    private fun scheduleTodaysAlarms() {


        //Workers
//        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<ScheduleAlarmsWorker>()
//            .build()
//        context?.let { WorkManager.getInstance(it).enqueue(workRequest) }
    }
}