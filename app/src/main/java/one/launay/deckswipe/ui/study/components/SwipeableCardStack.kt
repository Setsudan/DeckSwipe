package one.launay.deckswipe.ui.study.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import one.launay.deckswipe.ui.theme.LargeCardCornerShape

@Composable
fun SwipeableCardStack(
    modifier: Modifier = Modifier,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit,
    onCardTap: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val latestTap = rememberUpdatedState(onCardTap)
    val latestLeft = rememberUpdatedState(onSwipedLeft)
    val latestRight = rememberUpdatedState(onSwipedRight)
    val animScope: CoroutineScope = rememberCoroutineScope()

    val rotation = offsetX.value / 30f
    val swipeThreshold = 160f
    val swipeFraction = (offsetX.value / swipeThreshold).coerceIn(-1f, 1f)

    val leftColor: Color = if (swipeFraction < 0f) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = -swipeFraction * 0.18f)
    } else {
        Color.Transparent
    }
    val rightColor: Color = if (swipeFraction > 0f) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = swipeFraction * 0.55f)
    } else {
        Color.Transparent
    }

    val touchSlop = LocalViewConfiguration.current.touchSlop.toFloat()

    Box(
        modifier = modifier
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
                    shape = LargeCardCornerShape
                )
                .pointerInput(touchSlop, swipeThreshold, latestTap, latestLeft, latestRight, animScope) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val pointerId = down.id
                        down.consume()

                        val startX = offsetX.value
                        val startY = offsetY.value
                        var accumX = 0f
                        var accumY = 0f
                        var peakAbsOffsetX = 0f

                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val change = event.changes.firstOrNull { it.id == pointerId }
                                ?: break

                            if (change.changedToUpIgnoreConsumed()) {
                                change.consume()
                                val finalX = startX + accumX
                                if (peakAbsOffsetX < touchSlop) {
                                    latestTap.value?.invoke()
                                    animScope.launch {
                                        offsetX.animateTo(0f, animationSpec = spring())
                                        offsetY.animateTo(0f, animationSpec = spring())
                                    }
                                } else {
                                    when {
                                        finalX > swipeThreshold -> {
                                            animScope.launch {
                                                offsetX.animateTo(
                                                    targetValue = 1000f,
                                                    animationSpec = TweenSpec(durationMillis = 200)
                                                )
                                                latestRight.value.invoke()
                                                offsetX.snapTo(0f)
                                                offsetY.snapTo(0f)
                                            }
                                        }
                                        finalX < -swipeThreshold -> {
                                            animScope.launch {
                                                offsetX.animateTo(
                                                    targetValue = -1000f,
                                                    animationSpec = TweenSpec(durationMillis = 200)
                                                )
                                                latestLeft.value.invoke()
                                                offsetX.snapTo(0f)
                                                offsetY.snapTo(0f)
                                            }
                                        }
                                        else -> {
                                            animScope.launch {
                                                offsetX.animateTo(0f, animationSpec = spring())
                                                offsetY.animateTo(0f, animationSpec = spring())
                                            }
                                        }
                                    }
                                }
                                break
                            }

                            val pan = change.positionChange()
                            if (pan.x != 0f || pan.y != 0f) {
                                change.consume()
                                accumX += pan.x
                                accumY += pan.y
                                peakAbsOffsetX = max(peakAbsOffsetX, abs(accumX))
                                animScope.launch {
                                    offsetX.snapTo(startX + accumX)
                                    offsetY.snapTo(startY + accumY)
                                }
                            }
                        }
                    }
                }
        ) {
            content()
        }
    }
}
