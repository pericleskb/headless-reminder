package com.joyfulDonkey.headlessreminder.fragment

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.joyfulDonkey.headlessreminder.databinding.FragmentDashboardBinding
import com.joyfulDonkey.headlessreminder.worker.ScheduleAlarmsWorker
import java.util.concurrent.TimeUnit

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
            setUpAlarmScheduler()
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
        binding.hourPicker.maxValue = 24
        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 59
        binding.endHourPicker.minValue = 0
        binding.endHourPicker.maxValue = 24
        binding.endMinutePicker.minValue = 0
        binding.endMinutePicker.maxValue = 59
    }

    private fun setUpAlarmScheduler() {
        val scheduleAlarmsWork = PeriodicWorkRequestBuilder<ScheduleAlarmsWorker>(
            10, TimeUnit.MINUTES,
            2, TimeUnit.MINUTES)
            .build()
        context?.let { WorkManager.getInstance(it).enqueue(scheduleAlarmsWork) }
    }

    private fun scheduleTodaysAlarms() {
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<ScheduleAlarmsWorker>()
            .build()
        context?.let { WorkManager.getInstance(it).enqueue(workRequest) }
    }
}