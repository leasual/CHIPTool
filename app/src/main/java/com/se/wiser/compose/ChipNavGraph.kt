package com.se.wiser.compose

import android.util.Log
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.se.wiser.compose.ui.*
import com.se.wiser.compose.viewmodel.*
import com.se.wiser.data.AppContainer
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.DimmerDevice
import com.se.wiser.model.OnOffDevice
import com.se.wiser.utils.fromJson
import com.se.wiser.utils.toJson
import kotlinx.coroutines.launch

object MainDestinations {
    const val Home = "home"
    const val Group = "group"
    const val NoBottomBar = "noBottomBar"
    const val Device = "device"
    const val AddDevice = "addDevice"
    const val ScanQRCode = "scanQRCode"
    const val PairingDevice = "pairingDevice"
}

const val TAG = "ChipNavGraph"

//https://github.com/levinzonr/compose-safe-routing/blob/master/accompanist-navigation/src/main/java/cz/levinzonr/saferoute/accompanist/navigation/NavGraphBuilder%2BBottomSheet.kt
//for nav animation example

@Composable
fun ChipNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = MainDestinations.Home
) {
    val actions = remember(navController) { MainActions(navController) }
    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.Home) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                viewModel = viewModel,
                navigateToDevice = MainActions(navController).navigateToDeviceScreen,
                navigateToAddDevice = MainActions(navController).navigateToAddDeviceScreen,
                openDrawer = openDrawer,
                scaffoldState = scaffoldState
            )
        }
        composable(MainDestinations.Group) {
            val viewModel = hiltViewModel<GroupViewModel>()
            GroupScreen(
                viewModel = viewModel,
                scaffoldState = scaffoldState,
                openDrawer = openDrawer,
                navigateToAddDevice = MainActions(navController).navigateToAddDeviceScreen,
            )
        }
        composable(MainDestinations.AddDevice) {
            val viewModel = hiltViewModel<AddDeviceViewModel>()
            AddDeviceScreen(
                viewModel = viewModel,
                navigateToScanScreen = MainActions(navController).navigateToScanScreen,
                back = MainActions(navController).upPress,
            )
        }
        composable(
            route = "${MainDestinations.ScanQRCode}/{type}",
            arguments = listOf(
                navArgument("type") { type = NavType.IntType}
            )
        ) {  backStackEntry ->
            val viewModel = hiltViewModel<ScanViewModel>()
            val type = backStackEntry.arguments?.getInt("type") ?: return@composable
            ScanScreen(
                viewModel = viewModel,
                type = type,
                back = MainActions(navController).upPress,
                scaffoldState = scaffoldState
            )
        }
        composable(
            route = "${MainDestinations.Device}/{device}/{className}",
            //it doesn't need this when pass serializable or parcelable model
            //has error so use json instead
//            arguments = listOf(
//                navArgument("device") { type = NavType.Serializable }
//            )
            arguments = listOf(
                navArgument("device") { type = NavType.StringType },
                navArgument("className") { type = NavType.StringType }
            )
        ) {  backStackEntry ->
            val viewModel = hiltViewModel<SwitchOrDimmerViewModel>()
            //using serializable should do this
            val className = backStackEntry.arguments?.getString("className") ?: return@composable
            val cls = Class.forName(className)
            val device = backStackEntry.arguments?.getString("device")?.fromJson(cls) ?: return@composable
            when(device) {
                is OnOffDevice, is DimmerDevice -> SwitchOrDimmerScreen(
                        viewModel = viewModel,
                        device = if (device is OnOffDevice) device else device as DimmerDevice,
                        back = MainActions(navController).upPress,
                        scaffoldState = scaffoldState
                    )
            }
        }
    }
}

@Composable
fun NavController.currentScreen(): State<String> {
    val currentScreen = remember { mutableStateOf(MainDestinations.Home) }
    DisposableEffect(key1 = this){
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == MainDestinations.Home
                        || it.route == MainDestinations.Group } -> {
                    currentScreen.value = MainDestinations.Home
                } else -> currentScreen.value = MainDestinations.NoBottomBar
            }
        }
        addOnDestinationChangedListener(listener = listener)
        onDispose {  }
    }
    return currentScreen
}

class MainActions(navController: NavHostController) {

    val navigateToDeviceScreen: (BaseDevice, Class<*>) -> Unit = { device, cls ->
        //using serializable should do this
//        navController.currentBackStackEntry?.arguments?.putSerializable("device", device)
        Log.d(TAG, "before convert=$device, cls=${cls.canonicalName}")
        navController.navigate("${MainDestinations.Device}/${device.toJson()}/${cls.canonicalName}")
    }
    val navigateToAddDeviceScreen: () -> Unit = {
        navController.navigate(MainDestinations.AddDevice)
    }
    val navigateToScanScreen: (Int) -> Unit = { type ->
        navController.navigate("${MainDestinations.ScanQRCode}/$type")
    }
    val navigatePairingScreen: (Int) -> Unit = { type ->
        navController.navigate("${MainDestinations.PairingDevice}/$type")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
//        navController.popBackStack()
    }
}