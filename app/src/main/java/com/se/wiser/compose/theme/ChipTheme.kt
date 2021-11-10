package com.se.wiser.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors = lightColors(
    primary = lightGray,
    primaryVariant = lightGray,
    onPrimary = lightBlack,
    secondary = lightGray,
    secondaryVariant = lightGray,
    onSecondary = lightBlack,
    error = Red800,
    surface = lightGray,
    onSurface = lightBlack,
    background = lightGray,
    onBackground = lightBlack,
)

private val DarkThemeColors = darkColors(
    primary = darkGray,
    primaryVariant = darkGray,
    onPrimary = lightGray,
    secondary = darkGray,
    secondaryVariant = darkGray,
    onSecondary = lightGray,
    error = Red800,
    surface = darkGray,
    onSurface = lightGray,
    background = darkGray,
    onBackground = lightGray,
)
val Colors.schneider
    @Composable get() = if (isLight) Green else Green
val Colors.contentBackground
    @Composable get() = if (isLight) lightWhite else lightBlack
val Colors.switchOff: Color
    @Composable get() = if (isLight) Gray else Gray
val Colors.switchOn: Color
    @Composable get() = if (isLight) Green else Green

val Colors.tabItemColor: Color
    @Composable get() = if (isLight) iconNormal else iconDarkNormal
val Colors.tabItemSelectColor: Color
    @Composable get() = if (isLight) iconSelect else iconDarkSelect

@Composable
fun ChipTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        typography = ChipTypography,
        shapes = ChipShapes,
        content = content
    )
}