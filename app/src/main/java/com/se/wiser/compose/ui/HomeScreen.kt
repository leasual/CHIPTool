package com.se.wiser.compose.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.chip.chiptool.R
import com.se.wiser.App
import com.se.wiser.compose.TAG
import com.se.wiser.compose.component.*
import com.se.wiser.compose.theme.ChipTheme
import com.se.wiser.compose.viewmodel.HomeViewModel
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.DimmerDevice
import com.se.wiser.model.OnOffDevice
import com.se.wiser.utils.ClusterId
import com.se.wiser.utils.ProductModeId

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToDevice: (BaseDevice, Class<*>) -> Unit,
    navigateToAddDevice:() -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val expandedCardIds = viewModel.expandedCardIdsList.collectAsState()
    val scrollState = rememberLazyListState()
//    Log.d(TAG, "expandedCardIds= ${expandedCardIds.value}")
    HomeScreen(
        viewModel = viewModel,
        openDrawer = openDrawer,
        navigateToDevice = navigateToDevice,
        navigateToAddDevice = navigateToAddDevice,
        scaffoldState = scaffoldState,
        expandedCardIds = expandedCardIds,
        scrollState = scrollState
    )
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    openDrawer: () -> Unit,
    navigateToDevice: (BaseDevice, Class<*>) -> Unit,
    navigateToAddDevice:() -> Unit,
    scaffoldState: ScaffoldState,
    expandedCardIds: State<List<Int>>,
    scrollState: LazyListState,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.systemBarsPadding()) },
        topBar = {
            val title = stringResource(id = R.string.app_name)
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = "Kevin's home",
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp, bottom = 4.dp)
                            .offset(x = (-8).dp),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = openDrawer,
                    ) {
                        Icon(
                            painter = painterResource(id = R.mipmap.outline_menu_white_24),
                            contentDescription = "open drawer",
                            tint = MaterialTheme.colors.onBackground,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = navigateToAddDevice,
                    ) {
                        Icon(
                            painter = painterResource(id = R.mipmap.outline_add_white_24),
                            contentDescription = "add",
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = false,
            emptyContent = {
                NoDevice(
                    modifier = modifier,
                    navigateToAddDevice = navigateToAddDevice
                )
            },
            loading = false,
            onRefresh = {

            },
            content = {
                val deviceList = arrayListOf<Any>()
                val dimmer = DimmerDevice()
                dimmer.level = 200
                dimmer.productModeId = ProductModeId.DIMMER_1G
                deviceList.add(dimmer)
//                for (i in 0..20) {
//                    val onOff = OnOffDevice()
//                    onOff.productModeId = ProductModeId.SWITCH_1G
//                    deviceList.add(onOff)
//                }
                DeviceList(
                    deviceList = deviceList,
                    modifier = modifier,
                    scrollState = scrollState,
                    expandedCardIds = expandedCardIds,
                    viewModel = viewModel,
                    navigateToDevice = navigateToDevice,
                )
            }
        )
    }
}

@Composable
fun NoDevice(
    modifier: Modifier,
    navigateToAddDevice: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.mipmap.bg),
                contentScale = ContentScale.FillWidth,
                contentDescription = "background"
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = navigateToAddDevice
                ) {
                    Icon(
                        painter = painterResource(id = R.mipmap.outline_control_point_white_48),
                        contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Text(
                    text = stringResource(id = R.string.no_device_please_add)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Button(
                    onClick = navigateToAddDevice,
                    modifier = Modifier
                        .height(40.dp),
                    shape = RoundedCornerShape(corner = CornerSize(16))
                ) {
                    Text(text = stringResource(id = R.string.add_device))
                }
            }
        }
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
fun DeviceList(
    deviceList: ArrayList<Any>,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    expandedCardIds: State<List<Int>>,
    viewModel: HomeViewModel,
    navigateToDevice: (BaseDevice, Class<*>) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colors.primary),
        state = scrollState,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false
        )
    ) {
        itemsIndexed(deviceList.toList()) { index, card ->
//            Log.d(TAG, "contains index=$index ${expandedCardIds.value.contains(index)}")
            ExpandedCard(
                title = "$index",
                content = {
                    DeviceCard(
                        device = card,
                        viewModel = viewModel,
                        navigateToDevice = navigateToDevice
                    )
                },
                onCardArrowClick = {
                    Log.d(TAG, "click arrow")
                    viewModel.onCardArrowClicked(index)
                },
                expanded = expandedCardIds.value.contains(index)
            )
        }
    }
}

@Composable
fun DeviceCard(
    device: Any,
    viewModel: HomeViewModel,
    navigateToDevice: (BaseDevice, Class<*>) -> Unit
) {
    Log.d(TAG, "device=$device")
    when(device) {
        is OnOffDevice -> OneSwitchCard(
            device = device,
            onItemClick = navigateToDevice,
            onOffClick = { viewModel.onOffClick(device) }
        )
        is DimmerDevice -> OneDimmerCard(
            device = device,
            onItemClick = navigateToDevice,
            onOffClick = { viewModel.onOffClick(device) }
        )
    }
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = onRefresh,
            content = content,
        )
    }
}


@Preview("Home screen")
@Preview("Home screen (dark)", uiMode = UI_MODE_NIGHT_YES)
//@Preview("Home screen (big font)", fontScale = 1.5f)
//@Preview("Home screen (large screen)", device = Devices.PIXEL_C)
@Composable
fun PreviewHomeScreen() {
    ChipTheme {
        HomeScreen(
            viewModel = HomeViewModel(app = App()),
            navigateToDevice = { a, b -> },
            navigateToAddDevice = { /*TODO*/ },
            openDrawer = { /*TODO*/ })
    }
}