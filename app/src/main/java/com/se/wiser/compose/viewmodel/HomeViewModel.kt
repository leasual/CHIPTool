package com.se.wiser.compose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.chip.chiptool.ChipClient
import com.se.wiser.App
import com.se.wiser.data.MatterDatabase
import com.se.wiser.model.*
import com.se.wiser.utils.ClusterId
import com.se.wiser.utils.ClusterUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class HomeViewModel @Inject constructor(val app: App/*,
                    val gatewayRepository: GatewayRepository,
                    val deviceRepository: DeviceRepository*/
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _uiState = MutableStateFlow(MainUiModel(null))
    val uiState = _uiState
    private val mainDeviceList = arrayListOf<Any>()
    private var lastDeviceId: Long = 0
    //
    private val _expandedCardIdsList = MutableStateFlow(listOf<Int>())
    private val _expandedComposeCardIdsList = MutableStateFlow(listOf<Int>())
    val expandedCardIdsList: StateFlow<List<Int>> get() = _expandedCardIdsList
    val expandedComposeCardIdsList: StateFlow<List<Int>> get() = _expandedComposeCardIdsList

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

    fun getUserAndHomeList() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    /**
     * OnOff device
     */
    fun onOffClick(device: BaseDevice) {
        var state = false
        when(device) {
            is OnOffDevice -> state = device.state
            is DimmerDevice -> state = device.state
            is SocketDevice -> state = device.state
        }
//        viewModelScope.launch {
//            ClusterUtil.sendOnOff(
//                device.endpoint, DeviceIdUtil.getLastDeviceId(app),
//                state, app)
//        }
    }

    /**
     * expanded or fold sub device
     */
    fun onCardArrowClicked(cardId: Int) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) list.remove(cardId) else list.add(cardId)
        }
    }


    /**
     * expanded or fold compose device
     */
    fun onComposeCardArrowClicked(cardId: Int) {
        _expandedComposeCardIdsList.value = _expandedComposeCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) list.remove(cardId) else list.add(cardId)
        }
    }

    /**
     * get bridge device list
     */
    fun getDeviceList(lastDeviceId: Long) {
        _uiState.value = MainUiModel(arrayListOf(), isLoading = true)
        this.lastDeviceId = lastDeviceId
        viewModelScope.launch(Dispatchers.IO) {

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
                                state = ClusterUtil.readOnOffAttribute(endpoint, lastDeviceId, app)
                            }
                            ChipClient.getDeviceController(app).addDynamicEndpoint(
                                endpoint.toLong(),
                                device[0].type.toInt(),
                                serverListInt
                            )
                            val subDevice = ClusterUtil.createNewDevice(productId, endpoint, label, state)
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
                        serverList.mapIndexed { index, data -> serverListInt[index] = data.toInt() }
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
                        val subDevice = ClusterUtil.createNewDevice(productId, endpoint, label, state)
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
            _uiState.value = MainUiModel(deviceList = mainDeviceList, isLoading = false)
            if (readPartListTimer == null) {
//                taskForReadPartList()
            }
        }
    }

    /**
     * observe part list change
     */
//    private fun taskForReadPartList() {
//        readPartListTimer = Timer()
//        readPartListTimer?.schedule(object : TimerTask(){
//            override fun run() {
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
//                            getDeviceList(lastDeviceId)
//                        }
//                    }
//
//                    override fun onError(p0: Exception?) {
//                        p0?.printStackTrace()
//                    }
//
//                })
//                }
//        }, 2, 60 * 60 * 1000)
//    }

    /**
     * commission window
     */
//    fun openCommissioningWindow(deviceId: Long, descriminator: Int) {
//        openCommissioningJob?.cancel()
//        openCommissioningJob = null
//        openCommissioningJob = viewModelScope.launch(Dispatchers.IO) {
//            val commissioningInfo = ClusterUtil.openCommissionWindow(timeout = app.commissionTimeout, deviceId = deviceId, descriminator = descriminator, context = app)
//            Log.d(TAG, "commissioningInfo= $commissioningInfo")
//            val device = mainDeviceList.firstOrNull() { (it as? GroupDevice)?.type == BridgeDevice }
//            (device as? GroupDevice)?.let { it.isOpenCommissionWindow = true }
//            _uiState.value = MainUiModel(commissionState = true, commissioningInfo = commissioningInfo)
//            submitCommissionComplete(deviceId)
//        }
//    }
//
//    private fun submitCommissionComplete(deviceId: Long) {
//        ClusterUtil.getGeneralCommissionCluster(0, deviceId, app)
//            .commissioningComplete(object : ChipClusters.GeneralCommissioningCluster.CommissioningCompleteResponseCallback {
//                override fun onSuccess(errorCode: Int, debugText: String?) {
//                    Log.d(TAG, "commissioning complete errorCode=$errorCode debugText=$debugText")
//                }
//
//                override fun onError(e: Exception?) {
//                    Log.d(TAG, "commissioning complete error=${e?.message}")
//                }
//
//            })
//    }

    /**
     * update device state
     */
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