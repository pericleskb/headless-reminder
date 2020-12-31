package com.joyfulDonkey.headlessreminder.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.databinding.FragmentDashboardBinding
import com.joyfulDonkey.headlessreminder.service.ScheduleAlarmsService
import com.joyfulDonkey.headlessreminder.worker.PlaySoundWorker
import com.joyfulDonkey.headlessreminder.worker.ScheduleAlarmsWorker
import java.util.concurrent.TimeUnit

class DashboardFragment: Fragment() {

    private lateinit var binding: FragmentDashboardBinding

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
            scheduleTodaysAlarms()
        }
    }

    private fun setUpAlarmScheduler() {
        val scheduleAlarmsWork = PeriodicWorkRequestBuilder<ScheduleAlarmsWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES)
            .build()
        context?.let { WorkManager.getInstance(it).enqueue(scheduleAlarmsWork) }
    }

    private fun scheduleTodaysAlarms() {
        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<ScheduleAlarmsWorker>()
            .build()
        context?.let { WorkManager.getInstance(it).enqueue(workRequest) }
    }
}