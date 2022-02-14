package com.joyfulDonkey.headlessreminder.ui.dashboard.fragments.selectTime

import android.app.Activity.RESULT_OK
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.joyfulDonkey.headlessreminder.R
import com.joyfulDonkey.headlessreminder.components.alarms.broadcastReceivers.ScheduleAlarmsReceiver
import com.joyfulDonkey.headlessreminder.models.alarm.AlarmSchedulerPropertiesModel
import com.joyfulDonkey.headlessreminder.models.alarm.TimeOfDayModel
import com.joyfulDonkey.headlessreminder.databinding.FragmentDashboardBinding
import com.joyfulDonkey.headlessreminder.definitions.FileDefinitions
import com.joyfulDonkey.headlessreminder.components.alarms.delegates.ScheduleAlarmsDelegate
import com.joyfulDonkey.headlessreminder.ui.dashboard.viewModel.DashboardViewModel
import java.util.*

class SelectTimeFragment: Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel

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
    ) : View {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initResources()
        initLayout()
    }

    private fun initResources() {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.numOfAlarmsPicker.minValue = 1
        binding.numOfAlarmsPicker.maxValue = 10
        binding.numOfAlarmsPicker.wrapSelectorWheel = false
        binding.numOfAlarmsPicker.value = dashboardViewModel.getAlarmProperties().numberOfAlarms
        binding.startTimeSelector.text = dashboardViewModel.getAlarmProperties().earliestAlarmAt.toString()
        binding.endTimeSelector.text = dashboardViewModel.getAlarmProperties().latestAlarmAt.toString()
    }

    private fun initLayout() {
        //set up save button functionality
        binding.scheduleAlarmsButton.setOnClickListener {
            setUpAlarms(dashboardViewModel.getAlarmProperties())
            dashboardViewModel.storePreferences()
        }

        //start time timer
        val startTimeDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newTime = TimeOfDayModel(hourOfDay, minute)
                dashboardViewModel.updateStartTime(newTime)
                binding.startTimeSelector.text = newTime.toString()
            },
            dashboardViewModel.getAlarmProperties().earliestAlarmAt.hour,
            dashboardViewModel.getAlarmProperties().earliestAlarmAt.minute,
            true
        )
        startTimeDialog.setTitle(getString(R.string.select_start_time))
        binding.startTimeSelector.setOnClickListener {
            startTimeDialog.show()
        }

        //end time timer
        val endTimeDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newTime = TimeOfDayModel(hourOfDay, minute)
                dashboardViewModel.updateEndTime(newTime)
                binding.endTimeSelector.text = newTime.toString()
            },
            dashboardViewModel.getAlarmProperties().earliestAlarmAt.hour,
            dashboardViewModel.getAlarmProperties().earliestAlarmAt.minute,
            true
        )
        endTimeDialog.setTitle(getString(R.string.select_end_time))
        binding.endTimeSelector.setOnClickListener {
            endTimeDialog.show()
        }

        binding.numOfAlarmsPicker.setOnValueChangedListener { _, _, newVal ->
            dashboardViewModel.updateRemindersPerDay(newVal)
        }

        binding.loggingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startFileSelectionActivity();
            }
        }
    }

    private fun setUpAlarms(properties: AlarmSchedulerPropertiesModel) {
        if (properties.isBetweenAlarms()) {
            val intent = Intent(context, ScheduleAlarmsReceiver::class.java)
            activity?.sendBroadcast(intent)
        } else {
            val timeToStart = Calendar.getInstance()
            timeToStart.set(Calendar.HOUR_OF_DAY, properties.earliestAlarmAt.hour)
            timeToStart.set(Calendar.MINUTE, properties.earliestAlarmAt.minute)
            timeToStart.add(Calendar.DAY_OF_MONTH, 1)
            val delay = timeToStart.timeInMillis - System.currentTimeMillis()
            setUpAlarmScheduler(properties, delay)
        }
    }

    private fun setUpAlarmScheduler(properties: AlarmSchedulerPropertiesModel, delay: Long) {
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
//        val timeToStart = Calendar.getInstance()
//        timeToStart.timeInMillis = System.currentTimeMillis() + delay
//        val triggerTimeOfDay = TimeOfDayModel(
//            timeToStart.get(Calendar.HOUR_OF_DAY),
//            timeToStart.get(Calendar.MINUTE))
//        val content = "Time now = ${TimeOfDayModel.timeOfDayNow()} - Next schedule time: $triggerTimeOfDay ${timeToStart.get(Calendar.DAY_OF_MONTH)}\\${timeToStart.get(Calendar.MONTH) + 1} \n"
    }

    private fun startFileSelectionActivity() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, FileDefinitions.logFileName)
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.Builder().)
        }
        createLogFile.launch(intent)
    }

    private val createLogFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            it.data?.dataString?.let { uri -> dashboardViewModel.saveLogFileUri(uri) }
        }
    }

}