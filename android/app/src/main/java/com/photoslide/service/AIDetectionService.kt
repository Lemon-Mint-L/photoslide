package com.photoslide.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.photoslide.data.model.Photo
import com.photoslide.data.model.PhotoGroup
import com.photoslide.data.model.GroupType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.sqrt

@Singleton
class AIDetectionService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * 检测模糊照片
     * 使用拉普拉斯算子计算图像清晰度
     */
    suspend fun detectBlurryPhotos(photos: List<Photo>, threshold: Float = 50f): List<Photo> = withContext(Dispatchers.Default) {
        photos.filter { photo ->
            try {
                val bitmap = loadBitmap(photo.uri) ?: return@filter false
                val blurScore = calculateBlurScore(bitmap)
                bitmap.recycle()
                blurScore < threshold
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * 计算模糊分数
     * 使用拉普拉斯算子方差
     */
    private fun calculateBlurScore(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 转换为灰度
        val grayPixels = IntArray(width * height) { i ->
            val pixel = pixels[i]
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }

        // 应用拉普拉斯算子
        val laplacianValues = mutableListOf<Int>()
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val idx = y * width + x
                val laplacian = -grayPixels[idx - width] -
                        grayPixels[idx - 1] +
                        4 * grayPixels[idx] -
                        grayPixels[idx + 1] -
                        grayPixels[idx + width]
                laplacianValues.add(abs(laplacian))
            }
        }

        // 计算方差
        val mean = laplacianValues.average().toFloat()
        val variance = laplacianValues.map { (it - mean) * (it - mean) }.average().toFloat()
        
        return sqrt(variance)
    }

    /**
     * 检测重复照片
     * 使用感知哈希算法
     */
    suspend fun detectDuplicatePhotos(photos: List<Photo>): List<PhotoGroup> = withContext(Dispatchers.Default) {
        val hashMap = mutableMapOf<String, MutableList<Photo>>()
        
        photos.forEach { photo ->
            try {
                val bitmap = loadBitmap(photo.uri) ?: return@forEach
                val hash = calculatePerceptualHash(bitmap)
                bitmap.recycle()
                
                hashMap.getOrPut(hash) { mutableListOf() }.add(photo)
            } catch (e: Exception) {
                // 忽略加载失败的照片
            }
        }
        
        // 过滤出有重复的照片组
        hashMap.filter { it.value.size > 1 }.map { (_, group) ->
            val bestPhoto = group.maxByOrNull { it.width * it.height } ?: group.first()
            PhotoGroup(
                bestPhoto = bestPhoto,
                similarPhotos = group,
                type = GroupType.DUPLICATE
            )
        }
    }

    /**
     * 计算感知哈希
     * 使用pHash算法
     */
    private fun calculatePerceptualHash(bitmap: Bitmap): String {
        val size = 32
        val smallSize = 8
        
        // 缩放到32x32
        val scaled = Bitmap.createScaledBitmap(bitmap, size, size, true)
        val pixels = IntArray(size * size)
        scaled.getPixels(pixels, 0, size, 0, 0, size, size)
        scaled.recycle()

        // 转换为灰度
        val grayPixels = IntArray(size * size) { i ->
            val pixel = pixels[i]
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }

        // 计算DCT（简化版）
        val dctValues = DoubleArray(smallSize * smallSize)
        for (u in 0 until smallSize) {
            for (v in 0 until smallSize) {
                var sum = 0.0
                for (x in 0 until size) {
                    for (y in 0 until size) {
                        sum += grayPixels[y * size + x] *
                                Math.cos((2 * x + 1) * u * Math.PI / (2 * size)) *
                                Math.cos((2 * y + 1) * v * Math.PI / (2 * size))
                    }
                }
                dctValues[v * smallSize + u] = sum
            }
        }

        // 计算平均值（排除DC分量）
        val mean = dctValues.drop(1).average()

        // 生成哈希
        val hash = StringBuilder()
        for (i in dctValues.indices) {
            hash.append(if (dctValues[i] > mean) "1" else "0")
        }

        return hash.toString()
    }

    /**
     * 检测截图
     */
    suspend fun detectScreenshots(photos: List<Photo>): List<Photo> = withContext(Dispatchers.Default) {
        photos.filter { photo ->
            isScreenshot(photo)
        }
    }

    /**
     * 判断是否为截图
     */
    private fun isScreenshot(photo: Photo): Boolean {
        val name = photo.name.lowercase()
        return name.contains("screenshot") ||
                name.contains("截图") ||
                name.contains("screen") ||
                name.contains("capture") ||
                // 检查屏幕比例
                isScreenAspectRatio(photo.width, photo.height)
    }

    /**
     * 检查是否为屏幕比例
     */
    private fun isScreenAspectRatio(width: Int, height: Int): Boolean {
        if (width == 0 || height == 0) return false
        val ratio = width.toFloat() / height.toFloat()
        // 常见屏幕比例
        val commonRatios = listOf(
            16f / 9f,  // 16:9
            9f / 16f,  // 9:16
            18f / 9f,  // 18:9
            19.5f / 9f, // 19.5:9
            4f / 3f,   // 4:3
            3f / 4f    // 3:4
        )
        return commonRatios.any { abs(ratio - it) < 0.1f }
    }

    /**
     * 查找最佳照片
     * 基于清晰度、曝光、构图等因素
     */
    suspend fun findBestShots(photos: List<Photo>): List<Photo> = withContext(Dispatchers.Default) {
        photos.map { photo ->
            val score = calculatePhotoQuality(photo)
            photo.copy(qualityScore = score)
        }.sortedByDescending { it.qualityScore }
         .take(50)
    }

    /**
     * 计算照片质量分数
     */
    private suspend fun calculatePhotoQuality(photo: Photo): Float {
        try {
            val bitmap = loadBitmap(photo.uri) ?: return 0f
            
            var score = 0f
            
            // 1. 清晰度分数 (0-30分)
            val blurScore = calculateBlurScore(bitmap)
            score += (blurScore / 100f).coerceIn(0f, 1f) * 30f
            
            // 2. 分辨率分数 (0-20分)
            val resolution = photo.width * photo.height
            score += when {
                resolution >= 1920 * 1080 -> 20f
                resolution >= 1280 * 720 -> 15f
                resolution >= 800 * 600 -> 10f
                else -> 5f
            }
            
            // 3. 曝光分数 (0-25分)
            val exposureScore = calculateExposureScore(bitmap)
            score += exposureScore * 25f
            
            // 4. 对比度分数 (0-25分)
            val contrastScore = calculateContrastScore(bitmap)
            score += contrastScore * 25f
            
            bitmap.recycle()
            return score
        } catch (e: Exception) {
            return 0f
        }
    }

    /**
     * 计算曝光分数
     */
    private fun calculateExposureScore(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 计算平均亮度
        var totalBrightness = 0L
        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            totalBrightness += (0.299 * r + 0.587 * g + 0.114 * b).toLong()
        }
        val avgBrightness = totalBrightness.toFloat() / pixels.size

        // 理想亮度在100-150之间
        val idealBrightness = 125f
        val diff = abs(avgBrightness - idealBrightness)
        return (1f - diff / 125f).coerceIn(0f, 1f)
    }

    /**
     * 计算对比度分数
     */
    private fun calculateContrastScore(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 计算亮度标准差
        val brightnesses = pixels.map { pixel ->
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        }

        val mean = brightnesses.average()
        val variance = brightnesses.map { (it - mean) * (it - mean) }.average()
        val stdDev = sqrt(variance)

        // 标准差在50-80之间为最佳对比度
        return when {
            stdDev in 50.0..80.0 -> 1f
            stdDev < 50 -> (stdDev / 50f).toFloat()
            else -> (1f - (stdDev - 80f) / 100f).coerceIn(0f, 1f)
        }
    }

    /**
     * 加载Bitmap
     */
    private fun loadBitmap(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 2 // 降低采样率以提高性能
                }
                BitmapFactory.decodeStream(inputStream, null, options)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 计算两个哈希的汉明距离
     */
    private fun hammingDistance(hash1: String, hash2: String): Int {
        return hash1.zip(hash2).count { it.first != it.second }
    }

    /**
     * 判断两个照片是否相似
     */
    fun arePhotosSimilar(hash1: String, hash2: String, threshold: Int = 10): Boolean {
        return hammingDistance(hash1, hash2) <= threshold
    }
}