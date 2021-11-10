package com.se.wiser.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import chip.devicecontroller.ChipDeviceController
import com.google.chip.chiptool.ChipClient
import com.google.chip.chiptool.GenericChipDeviceListener
import com.google.chip.chiptool.databinding.ActivityWindowCoveringBinding
import com.se.wiser.App
import com.se.wiser.model.ShutterDevice
import com.se.wiser.utils.ClusterUtil
import com.se.wiser.viewmodel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WindowCoveringActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DoorSensorActivity"
    }
    private val deviceController: ChipDeviceController
        get() = ChipClient.getDeviceController(this)

    lateinit var binding: ActivityWindowCoveringBinding

    private val viewModel: DeviceViewModel by lazy { DeviceViewModel() }

    private lateinit var device: ShutterDevice
    private var isStopAnimation: Boolean = false
    private var lastAnimationFlag: Boolean = false
    private var lastDeviceId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWindowCoveringBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        deviceController.setCompletionListener(ChipControllerCallback())
        device = intent.getSerializableExtra("device") as ShutterDevice
        Log.d(TAG, "door device endpoint=${device.endpoint}")
        binding.include.ivBack.setOnClickListener {
            this.finish()
        }

        showPercentage(device)
        binding.include.swCommission.visibility = View.VISIBLE
        binding.include.swCommission.setOnCheckedChangeListener { buttonView, isChecked ->
        }
        lastDeviceId = (application as App).deviceIdMap[device.type.javaClass.simpleName] ?: 0L
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            (application as App).observeDeviceState.collect {
                Log.d(TAG, "update event=$it device=$device")
                Log.d(TAG, "device endpoint=${device.endpoint} update endpoint=${it.endpoint}")
                if (it.endpoint == device.endpoint) {
                    val updateDevice = (application as App).deviceStateMap[it.endpoint]
                    Log.d(TAG, "update")
                    if (updateDevice != null && updateDevice.endpoint == device.endpoint) {
                        device.liftPercentage = (updateDevice as ShutterDevice).liftPercentage
                        device.tiltPercentage = (updateDevice as ShutterDevice).tiltPercentage
                        showPercentage(device)
                    }
                }
            }
        }
        binding.btOpen.setOnClickListener {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                val open = ClusterUtil.openWindowCovering(device.endpoint, lastDeviceId, this@WindowCoveringActivity)
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    showOpenOrClose(true)
                    Toast.makeText(this@WindowCoveringActivity, "open window covering " + if (open) "success" else "failure", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btClose.setOnClickListener {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                val open = ClusterUtil.closeWindowCovering(device.endpoint, lastDeviceId, this@WindowCoveringActivity)
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    showOpenOrClose(false)
                    Toast.makeText(this@WindowCoveringActivity, "close window covering " + if (open) "success" else "failure", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btStop.setOnClickListener {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                val open = ClusterUtil.stopWindowCovering(device.endpoint, lastDeviceId, this@WindowCoveringActivity)
                isStopAnimation = !isStopAnimation
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    pauseOrResumeAnimation(isStopAnimation)
                    Toast.makeText(this@WindowCoveringActivity, "stop window covering " + if (open) "success" else "failure", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun pauseOrResumeAnimation(resume: Boolean) {
//        if (resume) {
//            binding.lavDoor.progress = binding.lavDoor.progress
//            binding.lavDoor.setMaxProgress(1f)
//            binding.lavDoor.speed = 0.1f
//            binding.lavDoor.repeatCount = 0
//            binding.lavDoor.playAnimation()
//        } else {
            binding.lavDoor.pauseAnimation()

//        }
    }

    private fun showOpenOrClose(open: Boolean) {
        if (isStopAnimation && (open == lastAnimationFlag)) {
            binding.lavDoor.resumeAnimation()
            isStopAnimation = !isStopAnimation
            return
        }
        if (open) {
//            binding.lavDoor.addValueCallback(
//                KeyPath("**"), LottieProperty.COLOR_FILTER,
//                {
//                    PorterDuffColorFilter(getColor(R.color.green), PorterDuff.Mode.SRC_ATOP)
//                })
    Log.d(TAG, "progress=${binding.lavDoor.progress}")
            binding.lavDoor.progress = binding.lavDoor.progress
            binding.lavDoor.setMaxProgress(0.5f)
            binding.lavDoor.speed = 0.1f
            binding.lavDoor.repeatCount = 0
            binding.lavDoor.playAnimation()
        } else {
            binding.lavDoor.progress = binding.lavDoor.progress
            binding.lavDoor.setMaxProgress(0.5f)
            binding.lavDoor.speed = -0.3f
            binding.lavDoor.repeatCount = 0
            binding.lavDoor.playAnimation()
//            binding.lavDoor.addValueCallback(
//                KeyPath("**"), LottieProperty.COLOR_FILTER,
//                {
//                    PorterDuffColorFilter(getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP)
//                })
//            binding.lavDoor.setMaxProgress(0f)
        }
        lastAnimationFlag = open
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

    private fun showPercentage(device: ShutterDevice) {
        binding.tvTiltPercentage.text = device.getTiltPercentage(this)
        binding.tvLiftPercentage.text = device.getLiftPercentage(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.lavDoor.cancelAnimation()
    }
}