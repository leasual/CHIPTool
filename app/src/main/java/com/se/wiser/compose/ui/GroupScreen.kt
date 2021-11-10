package com.se.wiser.compose.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.google.chip.chiptool.R
import com.se.wiser.compose.component.InsetAwareTopAppBar
import com.se.wiser.compose.viewmodel.GroupViewModel
import com.se.wiser.data.entity.SceneEntity

@Composable
fun GroupScreen(
    viewModel: GroupViewModel,
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit,
    navigateToAddDevice:() -> Unit,
) {
    val sceneList = arrayListOf<SceneEntity>()
    GroupScreen(
        viewModel = viewModel,
        sceneList = sceneList,
        scaffoldState = scaffoldState,
        openDrawer = openDrawer,
        navigateToAddDevice = navigateToAddDevice
    )
}

@Composable
fun GroupScreen(
    viewModel: GroupViewModel,
    sceneList: ArrayList<SceneEntity>,
    openDrawer: () -> Unit,
    navigateToAddDevice:() -> Unit,
    scaffoldState: ScaffoldState
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.systemBarsPadding()) },
        topBar = {
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

    }
}