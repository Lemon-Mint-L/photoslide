# photoslide Android 项目设置指南

## 环境要求

- Windows 10/11
- JDK 17 (已安装)
- Android Studio (已安装)
- Android SDK 34

## 快速开始

### 1. 打开项目

运行以下命令打开项目：
```powershell
.\open-project.ps1
```

或者手动操作：
1. 打开 Android Studio
2. 选择 "Open an existing Android Studio project"
3. 导航到 `C:\Users\lsj\Desktop\opencode\photoslide\android`
4. 点击 "OK"

### 2. 配置SDK

首次打开项目时，Android Studio会提示配置SDK：

1. 点击 "File" -> "Settings" -> "Appearance & Behavior" -> "System Settings" -> "Android SDK"
2. 确保已安装以下组件：
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android SDK Platform-Tools
   - Android Emulator

### 3. 创建模拟器

1. 打开 "Tools" -> "AVD Manager"
2. 点击 "Create Virtual Device"
3. 选择设备（推荐 Pixel 6）
4. 选择系统镜像（推荐 API 34）
5. 完成创建

### 4. 运行应用

1. 选择模拟器或连接的设备
2. 点击 "Run" (绿色三角形按钮)
3. 等待应用安装和启动

## 项目结构

```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/photoslide/
│   │   │   ├── data/              # 数据层
│   │   │   ├── di/                # 依赖注入
│   │   │   ├── service/           # AI服务
│   │   │   ├── ui/                # UI层
│   │   │   └── util/              # 工具类
│   │   ├── res/                   # 资源文件
│   │   └── AndroidManifest.xml    # 应用清单
│   └── build.gradle.kts           # 应用构建配置
├── build.gradle.kts               # 项目构建配置
└── settings.gradle.kts            # 项目设置
```

## 主要功能

1. **首页** - 滑动清理照片
2. **AI扫描** - 智能检测模糊、重复照片
3. **相册** - 相册管理
4. **分析** - 存储空间分析
5. **设置** - 应用设置

## 常见问题

### Q: Gradle同步失败
A: 检查网络连接，或使用代理

### Q: 缺少依赖
A: 运行 `./gradlew build` 下载依赖

### Q: 模拟器启动失败
A: 检查Hyper-V或HAXM是否启用

### Q: 应用崩溃
A: 查看Logcat日志，定位错误原因

## 技术栈

- Kotlin
- Jetpack Compose
- Hilt (依赖注入)
- Room (数据库)
- DataStore (设置存储)
- Coil (图片加载)
- ML Kit (AI检测)

## 联系方式

如有问题，请查看项目文档或联系开发者。