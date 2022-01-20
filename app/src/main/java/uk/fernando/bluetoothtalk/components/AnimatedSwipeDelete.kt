package uk.fernando.bluetoothtalk.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun <T> AnimatedSwipeDelete(
    modifier: Modifier = Modifier,
    item: T,
    background: @Composable (isDismissed: Boolean) -> Unit,
    content: @Composable (isDismissed: Boolean) -> Unit,
    directions: Set<DismissDirection> = setOf(DismissDirection.EndToStart),
    enter: EnterTransition = expandVertically(),
    exit: ExitTransition = shrinkVertically(
        animationSpec = tween(
            durationMillis = 500,
        )
    ),
    onDismiss: (T) -> Unit
) {
    val dismissState = rememberDismissState()
    val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == DismissValue.DismissedToStart) {
            onDismiss(item)
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = !isDismissed,
        enter = enter,
        exit = exit
    ) {
        SwipeToDismiss(
            modifier = modifier,
            state = dismissState,
            directions = directions,
            background = { background(isDismissed) },
            dismissContent = { content(isDismissed) }
        )
    }
}
