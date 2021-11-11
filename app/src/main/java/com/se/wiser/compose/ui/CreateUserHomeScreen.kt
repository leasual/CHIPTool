package com.se.wiser.compose.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.systemBarsPadding
import com.google.chip.chiptool.R
import com.se.wiser.compose.TAG
import com.se.wiser.compose.component.BackTopAppBar
import com.se.wiser.compose.viewmodel.CreateUserHomeViewModel
import com.se.wiser.compose.viewmodel.ScanViewModel

@ExperimentalAnimationApi
@Composable
fun CreateUserHomeScreen(
    viewModel: CreateUserHomeViewModel,
    navigateToHome: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val validateFlag = viewModel.validateFields.collectAsState()

    CreateUserHomeScreen(
        viewModel = viewModel,
        validateFlag = validateFlag,
        navigateToHome = navigateToHome,
        scaffoldState = scaffoldState
    )
}

@ExperimentalAnimationApi
@Composable
fun CreateUserHomeScreen(
    viewModel: CreateUserHomeViewModel,
    validateFlag: State<String>,
    navigateToHome: () -> Unit,
    scaffoldState: ScaffoldState
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.systemBarsPadding()) },

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(colorResource(id = R.color.splash_bg))
                .fillMaxSize(),
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = {
                        // Slide in from top
                        -it
                    },
                    animationSpec = tween(
                        durationMillis = HomeActivity.splashFadeDurationMillis,
                        easing = CubicBezierEasing(0f, 0f, 0f, 1f)

                    )
                ),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 0.dp)
                        .background(colorResource(id = R.color.splash_bg))
                        .fillMaxSize()
                ) {
                    Text(
                        "Start Your Home",
//                            stringResource(id = R.string.start_screen_title),
                        fontSize = 36.sp,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(16.dp)
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(color = Color.White)
                    )
                }
            }
        }
//        LaunchedEffect(true) {
//            visible = true
//        }
    }
}