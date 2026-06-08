package com.photoslide.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.photoslide.data.model.Photo
import com.photoslide.ui.theme.Danger
import com.photoslide.ui.theme.Success
import com.photoslide.ui.theme.Warning
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

enum class SwipeDirection {
    LEFT, RIGHT, NONE
}

@Composable
fun SwipeCard(
    photo: Photo,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val density = LocalDensity.current
    
    val swipeThreshold = with(density) { 100.dp.toPx() }
    val rotationFactor = 0.1f
    
    var currentDirection by remember { mutableFloatStateOf(0f) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > swipeThreshold -> {
                                    // 右滑 - 保留
                                    offsetX.animateTo(
                                        targetValue = size.width.toFloat(),
                                        animationSpec = tween(300)
                                    )
                                    onSwipeRight()
                                }
                                offsetX.value < -swipeThreshold -> {
                                    // 左滑 - 删除
                                    offsetX.animateTo(
                                        targetValue = -size.width.toFloat(),
                                        animationSpec = tween(300)
                                    )
                                    onSwipeLeft()
                                }
                                offsetY.value < -swipeThreshold -> {
                                    // 上滑 - 收藏
                                    offsetY.animateTo(
                                        targetValue = -size.height.toFloat(),
                                        animationSpec = tween(300)
                                    )
                                    onSwipeUp()
                                }
                                else -> {
                                    // 回到原位
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(300)
                                    )
                                    offsetY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(300)
                                    )
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            val newOffsetX = offsetX.value + dragAmount.x
                            val newOffsetY = offsetY.value + dragAmount.y
                            
                            // 限制垂直方向只能上滑
                            val constrainedOffsetY = if (newOffsetY > 0) 0f else newOffsetY
                            
                            offsetX.snapTo(newOffsetX)
                            offsetY.snapTo(constrainedOffsetY)
                            
                            // 计算当前方向
                            currentDirection = when {
                                abs(newOffsetX) > abs(constrainedOffsetY) -> {
                                    if (newOffsetX > 0) 1f else -1f
                                }
                                else -> 0f
                            }
                        }
                    }
                )
            }
    ) {
        // 方向指示器
        if (abs(offsetX.value) > 50 || offsetY.value < -50) {
            DirectionIndicator(
                direction = when {
                    offsetX.value > 50 -> SwipeDirection.RIGHT
                    offsetX.value < -50 -> SwipeDirection.LEFT
                    offsetY.value < -50 -> SwipeDirection.RIGHT // 上滑显示为收藏
                    else -> SwipeDirection.NONE
                },
                progress = when {
                    offsetX.value > 50 -> (offsetX.value - 50) / (swipeThreshold - 50)
                    offsetX.value < -50 -> (-offsetX.value - 50) / (swipeThreshold - 50)
                    offsetY.value < -50 -> (-offsetY.value - 50) / (swipeThreshold - 50)
                    else -> 0f
                }.coerceIn(0f, 1f)
            )
        }
        
        // 照片卡片
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        offsetX.value.roundToInt(),
                        offsetY.value.roundToInt()
                    )
                }
                .graphicsLayer {
                    rotationZ = offsetX.value * rotationFactor
                    // 添加透视效果
                    cameraDistance = 12f * density.density
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 照片图片
                AsyncImage(
                    model = photo.uri,
                    contentDescription = photo.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // 渐变遮罩
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 60f
                            )
                        )
                )
                
                // 照片信息
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = photo.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${photo.dateAdded} · ${String.format("%.1f", photo.sizeInMB)} MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // 操作按钮
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 删除按钮
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Danger, CircleShape)
                            .alpha(if (offsetX.value < -50) 1f else 0.6f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "删除",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    // 收藏按钮
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Warning, CircleShape)
                            .alpha(if (offsetY.value < -50) 1f else 0.6f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "收藏",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    // 保留按钮
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Success, CircleShape)
                            .alpha(if (offsetX.value > 50) 1f else 0.6f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "保留",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DirectionIndicator(
    direction: SwipeDirection,
    progress: Float
) {
    val color = when (direction) {
        SwipeDirection.LEFT -> Danger
        SwipeDirection.RIGHT -> Success
        SwipeDirection.NONE -> Color.Transparent
    }
    
    val text = when (direction) {
        SwipeDirection.LEFT -> "删除"
        SwipeDirection.RIGHT -> "保留"
        SwipeDirection.NONE -> ""
    }
    
    val icon = when (direction) {
        SwipeDirection.LEFT -> Icons.Default.Close
        SwipeDirection.RIGHT -> Icons.Default.Favorite
        SwipeDirection.NONE -> Icons.Default.Close
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(progress * 0.8f)
            .background(color.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}