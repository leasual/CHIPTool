package com.se.wiser.compose.component

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.se.wiser.compose.theme.tabItemColor
import com.se.wiser.compose.theme.tabItemSelectColor

data class TabItem(var icon: Int, var title: String, var route: String)

@Composable
fun BottomNavigationBar(tabItems: List<TabItem>, navController: NavController) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
//        contentColor = Color.White,
        elevation = 0.dp

    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        tabItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selectedContentColor = MaterialTheme.colors.tabItemSelectColor,
                unselectedContentColor = MaterialTheme.colors.tabItemColor,
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // re selecting the same item
                        launchSingleTop = true
                        // Restore state when re selecting a previously selected item
                        restoreState = true
                    }
                }

            )
        }
    }
}