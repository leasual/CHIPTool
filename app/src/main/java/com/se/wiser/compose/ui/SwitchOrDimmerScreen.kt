package com.se.wiser.compose.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.systemBarsPadding
import com.se.wiser.compose.component.BackTopAppBar
import com.se.wiser.compose.viewmodel.SwitchOrDimmerViewModel
import com.se.wiser.model.BaseDevice

@Composable
fun SwitchOrDimmerScreen(
    viewModel: SwitchOrDimmerViewModel,
    device: BaseDevice,
    back: () -> Unit,
    scaffoldState: ScaffoldState
) {
    SwitchOrDimmerScreen(
        device = device,
        back = back,
        scaffoldState = scaffoldState
    )
}

@Composable
fun SwitchOrDimmerScreen(
    device: BaseDevice,
    back: () -> Unit,
    scaffoldState: ScaffoldState
) {
    Surface(
    ) {

        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(
                    hostState = it,
                    modifier = Modifier.systemBarsPadding()
                )
            },
            topBar = {
                BackTopAppBar(title = "Device", back = back)
            },
        ) { innerPadding ->

        }
    }
}