package com.se.wiser

import android.app.Application
import com.se.wiser.data.AppContainer
import com.se.wiser.data.AppContainerImpl
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.UpdateEvent
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableStateFlow

@HiltAndroidApp
class App: Application() {

    var pairGatewayState: Boolean = false
    var currentConfigType: Int = 0
    var hasAnyCommissionComplete: Boolean = false
    var hasNewBridgePairingCompleted: Boolean = false
    val commissionTimeout = 120

    var deviceStateMap: HashMap<Int, BaseDevice> = hashMapOf()
    var wifiDeviceStateMap: HashMap<String, BaseDevice> = hashMapOf()
    var threadDeviceStateMap: HashMap<String, BaseDevice> = hashMapOf()
    var observeDeviceState: MutableStateFlow<UpdateEvent> = MutableStateFlow(UpdateEvent(0, 0))
    //deviceId
    var deviceIdMap: HashMap<String, Long> = hashMapOf()

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        //init
//        AndroidChipPlatform(
//            AndroidBleManager(),
//            PreferencesKeyValueStoreManager(this),
//            PreferencesConfigurationManager(this),
//            NsdManagerServiceResolver(this),
//            object : ChipMdnsCallbackImpl() {
//
//            })
    }

    fun clearAllCache() {
        pairGatewayState = false
        currentConfigType = 0
        hasAnyCommissionComplete = false
        hasNewBridgePairingCompleted = false
        deviceStateMap.clear()
    }
}