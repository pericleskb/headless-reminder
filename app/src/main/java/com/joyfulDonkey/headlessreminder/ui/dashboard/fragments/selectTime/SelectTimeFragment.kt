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
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.joyfulDonkey.headlessreminder.R
import com.joyfulDonkey.headlessreminder.components.alarms.broadcastReceivers.ScheduleAlarmsReceiver
import com.joyfulDonkey.headlessreminder.components.alarms.delegates.ScheduleAlarmsDelegate
import com.joyfulDonkey.headlessreminder.components.workers.ScheduleAlarmsWorker
import com.joyfulDonkey.headlessreminder.models.alarm.AlarmSchedulerPropertiesModel
import com.joyfulDonkey.headlessreminder.models.alarm.TimeOfDayModel
import com.joyfulDonkey.headlessreminder.databinding.FragmentDashboardBinding
import com.joyfulDonkey.headlessreminder.definitions.FileDefinitions
import com.joyfulDonkey.headlessreminder.ui.dashboard.viewModel.DashboardViewModel
import java.time.Duration
import java.util.concurrent.TimeUnit

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
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        initResources()
        initLayout()
    }

    private fun initResources() {
        binding.numOfAlarmsPicker.minValue = 1
        binding.numOfAlarmsPicker.maxValue = 10
        binding.numOfAlarmsPicker.wrapSelectorWheel = false
        binding.numOfAlarmsPicker.value = dashboardViewModel.getAlarmProperties().numberOfAlarms
        binding.startTimeSelector.text = dashboardViewModel.getAlarmProperties().earliestAlarmAt.toString()
        binding.endTimeSelector.text = dashboardViewModel.getAlarmProperties().latestAlarmAt.toString()
    }

    private fun initLayout() {
        binding.scheduleAlarmsButton.setOnClickListener {
            dashboardViewModel.storePreferences()
            setUpAlarms()
        }

        startTimeDialog.setTitle(getString(R.string.select_start_time))
        binding.startTimeSelector.setOnClickListener {
            startTimeDialog.show()
        }

        endTimeDialog.setTitle(getString(R.string.select_end_time))
        binding.endTimeSelector.setOnClickListener {
            endTimeDialog.show()
        }

        binding.numOfAlarmsPicker.setOnValueChangedListener { _, _, newVal ->
            dashboardViewModel.updateRemindersPerDay(newVal)
        }

        binding.loggingSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startFileSelectionActivity();
            }
        }
    }

    private fun setUpAlarms() {
        if (TimeOfDayModel.timeOfDayNow().isBetweenAlarms(dashboardViewModel.getAlarmProperties())) {
            ScheduleAlarmsDelegate(requireContext()).scheduleAlarms()
        }
        setUpAlarmScheduler()
    }

    private fun setUpAlarmScheduler() {
        val properties = dashboardViewModel.getAlarmProperties()
        assert(properties.earliestAlarmAt.hour in 0..23 && properties.earliestAlarmAt.minute in 0..59)
        val scheduleAlarmsRequest =
            PeriodicWorkRequestBuilder<ScheduleAlarmsWorker>(
                1, TimeUnit.DAYS,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MINUTES)
            .setInitialDelay(dashboardViewModel.getDelayForNextDay(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(scheduleAlarmsRequest)
    }

    private val startTimeDialog by lazy {
        TimePickerDialog(
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
    }

    private val endTimeDialog by lazy {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newTime = TimeOfDayModel(hourOfDay, minute)
                dashboardViewModel.updateEndTime(newTime)
                binding.endTimeSelector.text = newTime.toString()
            },
            dashboardViewModel.getAlarmProperties().latestAlarmAt.hour,
            dashboardViewModel.getAlarmProperties().latestAlarmAt.minute,
            true
        )
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
            it.data.let { intent ->
                intent?.data?.let { uri ->
                    //we use this so the permission to edit the URI will be persisted through device reboots
                    val contentResolver = activity?.applicationContext?.contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver?.takePersistableUriPermission(uri, takeFlags)
                }
                intent?.dataString?.let { uri ->
                    dashboardViewModel.saveLogFileUri(uri) }
                }

        }
    }
}