package com.se.wiser.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.se.wiser.compose.theme.ChipTheme
import com.se.wiser.compose.theme.contentBackground
import com.se.wiser.compose.theme.schneider
import com.se.wiser.model.*
import com.se.wiser.utils.DeviceUtil
import com.se.wiser.utils.ProductModeId

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OneStateSensorCard(
    device: BaseDevice,
    onItemClick: (BaseDevice, Class<*>) -> Unit,
    onOffClick: (BaseDevice) -> Unit
) {
    val context = LocalContext.current
    val state = when(device) {
        is MotionDevice -> device.state
        is WaterLeakageDevice -> device.state
        is DoorDevice -> device.state
        else -> 0
    }
    Card(
        backgroundColor = MaterialTheme.colors.contentBackground,
        elevation = 0.dp,
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        onClick = { onItemClick(device, device::class.java) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 80.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = DeviceUtil.getDeviceResId(device.productModeId)),
                        contentDescription = "icon",
                        tint = MaterialTheme.colors.schneider,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = DeviceUtil.getDeviceName(device.productModeId, context),
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Text(
                text = DeviceUtil.getStateName(device.productModeId, state, context),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(start = 8.dp, end = 16.dp)
            )
//            IconButton(
//                onClick = {
//                    when(device) {
//                        is MotionDevice -> device.state = if (device.state == 1)  0 else  1
//                        is WaterLeakageDevice -> device.state = if (device.state == 1)  0 else 1
//                        is DoorDevice -> device.state = if (device.state == 1) 0 else 1
//                    }
//                    onOffClick(device)
//                }
//            ) {
//                Icon(
//                    painter = painterResource(id = R.mipmap.outline_power_settings_new_white_36),
//                    contentDescription = "onOff",
////                    tint = if (device.state) MaterialTheme.colors.switchOn else MaterialTheme.colors.switchOff,
//                    modifier = Modifier.padding(end = 8.dp)
//                )
//            }
        }
    }
}
@Preview("OneStateSensorCard")
@Preview("OneStateSensorCard (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewOneStateSensorCard() {
    val device = MotionDevice()
    val context = LocalContext.current
    device.productModeId = ProductModeId.WATER_LEAKAGE_SENSOR
    device.state = 0
    device.endpoint = 1
    device.name = DeviceUtil.getDeviceName(ProductModeId.WATER_LEAKAGE_SENSOR, context)
    ChipTheme {
        OneStateSensorCard(
            device = device,
            onItemClick = { device, cls -> },
            onOffClick = { }
        )
    }
}

@Composable
fun TwoStateSensorCard(
    onItemClick: (OnOffDevice) -> Unit,
    onArrowClick: () -> Unit,
) {

}