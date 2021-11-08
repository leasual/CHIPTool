package com.se.wiser.viewmodel

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chip.devicecontroller.ChipClusters
import com.google.chip.chiptool.ChipClient
import com.google.chip.chiptool.GenericChipDeviceListener
import com.google.chip.chiptool.setuppayloadscanner.CHIPDeviceInfo
import com.google.chip.chiptool.util.DeviceIdUtil
import com.se.wiser.App
import com.se.wiser.utils.ClusterUtil
import com.se.wiser.view.PairingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class PairingViewModel(val app: Context): ViewModel() {

    private var _uiState = MutableStateFlow(PairingUiModel(null))
    public val uiState = _uiState
    private var lastDeviceId by Delegates.notNull<Long>()
    private var gatt: BluetoothGatt? = null
    companion object {
        private const val TAG = "PairingViewModel"
    }

    init {
        viewModelScope.launch {
            try {
                ChipClient.getDeviceController(app).setCompletionListener(ConnectionCallback())
            }catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    fun pairing(ipAddress: String, port: Int, deviceInfo: CHIPDeviceInfo, deviceId: Long) {

        var portInt = 5540
        try {
            portInt = port
        }catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        viewModelScope.launch(Dispatchers.IO) {

            try {
                ChipClient.getDeviceController(app).pairDeviceWithAddress(deviceId, ipAddress, portInt,
                    deviceInfo.discriminator, deviceInfo.setupPinCode, null)
            }catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }

    fun pairing(gatt: BluetoothGatt, deviceInfo: CHIPDeviceInfo, deviceId: Long, lastDeviceId: Long) {
        this.lastDeviceId = lastDeviceId
        this.gatt = gatt
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val connId = com.google.chip.chiptool.bluetooth.BluetoothManager().connectionId
                ChipClient.getDeviceController(app).pairDevice(gatt, connId, deviceId, deviceInfo.setupPinCode, null)
            }catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
    fun closeBle() {
        gatt?.disconnect()
    }

    inner class ConnectionCallback : GenericChipDeviceListener() {
        override fun onConnectDeviceComplete() {
            Log.d(TAG, "onConnectDeviceComplete")
        }

        override fun onStatusUpdate(status: Int) {
            Log.d(TAG, "Pairing status update: $status")
        }

        override fun onPairingComplete(code: Int) {
            Log.d(TAG, "onPairingComplete: $code")
            _uiState.value = PairingUiModel(pairingSuccess = code == 0)
        }

//        private fun refreshDeviceList(nextDeviceId: Long) {
//
//            for (deviceId: Long in 0..nextDeviceId) {
//                Log.d(TAG, "get deviceId index= $deviceId")
//                var devicePointer = 0L
//                try {
//                    devicePointer = ChipClient.getDeviceController(app).getDevicePointer(deviceId)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                if (devicePointer != 0L) {
//                    Log.d(TAG, "get deviceId result= $deviceId")
//                }
//            }
//        }

        override fun onPairingDeleted(code: Int) {
            Log.d(TAG, "onPairingDeleted: $code")
        }

        override fun onCommissioningComplete(nodeId: Long, code: Int) {
            Log.d(TAG, "onNetworkCommissioningComplete: $code")
            _uiState.value = PairingUiModel(closeBle = code == 0)

//            viewModelScope.launch(Dispatchers.IO) {
//                ClusterUtil.getThreadNetworkCommissioningCluster(
//                    0,
//                    lastDeviceId
//                )
//                    .readRouteTableListAttribute(object :
//                        ChipClusters.ThreadNetworkDiagnosticsCluster.RouteTableListAttributeCallback {
//                        override fun onSuccess(routeTableList: MutableList<ChipClusters.ThreadNetworkDiagnosticsCluster.RouteTableListAttribute>?) {
//                            Log.d(TAG, "readRouteTableListAttribute size=${routeTableList?.size}")
//                            routeTableList?.map {
//                                Log.d(TAG, "route table=$it")
//                            }
//                        }
//
//                        override fun onError(e: java.lang.Exception?) {
//                            Log.d(TAG, "readRouteTableListAttribute error")
//                            e?.printStackTrace()
//                        }
//
//                    })
//            }
        }

        override fun onCloseBleComplete() {
            Log.d(TAG, "onCloseBleComplete")
        }

        override fun onError(error: Throwable?) {
            Log.d(TAG, "onError: $error")
            _uiState.value = PairingUiModel(pairingSuccess = false)
        }
    }

    data class PairingUiModel(
        var pairingSuccess: Boolean? = null,
        var closeBle: Boolean? = null
    )

    override fun onCleared() {
        super.onCleared()
        try {
            gatt?.disconnect()
            gatt?.close()
            viewModelScope.cancel()
        }catch (e: Exception) {
           e.printStackTrace()
        }
    }
}