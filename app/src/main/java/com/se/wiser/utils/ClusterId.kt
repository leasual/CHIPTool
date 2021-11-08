package com.se.wiser.utils

/**
 * cluster id
 */
object ClusterId {

    const val ZCL_ON_OFF_CLUSTER_ID = 0x0006
    const val ZCL_LEVEL_CONTROL_CLUSTER_ID = 0x0008
    const val ZCL_IAS_ZONE_CLUSTER_ID = 0x0500
    const val ZCL_ILLUM_MEASUREMENT_CLUSTER_ID = 0x0400
    const val ZCL_TEMP_MEASUREMENT_CLUSTER_ID = 0x0402
    const val ZCL_RELATIVE_HUMIDITY_MEASUREMENT_CLUSTER_ID = 0x0405
    const val ZCL_ELECTRICAL_MEASUREMENT_CLUSTER_ID = 0x0B04
    const val ZCL_WINDOW_COVERING_CLUSTER_ID = 0x0102

}

/**
 * device product mode id
 */
object ProductModeId {
    const val POWER_TAG = "4RCBEM2"
    const val DOOR_SENSOR = "CCT591011_AS"
    const val WATER_LEAKAGE_SENSOR = "CCT592011_AS"
    const val TEMPERATURE_HUMIDITY_SENSOR = "CCT593011_AS"
    const val MOTION_SENSOR = "CCT595011_AS"
    const val DIMMER_1G = "E8331DST300ZB"
    const val SWITCH_1G = "E8331SRY800ZB"
    const val DIMMER_2G = "E8332DST350ZB"
    const val SWITCH_2G = "E8332SRY800ZB"
    const val SWITCH_3G = "E8333SRY800ZB"
    const val SMART_SOCKET = "E8331510SSZB_C1"
    const val NH_SHUTTER = "HNPB_SHUTTER_1"
}