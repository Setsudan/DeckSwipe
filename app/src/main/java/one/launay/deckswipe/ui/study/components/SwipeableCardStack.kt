package one.launay.deckswipe.ui.study.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableCardStack(
    modifier: Modifier = Modifier,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val rotation = offsetX.value / 30f
    val swipeThreshold = 160f
    val swipeFraction = (offsetX.value / swipeThreshold).coerceIn(-1f, 1f)

    val leftColor: Color = if (swipeFraction < 0f) {
        MaterialTheme.colorScheme.error.copy(alpha = -swipeFraction * 0.4f)
    } else {
        Color.Transparent
    }
    val rightColor: Color = if (swipeFraction > 0f) {
        MaterialTheme.colorScheme.primary.copy(alpha = swipeFraction * 0.4f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                coroutineScope {
                    detectDragGestures(
                        onDragEnd = {
                            val finalX = offsetX.value
                            when {
                                finalX > swipeThreshold -> {
                                    launch {
                                        offsetX.animateTo(
                                            targetValue = 1000f,
                                            animationSpec = TweenSpec(durationMillis = 200)
                                        )
                                        onSwipedRight()
                                        offsetX.snapTo(0f)
                                        offsetY.snapTo(0f)
                                    }
                                }
                                finalX < -swipeThreshold -> {
                                    launch {
                                        offsetX.animateTo(
                                            targetValue = -1000f,
                                            animationSpec = TweenSpec(durationMillis = 200)
                                        )
                                        onSwipedLeft()
                                        offsetX.snapTo(0f)
                                        offsetY.snapTo(0f)
                                    }
                                }
                                else -> {
                                    launch {
                                        offsetX.animateTo(0f, animationSpec = spring())
                                        offsetY.animateTo(0f, animationSpec = spring())
                                    }
                                }
                            }
                        },
                        onDragCancel = {
                            launch {
                                offsetX.animateTo(0f, animationSpec = spring())
                                offsetY.animateTo(0f, animationSpec = spring())
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        val (dx, dy) = dragAmount
                        launch {
                            offsetX.snapTo(offsetX.value + dx)
                            offsetY.snapTo(offsetY.value + dy)
                        }
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(leftColor)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(rightColor)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .offset {
                    IntOffset(
                        x = offsetX.value.roundToInt(),
                        y = offsetY.value.roundToInt()
                    )
                }
                .rotate(rotation)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            content()
        }
    }
}

