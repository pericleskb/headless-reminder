package com.joyfulDonkey.headlessreminder.fragment

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
import com.joyfulDonkey.headlessreminder.data.alarm.AlarmSchedulerProperties
import com.joyfulDonkey.headlessreminder.databinding.FragmentDashboardBinding
import com.joyfulDonkey.headlessreminder.service.ScheduleAlarmsService
import com.joyfulDonkey.headlessreminder.service.SoundPlayingService
import java.util.*


class DashboardFragment: Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

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
            : View? {
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
//            scheduleTodaysAlarms()
        }
    }

    private fun setUpAlarmScheduler() {
        alarmMgr = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(activity, ScheduleAlarmsService::class.java).let { intent ->
            PendingIntent.getService(activity, 0, intent, 0)
        }
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 20
            )
        }

        alarmMgr?.setInexactRepeating(
            AlarmManager.RTC,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_HOUR,
            alarmIntent
        )
    }

    private fun scheduleTodaysAlarms() {
        val intent = Intent(activity, ScheduleAlarmsService::class.java)
        val alarmSchedulerProperties = AlarmSchedulerProperties()
        intent.putExtra(ScheduleAlarmsService.ALARM_PROPERTIES_BUNDLE, alarmSchedulerProperties)
        activity?.startService(intent)
    }
}