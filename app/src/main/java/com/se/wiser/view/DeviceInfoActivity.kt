package com.se.wiser.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.chip.chiptool.databinding.ActivityDeviceInfoBinding
import com.google.chip.chiptool.setuppayloadscanner.CHIPDeviceInfo

class DeviceInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceInfoBinding
    private var deviceInfo: CHIPDeviceInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        deviceInfo = intent.getParcelableExtra("deviceInfo")

    }
}