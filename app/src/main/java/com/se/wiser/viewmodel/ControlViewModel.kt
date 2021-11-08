package com.se.wiser.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.chip.chiptool.ChipClient
import com.se.wiser.App
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.CommissioningInfo
import com.se.wiser.utils.ClusterUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class ControlViewModel: ViewModel() {

    fun sendOnOff(endpoint: Int, lastDeviceId: Long, onOff: Boolean, activity: Activity) {
        viewModelScope.launch {
            ClusterUtil.sendOnOff(endpoint, lastDeviceId, onOff, activity)
        }
    }
    fun sendLevelControl(endpoint: Int, lastDeviceId: Long, level: Int, activity: Activity) {
        viewModelScope.launch {
            ClusterUtil.sendLevelControl(endpoint, lastDeviceId, level, activity)
        }
    }
    fun updateDevice(context: Context, deviceId: Int) {
        viewModelScope.launch {
            ChipClient.getDeviceController(context).updateDevice(
                0,
                deviceId.toLong()
            )
        }
    }

    suspend fun openCommissionWindow(app: App, device: BaseDevice): CommissioningInfo {

        return withContext(viewModelScope.coroutineContext) {
            val lastDeviceId = app.deviceIdMap[device.type.javaClass.simpleName] ?: 0L
            ClusterUtil.openCommissionWindow(
                timeout = app.commissionTimeout,
                deviceId = lastDeviceId, descriminator = 3840,
                option = 1, context = app
            )
        }

    }

    override fun onCleared() {
        super.onCleared()
    }
}