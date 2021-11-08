package com.se.wiser.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import chip.devicecontroller.ChipDeviceController
import com.google.chip.chiptool.ChipClient
import com.google.chip.chiptool.GenericChipDeviceListener
import com.google.chip.chiptool.R
import com.google.chip.chiptool.databinding.ActivityDoorSensorBinding
import com.se.wiser.App
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.DoorDevice
import com.se.wiser.utils.DeviceUtil
import com.se.wiser.viewmodel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DoorSensorActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DoorSensorActivity"
    }
    private val deviceController: ChipDeviceController
        get() = ChipClient.getDeviceController(this)

    lateinit var binding: ActivityDoorSensorBinding

    private val viewModel: DeviceViewModel by lazy { DeviceViewModel() }

    private lateinit var device: DoorDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoorSensorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        deviceController.setCompletionListener(ChipControllerCallback())
        device = intent.getSerializableExtra("device") as DoorDevice
        Log.d(TAG, "door device endpoint=${device.endpoint}")
        binding.include.ivBack.setOnClickListener {
            this.finish()
        }

        showDoorOnOff(false)
        binding.include.swCommission.visibility = View.VISIBLE
        binding.include.swCommission.setOnCheckedChangeListener { buttonView, isChecked ->
        }

        viewModel.viewModelScope.launch(Dispatchers.Main) {
            (application as App).observeDeviceState.collect {
                Log.d(TAG, "update event=$it device=$device")
                Log.d(TAG, "device endpoint=${device.endpoint} update endpoint=${it.endpoint}")
                if (it.endpoint == device.endpoint) {
                    val updateDevice = (application as App).deviceStateMap[it.endpoint]
                    Log.d(TAG, "update")
                    if (updateDevice != null && updateDevice.endpoint == device.endpoint) {
                        device.state = (updateDevice as DoorDevice).state
                        showDoorOnOff(device.state == 0x31 || device.state == 0x32 || device.state == 0x01)
                    }
                }
            }
        }
    }

    inner class ChipControllerCallback : GenericChipDeviceListener() {
        override fun onConnectDeviceComplete() {}

//        override fun onSendMessageComplete(message: String?) {
//            Log.d(TAG, "send message response= $message")
//        }

        override fun onNotifyChipConnectionClosed() {
            Log.d(TAG, "onNotifyChipConnectionClosed")
        }

        override fun onCloseBleComplete() {
            Log.d(TAG, "onCloseBleComplete")
        }

        override fun onError(error: Throwable?) {
            Log.d(TAG, "onError: $error")
        }
    }

    private fun showDoorOnOff(onOff: Boolean) {
        if (onOff) {
//            binding.lavDoor.addValueCallback(
//                KeyPath("**"), LottieProperty.COLOR_FILTER,
//                {
//                    PorterDuffColorFilter(getColor(R.color.green), PorterDuff.Mode.SRC_ATOP)
//                })
            binding.lavDoor.progress = 0f
            binding.lavDoor.setMaxProgress(1f)
            binding.lavDoor.speed = 0.5f
            binding.lavDoor.repeatCount = 0
            binding.lavDoor.playAnimation()
        } else {
            binding.lavDoor.progress = 0f
//            binding.lavDoor.addValueCallback(
//                KeyPath("**"), LottieProperty.COLOR_FILTER,
//                {
//                    PorterDuffColorFilter(getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP)
//                })
            binding.lavDoor.setMaxProgress(0f)
        }
        binding.tvOnOff.text = DeviceUtil.getStateName(device.productModeId, if (onOff) 1 else 0, this)
        binding.tvOnOff.setTextColor(resources.getColor(if (onOff) R.color.green else android.R.color.darker_gray))
    }
}