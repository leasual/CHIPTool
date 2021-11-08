package com.se.wiser.compose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.google.chip.chiptool.R
import com.se.wiser.compose.component.BackTopAppBar
import com.se.wiser.compose.viewmodel.AddDeviceViewModel

@Composable
fun AddDeviceScreen(
    viewModel: AddDeviceViewModel,
    back: () -> Unit,
    navigateToScanScreen: (Int) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    AddDeviceScreen(
        back = back,
        navigateToScanScreen = navigateToScanScreen,
        scaffoldState = scaffoldState
    )
}

@Composable
fun ItemButton(
    title: String,
    type: Int,
    navigateToScanScreen: (type: Int) -> Unit,
) {
    Button(
        onClick = { navigateToScanScreen.invoke(type) },
        modifier = Modifier
            .fillMaxWidth()
            .size(width = Dp.Infinity, height = 60.dp),
        shape = RoundedCornerShape(corner = CornerSize(30.dp)),

        ) {
        Text(title)
    }
}

@Composable
fun AddDeviceScreen(
    back: () -> Unit,
    navigateToScanScreen: (type: Int) -> Unit,
    scaffoldState: ScaffoldState
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.systemBarsPadding()) },
        topBar = {
            val title = stringResource(id = R.string.app_name)
            BackTopAppBar(
                title = "Add Device",
                back = back
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp),
        ) {
           ItemButton(title = "On Thread Network", type = 1, navigateToScanScreen = navigateToScanScreen)
            Spacer(modifier = Modifier.size(30.dp))
            ItemButton(title = "On Bridge Network", type = 2, navigateToScanScreen = navigateToScanScreen)
            Spacer(modifier = Modifier.size(30.dp))
            ItemButton(title = "On WiFi Network", type = 3, navigateToScanScreen = navigateToScanScreen)
        }
    }
}