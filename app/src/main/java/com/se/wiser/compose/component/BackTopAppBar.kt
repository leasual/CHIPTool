package com.se.wiser.compose.component
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.chip.chiptool.R

@Composable
fun BackTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    back: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 0.dp
) {
    Surface(
        color = backgroundColor,
        elevation = elevation,
        modifier = modifier
    ) {
        TopAppBar(
            title = {
                    Text(
                        text = title,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp, bottom = 4.dp)
                            .offset((-32).dp),
                        textAlign = TextAlign.Center
                    )
            },
            navigationIcon = {
                IconButton(onClick = back) {
                    Icon(
                        painter = painterResource(id = R.mipmap.outline_chevron_left_white_24),
                        contentDescription = "back"
                    )
                }
            },
            actions = actions,
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false)
        )
    }
}