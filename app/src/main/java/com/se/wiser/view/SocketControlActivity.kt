package com.se.wiser.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import chip.devicecontroller.ChipDeviceController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.chip.chiptool.ChipClient
import com.google.chip.chiptool.GenericChipDeviceListener
import com.google.chip.chiptool.R
import com.google.chip.chiptool.databinding.ActivitySocketControlBinding
import com.se.wiser.App
import com.se.wiser.model.SocketDevice
import com.se.wiser.utils.DeviceUtil
import com.se.wiser.viewmodel.SocketControlViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SocketControlActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SocketControlActivity"
    }
    private val deviceController: ChipDeviceController
        get() = ChipClient.getDeviceController(this)

    private val viewModel: SocketControlViewModel by lazy { SocketControlViewModel() }
    private lateinit var binding: ActivitySocketControlBinding
    private var onOff: Boolean = false
    private lateinit var device: SocketDevice
    private var lastDeviceId: Long = 0
    private var isThread: Boolean = false
    private var isFirst: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocketControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        updateDeviceInfo(device)
    }

    private fun setupViews() {
        deviceController.setCompletionListener(ChipControllerCallback())
        device = intent.getSerializableExtra("device") as SocketDevice
        isThread = intent.getBooleanExtra("thread", false)
        lastDeviceId = (application as App).deviceIdMap[device.type.javaClass.simpleName] ?: 0L
        Log.d(TAG, "light control endpoint=${device}")
        onOff = device.state
        binding.lavLight.setOnClickListener {
            onOff = !onOff
            device.state = onOff
            updateDeviceInfo(device)
            viewModel.sendOnOff(device.endpoint, lastDeviceId,onOff, this)
            isFirst = true
        }
        binding.include.ivBack.setOnClickListener {
            this.finish()
        }
        binding.bottomSheetLayout.bottomSheetLayout.visibility = if (isThread) View.VISIBLE else View.GONE
        binding.bottomSheetLayout.bottomSheetArrow.setOnClickListener {
            if (BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).state != BottomSheetBehavior.STATE_EXPANDED) {
                BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.d(TAG, "newState=$newState")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.bottomSheetLayout.bottomSheetArrow.rotation = slideOffset * 180;
            }

        })
        binding.bottomSheetLayout.etDeviceId.setText(lastDeviceId.toString())
        binding.bottomSheetLayout.etEndpointId.setText("${device.endpoint}")
        binding.bottomSheetLayout.etFabricId.setText("${device.fabricId}")
        binding.bottomSheetLayout.btUpdate.setOnClickListener {
            try {
                val deviceId = binding.bottomSheetLayout.etDeviceId.text.toString().toInt()
                val endpoint = binding.bottomSheetLayout.etEndpointId.text.toString().toInt()
                device.id = deviceId
                device.endpoint = endpoint
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        binding.include.swCommission.visibility = View.VISIBLE
        binding.include.swCommission.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val deviceId = binding.bottomSheetLayout.etDeviceId.text.toString().toLong()
                val endpoint = binding.bottomSheetLayout.etEndpointId.text.toString().toInt()
//                ClusterUtil.openCommissionWindow(0, deviceId, 60, this)
            }
        }

        viewModel.viewModelScope.launch(Dispatchers.Main) {
            (application as App).observeDeviceState.collect {
                Log.d(TAG, "update event=$it device=$device")
                if (it.endpoint == device.endpoint) {
                    val updateDevice = (application as App).deviceStateMap[it.endpoint]
                    if (updateDevice != null && updateDevice.endpoint == device.endpoint) {
                        Log.d(TAG, "updateDevice=$updateDevice")
                        device.state = (updateDevice as SocketDevice).state
                        device.current = updateDevice.current
                        device.totalActivePower = updateDevice.totalActivePower
                        device.voltage = updateDevice.voltage
                        device.currentMin = updateDevice.currentMin
                        device.currentMax = updateDevice.currentMax
                        device.voltageMax = updateDevice.voltageMax
                        device.voltageMin = updateDevice.voltageMin
                        device.activePower = updateDevice.activePower

                        updateDeviceInfo(updateDevice)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }

    private fun updateDeviceInfo(device: SocketDevice) {
        Log.d(TAG, "updateDeviceInfo=${device.state}")
        if (device.state != onOff || isFirst) {
            if (device.state) {
                binding.lavLight.cancelAnimation()
                binding.lavLight.progress = 0f
                binding.lavLight.setMaxProgress(0.5f)
                binding.lavLight.speed = 2f
                binding.lavLight.repeatCount = 0
                binding.lavLight.playAnimation()
            } else {
                binding.lavLight.cancelAnimation()
                binding.lavLight.progress = 1f
                binding.lavLight.setMaxProgress(0.5f)
                binding.lavLight.speed = -2f
                binding.lavLight.repeatCount = 0
                binding.lavLight.playAnimation()
            }
            onOff = device.state
            isFirst = false
        }
        DeviceUtil.getStateName(device.productModeId, if (device.state) 1 else 0, this)
            .also { binding.tvOnOff.text = it }
        binding.tvPower.text = device.getActivePower(this)
        binding.tvCurrent.text = device.getCurrent(this)
        binding.tvVoltage.text = device.getVoltage(this)
//        binding.tvGenerate.text = device.getCurrentMin(this)
        binding.tvConsumption.text = device.getVoltageMin(this)

        binding.tvOnOff.setTextColor(resources.getColor(if (device.state) R.color.green else android.R.color.darker_gray))
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
}