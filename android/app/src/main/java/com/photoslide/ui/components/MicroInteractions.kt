package com.photoslide.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.photoslide.ui.theme.Danger
import com.photoslide.ui.theme.Success
import com.photoslide.ui.theme.Warning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 可点击动画按钮
 */
@Composable
fun AnimatedIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    enabled: Boolean = true
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) color else color.copy(alpha = 0.5f)
        )
    }
}

/**
 * 点赞动画
 */
@Composable
fun LikeAnimation(
    modifier: Modifier = Modifier,
    triggered: Boolean = false
) {
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(triggered) {
        if (triggered) {
            // 弹跳效果
            scale.animateTo(
                targetValue = 1.5f,
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
            
            // 旋转效果
            rotation.animateTo(
                targetValue = 15f,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutSlowInEasing
                )
            )
            rotation.animateTo(
                targetValue = -15f,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutSlowInEasing
                )
            )
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    Box(
        modifier = modifier
            .scale(scale.value)
            .graphicsLayer {
                rotationZ = rotation.value
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "点赞",
            tint = Danger,
            modifier = Modifier.size(48.dp)
        )
    }
}

/**
 * 收藏动画
 */
@Composable
fun FavoriteAnimation(
    modifier: Modifier = Modifier,
    triggered: Boolean = false
) {
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }
    
    LaunchedEffect(triggered) {
        if (triggered) {
            // 放大效果
            scale.animateTo(
                targetValue = 1.3f,
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
            
            // 闪烁效果
            alpha.animateTo(
                targetValue = 0.5f,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutSlowInEasing
                )
            )
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    Box(
        modifier = modifier
            .scale(scale.value)
            .graphicsLayer {
                this.alpha = alpha.value
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "收藏",
            tint = Warning,
            modifier = Modifier.size(48.dp)
        )
    }
}

/**
 * 删除动画
 */
@Composable
fun DeleteAnimation(
    modifier: Modifier = Modifier,
    triggered: Boolean = false
) {
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    
    LaunchedEffect(triggered) {
        if (triggered) {
            // 旋转缩小效果
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
            scale.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
            
            // 重置
            delay(100)
            rotation.snapTo(0f)
            scale.snapTo(1f)
            alpha.snapTo(1f)
        }
    }
    
    Box(
        modifier = modifier
            .scale(scale.value)
            .graphicsLayer {
                rotationZ = rotation.value
                this.alpha = alpha.value
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "删除",
            tint = Danger,
            modifier = Modifier.size(48.dp)
        )
    }
}

/**
 * 成功动画
 */
@Composable
fun SuccessAnimation(
    modifier: Modifier = Modifier,
    triggered: Boolean = false
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(triggered) {
        if (triggered) {
            // 弹出效果
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
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
                )
            )
            
            // 保持显示
            delay(1000)
            
            // 淡出
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
            scale.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
            
            // 重置
            delay(100)
            scale.snapTo(0f)
            alpha.snapTo(0f)
        }
    }
    
    Box(
        modifier = modifier
            .scale(scale.value)
            .graphicsLayer {
                this.alpha = alpha.value
            }
            .background(
                color = Success,
                shape = CircleShape
            )
            .size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "成功",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * 涟漪效果
 */
@Composable
fun RippleEffect(
    modifier: Modifier = Modifier,
    triggered: Boolean = false,
    color: Color = Color.Blue
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0.5f) }
    
    LaunchedEffect(triggered) {
        if (triggered) {
            // 涟漪扩散
            scale.animateTo(
                targetValue = 3f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = LinearEasing
                )
            )
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = LinearEasing
                )
            )
            
            // 重置
            delay(100)
            scale.snapTo(0f)
            alpha.snapTo(0.5f)
        }
    }
    
    Box(
        modifier = modifier
            .scale(scale.value)
            .graphicsLayer {
                this.alpha = alpha.value
            }
            .background(
                color = color,
                shape = CircleShape
            )
            .size(32.dp)
    )
}

/**
 * 进度动画
 */
@Composable
fun ProgressAnimation(
    modifier: Modifier = Modifier,
    progress: Float = 0f
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "progress"
    )
    
    Box(modifier = modifier) {
        // 进度条实现
    }
}

/**
 * 加载动画
 */
@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                )
            )
            rotation.snapTo(0f)
        }
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = rotation.value
            }
            .background(
                color = Color.LightGray,
                shape = CircleShape
            )
            .size(32.dp)
    )
}