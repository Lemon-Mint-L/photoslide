package com.photoslide.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

/**
 * 动画容器
 */
@Composable
fun AnimatedContainer(
    visible: Boolean,
    modifier: Modifier = Modifier,
    delay: Int = 0,
    content: @Composable BoxScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            )
        ) + expandVertically(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + shrinkVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ),
        modifier = modifier
    ) {
        Box(content = content)
    }
}

/**
 * 滑入动画容器
 */
@Composable
fun SlideInContainer(
    visible: Boolean,
    modifier: Modifier = Modifier,
    delay: Int = 0,
    content: @Composable BoxScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            ),
            initialOffsetY = { it }
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + slideOutVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            ),
            targetOffsetY = { it }
        ),
        modifier = modifier
    ) {
        Box(content = content)
    }
}

/**
 * 脉冲动画效果
 */
@Composable
fun PulseEffect(
    modifier: Modifier = Modifier,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    duration: Int = 1000,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            scale.animateTo(
                targetValue = maxScale,
                animationSpec = tween(
                    durationMillis = duration / 2,
                    easing = FastOutSlowInEasing
                )
            )
            scale.animateTo(
                targetValue = minScale,
                animationSpec = tween(
                    durationMillis = duration / 2,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    Box(
        modifier = modifier.scale(scale.value)
    ) {
        content()
    }
}

/**
 * 旋转动画效果
 */
@Composable
fun SpinEffect(
    modifier: Modifier = Modifier,
    duration: Int = 2000,
    content: @Composable () -> Unit
) {
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = duration,
                    easing = LinearEasing
                )
            )
            rotation.snapTo(0f)
        }
    }
    
    Box(
        modifier = modifier.graphicsLayer {
            rotationZ = rotation.value
        }
    ) {
        content()
    }
}

/**
 * 弹跳动画效果
 */
@Composable
fun BounceEffect(
    modifier: Modifier = Modifier,
    triggered: Boolean = false,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(triggered) {
        if (triggered) {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }
    
    Box(
        modifier = modifier.scale(scale.value)
    ) {
        content()
    }
}

/**
 * 摇晃动画效果
 */
@Composable
fun ShakeEffect(
    modifier: Modifier = Modifier,
    triggered: Boolean = false,
    content: @Composable () -> Unit
) {
    val offset = remember { Animatable(0f) }
    
    LaunchedEffect(triggered) {
        if (triggered) {
            repeat(3) {
                offset.animateTo(
                    targetValue = 10f,
                    animationSpec = tween(
                        durationMillis = 50,
                        easing = FastOutSlowInEasing
                    )
                )
                offset.animateTo(
                    targetValue = -10f,
                    animationSpec = tween(
                        durationMillis = 50,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            offset.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 50,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    Box(
        modifier = modifier.graphicsLayer {
            translationX = offset.value
        }
    ) {
        content()
    }
}

/**
 * 渐变动画效果
 */
@Composable
fun FadeInEffect(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
    }
    
    Box(
        modifier = modifier.alpha(alpha.value)
    ) {
        content()
    }
}

/**
 * 缩放进入动画
 */
@Composable
fun ScaleInEffect(
    modifier: Modifier = Modifier,
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutSlowInEasing
            )
        )
    }
    
    Box(
        modifier = modifier
            .scale(scale.value)
            .alpha(alpha.value)
    ) {
        content()
    }
}

/**
 * 交错动画容器
 */
@Composable
fun StaggeredContainer(
    modifier: Modifier = Modifier,
    staggerDelay: Int = 100,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        content()
    }
}

/**
 * 动画数字
 */
@Composable
fun AnimatedNumber(
    number: Int,
    modifier: Modifier = Modifier,
    duration: Int = 1000
) {
    val animatedNumber = remember { Animatable(0f) }
    
    LaunchedEffect(number) {
        animatedNumber.snapTo(0f)
        animatedNumber.animateTo(
            targetValue = number.toFloat(),
            animationSpec = tween(
                durationMillis = duration,
                easing = FastOutSlowInEasing
            )
        )
    }
    
    Box(modifier = modifier) {
        // 这里可以显示数字
    }
}

/**
 * 打字机效果
 */
@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    duration: Int = 1000
) {
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(text) {
        displayedText = ""
        text.forEachIndexed { index, _ ->
            delay(duration.toLong() / text.length)
            displayedText = text.substring(0, index + 1)
        }
    }
    
    Box(modifier = modifier) {
        // 这里可以显示文本
    }
}