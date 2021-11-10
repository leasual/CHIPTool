package com.se.wiser.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.chip.chiptool.R
import com.se.wiser.compose.theme.*
import com.se.wiser.model.DimmerDevice
import com.se.wiser.model.OnOffDevice
import com.se.wiser.utils.DeviceUtil
import com.se.wiser.utils.ProductModeId

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OneDimmerCard(
    device: DimmerDevice,
    onItemClick: (DimmerDevice, Class<*>) -> Unit,
    onOffClick: (DimmerDevice) -> Unit
) {
    val context = LocalContext.current
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
        ConstraintLayout(
            modifier = Modifier
                .padding(8.dp),
        ) {
            val (image, name, brightness, onOff) =createRefs()
            Icon(
                painter = painterResource(id = DeviceUtil.getDeviceResId(device.productModeId)),
                contentDescription = "icon",
                tint = MaterialTheme.colors.schneider,
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(parent.top, 8.dp)
                        start.linkTo(parent.start, 8.dp)
                    }
            )
            Text(
                text = DeviceUtil.getDeviceName(device.productModeId, context),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .constrainAs(name) {
                        top.linkTo(image.top)
                        start.linkTo(image.end, 8.dp)
                        bottom.linkTo(image.bottom)
//                        end.linkTo(onOff.start, 8.dp)
                    }
            )
            Text(
                text = device.getLevel(context),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .constrainAs(brightness) {
                        top.linkTo(image.bottom, 8.dp)
                        start.linkTo(parent.start, 8.dp)
                        end.linkTo(onOff.start, 8.dp)
                        bottom.linkTo(parent.bottom, 8.dp)
                        //wrap text length
                        width = Dimension.fillToConstraints
                    }
            )
            IconButton(
                onClick = {
                    device.state = !device.state
                    onOffClick(device)
                },
                modifier = Modifier
                    .constrainAs(onOff) {
                        top.linkTo(parent.top, 8.dp)
                        bottom.linkTo(parent.bottom, 8.dp)
                        end.linkTo(parent.end, 0.dp)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.mipmap.outline_power_settings_new_white_36),
                    contentDescription = "onOff",
                    tint = if (device.state) MaterialTheme.colors.switchOn else MaterialTheme.colors.switchOff,
                    modifier = Modifier.padding(end = 0.dp)
                )
            }
        }
    }
}

@Preview("OneDimmerCard")
@Preview("OneDimmerCard (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewOneDimmerCard() {
    val device = DimmerDevice()
    val context = LocalContext.current
    device.productModeId = ProductModeId.DIMMER_1G
    device.state = false
    device.endpoint = 1
    device.name = DeviceUtil.getDeviceName(ProductModeId.DIMMER_1G, context)
    ChipTheme {
        OneDimmerCard(
            device = device,
            onItemClick = { device, cls -> },
            onOffClick = { }
        )
    }
}

@Composable
fun TwoDimmerCard(
    onItemClick: (OnOffDevice) -> Unit,
    onArrowClick: () -> Unit,
) {

}