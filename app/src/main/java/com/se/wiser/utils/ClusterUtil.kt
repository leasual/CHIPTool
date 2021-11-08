package com.se.wiser.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import chip.devicecontroller.ChipClusters
import chip.devicecontroller.ChipClusters.BaseChipCluster
import chip.devicecontroller.ChipDeviceController
import chip.devicecontroller.GetConnectedDeviceCallbackJni
import com.google.chip.chiptool.ChipClient
import com.google.chip.chiptool.GenericChipDeviceListener
import com.se.wiser.App
import com.se.wiser.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

object ClusterUtil {

    private const val TAG = "ClusterUtil"

    private const val NUM_CHANNEL_BYTES = 3
    private const val NUM_PANID_BYTES = 2
    private const val NUM_XPANID_BYTES = 8
    private const val NUM_MASTER_KEY_BYTES = 16
    private const val TYPE_CHANNEL = 0 // Type of Thread Channel TLV.
    private const val TYPE_PANID = 1 // Type of Thread PAN ID TLV.
    private const val TYPE_XPANID = 2 // Type of Thread Extended PAN ID TLV.
    private const val TYPE_MASTER_KEY = 5 // Type of Thread Network Master Key TLV.

    public var globalNodeId: Long = 0

    init {
        globalNodeId = System.currentTimeMillis()
    }

