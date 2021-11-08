package com.se.wiser.data

import com.se.wiser.App
import com.se.wiser.data.device.GatewayRepository
import com.se.wiser.data.gateway.DeviceRepository

interface AppContainer {
    val gatewayRepository: GatewayRepository
    val deviceRepository: DeviceRepository
}

class AppContainerImpl(private val app: App): AppContainer {
    override val gatewayRepository: GatewayRepository by lazy { GatewayRepository() }
    override val deviceRepository: DeviceRepository by lazy { DeviceRepository() }
}