package com.se.wiser.compose

import android.util.Log
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.se.wiser.compose.ui.AddDeviceScreen
import com.se.wiser.compose.ui.HomeScreen
import com.se.wiser.compose.ui.ScanScreen
import com.se.wiser.compose.ui.SwitchOrDimmerScreen
import com.se.wiser.compose.viewmodel.AddDeviceViewModel
import com.se.wiser.compose.viewmodel.HomeViewModel
import com.se.wiser.compose.viewmodel.ScanViewModel
import com.se.wiser.compose.viewmodel.SwitchOrDimmerViewModel
import com.se.wiser.data.AppContainer
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.DimmerDevice
import com.se.wiser.model.OnOffDevice
import com.se.wiser.utils.fromJson
import com.se.wiser.utils.toJson
import kotlinx.coroutines.launch

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val DEVICE = "device"
    const val ADD_DEVICE = "addDevice"
    const val SCAN_QRCODE = "scanQRCode"
    const val PAIRING_DEVICE = "pairingDevice"
}

const val TAG = "ChipNavGraph"

//https://github.com/levinzonr/compose-safe-routing/blob/master/accompanist-navigation/src/main/java/cz/levinzonr/saferoute/accompanist/navigation/NavGraphBuilder%2BBottomSheet.kt
//for nav animation example

@Composable
fun ChipNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = MainDestinations.HOME_ROUTE
) {
    val actions = remember(navController) { MainActions(navController) }
    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                viewModel = viewModel,
                navigateToDevice = MainActions(navController).navigateToDeviceScreen,
                navigateToAddDevice = MainActions(navController).navigateToAddDeviceScreen,
                openDrawer = openDrawer,
                scaffoldState = scaffoldState
            )
        }
        composable(MainDestinations.ADD_DEVICE) {
            val viewModel = hiltViewModel<AddDeviceViewModel>()
            AddDeviceScreen(
                viewModel = viewModel,
                navigateToScanScreen = MainActions(navController).navigateToScanScreen,
                back = MainActions(navController).upPress,
            )
        }
        composable(
            route = "${MainDestinations.SCAN_QRCODE}/{type}",
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
            route = "${MainDestinations.DEVICE}/{device}/{className}",
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

class MainActions(navController: NavHostController) {

    val navigateToDeviceScreen: (BaseDevice, Class<*>) -> Unit = { device, cls ->
        //using serializable should do this
//        navController.currentBackStackEntry?.arguments?.putSerializable("device", device)
        Log.d(TAG, "before convert=$device, cls=${cls.canonicalName}")
        navController.navigate("${MainDestinations.DEVICE}/${device.toJson()}/${cls.canonicalName}")
    }
    val navigateToAddDeviceScreen: () -> Unit = {
        navController.navigate(MainDestinations.ADD_DEVICE)
    }
    val navigateToScanScreen: (Int) -> Unit = { type ->
        navController.navigate("${MainDestinations.SCAN_QRCODE}/$type")
    }
    val navigatePairingScreen: (Int) -> Unit = { type ->
        navController.navigate("${MainDestinations.PAIRING_DEVICE}/$type")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
//        navController.popBackStack()
    }
}