    private suspend fun getNetworkCommissioningCluster(endpoint: Int, lastDeviceId: Long, context: Context): ChipClusters.NetworkCommissioningCluster {
        return ChipClusters.NetworkCommissioningCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getThreadNetworkCommissioningCluster(endpoint: Int, lastDeviceId: Long, context: Context): ChipClusters.ThreadNetworkDiagnosticsCluster {
        return ChipClusters.ThreadNetworkDiagnosticsCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    suspend fun getDescriptorCluster(endpoint: Int, lastDeviceId: Long, context: Context): ChipClusters.DescriptorCluster {
        return ChipClusters.DescriptorCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getFixedLabelCluster(endpoint: Int, lastDeviceId: Long, context: Context): ChipClusters.FixedLabelCluster {
        return ChipClusters.FixedLabelCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getBridgeDeviceBasicCluster(
        endpoint: Int,
        lastDeviceId: Long, context: Context
    ): ChipClusters.BridgedDeviceBasicCluster {
        return ChipClusters.BridgedDeviceBasicCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    suspend fun getOnOffCluster(endpoint: Int, lastDeviceId: Long, context: Context): ChipClusters.OnOffCluster {
        return ChipClusters.OnOffCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    public suspend fun getAdministratorCommissioningCluster(endpoint: Int,
                                                     lastDeviceId: Long, context: Context): ChipClusters.AdministratorCommissioningCluster {
        return ChipClusters.AdministratorCommissioningCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    public suspend fun getGeneralCommissionCluster(endpoint: Int,
                                           lastDeviceId: Long, context: Context): ChipClusters.GeneralCommissioningCluster {
        return ChipClusters.GeneralCommissioningCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    suspend fun openCommissionWindow(deviceId: Long = 0, descriminator: Int, timeout: Int = 300, iteration: Int = 1000,
                                     option: Int = 1, context: Context): CommissioningInfo {

        return callbackFlow {
            val callback =
                ChipDeviceController.OnCommissioningListener { manualCode, qrCode ->
                    Log.d("ClusterUtils", "openCommissionWindow success")
                    kotlin.runCatching {
                        trySend(CommissioningInfo(manualCode, qrCode))
                    }
                }
            try {
                //deviceId, timeout, iteration, discriminator, option
                ChipClient.getDeviceController(context).openCommissioningWindow(deviceId, timeout, iteration,
                    descriminator, option, callback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(CommissioningInfo("", ""))
                }
            }
            awaitClose {  }
        }.first()
    }

    suspend fun getLevelControlCluster(
        endpoint: Int,
        lastDeviceId: Long, context: Context
    ): ChipClusters.LevelControlCluster {
        return ChipClusters.LevelControlCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getBindingCluster(endpoint: Int, lastDeviceId: Long, context: Context): ChipClusters.BindingCluster {
        return ChipClusters.BindingCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getIasZoneCluster(endpoint: Int,
                                  lastDeviceId: Long, context: Context): ChipClusters.IasZoneCluster {
        return ChipClusters.IasZoneCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getIlluminanceCluster(endpoint: Int, lastDeviceId: Long, context: Context): ChipClusters.IlluminanceMeasurementCluster {
        return ChipClusters.IlluminanceMeasurementCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getTemperatureMeasurementCluster(endpoint: Int,
                                                 lastDeviceId: Long, context: Context): ChipClusters.TemperatureMeasurementCluster {
        return ChipClusters.TemperatureMeasurementCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getRelativeHumidityMeasurementCluster(endpoint: Int,
                                                      lastDeviceId: Long, context: Context): ChipClusters.RelativeHumidityMeasurementCluster {
        return ChipClusters.RelativeHumidityMeasurementCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getWindowCoveringCluster(endpoint: Int,
                                         lastDeviceId: Long, context: Context): ChipClusters.WindowCoveringCluster {
        return ChipClusters.WindowCoveringCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getElectricalMeasurementCluster(endpoint: Int,
                                                lastDeviceId: Long, context: Context): ChipClusters.ElectricalMeasurementCluster {
        return ChipClusters.ElectricalMeasurementCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    private suspend fun getBridgedDeviceBasicCluster(endpoint: Int,
                                                lastDeviceId: Long, context: Context): ChipClusters.BridgedDeviceBasicCluster {
        return ChipClusters.BridgedDeviceBasicCluster(
            ChipClient.getConnectedDevicePointer(context, lastDeviceId),
            endpoint
        )
    }

    suspend fun getProductName(endpoint: Int,
                       lastDeviceId: Long, context: Context): String {
        return callbackFlow {
            val callback = object : ChipClusters.CharStringAttributeCallback {
                override fun onSuccess(name: String?) {
                    Log.d(TAG, "getProductName success name=$name")
                    kotlin.runCatching {
                        trySend(name ?: "Unknown")
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    trySend("UnKnown")
                    Log.d(TAG, "getProductName fail")
                    e?.printStackTrace()
                }

            }
            getBridgeDeviceBasicCluster(endpoint, lastDeviceId, context).readProductNameAttribute(callback)
            awaitClose { }
        }.first()
    }

    suspend fun readIasZoneStatus(endpoint: Int, lastDeviceId: Long, context: Context): Int {
        return callbackFlow {
            val readOnOffCallback =
                object : ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        kotlin.runCatching {
                            trySend(status)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(0)
                        }
                    }

                }
            try {
                getIasZoneCluster(endpoint, lastDeviceId, context).readZoneStatusAttribute(readOnOffCallback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(0)
                }
            }
            awaitClose { }
        }.first()
    }

    fun reportIasZoneStatus(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getIasZoneCluster(endpoint, lastDeviceId, app).reportZoneStatusAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is MotionDevice -> device.state = status
                            is DoorDevice -> device.state = status
                            is WaterLeakageDevice -> device.state = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(
                            TAG,
                            "endpoint$endpoint report ias zone status= $status device=$device"
                        )
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportIasZoneStatus(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportIasZoneStatus(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun readTemperature(endpoint: Int, lastDeviceId: Long, context: Context): Int {
        return callbackFlow {
            val callback =
                object : ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(temp: Int) {
                        kotlin.runCatching {
                            trySend(temp)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(0)
                        }
                    }

                }
            try {
                getTemperatureMeasurementCluster(endpoint, lastDeviceId, context).readMeasuredValueAttribute(callback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(0)
                }
            }
            awaitClose { }
        }.first()
    }

    fun reportTemperature(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getTemperatureMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportMeasuredValueAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is TemperatureHumidityDevice -> device.temperature = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report temperature= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                    reportTemperature(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                    reportTemperature(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun readHumidity(endpoint: Int, lastDeviceId: Long, context: Context): Int {
        return callbackFlow {
            val callback =
                object : ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(temp: Int) {
                        kotlin.runCatching {
                            trySend(temp)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(0)
                        }
                    }

                }
            try {
                getRelativeHumidityMeasurementCluster(endpoint, lastDeviceId, context).readMeasuredValueAttribute(callback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(0)
                }
            }
            awaitClose { }
        }.first()
    }

    fun reportHumidity(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getRelativeHumidityMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportMeasuredValueAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is TemperatureHumidityDevice -> device.humidity = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report humidity= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportHumidity(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportHumidity(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun readIlluminance(endpoint: Int, lastDeviceId: Long, context: Context): Int {
        return callbackFlow {
            val callback =
                object : ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(temp: Int) {
                        kotlin.runCatching {
                            trySend(temp)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(0)
                        }
                    }

                }
            try {
                getIlluminanceCluster(endpoint, lastDeviceId, context).readMeasuredValueAttribute(callback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(0)
                }
            }
            awaitClose { }
        }.first()
    }

    fun reportIlluminance(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getIlluminanceCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportMeasuredValueAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is IlluminanceDevice -> device.state = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report humidity= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportIlluminance(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportIlluminance(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportTotalActivePower(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getElectricalMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportTotalActivePowerAttribute(object :
                    ChipClusters.LongAttributeCallback {
                    override fun onSuccess(status: Long) {
                        when (device) {
                            is ElectricalMeasurementDevice -> device.totalActivePower = status
                            is SocketDevice -> device.totalActivePower = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(
                            TAG,
                            "endpoint$endpoint report total active power status= $status device=$device"
                        )
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportTotalActivePower(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportTotalActivePower(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportActivePower(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getElectricalMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportActivePowerAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ElectricalMeasurementDevice -> device.activePower = status
                            is SocketDevice -> device.activePower = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(
                            TAG,
                            "endpoint$endpoint report active power status= $status device=$device"
                        )
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportActivePower(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportActivePower(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportRmsCurrent(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getElectricalMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportRmsCurrentAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ElectricalMeasurementDevice -> device.current = status
                            is SocketDevice -> device.current = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report rms current= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportRmsCurrent(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportRmsCurrent(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportRmsCurrentMax(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getElectricalMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportRmsCurrentMaxAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ElectricalMeasurementDevice -> device.currentMax = status
                            is SocketDevice -> device.currentMax = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report current max= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportRmsCurrentMax(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportRmsCurrentMax(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportRmsCurrentMin(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getElectricalMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportRmsCurrentMinAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ElectricalMeasurementDevice -> device.currentMin = status
                            is SocketDevice -> device.currentMin = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report current min= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportRmsCurrentMin(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportRmsCurrentMin(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportRmsVoltage(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getElectricalMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportRmsVoltageAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ElectricalMeasurementDevice -> device.voltage = status
                            is SocketDevice -> device.voltage = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report voltage= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportRmsVoltage(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportRmsVoltage(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportRmsVoltageMax(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getElectricalMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportRmsVoltageMaxAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ElectricalMeasurementDevice -> device.voltageMax = status
                            is SocketDevice -> device.voltageMax = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report voltage max= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportRmsVoltageMax(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportRmsVoltageMax(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportRmsVoltageMin(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getElectricalMeasurementCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportRmsVoltageMinAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ElectricalMeasurementDevice -> device.voltageMin = status
                            is SocketDevice -> device.voltageMin = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report voltage min= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportRmsVoltageMin(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportRmsVoltageMin(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportCurrentPositionLiftPercentageAttribute(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getWindowCoveringCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportCurrentPositionLiftPercentageAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ShutterDevice -> device.liftPercentage = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(
                            TAG,
                            "endpoint$endpoint report window covering lift percentage max= $status device=$device"
                        )
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportCurrentPositionLiftPercentageAttribute(
                            endpoint,
                            lastDeviceId,
                            app,
                            device,
                            coroutineScope
                        )
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportCurrentPositionLiftPercentageAttribute(
                            endpoint,
                            lastDeviceId,
                            app,
                            device,
                            coroutineScope
                        )
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun reportCurrentPositionTiltPercentageAttribute(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getWindowCoveringCluster(
                    endpoint,
                    lastDeviceId,
                    app
                ).reportCurrentPositionTiltPercentageAttribute(object :
                    ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(status: Int) {
                        when (device) {
                            is ShutterDevice -> device.tiltPercentage = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(
                            TAG,
                            "endpoint$endpoint report window covering tilt percentage max= $status device=$device"
                        )
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportCurrentPositionTiltPercentageAttribute(
                            endpoint,
                            lastDeviceId,
                            app,
                            device,
                            coroutineScope
                        )
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        reportCurrentPositionTiltPercentageAttribute(
                            endpoint,
                            lastDeviceId,
                            app,
                            device,
                            coroutineScope
                        )
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun openWindowCovering(endpoint: Int, lastDeviceId: Long, context: Context): Boolean {
        return callbackFlow {
            val callback = object :
                ChipClusters.DefaultClusterCallback {
                override fun onSuccess() {
                    kotlin.runCatching {
                        trySend(true)
                    }
                }

                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                    kotlin.runCatching {
                        trySend(false)
                    }
                }

            }
            try {
                getWindowCoveringCluster(endpoint, lastDeviceId, context).upOrOpen(callback)
            }catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(true)
                }
            }
            awaitClose {  }
        }.first()
    }

    suspend fun closeWindowCovering(endpoint: Int, lastDeviceId: Long, context: Context): Boolean {
        return callbackFlow {
            val callback = object :
                ChipClusters.DefaultClusterCallback {
                override fun onSuccess() {
                    kotlin.runCatching {
                        trySend(true)
                    }
                }

                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                    kotlin.runCatching {
                        trySend(false)
                    }
                }

            }
            try {
                getWindowCoveringCluster(endpoint, lastDeviceId, context).downOrClose(callback)
            }catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose {  }
        }.first()
    }

    suspend fun stopWindowCovering(endpoint: Int, lastDeviceId: Long, context: Context): Boolean {
        return callbackFlow {
            val callback = object :
                ChipClusters.DefaultClusterCallback {
                override fun onSuccess() {
                    kotlin.runCatching {
                        trySend(true)
                    }
                }

                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                    kotlin.runCatching {
                        trySend(false)
                    }
                }

            }
            try {
                getWindowCoveringCluster(endpoint, lastDeviceId, context).stopMotion(callback)
            }catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose {  }
        }.first()
    }

    suspend fun getLabelList(
        endpoint: Int,
        lastDeviceId: Long, context: Context
    ): MutableList<ChipClusters.FixedLabelCluster.LabelListAttribute> {
        return callbackFlow {
            val labelListCallback =
                object : ChipClusters.FixedLabelCluster.LabelListAttributeCallback {
                    override fun onSuccess(labelList: MutableList<ChipClusters.FixedLabelCluster.LabelListAttribute>?) {
                        kotlin.runCatching {
                            trySend(labelList ?: arrayListOf<ChipClusters.FixedLabelCluster.LabelListAttribute>())
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(arrayListOf<ChipClusters.FixedLabelCluster.LabelListAttribute>())
                        }
                    }
                }
            try {
                getFixedLabelCluster(endpoint, lastDeviceId, context).readLabelListAttribute(
                    labelListCallback
                )
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(arrayListOf<ChipClusters.FixedLabelCluster.LabelListAttribute>())
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun getUserLabel(endpoint: Int, lastDeviceId: Long, context: Context): String {
        return callbackFlow {
            val labelListCallback =
                object : ChipClusters.CharStringAttributeCallback {
                    override fun onSuccess(userLabel: String) {
                        kotlin.runCatching {
                            trySend(userLabel)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend("UnknownDevice")
                        }
                    }
                }
            try {
                getBridgeDeviceBasicCluster(endpoint, lastDeviceId, context).readUserLabelAttribute(
                    labelListCallback
                )
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend("UnknownDevice")
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun getPartList(endpoint: Int, lastDeviceId: Long, context: Context): MutableList<Int> {
        return callbackFlow {
            val partListCallback =
                object : ChipClusters.DescriptorCluster.PartsListAttributeCallback {
                    override fun onSuccess(partList: MutableList<Int>?) {
                        kotlin.runCatching {
                            trySend(partList ?: mutableListOf<Int>())
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(mutableListOf<Int>())
                        }
                    }

                }
            try {
                getDescriptorCluster(
                    endpoint,
                    lastDeviceId, context
                ).readPartsListAttribute(partListCallback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(mutableListOf<Int>())
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun getDeviceList(
        endpoint: Int,
        lastDeviceId: Long, context: Context
    ): MutableList<ChipClusters.DescriptorCluster.DeviceListAttribute> {
        return callbackFlow {
            val deviceListCallback =
                object : ChipClusters.DescriptorCluster.DeviceListAttributeCallback {
                    override fun onSuccess(deviceList: MutableList<ChipClusters.DescriptorCluster.DeviceListAttribute>?) {
                        Log.d(TAG, "getDeviceList= $deviceList")
                        kotlin.runCatching {
                            trySend(deviceList ?: arrayListOf<ChipClusters.DescriptorCluster.DeviceListAttribute>())
                        }
                    }

                    override fun onError(p0: Exception?) {
                        Log.e(TAG, "getDeviceList= $p0")
                        kotlin.runCatching {
                            trySend(arrayListOf<ChipClusters.DescriptorCluster.DeviceListAttribute>())
                        }
                    }

                }
            try {
                getDescriptorCluster(endpoint, lastDeviceId, context).readDeviceListAttribute(
                    deviceListCallback
                )
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(arrayListOf<ChipClusters.DescriptorCluster.DeviceListAttribute>())
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun getServerList(endpoint: Int, lastDeviceId: Long, context: Context): MutableList<Long> {
        return callbackFlow {
            val serverListCallback =
                object : ChipClusters.DescriptorCluster.ServerListAttributeCallback {
                    override fun onSuccess(serverList: MutableList<Long>?) {
                        kotlin.runCatching {
                            trySend(serverList ?: mutableListOf<Long>())
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(mutableListOf<Long>())
                        }
                    }

                }
            try {
                getDescriptorCluster(endpoint, lastDeviceId, context).readServerListAttribute(
                    serverListCallback
                )
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(mutableListOf<Long>())
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun readOnOffAttribute(endpoint: Int, lastDeviceId: Long, context: Context): Boolean {
        return callbackFlow {
            val readOnOffCallback =
                object : ChipClusters.BooleanAttributeCallback {
                    override fun onSuccess(onOff: Boolean) {
                        kotlin.runCatching {
                            trySend(onOff)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(false)
                        }
                    }

                }
            try {
                getOnOffCluster(endpoint, lastDeviceId, context).readOnOffAttribute(readOnOffCallback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose { }
        }.first()
    }

    fun reportOnOff(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getOnOffCluster(endpoint, lastDeviceId, app).reportOnOffAttribute(object :
                    ChipClusters.BooleanAttributeCallback {
                    override fun onSuccess(status: Boolean) {
                        when (device) {
                            is OnOffDevice -> device.state = status
                            is DimmerDevice -> device.state = status
                            is SocketDevice -> device.state = status
                        }
                        app.deviceStateMap[device.endpoint] = device
                        Log.d(TAG, "endpoint$endpoint report onOff= $status device=$device")
                        app.observeDeviceState.tryEmit(
                            UpdateEvent(
                                System.currentTimeMillis(),
                                endpoint
                            )
                        )
                        reportOnOff(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                    override fun onError(p0: Exception?) {
                        p0?.printStackTrace()
                        reportOnOff(endpoint, lastDeviceId, app, device, coroutineScope)
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun sendOnOff(endpoint: Int, lastDeviceId: Long, onOff: Boolean, activity: Activity) {
        if (onOff) {
            try {
                getOnOffCluster(endpoint, lastDeviceId, activity).on(object :
                    ChipClusters.DefaultClusterCallback {
                    override fun onSuccess() {
                        activity.runOnUiThread {
                            Toast.makeText(
                                activity,
                                "On success",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(p0: Exception?) {
                        activity.runOnUiThread {
                            Toast.makeText(
                                activity,
                                "On failure",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                getOnOffCluster(endpoint, lastDeviceId, activity).off(object :
                    ChipClusters.DefaultClusterCallback {
                    override fun onSuccess() {
                        activity.runOnUiThread {
                            Toast.makeText(
                                activity,
                                "Off success",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(p0: Exception?) {
                        activity.runOnUiThread {
                            Toast.makeText(
                                activity,
                                "Off failure",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun readLevelControlAttribute(endpoint: Int, lastDeviceId: Long, context: Context): Int {
        return callbackFlow {
            val readOnOffCallback =
                object : ChipClusters.IntegerAttributeCallback {
                    override fun onSuccess(level: Int) {
                        kotlin.runCatching {
                            trySend(level)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(0)
                        }
                    }

                }
            try {
                getLevelControlCluster(endpoint, lastDeviceId, context).readCurrentLevelAttribute(
                    readOnOffCallback
                )
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(0)
                }
            }
            awaitClose { }
        }.first()
    }

    fun reportCurrentLevel(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            try {
                getLevelControlCluster(endpoint, lastDeviceId, app).reportCurrentLevelAttribute(
                    object :
                        ChipClusters.IntegerAttributeCallback {
                        override fun onSuccess(level: Int) {
                            reportCurrentLevel(endpoint, lastDeviceId, app, device, coroutineScope)
                            Log.d(TAG, "endpoint$endpoint report level=$level  device=$device")
                            when (device) {
                                is DimmerDevice -> {
                                    device.level = level
                                }
                            }
                            app.deviceStateMap[device.endpoint] = device
                            app.observeDeviceState.tryEmit(
                                UpdateEvent(
                                    System.currentTimeMillis(),
                                    endpoint
                                )
                            )
//                    getLevelControlCluster(endpoint, lastDeviceId).readCurrentLevelAttribute(
//                        object : ChipClusters.IntegerAttributeCallback {
//                            override fun onSuccess(level: Int) {
//
//                            }
//
//                            override fun onError(p0: Exception?) {
//                                reportCurrentLevel(endpoint, lastDeviceId, app, device)
//                            }
//
//                        }
//                    )

                        }

                        override fun onError(p0: Exception?) {
                            p0?.printStackTrace()
                            reportCurrentLevel(endpoint, lastDeviceId, app, device, coroutineScope)
                        }

                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun sendLevelControl(endpoint: Int, lastDeviceId: Long, level: Int, context: Context) {
        try {
            getLevelControlCluster(endpoint, lastDeviceId, context).moveToLevelWithOnOff(object :
                ChipClusters.DefaultClusterCallback {
                override fun onSuccess() {
                    Log.d(TAG, "MoveToLevel command success")
                }

                override fun onError(ex: Exception) {
                    Log.e(TAG, "MoveToLevel command failure", ex)
                }

            }, convertToLevel(level), 0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun convertToLevel(progress: Int): Int {
        Log.d(TAG, "set level= ${(progress * (255f / 100f)).roundToInt()}")
        return (progress * (254f / 100f)).roundToInt()
    }

    fun convertToProgress(level: Int): Int {
        return (level * 100f / 254f).roundToInt()
    }

    suspend fun getConnectedDevicePointer(deviceId: Long, context: Context): Boolean {
        return callbackFlow {
            val getConnectedDeviceCallback =
                object : GetConnectedDeviceCallbackJni.GetConnectedDeviceCallback {
                    override fun onDeviceConnected(devicePointer: Long) {
                        kotlin.runCatching {
                            trySend(true)
                        }
                    }

                    override fun onConnectionFailure(p0: Long, p1: Exception?) {
                        kotlin.runCatching {
                            trySend(false)
                        }
                    }
                }
            try {
                ChipClient.getDeviceController(context).getConnectedDevicePointer(deviceId, getConnectedDeviceCallback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun addWifiNetwork(ssid: String, password: String, endpoint: Int, deviceId: Long, context: Context): Boolean {
        return callbackFlow {
            val addNetworkCallback = object :
                ChipClusters.NetworkCommissioningCluster.AddWiFiNetworkResponseCallback {
                override fun onSuccess(errorCode: Int, debugText: String) {
                    Log.v(TAG, "EnableNetwork for $ssid succeeded, proceeding to OnOff")
                    kotlin.runCatching {
                        trySend(true)
                    }
                }

                override fun onError(ex: Exception) {
                    Log.e(TAG, "EnableNetwork for $ssid failed", ex)
                    kotlin.runCatching {
                        trySend(false)
                    }
                }
            }
            try {
                getNetworkCommissioningCluster(endpoint, deviceId, context).addWiFiNetwork(addNetworkCallback,
                    ssid.toByteArray(), password.toByteArray(), /* breadcrumb = */ 0L,
                    10000L
                )
            }catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun enableWifiNetwork(ssid: String, endpoint: Int, deviceId: Long, context: Context): Boolean {
        return callbackFlow {
            val enableNetworkCallback = object :
                ChipClusters.NetworkCommissioningCluster.EnableNetworkResponseCallback {
                override fun onSuccess(errorCode: Int, debugText: String) {
                    Log.v(TAG, "EnableNetwork for $ssid succeeded, proceeding to OnOff")
                    kotlin.runCatching {
                        trySend(true)
                    }
                }

                override fun onError(ex: Exception) {
                    Log.e(TAG, "EnableNetwork for $ssid failed", ex)
                    kotlin.runCatching {
                        trySend(false)
                    }
                }
            }
            try {
                getNetworkCommissioningCluster(endpoint, deviceId, context).enableNetwork(enableNetworkCallback,
                    ssid.toByteArray(), /* breadcrumb = */ 0L,
                    10000L
                )
            }catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun addThreadNetwork(deviceId: Long, panId: Int, channel: Int, xpanId: String, masterKey: String, context: Context): Boolean {
        return callbackFlow {
            val addThreadNetworkResponseCallback =
                object : ChipClusters.NetworkCommissioningCluster.AddThreadNetworkResponseCallback {
                    override fun onSuccess(errorCode: Int, debugText: String?) {
                        kotlin.runCatching {
                            trySend(true)
                        }
                    }

                    override fun onError(e: java.lang.Exception?) {
                        kotlin.runCatching {
                            trySend(false)
                        }
                    }

                }
            try {
                getNetworkCommissioningCluster(0, deviceId, context).addThreadNetwork(addThreadNetworkResponseCallback,
                    makeThreadOperationalDataset(
                        channel,
                        panId,
                        xpanId.hexToByteArray(),
                        masterKey.hexToByteArray()
                    ), 0, 3000)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun enableThreadNetwork(deviceId: Long, xpanId: String, context: Context): Boolean {
        return callbackFlow {
            val enableNetworkResponseCallback =
                object : ChipClusters.NetworkCommissioningCluster.EnableNetworkResponseCallback {
                    override fun onSuccess(p0: Int, p1: String?) {
                        kotlin.runCatching {
                            trySend(true)
                        }
                    }

                    override fun onError(p0: java.lang.Exception?) {
                        kotlin.runCatching {
                            trySend(false)
                        }
                    }

                }
            try {
                getNetworkCommissioningCluster(0, deviceId, context).enableNetwork(enableNetworkResponseCallback,
                    xpanId.hexToByteArray(), 0, 10000L)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose { }
        }.first()
    }

    suspend fun subscribeAndReportByServerList(endpoint: Int, lastDeviceId: Long, app: App, device: BaseDevice,
                                               serverList: IntArray, coroutineScope: CoroutineScope) {
        serverList.map {
            when(it) {
                ClusterId.ZCL_ON_OFF_CLUSTER_ID -> {
                    Log.d(TAG, "ZCL_ON_OFF_CLUSTER_ID endpoint=$endpoint")
                    val bindFlag = bindClusterById(endpoint, lastDeviceId, ClusterId.ZCL_ON_OFF_CLUSTER_ID, app)
                    Log.d(TAG, "bind onoff status flag= $bindFlag")
                    if (bindFlag) {
                        val subscribeFlag = subscribeCluster {
                            coroutineScope.launch {
                                getOnOffCluster(
                                    endpoint,
                                    lastDeviceId,
                                    app
                                ).subscribeOnOffAttribute(it, 0, 900)
                            }
                        }
                        Log.d(TAG, "subscribe onoff status flag= $subscribeFlag")
                        if (subscribeFlag) reportOnOff(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                    } else { }
                }
                ClusterId.ZCL_LEVEL_CONTROL_CLUSTER_ID -> {
                    Log.d(TAG, "ZCL_LEVEL_CONTROL_CLUSTER_ID endpoint=$endpoint")
                    val bindFlag = bindClusterById(endpoint, lastDeviceId, ClusterId.ZCL_LEVEL_CONTROL_CLUSTER_ID, app)
                    Log.d(TAG, "bind level control current level status flag= $bindFlag")
                    if (bindFlag) {
                        val subscribeFlag = subscribeCluster {
                            coroutineScope.launch {
                                getLevelControlCluster(
                                    endpoint,
                                    lastDeviceId,
                                    app
                                ).subscribeCurrentLevelAttribute(it, 0, 900)
                            }
                        }
                        Log.d(TAG, "subscribe level control current level flag= $subscribeFlag")
                        if (subscribeFlag) reportCurrentLevel(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                    } else { }
                }
                ClusterId.ZCL_IAS_ZONE_CLUSTER_ID -> {
                    Log.d(TAG, "ZCL_IAS_ZONE_CLUSTER_ID endpoint=$endpoint")
                    val bindFlag = bindClusterById(endpoint, lastDeviceId, ClusterId.ZCL_IAS_ZONE_CLUSTER_ID, app)
                    Log.d(TAG, "bind ias zone status flag= $bindFlag")
                    if (bindFlag) {
                        val subscribeFlag = subscribeCluster {
                            coroutineScope.launch {
                                getIasZoneCluster(
                                    endpoint,
                                    lastDeviceId,
                                    app
                                ).subscribeZoneStatusAttribute(it, 0, 900)
                            }
                        }
                        Log.d(TAG, "subscribe ias zone status flag= $subscribeFlag")
                        if (subscribeFlag) reportIasZoneStatus(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                    } else { }
                }
                ClusterId.ZCL_TEMP_MEASUREMENT_CLUSTER_ID -> {
                    Log.d(TAG, "ZCL_TEMP_MEASUREMENT_CLUSTER_ID endpoint=$endpoint")
                    val bindFlag = bindClusterById(endpoint, lastDeviceId, ClusterId.ZCL_TEMP_MEASUREMENT_CLUSTER_ID, app)
                    Log.d(TAG, "bind temperature status flag= $bindFlag")
                    if (bindFlag) {
                        val subscribeFlag = subscribeCluster {
                            coroutineScope.launch {
                                getTemperatureMeasurementCluster(
                                    endpoint,
                                    lastDeviceId,
                                    app
                                ).subscribeMeasuredValueAttribute(it, 0, 900)
                            }
                        }
                        Log.d(TAG, "subscribe temperature status flag= $subscribeFlag")
                        if (subscribeFlag) reportTemperature(endpoint, lastDeviceId, app, device, coroutineScope) else { }

                    } else { }
                }
                ClusterId.ZCL_RELATIVE_HUMIDITY_MEASUREMENT_CLUSTER_ID -> {
                    Log.d(TAG, "ZCL_RELATIVE_HUMIDITY_MEASUREMENT_CLUSTER_ID endpoint=$endpoint")
                    val bindFlag = bindClusterById(endpoint, lastDeviceId, ClusterId.ZCL_RELATIVE_HUMIDITY_MEASUREMENT_CLUSTER_ID, app)
                    Log.d(TAG, "bind humidity status flag= $bindFlag")
                    if (bindFlag) {
                        val subscribeFlag = subscribeCluster {
                            coroutineScope.launch {
                                getRelativeHumidityMeasurementCluster(
                                    endpoint,
                                    lastDeviceId,
                                    app
                                ).subscribeMeasuredValueAttribute(it, 0, 900)
                            }
                        }
                        Log.d(TAG, "subscribe humidity status flag= $subscribeFlag")
                        if (subscribeFlag) reportHumidity(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                    } else { }
                }
                ClusterId.ZCL_ELECTRICAL_MEASUREMENT_CLUSTER_ID -> {
                    Log.d(TAG, "ZCL_ELECTRICAL_MEASUREMENT_CLUSTER_ID endpoint=$endpoint")
                    val bindFlag = bindClusterById(endpoint, lastDeviceId, ClusterId.ZCL_ELECTRICAL_MEASUREMENT_CLUSTER_ID, app)
                    Log.d(TAG, "bind electrical status flag= $bindFlag")
                    if (bindFlag) {
                        var subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getElectricalMeasurementCluster(endpoint, lastDeviceId, app).subscribeTotalActivePowerAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe total active power status flag= $subscribeFlag")
                        if (subscribeFlag) reportTotalActivePower(endpoint, lastDeviceId, app, device, coroutineScope)
                        subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getElectricalMeasurementCluster(endpoint, lastDeviceId, app).subscribeActivePowerAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe active power status flag= $subscribeFlag")
                        if (subscribeFlag) reportActivePower(endpoint, lastDeviceId, app, device, coroutineScope)
                        subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getElectricalMeasurementCluster(endpoint, lastDeviceId, app).subscribeRmsCurrentAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe current status flag= $subscribeFlag")
                        if (subscribeFlag) reportRmsCurrent(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                        subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getElectricalMeasurementCluster(endpoint, lastDeviceId, app).subscribeRmsCurrentMaxAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe current max status flag= $subscribeFlag")
                        if (subscribeFlag) reportRmsCurrentMax(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                        subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getElectricalMeasurementCluster(endpoint, lastDeviceId, app).subscribeRmsCurrentMinAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe current min status flag= $subscribeFlag")
                        if (subscribeFlag) reportRmsCurrentMin(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                        subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getElectricalMeasurementCluster(endpoint, lastDeviceId, app).subscribeRmsVoltageAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe voltage status flag= $subscribeFlag")
                        if (subscribeFlag) reportRmsVoltage(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                        subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getElectricalMeasurementCluster(endpoint, lastDeviceId, app).subscribeRmsVoltageMaxAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe voltage max status flag= $subscribeFlag")
                        if (subscribeFlag) reportRmsVoltageMax(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                        subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getElectricalMeasurementCluster(endpoint, lastDeviceId, app).subscribeRmsVoltageMinAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe voltage min status flag= $subscribeFlag")
                        if (subscribeFlag) reportRmsVoltageMin(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                    }else { }

                }
                ClusterId.ZCL_WINDOW_COVERING_CLUSTER_ID -> {
                    Log.d(TAG, "ZCL_WINDOW_COVERING_CLUSTER_ID endpoint=$endpoint")
                    val bindFlag = bindClusterById(endpoint, lastDeviceId,ClusterId.ZCL_WINDOW_COVERING_CLUSTER_ID, app)
                    if (bindFlag) {
                        var subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getWindowCoveringCluster(endpoint, lastDeviceId, app).subscribeCurrentPositionLiftPercentageAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe lift percentage status flag= $subscribeFlag")
                        if (subscribeFlag) reportCurrentPositionLiftPercentageAttribute(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                        subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getWindowCoveringCluster(endpoint, lastDeviceId, app).subscribeCurrentPositionTiltPercentageAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe tilt percentage status flag= $subscribeFlag")
                        if (subscribeFlag) reportCurrentPositionTiltPercentageAttribute(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                    } else { }
                }
                ClusterId.ZCL_ILLUM_MEASUREMENT_CLUSTER_ID -> {
                    val bindFlag = bindClusterById(endpoint, lastDeviceId,ClusterId.ZCL_ILLUM_MEASUREMENT_CLUSTER_ID, app)
                    if (bindFlag) {
                        val subscribeFlag = subscribeCluster {
                            coroutineScope.launch { getIlluminanceCluster(endpoint, lastDeviceId, app).subscribeMeasuredValueAttribute(it, 0, 900) }
                        }
                        Log.d(TAG, "subscribe illuminance status flag= $subscribeFlag")
                        if (subscribeFlag) reportIlluminance(endpoint, lastDeviceId, app, device, coroutineScope) else { }
                    } else { }
                }
                else -> { }
            }
        }
    }

    fun createNewDevice(productModeId: String, endpoint: Int, name: String, state: Boolean): BaseDevice {
        return when(productModeId) {
            ProductModeId.POWER_TAG -> {
                val device = ElectricalMeasurementDevice()
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            ProductModeId.SMART_SOCKET -> {
                val device = SocketDevice(state)
                device.state = state
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            ProductModeId.NH_SHUTTER -> {
                val device = ShutterDevice()
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            ProductModeId.DIMMER_1G, ProductModeId.DIMMER_2G -> {
                val device = DimmerDevice()
                device.state = state
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            ProductModeId.SWITCH_1G, ProductModeId.SWITCH_2G, ProductModeId.SWITCH_3G -> {
                val device = OnOffDevice(state)
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            ProductModeId.MOTION_SENSOR -> {
                val device = MotionDevice()
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            ProductModeId.WATER_LEAKAGE_SENSOR -> {
                val device = WaterLeakageDevice()
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            ProductModeId.DOOR_SENSOR -> {
                val device = DoorDevice()
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            ProductModeId.TEMPERATURE_HUMIDITY_SENSOR -> {
                val device = TemperatureHumidityDevice()
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
            else -> {
                val device = BaseDevice()
                device.productModeId = productModeId
                device.endpoint = endpoint
                device.name = name
                device
            }
        }
    }

    private suspend fun bindClusterById(endpoint: Int, lastDeviceId: Long, clusterId: Int, context: Context): Boolean {
        return callbackFlow {
            val bindCallback =
                object : ChipClusters.DefaultClusterCallback {
                    override fun onSuccess() {
                        kotlin.runCatching {
                            trySend(true)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(false)
                        }
                    }

                }
            try {
                getBindingCluster(endpoint, lastDeviceId, context).bind(
                    bindCallback,
                    globalNodeId,
                    0,
                    endpoint,
                    clusterId.toLong()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose { }
        }.first()
    }

    private suspend fun subscribeCluster(action: (callback: ChipClusters.DefaultClusterCallback)-> Unit): Boolean {
        return callbackFlow {
            val subscribeCallback =
                object : ChipClusters.DefaultClusterCallback {
                    override fun onSuccess() {
                        kotlin.runCatching {
                            trySend(true)
                        }
                    }

                    override fun onError(p0: Exception?) {
                        kotlin.runCatching {
                            trySend(false)
                        }
                    }

                }
            try {
                action.invoke(subscribeCallback)
            } catch (e: Exception) {
                e.printStackTrace()
                kotlin.runCatching {
                    trySend(false)
                }
            }
            awaitClose { }
        }.first()
    }

    private fun makeThreadOperationalDataset(
        channel: Int,
        panId: Int,
        xpanId: ByteArray,
        masterKey: ByteArray
    ): ByteArray {
        // channel
        var dataset = byteArrayOf(TYPE_CHANNEL.toByte(), NUM_CHANNEL_BYTES.toByte())
        dataset += 0x00.toByte() // Channel Page 0.
        dataset += (channel.shr(8) and 0xFF).toByte()
        dataset += (channel and 0xFF).toByte()

        // PAN ID
        dataset += TYPE_PANID.toByte()
        dataset += NUM_PANID_BYTES.toByte()
        dataset += (panId.shr(8) and 0xFF).toByte()
        dataset += (panId and 0xFF).toByte()

        // Extended PAN ID
        dataset += TYPE_XPANID.toByte()
        dataset += NUM_XPANID_BYTES.toByte()
        dataset += xpanId

        // Network Master Key
        dataset += TYPE_MASTER_KEY.toByte()
        dataset += NUM_MASTER_KEY_BYTES.toByte()
        dataset += masterKey

        return dataset
    }

    private fun String.hexToByteArray(): ByteArray {
        return chunked(2).map { byteStr -> byteStr.toUByte(16).toByte() }.toByteArray()
    }


    class ChipControllerCallback : GenericChipDeviceListener() {
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