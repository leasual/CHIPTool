package com.se.wiser.compose.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.chip.chiptool.R
import com.se.wiser.compose.Constants.COLLAPSE_ANIMATION_DURATION
import com.se.wiser.compose.Constants.EXPAND_ANIMATION_DURATION
import com.se.wiser.compose.Constants.FADE_IN_ANIMATION_DURATION
import com.se.wiser.compose.Constants.FADE_OUT_ANIMATION_DURATION
import com.se.wiser.compose.TAG
import com.se.wiser.compose.theme.contentBackground

@Composable
fun ExpandedCard(
    title: String,
    content: @Composable () -> Unit,
    onCardArrowClick: () -> Unit,
    expanded: Boolean
) {
    //https://developer.android.com/reference/kotlin/androidx/compose/animation/core/MutableTransitionState
    val transitionState = remember { MutableTransitionState(expanded) }
    transitionState.targetState = !expanded
    val transition = updateTransition(transitionState, label = "transition")
    val arrowRotationDegree by transition.animateFloat(
        {
            tween(durationMillis = EXPAND_ANIMATION_DURATION)
        },
        label = "rotationDegreeTransition"
    ) {
        if (it) 0f else 180f
    }
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {
        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardTitle(title = title)
                CardArrow(
                    degrees = arrowRotationDegree,
                    onClick = onCardArrowClick
                )
            }
            ExpandableContent(
                visible = expanded,
                content = content
            )
        }

    }
}

@Composable
fun CardArrow(
    degrees: Float,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            tint = MaterialTheme.colors.onBackground,
            painter = painterResource(id = R.mipmap.outline_keyboard_arrow_down_white_24),
            contentDescription = "arrow",
            modifier = Modifier
                .size(30.dp, 30.dp)
                .rotate(degrees = degrees)
        )
    }
}

@Composable
fun CardTitle(title: String) {
    val typography = MaterialTheme.typography
    Text(
        text = title,
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier
//            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Left,
        style = typography.h6
    )
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("RememberReturnType")
@Composable
fun ExpandableContent(
    visible: Boolean = true,
    content: @Composable () -> Unit
) {
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = FADE_IN_ANIMATION_DURATION,
                easing = FastOutLinearInEasing
            )
        )
    }
    val enterExpand = remember {
        expandVertically(animationSpec = tween(EXPAND_ANIMATION_DURATION))
    }
    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = FADE_OUT_ANIMATION_DURATION,
                easing = LinearOutSlowInEasing
            )
        )
    }
    val exitCollapse = remember {
        shrinkVertically(animationSpec = tween(COLLAPSE_ANIMATION_DURATION))
    }
    AnimatedVisibility(
        visible = visible,
        enter = enterExpand + enterFadeIn,
        exit = exitCollapse + exitFadeOut
    ) {
        content()
    }
}
