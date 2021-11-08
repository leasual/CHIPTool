package com.se.wiser.view

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import chip.devicecontroller.ChipDeviceController
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.chip.chiptool.ChipClient
import com.google.chip.chiptool.GenericChipDeviceListener
import com.google.chip.chiptool.R
import com.google.chip.chiptool.databinding.ActivityLightControlBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.se.wiser.App
import com.se.wiser.adapter.GroupAdapter
import com.se.wiser.model.*
import com.se.wiser.utils.ClusterUtil
import com.se.wiser.utils.DeviceUtil
import com.se.wiser.viewmodel.LightControlViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LightControlActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "LightControlActivity"
    }
    private val deviceController: ChipDeviceController
        get() = ChipClient.getDeviceController(this)

    private val viewModel: LightControlViewModel by lazy { LightControlViewModel() }
    private lateinit var binding: ActivityLightControlBinding
    private var onOff: Boolean = false
    private var device: BaseDevice? = null
    private var lastDeviceId: Long = 0
    private var isThread: Boolean = false
    private var commissionCountDownTimer: CountDownTimer? = null
    var commissionTimeout: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLightControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        showLightOnOff(onOff)
    }

    private fun setupViews() {
        deviceController.setCompletionListener(ChipControllerCallback())
        device = intent.getSerializableExtra("device") as BaseDevice
        isThread = intent.getBooleanExtra("thread", false)
        (application as App).deviceIdMap.map {
            Log.d(TAG, "type=${it.key} value=${it.value}")
        }
        lastDeviceId = (application as App).deviceIdMap[(device as BaseDevice).type.javaClass.simpleName] ?: 0L
        Log.d(TAG, "deviceType=${(device as BaseDevice).type} lastDeviceId=$lastDeviceId")
        Log.d(TAG, "light control endpoint=${device}")
        when(device) {
            is OnOffDevice -> { onOff = (device as OnOffDevice).state }
            is DimmerDevice -> { onOff = (device as DimmerDevice).state }
        }
        Log.d(TAG, "device=$device")
        binding.lavLight.setOnClickListener {
            onOff = !onOff
            when(device) {
                is OnOffDevice -> { (device as OnOffDevice).state = onOff }
                is DimmerDevice -> { (device as DimmerDevice).state = onOff }
            }
            showLightOnOff(onOff)
            viewModel.sendOnOff(device!!.endpoint, lastDeviceId,onOff, this)
        }
        binding.include.ivBack.setOnClickListener {
            this.finish()
        }
        if (device is DimmerDevice) {
            binding.mainVerticalSeekBar.progress = ClusterUtil.convertToProgress((device as DimmerDevice).level)
        }
        binding.tvOnProgress.text = "${binding.mainVerticalSeekBar.progress}%"
        binding.mainVerticalSeekBar.setOnProgressChangeListener {
            Log.d(TAG, "progress= $it")
            binding.tvOnProgress.text = "$it%"
        }
        binding.mainVerticalSeekBar.setOnReleaseListener {
            Log.d(TAG, "get progress= ${binding.mainVerticalSeekBar.progress}")
            (device as DimmerDevice).level = ClusterUtil.convertToLevel(binding.mainVerticalSeekBar.progress)
            viewModel.sendLevelControl(device!!.endpoint, lastDeviceId, binding.mainVerticalSeekBar.progress, this)
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
        binding.bottomSheetLayout.etEndpointId.setText("${device!!.endpoint}")
        binding.bottomSheetLayout.etFabricId.setText("${device!!.fabricId}")
        binding.bottomSheetLayout.btUpdate.setOnClickListener {
            try {
                val deviceId = binding.bottomSheetLayout.etDeviceId.text.toString().toInt()
                val endpoint = binding.bottomSheetLayout.etEndpointId.text.toString().toInt()
                viewModel.updateDevice(this, deviceId)
                device?.id = deviceId
                device?.endpoint = endpoint
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        binding.include.swCommission.visibility = View.VISIBLE

        binding.include.swCommission.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d(TAG, "device type")
            //if (isChecked && (device as BaseDevice).type == WIFIDevice ||
            //    (device as BaseDevice).type == ThreadDevice) {
//                val deviceId = binding.bottomSheetLayout.etDeviceId.text.toString().toLong()
//                val endpoint = binding.bottomSheetLayout.etEndpointId.text.toString().toInt()
//                ClusterUtil.openCommissionWindow(0, deviceId, 60, this)
                viewModel.viewModelScope.launch {
//                    val lastDeviceId = (application as App).deviceIdMap[(device as BaseDevice).type.javaClass.simpleName] ?: 0L
//                    val commissioningInfo = ClusterUtil.openCommissionWindow(timeout = (application as App).commissionTimeout,
//                        deviceId = lastDeviceId, descriminator = 3840,
//                        option = 1, context = this@LightControlActivity)
                    val commissioningInfo = viewModel.openCommissionWindow((application as App), (device as BaseDevice))
                    Log.d(TAG, "commissioningInfo= $commissioningInfo")
//                    val qrcode = PreferenceManager.getDefaultSharedPreferences(this@LightControlActivity)
//                        .getString("barcode", "") ?: ""
//                    commissioningInfo.qrCode = qrcode
                    if (isChecked) startCommissionCountDownTimer(commissioningInfo) else stopCommissionCountDownTimer()
                }
            //}
        }

        viewModel.viewModelScope.launch(Dispatchers.Main) {
            (application as App).observeDeviceState.collect {
                Log.d(TAG, "update event=$it device=$device")
                if (it.endpoint == device?.endpoint) {
                    val updateDevice = (application as App).deviceStateMap[it.endpoint]
                    if (updateDevice != null && updateDevice.endpoint == device?.endpoint) {
                        Log.d(TAG, "updateDevice=$updateDevice")
                        when(device) {
                            is OnOffDevice -> {
                                (device as OnOffDevice).state = (updateDevice as OnOffDevice).state
                                showLightOnOff((updateDevice as OnOffDevice).state)
                            }
                            is DimmerDevice -> {
                                (device as DimmerDevice).state = (updateDevice as DimmerDevice).state
                                (device as DimmerDevice).level = (updateDevice as DimmerDevice).level
                                showLightOnOff((updateDevice as DimmerDevice).state)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startCommissionCountDownTimer(commissioningInfo: CommissioningInfo) {
        Log.d(TAG, "start count down timer")
        commissionCountDownTimer?.cancel()
        commissionCountDownTimer = null
        binding.cvCommissionWindow.visibility = View.VISIBLE
        commissionCountDownTimer = object : CountDownTimer((application as App).commissionTimeout * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "tick= ${millisUntilFinished / 1000}s")
                binding.tvCommissionTime.text = "${millisUntilFinished / 1000}s"
                binding.tvCommissionCode.text = commissioningInfo?.manualCode
                try {
                    val barcodeEncoder = BarcodeEncoder()

                    val qrcodeBitmap = barcodeEncoder.encodeBitmap(
                        commissioningInfo?.qrCode,
                        BarcodeFormat.QR_CODE,
                        100,
                        100
                    )
                    binding.ivQRCode.setImageBitmap(qrcodeBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                Log.d(GroupAdapter.TAG, "tick onFinish")
                binding.include.swCommission.setCheckedNoEvent(false)
                binding.cvCommissionWindow.visibility = View.GONE
            }
        }
        commissionCountDownTimer?.start()
    }
    private fun stopCommissionCountDownTimer() {
        commissionCountDownTimer?.cancel()
        commissionCountDownTimer = null
        binding.include.swCommission.setCheckedNoEvent(false)
        binding.cvCommissionWindow.visibility = View.GONE
        binding.tvCommissionTime.text = ""
    }

    override fun onStop() {
        super.onStop()
        stopCommissionCountDownTimer()
    }

    private fun showLightOnOff(onOff: Boolean) {
        Log.d(TAG, "showLightOnOff=$onOff")
        binding.mainVerticalSeekBar.visibility = if (device is DimmerDevice) View.VISIBLE else View.GONE
        binding.tvOnProgress.visibility = if (device is DimmerDevice) View.VISIBLE else View.GONE
        if (device is DimmerDevice) {
            Log.d(TAG, "level=${(device as DimmerDevice).level}")
            binding.mainVerticalSeekBar.progress = ClusterUtil.convertToProgress((device as DimmerDevice).level)
        }
        if (onOff) {
            binding.lavLight.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
                {
                    PorterDuffColorFilter(getColor(R.color.green), PorterDuff.Mode.SRC_ATOP)
                })
            binding.lavLight.progress = 0f
            binding.lavLight.setMaxProgress(1f)
            binding.lavLight.speed = 2f
            binding.lavLight.repeatCount = 0
            binding.lavLight.playAnimation()
        } else {
            binding.lavLight.progress = 0f
            binding.lavLight.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER,
                {
                    PorterDuffColorFilter(getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP)
                })
            binding.lavLight.setMaxProgress(0f)
        }
        when(device) {
            is OnOffDevice -> DeviceUtil.getStateName(device!!.productModeId, if ((device as OnOffDevice).state) 1 else 0, this)
                .also { binding.tvOnOff.text = it }
            is DimmerDevice -> DeviceUtil.getStateName(device!!.productModeId, if ((device as DimmerDevice).state) 1 else 0, this)
                .also { binding.tvOnOff.text = it }
        }

        binding.tvOnOff.setTextColor(resources.getColor(if (onOff) R.color.green else android.R.color.darker_gray))
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