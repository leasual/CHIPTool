package com.se.wiser.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.chip.chiptool.databinding.ActivityAddDeviceBinding

class AddDeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDeviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.include.ivBack.setOnClickListener {
            this.finish()
        }
        binding.btQRCode.setOnClickListener {
            val intent = Intent(this, ScanQRCodeZxingActivity::class.java)
            intent.putExtra("networkType", 1)
            startActivity(intent)
            this.finish()
        }
        binding.btOnNetwork.setOnClickListener {
            val intent = Intent(this, ScanQRCodeZxingActivity::class.java)
            intent.putExtra("networkType", 0)
            startActivity(intent)
            this.finish()
        }
        binding.btWifiConfig.setOnClickListener {
            val intent = Intent(this, ScanQRCodeZxingActivity::class.java)
            intent.putExtra("networkType", 2)
            startActivity(intent)
            this.finish()
        }
    }
}