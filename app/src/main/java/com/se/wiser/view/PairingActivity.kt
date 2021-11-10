package com.se.wiser.view

import android.bluetooth.BluetoothGatt
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.github.druk.dnssd.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.chip.chiptool.ChipClient
import com.google.chip.chiptool.R
import com.google.chip.chiptool.bluetooth.BluetoothManager
import com.google.chip.chiptool.databinding.ActivityPairingBinding
import com.google.chip.chiptool.setuppayloadscanner.CHIPDeviceInfo
import com.google.chip.chiptool.util.DeviceIdUtil
import com.se.wiser.App
import com.se.wiser.model.*
import com.se.wiser.utils.ClusterUtil
import com.se.wiser.utils.MDNSUtil
import com.se.wiser.viewmodel.PairingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class PairingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPairingBinding
    private val viewModel: PairingViewModel by lazy { PairingViewModel(applicationContext) }
    private var gatt: BluetoothGatt? = null
    private val bluetoothManager: BluetoothManager by lazy { BluetoothManager() }
    private var networkType: Int = 0

    companion object {
        private const val TAG = "PairingViewModel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPairingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    override fun onStop() {
        super.onStop()
        MDNSUtil.stop()
        gatt = null
    }

    private fun setupViews() {
        val deviceInfo = intent.getParcelableExtra("deviceInfo") as? CHIPDeviceInfo
        networkType = intent.getIntExtra("networkType", 0)
        Log.d(TAG, "networkType= $networkType")

        binding.tvVersion.text = "Version: ${deviceInfo?.version}"
        binding.tvProductId.text = "ProductId: ${deviceInfo?.productId}"
        binding.tvSetupPinCode.text = "SetupPinCode: ${deviceInfo?.setupPinCode}"
        binding.tvDiscriminator.text = "Discriminator: ${deviceInfo?.discriminator}"
        binding.tvVendorId.text = "VendorId: ${deviceInfo?.vendorId}"
        binding.include.ivBack.setOnClickListener {
            this.finish()
        }

        BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).isDraggable = true
        BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).isGestureInsetBottomIgnored = true

        BottomSheetBehavior.from(binding.bottomSheetWifiLayout.bottomSheetWifiLayout).isDraggable = true
        BottomSheetBehavior.from(binding.bottomSheetWifiLayout.bottomSheetWifiLayout).isGestureInsetBottomIgnored = true

        //set thread info
        if (networkType == 1) {
            binding.bottomSheetLayout.etPanId.setText(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("panId", "36a3"))
            binding.bottomSheetLayout.etMasterKey.setText(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("masterKey", "22da2b5d48ed76802b6f426e72d57a64"))
            binding.bottomSheetLayout.etXpanId.setText(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("xpanId", "dead00beef00cafe"))
            binding.bottomSheetLayout.etChannel.setText(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("channel", "11"))
        } else if (networkType == 2) {
            binding.bottomSheetWifiLayout.etSsid.setText(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("ssid", "Wiser"))
            binding.bottomSheetWifiLayout.etPassword.setText(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("ssidpassword", "wisersmarthome"))
        }
        binding.bottomSheetLayout.btConnect.setOnClickListener {
            if (binding.bottomSheetLayout.etPanId.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter panId", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.bottomSheetLayout.etChannel.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter channel", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.bottomSheetLayout.etXpanId.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter xpanId", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.bottomSheetLayout.etMasterKey.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter master key", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("panId", binding.bottomSheetLayout.etPanId.text.toString())
                .putString("channel", binding.bottomSheetLayout.etChannel.text.toString())
                .putString("xpanId", binding.bottomSheetLayout.etXpanId.text.toString())
                .putString("masterKey", binding.bottomSheetLayout.etMasterKey.text.toString())
                .apply()
            addAndEnableThreadNetwork()
        }
        binding.bottomSheetWifiLayout.btConnect.setOnClickListener {
            if (binding.bottomSheetWifiLayout.etSsid.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter ssid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.bottomSheetWifiLayout.etPassword.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("ssid", binding.bottomSheetWifiLayout.etSsid.text.toString())
                .putString("ssidpassword", binding.bottomSheetWifiLayout.etPassword.text.toString())
                .apply()
            addAndEnableWifiNetwork()
        }

        viewModel.viewModelScope.launch {

            when (networkType) {
                0 -> {
                    val address = ""//findMDNSAddress()
                    Log.d(TAG, "find server address=$address")
                    val browserResponse = MDNSUtil.browser(this@PairingActivity)
                    val resolveResponse = MDNSUtil.startResolve(
                        browserResponse.flags,
                        browserResponse.ifIndex,
                        browserResponse.serviceName ?: "",
                        browserResponse.regType ?: "",
                        browserResponse.domain ?: ""
                    )
                    val addressResponse = MDNSUtil.startRecord(
                        resolveResponse.ifIndex,
                        resolveResponse.serviceName,
                        resolveResponse.regType,
                        resolveResponse.domain,
                        resolveResponse.hostName,
                        resolveResponse.port,
                        resolveResponse.txtRecord
                    )
                    Log.d(TAG, "addressResponse=$addressResponse")
                    if (addressResponse.port == 0) {
                        runOnUiThread { showSuccessView(false) }
                        MDNSUtil.stop()
                        return@launch
                    }
                    val deviceId = DeviceIdUtil.getNextAvailableId(this@PairingActivity)
                    viewModel.pairing(
                        addressResponse.address,
                        addressResponse.port,
                        deviceInfo!!,
                        deviceId
                    )
                    //TODO 当有两个同类设备时会覆盖，有可能导致控制同类最后一个同类设备，需要找到一个区别同类设备当ID
                    (application as App).deviceIdMap[BridgeDevice.javaClass.simpleName] = deviceId
                    DeviceIdUtil.setNextAvailableId(this@PairingActivity, deviceId + 1)
                }
                1, 2 -> {
                    if (networkType == 1) {
                        binding.bottomSheetLayout.bottomSheetLayout.visibility = View.VISIBLE
                    } else if (networkType == 2) {
                        binding.bottomSheetWifiLayout.bottomSheetWifiLayout.visibility = View.VISIBLE
                    }
                    val deviceId = DeviceIdUtil.getNextAvailableId(this@PairingActivity)
                    viewModel.viewModelScope.launch {
                        if (deviceInfo == null) {
                            runOnUiThread { showSuccessView(false) }
                            return@launch
                        }
                        Log.d(TAG, "get deviceInfo= $deviceInfo")
//                    bluetoothManager.getBlueDevices(deviceInfo!!.discriminator)
                        val device = bluetoothManager.getBluetoothDevice(this@PairingActivity, deviceInfo.discriminator)
                        if (device == null) {
                            runOnUiThread { showSuccessView(false) }
                            return@launch
                        }
                        Log.d(TAG, "get deviceInfo= $device")
                        gatt = bluetoothManager.connect(this@PairingActivity, device)
                        if (gatt == null) {
                            runOnUiThread { showSuccessView(false) }
                            return@launch
                        }
                        Log.d(TAG, "get deviceInfo= $gatt")
//                        val lastDeviceId = (application as App).deviceIdMap[if (networkType == 1) ThreadDevice else WIFIDevice] ?: 0L
                        try {
                            viewModel.pairing(
                                gatt!!,
                                deviceInfo,
                                deviceId,
                                deviceId
                            )
                            Log.d(TAG, "get pairing deviceInfo=$deviceInfo")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    (application as App).deviceIdMap[if (networkType == 1) ThreadDevice.javaClass.simpleName else WIFIDevice.javaClass.simpleName] = deviceId
                    DeviceIdUtil.setNextAvailableId(this@PairingActivity, deviceId + 1)
                }
            }
        }

        viewModel.viewModelScope.launch {
            viewModel.uiState.collect {
                it.pairingSuccess?.let {
//                    launch(Dispatchers.IO) {
//                        addThreadNetwork()
//
//                    }
                    if (networkType == 1) {
                        BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).state =
                            BottomSheetBehavior.STATE_EXPANDED
                        Log.d(TAG, "success to saveThreadNetwork")
                    } else if (networkType == 2) {
                        BottomSheetBehavior.from(binding.bottomSheetWifiLayout.bottomSheetWifiLayout).state =
                            BottomSheetBehavior.STATE_EXPANDED
                        Log.d(TAG, "success to save ssid and password")
//                        addAndEnableWifiNetwork()
                    } else {
                        showSuccessView(it)
                    }
//                    showSuccessView(it)
                }
                it.closeBle?.let {
                    viewModel.closeBle()
//                    bluetoothManager.closeBluetooth()
                }
                (application as App).pairGatewayState = it.pairingSuccess ?: false
                (application as App).currentConfigType = networkType
                (application as App).hasNewBridgePairingCompleted = it.pairingSuccess ?: false
                if (!(application as App).hasAnyCommissionComplete && it.pairingSuccess == true) {
                    (application as App).hasAnyCommissionComplete = true
                }
            }
        }
    }

    private fun addAndEnableWifiNetwork() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            try {
                val deviceId = (application as App).deviceIdMap[WIFIDevice.javaClass.simpleName] ?: 0L
                val addWifiNetworkState = ClusterUtil.addWifiNetwork(
                    binding.bottomSheetWifiLayout.etSsid.text.toString(),
                    binding.bottomSheetWifiLayout.etPassword.text.toString(),
                    0,
                    deviceId,
                    this@PairingActivity
                )
                if (addWifiNetworkState) {
                    val enableWifiNetwork = ClusterUtil.enableWifiNetwork(binding.bottomSheetWifiLayout.etSsid.text.toString(),
                    0,
                    deviceId,
                    this@PairingActivity)
                    if (enableWifiNetwork) {
                        gatt?.disconnect()
                        gatt?.close()
                        delay(1000)
                        ChipClient.getDeviceController(this@PairingActivity).updateDevice(
                            0,
                            deviceId
                        )
                        delay(8000)
                        runOnUiThread {
                            showSuccessView(true)
                                this@PairingActivity.finish()
//                            val intent = Intent(
//                                this@PairingActivity,
//                                LightControlActivity::class.java
//                            )
//                            val device = OnOffDevice(false)
//                            device.id = deviceId.toInt()
//                            device.name = "BleLight"
//                            device.endpoint = 1
//                            device.fabricId = 1
//                            intent.putExtra(
//                                "device",
//                                device
//                            )
//                            intent.putExtra("thread", true)
//                            startActivity(intent)
//                            this@PairingActivity.finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@PairingActivity,
                                R.string.rendezvous_over_ble_commissioning_failure_text,
                                Toast.LENGTH_SHORT
                            ).show()
                            showSuccessView(false)
                            BottomSheetBehavior.from(binding.bottomSheetWifiLayout.bottomSheetWifiLayout).state =
                                BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@PairingActivity,
                            R.string.rendezvous_over_ble_commissioning_failure_text,
                            Toast.LENGTH_SHORT
                        ).show()
                        showSuccessView(false)
                        BottomSheetBehavior.from(binding.bottomSheetWifiLayout.bottomSheetWifiLayout).state =
                            BottomSheetBehavior.STATE_COLLAPSED
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addAndEnableThreadNetwork() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            try {
                val deviceId = (application as App).deviceIdMap[ThreadDevice.javaClass.simpleName] ?: 0L
                val addThreadNetworkState = ClusterUtil.addThreadNetwork(
                    deviceId,
                    binding.bottomSheetLayout.etPanId.text.toString().toInt(16),
                    binding.bottomSheetLayout.etChannel.text.toString().toInt(),
                    binding.bottomSheetLayout.etXpanId.text.toString(),
                    binding.bottomSheetLayout.etMasterKey.text.toString(),
                this@PairingActivity)
                Log.d(TAG, "etPanId=${binding.bottomSheetLayout.etPanId.text.toString().toInt(16)}")
                Log.d(TAG, "etChannel=${binding.bottomSheetLayout.etChannel.text.toString().toInt()}")
                Log.d(TAG, "etXpanId=${binding.bottomSheetLayout.etXpanId.text}")
                Log.d(TAG, "etMasterKey=${binding.bottomSheetLayout.etMasterKey.text}")
                Log.d(TAG, "deviceId=$deviceId")
                if (addThreadNetworkState) {
                    val enableThreadNetworkState =
                        ClusterUtil.enableThreadNetwork(deviceId, binding.bottomSheetLayout.etXpanId.text.toString(), this@PairingActivity)
                    if (enableThreadNetworkState) {
                        gatt?.disconnect()
                        gatt?.close()
                        delay(1000)
                        ChipClient.getDeviceController(this@PairingActivity).updateDevice(
                            0,
                            deviceId
                        )
                        delay(5000)
                        runOnUiThread {
                            showSuccessView(true)
                            this@PairingActivity.finish()
//                            val intent = Intent(
//                                this@PairingActivity,
//                                LightControlActivity::class.java
//                            )
//                            val device = OnOffDevice(false)
//                            device.id = deviceId.toInt()
//                            device.name = "BleLight"
//                            device.endpoint = 1
//                            device.fabricId = 1
//                            intent.putExtra(
//                                "device",
//                                device
//                            )
//                            intent.putExtra("thread", true)
//                            startActivity(intent)
//                            this@PairingActivity.finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@PairingActivity,
                                R.string.rendezvous_over_ble_commissioning_failure_text,
                                Toast.LENGTH_SHORT
                            ).show()
                            showSuccessView(false)
                            BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).state =
                                BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@PairingActivity,
                            R.string.rendezvous_over_ble_commissioning_failure_text,
                            Toast.LENGTH_SHORT
                        ).show()
                        showSuccessView(false)
                        BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheetLayout).state =
                            BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@PairingActivity,
                        "parameter is not correct",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun showSuccessView(success: Boolean) {
        binding.animationView.setAnimation(if (success) R.raw.success else R.raw.error)
        binding.animationView.repeatCount = 0
        binding.animationView.playAnimation()
        binding.tvPairing.text = if (success) "Success" else "Failure"
        binding.tvPairing.setTextColor(resources.getColor(if (success) R.color.green else R.color.red))
    }

    private fun addThreadNetwork() {
//        Log.d(TAG, "addThreadNetwork")
//        try {
////            var deviceId = DeviceIdUtil.getNextAvailableId(this@PairingActivity)
////            if (deviceId > 1) {
////                deviceId--
////            }
//            var deviceId = DeviceIdUtil.getLastDeviceId(this)
//            ChipClient.getDeviceController().getConnectedDevicePointer(deviceId, object : GetConnectedDeviceCallbackJni.GetConnectedDeviceCallback {
//                override fun onDeviceConnected(devicePointer: Long) {
//                    Log.d(TAG, "onDeviceConnected=$devicePointer")
//                    //if (devicePointer > 0) {
//                        //val lastDeviceId = DeviceIdUtil.getLastDeviceId(this@PairingActivity)
//                    viewModel.viewModelScope.launch(Dispatchers.IO) {
//                        Log.d(TAG, "addThreadNetwork=$devicePointer")
//                        ClusterUtil.getNetworkCommissioningCluster(0, deviceId).addThreadNetwork(
//                            object :
//                                ChipClusters.NetworkCommissioningCluster.AddThreadNetworkResponseCallback {
//                                override fun onSuccess(errorCode: Int, debugText: String?) {
//                                    Log.d(TAG, "errorCode=$errorCode debugText=$debugText")
//                                    //if (errorCode == 0) {
//                                        enableThreadNetwork(deviceId)
//                                        Log.d(TAG, "updateDevice deviceId=$deviceId")
//                                    //}
//                                }
//
//                                override fun onError(e: java.lang.Exception?) {
//                                    runOnUiThread { showSuccessView(false) }
//                                    e?.printStackTrace()
//                                }
//
//                            }, makeThreadOperationalDataset(
//                                20,
//                                0x2e41,
//                                "50ea07e3f4d6e148".hexToByteArray(),
//                                "1671970599aca4ea2b54587f03c58b86".hexToByteArray()
//                            ), 0, 3000
//                        )
//
//                    }
//                    //}
//                }
//
//                override fun onConnectionFailure(devicePointer: Long, e: java.lang.Exception?) {
//                    Log.d(TAG, "onConnectionFailure=$devicePointer")
//                    e?.printStackTrace()
//                }
//
//            })
//        }catch (e: Exception) {
//            e.printStackTrace()
//        }
    }


    private fun enableThreadNetwork(deviceId: Long) {
//        Log.d(TAG, "enableThreadNetwork")
//        ClusterUtil.getNetworkCommissioningCluster(0, deviceId).enableNetwork(object : ChipClusters.NetworkCommissioningCluster.EnableNetworkResponseCallback {
//            override fun onSuccess(p0: Int, p1: String?) {
//                Log.d(TAG, "enableNetwork p0=$p0 p1=$p1")
//                viewModel.viewModelScope.launch(Dispatchers.IO) {
//                    gatt?.disconnect()
//                    gatt?.close()
//                    delay(5000)
//                    var retryTime = 0
//                    //while (retryTime++ < 10) {
//                        ChipClient.getDeviceController().updateDevice(
//                            0,
//                            deviceId
//                        )
//                        //delay(10 * 1000)
//                    //}
//                    runOnUiThread {
//                        showSuccessView(true)
//                        val intent = Intent(
//                            this@PairingActivity,
//                            LightControlActivity::class.java
//                        )
//                        intent.putExtra(
//                            "device",
//                            Device(1, "BleLight", false, 1, 0)
//                        )
//                        startActivity(intent)
//                        this@PairingActivity.finish()
//                    }
//                }
//            }
//
//            override fun onError(p0: java.lang.Exception?) {
//                p0?.printStackTrace()
//                runOnUiThread {
//                    Toast.makeText(
//                        this@PairingActivity,
//                        R.string.rendezvous_over_ble_commissioning_failure_text,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    showSuccessView(false)
//                }
//            }
//
//        }, "50ea07e3f4d6e148".hexToByteArray(), 0, 10000L)
//        try {
//            ChipClient.getDeviceController().enableThreadNetwork(
//                DeviceIdUtil.getLastDeviceId(this),
//                makeThreadOperationalDataset(
//                    15,
//                    0xfe55,
//                    "c823e90be928522c".hexToByteArray(),
//                    "cefdba6ae7320b974d3b4d82b6c48fc2".hexToByteArray()
//                )
//            )
//            ChipClient.getDeviceController().updateDevice(0, DeviceIdUtil.getLastDeviceId(this))
//            runOnUiThread {
//                showSuccessView(true)
//                val intent = Intent(this, LightControlActivity::class.java)
//                intent.putExtra("device", Device(1, "BleLight", false, 1, 0))
//                startActivity(intent)
//                this.finish()
//            }
//        } catch (e: ChipDeviceControllerException) {
//            runOnUiThread { Toast.makeText(
//                this,
//                R.string.rendezvous_over_ble_commissioning_failure_text,
//                Toast.LENGTH_SHORT
//            ).show() }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            BluetoothManager().cancel()
            gatt?.disconnect()
            gatt?.close()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

}