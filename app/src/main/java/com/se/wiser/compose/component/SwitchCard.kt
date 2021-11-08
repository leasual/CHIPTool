package com.se.wiser.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.chip.chiptool.R
import com.se.wiser.App
import com.se.wiser.compose.theme.ChipTheme
import com.se.wiser.compose.theme.switchOff
import com.se.wiser.compose.theme.switchOn
import com.se.wiser.compose.ui.HomeScreen
import com.se.wiser.compose.viewmodel.HomeViewModel
import com.se.wiser.model.OnOffDevice
import com.se.wiser.utils.ClusterId
import com.se.wiser.utils.DeviceUtil
import com.se.wiser.utils.ProductModeId

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OneSwitchCard(
    device: OnOffDevice,
    onItemClick: (OnOffDevice, Class<*>) -> Unit,
    onOffClick: (OnOffDevice) -> Unit
) {
    val context = LocalContext.current
    Card(
        backgroundColor = MaterialTheme.colors.onBackground,
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
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = DeviceUtil.getDeviceName(device.productModeId, context),
                        style = typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            IconButton(
                onClick = {
                    device.state = !device.state
                    onOffClick(device)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.mipmap.outline_power_settings_new_white_36),
                    contentDescription = "onOff",
                    tint = if (device.state) MaterialTheme.colors.switchOn else MaterialTheme.colors.switchOff,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
@Preview("OneSwitchCard")
@Preview("OneSwitchCard (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewOneSwitchCard() {
    val device = OnOffDevice()
    val context = LocalContext.current
    device.productModeId = ProductModeId.SWITCH_1G
    device.state = false
    device.endpoint = 1
    device.name = DeviceUtil.getDeviceName(ProductModeId.SWITCH_1G, context)
    ChipTheme {
        OneSwitchCard(
            device = device,
            onItemClick = { device, cls -> },
            onOffClick = { }
        )
    }
}

@Composable
fun TwoSwitchCard(
    onItemClick: (OnOffDevice) -> Unit,
) {

}

@Composable
fun TheeSwitchCard(
    onItemClick: (OnOffDevice) -> Unit,
) {

}