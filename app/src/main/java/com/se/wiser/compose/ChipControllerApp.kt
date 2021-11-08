package com.se.wiser.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.chip.chiptool.R
import com.se.wiser.compose.component.BottomNavigationBar
import com.se.wiser.compose.component.TabItem
import com.se.wiser.compose.theme.ChipTheme
import com.se.wiser.data.AppContainer

@Composable
fun ChipControllerApp(
    appContainer: AppContainer
) {
    ChipTheme {
        ProvideWindowInsets {

            val systemUiController = rememberSystemUiController()
            var darkIcons = MaterialTheme.colors.isLight
            systemUiController.run {
                setStatusBarColor(MaterialTheme.colors.primary, darkIcons = darkIcons)
                setSystemBarsColor(MaterialTheme.colors.primary, darkIcons = darkIcons)
                setNavigationBarColor(MaterialTheme.colors.primary, darkIcons = darkIcons)
            }
//            SideEffect {
//                systemUiController.run {
//                    setStatusBarColor(MaterialTheme.colors.background, darkIcons = darkIcons)
//                    setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
//                    setNavigationBarColor(Color.Transparent, darkIcons = darkIcons)
//                }
//            }
            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()
            val tabItems = listOf<TabItem>(
                TabItem(R.mipmap.outline_add_white_24, "Device", MainDestinations.HOME_ROUTE),
                TabItem(R.mipmap.outline_add_white_24, "Group", MainDestinations.ADD_DEVICE),
            )

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: MainDestinations.HOME_ROUTE
            Scaffold(
                scaffoldState = scaffoldState,
                drawerContent = {
//                    AppDrawer(
//
//                    )
                },
                bottomBar = {
                    BottomNavigationBar(tabItems = tabItems, navController = navController)
                },
                //https://medium.com/mobile-app-development-publication/android-jetpack-compose-inset-padding-made-easy-5f156a790979
                //modified virtual system navigation bar padding
                modifier = Modifier.navigationBarsPadding()
            ) {
                ChipNavGraph(
                    appContainer = appContainer,
                    navController = navController,
                    scaffoldState = scaffoldState
                )
            }
        }
    }
}