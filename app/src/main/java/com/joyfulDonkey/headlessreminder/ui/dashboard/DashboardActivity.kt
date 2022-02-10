package com.joyfulDonkey.headlessreminder.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joyfulDonkey.headlessreminder.databinding.ActivityMainBinding
import com.joyfulDonkey.headlessreminder.ui.dashboard.fragments.selectTime.SelectTimeFragment

class DashboardActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFrameLayout.id, SelectTimeFragment.newInstance()).commit()
    }
}