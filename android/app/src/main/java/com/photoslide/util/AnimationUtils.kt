package com.photoslide.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
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
import kotlinx.coroutines.launch

// 动画持续时间
object AnimationDuration {
    const val FAST = 150
    const val NORMAL = 300
    const val SLOW = 500
    const val EXTRA_SLOW = 800
}

// 缓动函数
object AnimationEasing {
    val FastOutSlowIn = FastOutSlowInEasing
    val LinearOutSlowIn = LinearOutSlowInEasing
    val Linear = LinearEasing
}

// 弹簧配置
object SpringConfig {
    val Gentle = Spring.StiffnessLow
    val Medium = Spring.StiffnessMedium
    val Firm = Spring.StiffnessHigh
    val Rigid = Spring.StiffnessVeryHigh
}

// 淡入动画
fun fadeInAnimation(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        )
    )
}

// 淡出动画
fun fadeOutAnimation(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        )
    )
}

// 从右侧滑入
fun slideInFromRight(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        ),
        initialOffsetX = { it }
    )
}

// 从左侧滑入
fun slideInFromLeft(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        ),
        initialOffsetX = { -it }
    )
}

// 向右滑出
fun slideOutToRight(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        ),
        targetOffsetX = { it }
    )
}

// 向左滑出
fun slideOutToLeft(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        ),
        targetOffsetX = { -it }
    )
}

// 从底部滑入
fun slideInFromBottom(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): EnterTransition {
    return slideInVertically(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        ),
        initialOffsetY = { it }
    )
}

// 向底部滑出
fun slideOutToBottom(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): ExitTransition {
    return slideOutVertically(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        ),
        targetOffsetY = { it }
    )
}

// 展开动画
fun expandAnimation(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): EnterTransition {
    return expandVertically(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        )
    )
}

// 收起动画
fun collapseAnimation(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): ExitTransition {
    return shrinkVertically(
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        )
    )
}

// 组合动画：淡入 + 从底部滑入
fun fadeInSlideUp(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): EnterTransition {
    return fadeInAnimation(duration, delay) + slideInFromBottom(duration, delay)
}

// 组合动画：淡出 + 向底部滑出
fun fadeOutSlideDown(
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): ExitTransition {
    return fadeOutAnimation(duration, delay) + slideOutToBottom(duration, delay)
}

// 弹簧动画
fun <T> springAnimation(
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessLow
): Spring<T> {
    return spring(
        dampingRatio = dampingRatio,
        stiffness = stiffness
    )
}

// 脉冲动画
@Composable
fun pulseAnimation(
    initialValue: Float = 1f,
    targetValue: Float = 1.2f,
    duration: Int = 1000
): Float {
    var value by remember { mutableStateOf(initialValue) }
    
    LaunchedEffect(Unit) {
        while (true) {
            value = targetValue
            delay(duration.toLong())
            value = initialValue
            delay(duration.toLong())
        }
    }
    
    return value
}

// 旋转动画
@Composable
fun rotateAnimation(
    duration: Int = 2000,
    repeatCount: Int = Int.MAX_VALUE
): Float {
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(
                durationMillis = duration,
                easing = LinearEasing
            )
        )
    }
    
    return rotation.value
}

// 缩放动画
@Composable
fun scaleAnimation(
    initialValue: Float = 0f,
    targetValue: Float = 1f,
    duration: Int = AnimationDuration.NORMAL,
    delay: Int = 0
): Float {
    val scale by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = AnimationEasing.FastOutSlowIn
        ),
        label = "scale"
    )
    
    return scale
}

// 弹跳动画
@Composable
fun bounceAnimation(
    duration: Int = 1000
): Float {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            scale.animateTo(
                targetValue = 1.1f,
                animationSpec = tween(
                    durationMillis = duration / 2,
                    easing = AnimationEasing.FastOutSlowIn
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration / 2,
                    easing = AnimationEasing.FastOutSlowIn
                )
            )
        }
    }
    
    return scale.value
}

// 摇晃动画
@Composable
fun shakeAnimation(
    duration: Int = 500,
    shakeCount: Int = 3
): Float {
    val offset = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            repeat(shakeCount) {
                offset.animateTo(
                    targetValue = 10f,
                    animationSpec = tween(
                        durationMillis = duration / (shakeCount * 2),
                        easing = AnimationEasing.FastOutSlowIn
                    )
                )
                offset.animateTo(
                    targetValue = -10f,
                    animationSpec = tween(
                        durationMillis = duration / (shakeCount * 2),
                        easing = AnimationEasing.FastOutSlowIn
                    )
                )
            }
            offset.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = duration / (shakeCount * 2),
                    easing = AnimationEasing.FastOutSlowIn
                )
            )
            delay(2000) // 等待2秒后再次摇晃
        }
    }
    
    return offset.value
}

// 渐变动画
@Composable
fun gradientAnimation(
    duration: Int = 3000
): Float {
    val progress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration,
                    easing = LinearEasing
                )
            )
            progress.snapTo(0f)
        }
    }
    
    return progress.value
}

// 打字机效果
@Composable
fun typewriterAnimation(
    text: String,
    duration: Int = 1000
): String {
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(text) {
        displayedText = ""
        text.forEachIndexed { index, _ ->
            delay(duration.toLong() / text.length)
            displayedText = text.substring(0, index + 1)
        }
    }
    
    return displayedText
}

// 数字计数动画
@Composable
fun countAnimation(
    targetValue: Int,
    duration: Int = 1000,
    delay: Int = 0
): Int {
    val currentValue = remember { Animatable(0f) }
    
    LaunchedEffect(targetValue) {
        currentValue.snapTo(0f)
        delay(delay.toLong())
        currentValue.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = tween(
                durationMillis = duration,
                easing = AnimationEasing.FastOutSlowIn
            )
        )
    }
    
    return currentValue.value.toInt()
}

// 延迟显示动画
@Composable
fun DelayedVisibility(
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeInAnimation() + expandAnimation(),
        exit = fadeOutAnimation() + collapseAnimation()
    ) {
        content()
    }
}

// 动画修饰符
fun Modifier.animateScale(
    scale: Float,
    duration: Int = AnimationDuration.NORMAL
): Modifier {
    return this.scale(scale)
}

fun Modifier.animateAlpha(
    alpha: Float,
    duration: Int = AnimationDuration.NORMAL
): Modifier {
    return this.alpha(alpha)
}

fun Modifier.animateRotation(
    rotation: Float,
    duration: Int = AnimationDuration.NORMAL
): Modifier {
    return this.graphicsLayer {
        rotationZ = rotation
    }
}