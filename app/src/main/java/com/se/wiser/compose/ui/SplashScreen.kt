package com.se.wiser.compose.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.chip.chiptool.R

@ExperimentalAnimationApi
@Composable
fun SplashScreen() {
    var visible by remember { mutableStateOf(false) }
    val systemUiController = rememberSystemUiController()
    val darkIcons = MaterialTheme.colors.isLight
    systemUiController.run {
        setStatusBarColor(colorResource(id = R.color.splash_bg), darkIcons = false)
    }
    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .background(colorResource(id = R.color.splash_bg))
                    .fillMaxSize(),
            ) {
                AnimatedVisibility(
                    visible = visible,
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
            LaunchedEffect(true) {
                visible = true
            }
        }
    )
}