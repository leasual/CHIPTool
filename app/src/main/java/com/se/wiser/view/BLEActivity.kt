package com.se.wiser.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.chip.chiptool.databinding.ActivityBleactivityBinding

class BLEActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBleactivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBleactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {

    }
}