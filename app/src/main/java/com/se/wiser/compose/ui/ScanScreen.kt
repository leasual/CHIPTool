package com.se.wiser.compose.ui

import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.systemBarsPadding
import com.google.chip.chiptool.R
import com.se.wiser.compose.TAG
import com.se.wiser.compose.component.BackTopAppBar
import com.se.wiser.compose.viewmodel.ScanViewModel

@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    type: Int,
    back: () -> Unit,
    scaffoldState: ScaffoldState
) {
    Log.d(TAG, "type=$type")
    ScanScreen(
        back = back,
        type = type,
        scaffoldState = scaffoldState
    )
}

@Composable
fun ScanScreen(
    back: () -> Unit,
    type: Int,
    scaffoldState: ScaffoldState
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.systemBarsPadding()) },
        topBar = {
            val title = stringResource(id = R.string.app_name)
            BackTopAppBar(
                title = "Scan Device",
                back = back
            )
        }
    ) { innerPadding ->

    }
}