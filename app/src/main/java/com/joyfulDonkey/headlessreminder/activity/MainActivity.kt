package com.joyfulDonkey.headlessreminder.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joyfulDonkey.headlessreminder.databinding.ActivityMainBinding
import com.joyfulDonkey.headlessreminder.fragment.DashboardFragment

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFrameLayout.id, DashboardFragment.newInstance()).commit()
    }
}