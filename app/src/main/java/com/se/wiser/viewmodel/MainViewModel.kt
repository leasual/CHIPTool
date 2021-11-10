package com.se.wiser.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chip.devicecontroller.ChipClusters
import com.google.chip.chiptool.ChipClient
import com.se.wiser.App
import com.se.wiser.model.*
import com.se.wiser.utils.ClusterId
import com.se.wiser.utils.ClusterUtil
import com.se.wiser.utils.DeviceUtil
import com.se.wiser.utils.ProductModeId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random


class MainViewModel(val app: App) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _uiState = MutableStateFlow(MainUiModel(null))
    val uiState = _uiState
    private val mainDeviceList = arrayListOf<Any>()
    private var lastDeviceId: Long = 0

    private var partListCache = mutableListOf<Int>()
    private var readPartListTimer: Timer? = null
    private var openCommissioningJob: Job? = null
    private var revokeCommissioningJob: Job? = null

    data class MainUiModel(
        val deviceList: ArrayList<Any>? = null,
        var isLoading: Boolean? = null,
        var commissionState: Boolean? = null,
        var revokeCommissionState: Boolean? = null,
        var commissioningInfo: CommissioningInfo?= null,
        val random: Long = System.currentTimeMillis()
    )

    fun getDeviceList(lastDeviceId: Long, deviceType: DeviceType) {
//        testData()
//        return
        _uiState.value = MainUiModel(arrayListOf(), isLoading = true)
        this.lastDeviceId = lastDeviceId
        viewModelScope.launch(Dispatchers.IO) {
            when(deviceType) {
                BridgeDevice -> {
                    val partList = ClusterUtil.getPartList(0, lastDeviceId, app)
                    //cache first
                    if (partListCache.isEmpty()) {
                        partListCache.addAll(partList)
                    }
                    val deviceList = arrayListOf<Any>()
                    partList.mapIndexed { _, endpoint ->
                        Log.d(TAG, "partList Compose endpoint= $endpoint")

                        val composeLabel = ClusterUtil.getUserLabel(endpoint, lastDeviceId, app)
                        val composeDevice = ComposeDevice(
                            Random(1000).nextLong(), composeLabel,
                            arrayListOf(), true
                        )
                        val composeDeviceList = arrayListOf<Any>()
                        val composePartList = ClusterUtil.getPartList(endpoint, lastDeviceId, app)
                        Log.d(TAG, "composeLabel=$composeLabel partList size= ${composePartList.size}")
                        val level = 0
                        if (composePartList.isNotEmpty()) {
                            val productId = ClusterUtil.getProductName(endpoint, lastDeviceId, app)
                            Log.d(TAG, "compose productId=$productId")
                            composePartList.mapIndexed { index, endpoint ->
                                Log.d(TAG, "compose device partList= $endpoint")
                                val device = ClusterUtil.getDeviceList(endpoint, lastDeviceId, app)
                                val label = ClusterUtil.getUserLabel(endpoint, lastDeviceId, app)
                                val serverList = ClusterUtil.getServerList(endpoint, lastDeviceId, app)
                                Log.d(TAG, "serverList=$serverList")
                                if (device.isNotEmpty() && serverList.isNotEmpty()) {
                                    val serverListInt = IntArray(serverList.size)
                                    serverList.mapIndexed { index, data ->
                                        serverListInt[index] = data.toInt()
                                    }
                                    var state = false
                                    if (serverListInt.contains(ClusterId.ZCL_ON_OFF_CLUSTER_ID)) {
                                        state =
                                            ClusterUtil.readOnOffAttribute(endpoint, lastDeviceId, app)
                                    }
                                    ChipClient.getDeviceController(app).addDynamicEndpoint(
                                        endpoint.toLong(),
                                        device[0].type.toInt(),
                                        serverListInt
                                    )
                                    val subDevice =
                                        ClusterUtil.createNewDevice(productId, endpoint, label, state)
                                    ClusterUtil.subscribeAndReportByServerList(
                                        endpoint,
                                        lastDeviceId,
                                        app,
                                        subDevice,
                                        serverListInt,
                                        this
                                    )
                                    composeDeviceList.add(subDevice)
                                    app.deviceStateMap[endpoint] = subDevice
                                } else {
                                    Log.e(TAG, "no device")
                                }
                            }
                            composeDevice.subDeviceList = composeDeviceList
                            deviceList.add(composeDevice)
                        } else {
                            val device = ClusterUtil.getDeviceList(endpoint, lastDeviceId, app)
                            val label = ClusterUtil.getUserLabel(endpoint, lastDeviceId, app)
                            val serverList = ClusterUtil.getServerList(endpoint, lastDeviceId, app)
                            val productId = ClusterUtil.getProductName(endpoint, lastDeviceId, app)
                            Log.d(TAG, "productId=$productId")
                            Log.d(TAG, "serverList=$serverList")
                            if (device.isNotEmpty() && serverList.isNotEmpty()) {
                                val serverListInt = IntArray(serverList.size)
                                serverList.mapIndexed { index, data ->
                                    serverListInt[index] = data.toInt()
                                }
                                var state = false
                                if (serverListInt.contains(ClusterId.ZCL_ON_OFF_CLUSTER_ID)) {
                                    state = ClusterUtil.readOnOffAttribute(endpoint, lastDeviceId, app)
                                }
                                ChipClient.getDeviceController(app)
                                    .addDynamicEndpoint(
                                        endpoint.toLong(),
                                        device[0].type.toInt(),
                                        serverListInt
                                    )
                                //create new device
                                val subDevice =
                                    ClusterUtil.createNewDevice(productId, endpoint, label, state)
                                ClusterUtil.subscribeAndReportByServerList(
                                    endpoint,
                                    lastDeviceId,
                                    app,
                                    subDevice,
                                    serverListInt,
                                    this
                                )
                                Log.d(TAG, "endpoint=$endpoint read level=$level")
                                deviceList.add(subDevice)
                                app.deviceStateMap[endpoint] = subDevice
                            } else {
                                Log.e(TAG, "no device")
                            }
                        }
                    }
                    updateSubDevices(deviceList, BridgeDevice)
                    if (readPartListTimer == null) {
                        taskForReadPartList()
                    }
                }
                WIFIDevice -> {
                    val serverList = IntArray(1)
                    serverList[0] = ClusterId.ZCL_ON_OFF_CLUSTER_ID
                    val subDevice =
                        ClusterUtil.createNewDevice(ProductModeId.SWITCH_1G, 1, "WiFi", false)
                    ClusterUtil.subscribeAndReportByServerList(
                        1,
                        lastDeviceId,
                        app,
                        subDevice,
                        serverList,
                        this
                    )
                    subDevice.type = WIFIDevice
                    app.deviceStateMap[++DeviceUtil.WIFI_PREFIX] = subDevice
                    Log.d(TAG, "endpoint=${DeviceUtil.WIFI_PREFIX}")
                    val wifiDeviceList = arrayListOf<Any>()
                    app.deviceStateMap.map {
                        Log.d(TAG, "wifi device=$it key=${it.key}")
                        if (it.key >= 10000) {
                            wifiDeviceList.add(it.value)
                        }
                    }
                    updateSubDevices(wifiDeviceList, WIFIDevice)
                }
                ThreadDevice -> {
                    val serverList = IntArray(1)
                    serverList[0] = ClusterId.ZCL_ON_OFF_CLUSTER_ID
                    val subDevice =
                        ClusterUtil.createNewDevice(ProductModeId.SWITCH_1G, 1, "Thread", false)
                    ClusterUtil.subscribeAndReportByServerList(
                        1,
                        lastDeviceId,
                        app,
                        subDevice,
                        serverList,
                        this
                    )
                    subDevice.type = ThreadDevice
                    app.deviceStateMap[DeviceUtil.THREAD_PREFIX++] = subDevice
                    Log.d(TAG, "endpoint=${DeviceUtil.THREAD_PREFIX}")
                    val threadDeviceList = arrayListOf<Any>()
                    app.deviceStateMap.map {
                        Log.d(TAG, "wifi device=$it key=${it.key}")
                        if (it.key in 1000..9999) {
                            threadDeviceList.add(it.value)
                        }
                    }
                    updateSubDevices(threadDeviceList, ThreadDevice)
                }
            }

            _uiState.value = MainUiModel(deviceList = mainDeviceList, isLoading = false)
        }
    }

    private fun testData() {
        viewModelScope.launch {
            var subDevice = ClusterUtil.createNewDevice(ProductModeId.SWITCH_1G, 0, "Dimmer", true)
            val deviceList = arrayListOf<Any>()
            deviceList.add(subDevice)
            app.deviceStateMap[0] = subDevice
            subDevice = ClusterUtil.createNewDevice(ProductModeId.SMART_SOCKET, 1, "Socket", true)
            deviceList.add(subDevice)
            app.deviceStateMap[1] = subDevice
            updateSubDevices(deviceList, BridgeDevice)
            _uiState.value = MainUiModel(deviceList = mainDeviceList, isLoading = false)
        }
    }

    private suspend fun taskForReadPartList() {
        readPartListTimer = Timer()
        readPartListTimer?.schedule(object : TimerTask(){
            override fun run() {

//                ClusterUtil.getDescriptorCluster(
//                    0,
//                    lastDeviceId
//                    , app).readPartsListAttribute(object : ChipClusters.DescriptorCluster.PartsListAttributeCallback {
//                    override fun onSuccess(partList: MutableList<Int>?) {
//                        Log.d(TAG, "partList=$partList")
//                        if (partList == null) return
//                        if (!((partList.containsAll(partListCache)) && partListCache.containsAll(partList))) {
//                            Log.d(TAG, "partList change=$partList")
//                            partListCache.clear()
//                            partListCache.addAll(partList)
//                            getDeviceList(lastDeviceId, BridgeDevice)
//                        }
//                    }
//
//                    override fun onError(p0: Exception?) {
//                        p0?.printStackTrace()
//                    }
//
//                })
                }
        }, 2, 60*60*24 * 1000)
    }

    fun openCommissioningWindow(deviceId: Long, descriminator: Int) {
        openCommissioningJob?.cancel()
        openCommissioningJob = null
        openCommissioningJob = viewModelScope.launch(Dispatchers.IO) {
            val commissioningInfo = ClusterUtil.openCommissionWindow(timeout = app.commissionTimeout, deviceId = deviceId, descriminator = descriminator, context = app)
            Log.d(TAG, "commissioningInfo= $commissioningInfo")
            val device = mainDeviceList.firstOrNull() { (it as? GroupDevice)?.type == BridgeDevice }
            (device as? GroupDevice)?.let { it.isOpenCommissionWindow = true }
            _uiState.value = MainUiModel(commissionState = true, commissioningInfo = commissioningInfo)
            submitCommissionComplete(deviceId)
        }
    }

    private suspend fun submitCommissionComplete(deviceId: Long) {
        ClusterUtil.getGeneralCommissionCluster(0, deviceId, app)
            .commissioningComplete(object : ChipClusters.GeneralCommissioningCluster.CommissioningCompleteResponseCallback {
                override fun onSuccess(errorCode: Int, debugText: String?) {
                    Log.d(TAG, "commissioning complete errorCode=$errorCode debugText=$debugText")
                }

                override fun onError(e: Exception?) {
                    Log.d(TAG, "commissioning complete error=${e?.message}")
                }

            })
    }

    private fun updateSubDevices(deviceList: ArrayList<Any>, type: DeviceType) {
        val device = mainDeviceList.firstOrNull() { (it as? GroupDevice)?.type == type }
        val name = when(type) {
            BridgeDevice -> "ZB-Matter Bridge"
            ThreadDevice -> "Thread Group"
            WIFIDevice -> "WIFI Group"
            else -> ""
        }
        (device as? GroupDevice)?.let { device.subDeviceList = deviceList } ?: mainDeviceList.add(
            GroupDevice(
                type,
                name,
                deviceList,
                true
            ))
    }

    override fun onCleared() {
        super.onCleared()
        readPartListTimer?.cancel()
        readPartListTimer = null
        openCommissioningJob?.cancel()
        openCommissioningJob = null
        revokeCommissioningJob?.cancel()
        revokeCommissioningJob = null
    }
}