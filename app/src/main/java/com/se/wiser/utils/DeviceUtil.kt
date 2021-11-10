package com.se.wiser.utils

import android.content.Context
import com.google.chip.chiptool.R
import com.se.wiser.model.DeviceType
import com.se.wiser.model.ThreadDevice
import com.se.wiser.model.WIFIDevice

object DeviceUtil {

    const val BRIDGE_PREFIX = 0
    var THREAD_PREFIX = 1000
    var WIFI_PREFIX = 10000


    fun getEndpoint(type: DeviceType, endpoint: Int): Int {
        return when (type) {
            ThreadDevice -> endpoint
            WIFIDevice -> endpoint + WIFI_PREFIX
            else -> endpoint
        }
    }

    /**
     * get device icon by product mode id
     */
    fun getDeviceResId(modeId: String): Int {
        return when(modeId) {
            //power tag
            ProductModeId.POWER_TAG -> R.mipmap.outline_power_white_36
            //door sensor
            ProductModeId.DOOR_SENSOR -> R.mipmap.outline_sensor_door_white_36
            //water leakage sensor
            ProductModeId.WATER_LEAKAGE_SENSOR -> R.mipmap.outline_water_drop_white_36
            //temperature humidity sensor
            ProductModeId.TEMPERATURE_HUMIDITY_SENSOR -> R.mipmap.outline_device_thermostat_white_36
            //motion sensor
            ProductModeId.MOTION_SENSOR -> R.mipmap.outline_motion_photos_on_white_36
            //1g dimmer
            ProductModeId.DIMMER_1G -> R.mipmap.outline_wb_incandescent_white_36
            //1g switch
            ProductModeId.SWITCH_1G -> R.mipmap.outline_sensor_door_white_36
            //2g dimmer
            ProductModeId.DIMMER_2G -> R.mipmap.outline_wb_incandescent_white_36
            //2g switch
            ProductModeId.SWITCH_2G -> R.mipmap.outline_lightbulb_white_36
            //3g switch
            ProductModeId.SWITCH_3G -> R.mipmap.outline_lightbulb_white_36
            //socket
            ProductModeId.SMART_SOCKET -> R.mipmap.outline_electrical_services_white_36
            //shutter
            ProductModeId.NH_SHUTTER -> R.mipmap.outline_window_white_36
            else -> R.mipmap.outline_lightbulb_white_36
        }
    }

    /**
     * get device name by product mode id
     */
    fun getDeviceName(modeId: String, context: Context): String {
        return when(modeId) {
            ProductModeId.POWER_TAG -> context.getString(R.string.power_tag)
            //door sensor
            ProductModeId.DOOR_SENSOR -> context.getString(R.string.door_sensor)
            //water leakage sensor
            ProductModeId.WATER_LEAKAGE_SENSOR-> context.getString(R.string.water_leakage_sensor)
            //temperature humidity sensor
            ProductModeId.TEMPERATURE_HUMIDITY_SENSOR -> context.getString(R.string.temp_humidity_sensor)
            //motion sensor
            ProductModeId.MOTION_SENSOR -> context.getString(R.string.motion_sensor)
            //1g dimmer
            ProductModeId.DIMMER_1G -> context.getString(R.string.dimmer_1g)
            //1g switch
            ProductModeId.SWITCH_1G -> context.getString(R.string.switch_1g)
            //2g dimmer
            ProductModeId.DIMMER_2G -> context.getString(R.string.dimmer_2g)
            //2g switch
            ProductModeId.SWITCH_2G -> context.getString(R.string.switch_2g)
            //3g switch
            ProductModeId.SWITCH_3G -> context.getString(R.string.switch_3g)
            //socket
            ProductModeId.SMART_SOCKET -> context.getString(R.string.socket)
            //shutter
            ProductModeId.NH_SHUTTER -> context.getString(R.string.shutter)
            else -> modeId
        }
    }

    fun getStateName(modeId: String, state: Int, context: Context): String {
        return when(modeId) {
            ProductModeId.SWITCH_3G, ProductModeId.SWITCH_2G, ProductModeId.SWITCH_1G,
            ProductModeId.DIMMER_2G, ProductModeId.DIMMER_1G, ProductModeId.SMART_SOCKET,
            -> if (state == 1) context.getString(R.string.on) else context.getString(R.string.off)
            //door sensor
            ProductModeId.DOOR_SENSOR -> if (state == 1) context.getString(R.string.on) else context.getString(R.string.off)
            //water leakage sensor
            ProductModeId.WATER_LEAKAGE_SENSOR-> if (state == 1) context.getString(R.string.normal) else context.getString(R.string.abnormal)
            ProductModeId.MOTION_SENSOR-> if (state == 1) context.getString(R.string.has_person) else context.getString(R.string.no_person)
            else -> "context.getString(R.string.unknown)"
        }
    }
}
