package com.se.wiser.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.chip.chiptool.databinding.ActivityOnTheNetworkBinding
import com.se.wiser.utils.KeyStoreUtil

class OnTheNetworkActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "OnTheNetworkActivity"
    }
    lateinit var binding: ActivityOnTheNetworkBinding
//    var deviceInfo: CHIPDeviceInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnTheNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
//        deviceInfo = intent.getParcelableExtra("deviceInfo")
        //set default port
//        PreferencesKeyValueStoreManager(this).set("port", "5540")
        binding.etIP.setText(KeyStoreUtil.getValue(this, "ipAddress"))
        binding.etPort.setText(KeyStoreUtil.getValue(this, "port"))
        binding.include.ivBack.setOnClickListener {
            this.finish()
        }
        binding.btConnect.setOnClickListener {
//            PreferencesKeyValueStoreManager(this).set("ipAddress", binding.etIP.text.toString())
//            PreferencesKeyValueStoreManager(this).set("port", binding.etPort.text.toString())
//            val intent = Intent(this, PairingActivity::class.java)
//            intent.putExtra("deviceInfo", deviceInfo)
//            intent.putExtra("ipAddress", binding.etIP.text.toString())
//            intent.putExtra("port", binding.etPort.text.toString())
//            startActivity(intent)
//            this.finish()
            Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show()
        }
    }
}