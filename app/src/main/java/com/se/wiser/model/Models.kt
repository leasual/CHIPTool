package com.se.wiser.model

import android.content.Context
import com.google.chip.chiptool.R
import com.se.wiser.utils.ClusterUtil
import java.io.Serializable
import kotlin.math.roundToInt


data class GroupDevice(
    val type: DeviceType,
    val name: String,
    var subDeviceList: MutableList<Any> = mutableListOf(),
    var expand: Boolean = true,
    var isOpenCommissionWindow: Boolean = false
)

data class ComposeDevice(
    val id: Long,
    val name: String,
    var subDeviceList: MutableList<Any>,
    var expand: Boolean = true
)

//@Parcelize
//data class Device(
//    var id: Int,
//    var name: String,
//    var state: Boolean,
//    var endpoint: Long,
//    var level: Int = 0,
//    var fabricId: Int = 0,
//    var status: Int = 0
//): Parcelable

open class BaseDevice: Serializable {
    var id: Int = 0
    var name: String = ""
    var endpoint: Int = 0
    var type: DeviceType = BridgeDevice
    var productModeId: String = ""
    var fabricId: Int = 0
}

open class OnOffDevice(
    var state: Boolean = false,
):BaseDevice(), Serializable {

    override fun toString(): String {
        return "OnOffDevice(state=$state)"
    }
}

data class DimmerDevice(
    var state: Boolean = false,
    var level: Int = 0,
):BaseDevice(), Serializable {
    fun getLevel(context: Context): String {
        return String.format(context.getString(R.string.brightness), ClusterUtil.convertToProgress(level))
    }
}

data class MotionDevice(
    var state: Int = 0,
):BaseDevice(), Serializable {
}

data class DoorDevice(
    var state: Int = 0,
):BaseDevice(), Serializable {
}

data class WaterLeakageDevice(
    var state: Int = 0,
):BaseDevice(), Serializable {
}

data class IlluminanceDevice(
    var state: Int = 0,
):BaseDevice(), Serializable {
}

data class TemperatureHumidityDevice(
    var temperature: Int = 0,
    var humidity: Int = 0,
):BaseDevice(), Serializable {

    fun getTemperature(context: Context): String {
        return String.format(context.getString(R.string.temperature), (temperature / 100f))
    }
    fun getHumidity(context: Context): String {
        return String.format(context.getString(R.string.humidity), (humidity / 100f))
    }
}

open class ElectricalMeasurementDevice(
    var activePower: Int = 0,
    var totalActivePower: Long = 0,
    var voltage: Int = 0,
    var current: Int = 0,
    var voltageMax: Int = 0,
    var voltageMin: Int = 0,
    var currentMax: Int = 0,
    var currentMin: Int = 0,
):BaseDevice(), Serializable {

    fun getActivePower(context: Context): String {
        return String.format(context.getString(R.string.power), activePower)
    }
//    voltageMin=5806,消耗  产生currentMin=34157
    fun getTotalActivePower(context: Context): String {
        return String.format(context.getString(R.string.power), totalActivePower)
    }
    fun getCurrentMin(context: Context): String {
        return String.format(context.getString(R.string.generate_electric), (currentMin / 1000f))
    }
    fun getCurrentMax(): String {
        return String.format("%.2fkW⋅h", (currentMax / 1000f))
    }
    fun getVoltageMin(context: Context): String {
        return String.format(context.getString(R.string.consumption_electric), (voltageMin / 1000f))
    }
    fun getVoltageMax(): String {
        return String.format("%.2fkW⋅h", (voltageMax / 1000f))
    }
    fun getCurrent(context: Context): String {
        return String.format(context.getString(R.string.current), current)
    }
    fun getVoltage(context: Context): String {
        return String.format(context.getString(R.string.voltage), voltage / 100f)
    }

    override fun toString(): String {
        return "ElectricalMeasurementDevice(activePower=$activePower, totalActivePower=$totalActivePower, voltage=$voltage, current=$current, voltageMax=$voltageMax, voltageMin=$voltageMin, currentMax=$currentMax, currentMin=$currentMin)"
    }
}

open class SocketDevice(
    var state: Boolean,
    var activePower: Int = 0,
    var totalActivePower: Long = 0,
    var voltage: Int = 0,
    var current: Int = 0,
    var voltageMax: Int = 0,
    var voltageMin: Int = 0,
    var currentMax: Int = 0,
    var currentMin: Int = 0,
):BaseDevice(), Serializable {

    fun getActivePower(context: Context): String {
        return String.format(context.getString(R.string.power), (activePower / 10f).roundToInt())
    }
    //    voltageMin=5806,消耗  产生currentMin=34157
    fun getTotalActivePower(context: Context): String {
        return String.format(context.getString(R.string.power), (totalActivePower / 10f).roundToInt())
    }
    fun getCurrentMin(context: Context): String {
        return String.format(context.getString(R.string.socket_generate_electric), (currentMin / 10f).roundToInt())
    }
    fun getCurrentMax(): String {
        return String.format("%.2fkW⋅h", (currentMax / 1000f))
    }
    fun getVoltageMin(context: Context): String {
        return String.format(context.getString(R.string.socket_consumption_electric), (voltageMin / 10f).roundToInt())
    }
    fun getVoltageMax(): String {
        return String.format("%.2fkW⋅h", (voltageMax / 1000f))
    }
    fun getCurrent(context: Context): String {
        return String.format(context.getString(R.string.current), (current / 100f).roundToInt())
    }
    fun getVoltage(context: Context): String {
        return String.format(context.getString(R.string.voltage), voltage / 100f)
    }

    override fun toString(): String {
        return "ElectricalMeasurementDevice(activePower=$activePower, totalActivePower=$totalActivePower, voltage=$voltage, current=$current, voltageMax=$voltageMax, voltageMin=$voltageMin, currentMax=$currentMax, currentMin=$currentMin)"
    }
}

data class ShutterDevice(
    var liftPercentage: Int = 0,
    var tiltPercentage: Int = 0,
):BaseDevice(), Serializable {

    fun getLiftPercentage(context: Context): String {
        return String.format(context.getString(R.string.lift), liftPercentage)
    }
    fun getTiltPercentage(context: Context): String {
        return String.format(context.getString(R.string.tilt), tiltPercentage)
    }
}

data class UpdateEvent(
    var id: Long,
    var endpoint: Int
) {
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

data class BrowserResponse(
    val flags: Int,
    val ifIndex: Int,
    val serviceName: String?,
    val regType: String?,
    val domain: String?
)

data class ResolveResponse(
    val ifIndex: Int,
    val serviceName: String,
    val regType: String,
    val domain: String,
    val hostName: String,
    val port: Int,
    val txtRecord: Map<String, String>
)

data class AddressResponse(
    val address: String,
    val port: Int
)

data class CommissioningInfo(
    var manualCode: String,
    var qrCode: String
)

sealed class DeviceType: Serializable
object ThreadDevice: DeviceType()
object WIFIDevice: DeviceType()
object BridgeDevice: DeviceType()
object NoneDevice: DeviceType()


