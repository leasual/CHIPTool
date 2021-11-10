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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.se.wiser.compose.component.ExpandedCard
import com.se.wiser.compose.component.InsetAwareTopAppBar
import com.se.wiser.compose.component.OneDimmerCard
import com.se.wiser.compose.component.OneSwitchCard
import com.se.wiser.compose.theme.ChipTheme
import com.se.wiser.compose.viewmodel.HomeViewModel
import com.se.wiser.data.dao.GatewayDao
import com.se.wiser.data.dao.UserDao
import com.se.wiser.data.entity.UserEntity
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.DimmerDevice
import com.se.wiser.model.OnOffDevice
import com.se.wiser.utils.ClusterUtil
import com.se.wiser.utils.DeviceUtil
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
    val expandedComposeCardIds = viewModel.expandedComposeCardIdsList.collectAsState()
    val scrollState = rememberLazyListState()
    val userAndHomeList = viewModel.getUserAndHomeList().collectAsState(arrayListOf())
//    Log.d(TAG, "expandedCardIds= ${expandedCardIds.value}")
    Log.d(TAG, "userAndHomeList=${userAndHomeList.value.size}")
    HomeScreen(
        viewModel = viewModel,
        openDrawer = openDrawer,
        navigateToDevice = navigateToDevice,
        navigateToAddDevice = navigateToAddDevice,
        scaffoldState = scaffoldState,
        expandedCardIds = expandedCardIds,
        expandedComposeCardIds = expandedComposeCardIds,
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
    expandedComposeCardIds: State<List<Int>>,
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
//                        onClick = navigateToAddDevice,
                    onClick = {
                        viewModel.addUser(
                            UserEntity(
                                1, "James", "123456"
                            )
                        )
                    }
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
        var showAddUserDialog by remember { mutableStateOf(false) }
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
                val dimmer = ClusterUtil.createNewDevice(ProductModeId.DIMMER_1G, 0, "Dimmer", true)
                deviceList.add(dimmer)
                val onOff = ClusterUtil.createNewDevice(ProductModeId.SWITCH_1G, 0, "OnOff", true)
                deviceList.add(onOff)
                val composeList = arrayListOf<BaseDevice>()
                val onOff1 = ClusterUtil.createNewDevice(ProductModeId.SWITCH_2G, 0, "OnOff", true)
                val onOff2 = ClusterUtil.createNewDevice(ProductModeId.SWITCH_2G, 1, "OnOff", false)
                composeList.add(onOff1)
                composeList.add(onOff2)
                deviceList.add(composeList)
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
                    expandedComposeCardIds = expandedComposeCardIds,
                    viewModel = viewModel,
                    navigateToDevice = navigateToDevice,
                )
            }
        )
    }
}

//TODO popup add user and home
//popup window https://dev.to/mahendranv/jetpack-compose-1-dropdownmenu-weather-ui-5fnk
//dialog https://juejin.cn/post/6961239810463760398
// https://stackoverflow.com/questions/68852110/show-custom-alert-dialog-in-jetpack-compose
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
    expandedComposeCardIds: State<List<Int>>,
    viewModel: HomeViewModel,
    navigateToDevice: (BaseDevice, Class<*>) -> Unit
) {
    val context = LocalContext.current
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
                    if (card is BaseDevice) {
                        DeviceCard(
                            index = -1,
                            device = card,
                            viewModel = viewModel,
                            navigateToDevice = navigateToDevice
                        )
                    } else {
                        ExpandedCard(
                            title = DeviceUtil.getDeviceName((card as ArrayList<BaseDevice>)[0].productModeId, context),
                            content = {
                                Column {
                                    (card as? ArrayList<Any>)?.let {
                                        it.mapIndexed { index, device ->
                                            DeviceCard(
                                                index = index,
                                                device = device,
                                                viewModel = viewModel,
                                                navigateToDevice = navigateToDevice
                                            )
                                        }
                                    }
                                }
                            },
                            onCardArrowClick = { viewModel.onComposeCardArrowClicked(index) },
                            expanded = expandedComposeCardIds.value.contains(index)
                        )
                    }
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
    index: Int,
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
//    ChipTheme {
//        HomeScreen(
//            viewModel = HomeViewModel(app = App(), , ),
//            navigateToDevice = { a, b -> },
//            navigateToAddDevice = { /*TODO*/ },
//            openDrawer = { /*TODO*/ })
//    }
}