package com.photoslide.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.photoslide.ui.theme.Danger
import com.photoslide.ui.theme.Primary
import com.photoslide.ui.theme.Success
import com.photoslide.ui.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部状态栏
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Primary, Color(0xFF8B5CF6))
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "设置",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "自定义您的应用体验",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        
        // 设置列表
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 外观设置
            item {
                SettingsSection(title = "外观") {
                    SettingCard(
                        title = "深色模式",
                        subtitle = "切换深色/浅色主题",
                        icon = Icons.Default.DarkMode,
                        color = Primary,
                        hasSwitch = true,
                        switchState = uiState.darkMode,
                        onSwitchChange = { viewModel.setDarkMode(it) }
                    )
                    
                    SettingCard(
                        title = "动态颜色",
                        subtitle = "使用Material You动态颜色",
                        icon = Icons.Default.Star,
                        color = Color(0xFF8B5CF6),
                        hasSwitch = true,
                        switchState = uiState.dynamicColor,
                        onSwitchChange = { viewModel.setDynamicColor(it) }
                    )
                }
            }
            
            // 通知设置
            item {
                SettingsSection(title = "通知") {
                    SettingCard(
                        title = "通知设置",
                        subtitle = "清理提醒和通知",
                        icon = Icons.Default.Notifications,
                        color = Success,
                        hasSwitch = true,
                        switchState = uiState.notificationsEnabled,
                        onSwitchChange = { viewModel.setNotificationsEnabled(it) }
                    )
                    
                    if (uiState.notificationsEnabled) {
                        SettingCard(
                            title = "清理提醒",
                            subtitle = "定期提醒您清理照片",
                            icon = Icons.Default.Notifications,
                            color = Color(0xFF3B82F6),
                            hasSwitch = true,
                            switchState = uiState.cleaningReminder,
                            onSwitchChange = { viewModel.setCleaningReminder(it) }
                        )
                        
                        if (uiState.cleaningReminder) {
                            SettingSlider(
                                title = "提醒间隔",
                                subtitle = "每 ${uiState.reminderIntervalDays} 天提醒一次",
                                value = uiState.reminderIntervalDays.toFloat(),
                                valueRange = 1f..30f,
                                onValueChange = { viewModel.setReminderIntervalDays(it.toInt()) }
                            )
                        }
                    }
                }
            }
            
            // 清理设置
            item {
                SettingsSection(title = "清理") {
                    SettingCard(
                        title = "自动删除",
                        subtitle = "${uiState.autoDeleteDays}天后自动删除标记的照片",
                        icon = Icons.Default.Delete,
                        color = Danger,
                        hasSwitch = true,
                        switchState = uiState.autoDeleteEnabled,
                        onSwitchChange = { viewModel.setAutoDeleteEnabled(it) }
                    )
                    
                    if (uiState.autoDeleteEnabled) {
                        SettingSlider(
                            title = "自动删除天数",
                            subtitle = "${uiState.autoDeleteDays}天后自动删除",
                            value = uiState.autoDeleteDays.toFloat(),
                            valueRange = 1f..90f,
                            onValueChange = { viewModel.setAutoDeleteDays(it.toInt()) }
                        )
                    }
                    
                    SettingCard(
                        title = "删除前确认",
                        subtitle = "删除照片前显示确认对话框",
                        icon = Icons.Default.Security,
                        color = Color(0xFF14B8A6),
                        hasSwitch = true,
                        switchState = uiState.confirmBeforeDelete,
                        onSwitchChange = { viewModel.setConfirmBeforeDelete(it) }
                    )
                    
                    SettingCard(
                        title = "保留收藏",
                        subtitle = "自动保留收藏的照片",
                        icon = Icons.Default.Star,
                        color = Color(0xFFF59E0B),
                        hasSwitch = true,
                        switchState = uiState.keepFavorites,
                        onSwitchChange = { viewModel.setKeepFavorites(it) }
                    )
                }
            }
            
            // AI设置
            item {
                SettingsSection(title = "AI检测") {
                    SettingCard(
                        title = "AI扫描",
                        subtitle = "启用智能照片检测",
                        icon = Icons.Default.Star,
                        color = Primary,
                        hasSwitch = true,
                        switchState = uiState.aiScanEnabled,
                        onSwitchChange = { viewModel.setAiScanEnabled(it) }
                    )
                    
                    if (uiState.aiScanEnabled) {
                        SettingSlider(
                            title = "模糊检测阈值",
                            subtitle = "阈值: ${uiState.blurThreshold}",
                            value = uiState.blurThreshold.toFloat(),
                            valueRange = 10f..100f,
                            onValueChange = { viewModel.setBlurThreshold(it.toInt()) }
                        )
                        
                        SettingSlider(
                            title = "重复检测阈值",
                            subtitle = "阈值: ${uiState.duplicateThreshold}",
                            value = uiState.duplicateThreshold.toFloat(),
                            valueRange = 1f..20f,
                            onValueChange = { viewModel.setDuplicateThreshold(it.toInt()) }
                        )
                    }
                }
            }
            
            // 统计信息
            item {
                SettingsSection(title = "统计") {
                    StatCard(
                        title = "已删除照片",
                        value = "${uiState.totalPhotosDeleted}",
                        subtitle = "张"
                    )
                    
                    StatCard(
                        title = "已释放空间",
                        value = formatFileSize(uiState.totalSpaceFreed),
                        subtitle = ""
                    )
                    
                    StatCard(
                        title = "连续清理",
                        value = "${uiState.cleaningStreak}",
                        subtitle = "天"
                    )
                }
            }
            
            // 其他设置
            item {
                SettingsSection(title = "其他") {
                    SettingCard(
                        title = "语言设置",
                        subtitle = "简体中文",
                        icon = Icons.Default.Language,
                        color = Color(0xFF8B5CF6)
                    )
                    
                    SettingCard(
                        title = "存储路径",
                        subtitle = "内部存储",
                        icon = Icons.Default.Folder,
                        color = Color(0xFF3B82F6)
                    )
                    
                    SettingCard(
                        title = "隐私政策",
                        subtitle = "查看隐私政策",
                        icon = Icons.Default.Security,
                        color = Color(0xFF14B8A6)
                    )
                    
                    SettingCard(
                        title = "关于我们",
                        subtitle = "版本 1.0.0",
                        icon = Icons.Default.Info,
                        color = Color(0xFF6B7280)
                    )
                    
                    SettingCard(
                        title = "给个好评",
                        subtitle = "如果您喜欢这个应用",
                        icon = Icons.Default.Star,
                        color = Color(0xFFF59E0B)
                    )
                }
            }
            
            // 重置按钮
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    Text("重置所有设置")
                }
            }
        }
        
        // 底部信息
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "photoslide",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
                Text(
                    text = "智能照片清理应用",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "完全中文支持 · 完全免费 · 隐私保护",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    // 重置确认对话框
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("重置所有设置") },
            text = { Text("确定要重置所有设置吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllSettings()
                        showResetDialog = false
                    }
                ) {
                    Text("重置", color = Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    hasSwitch: Boolean = false,
    switchState: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (hasSwitch) {
                Switch(
                    checked = switchState,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }
    }
}

@Composable
fun SettingSlider(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Primary,
                    activeTrackColor = Primary
                )
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}