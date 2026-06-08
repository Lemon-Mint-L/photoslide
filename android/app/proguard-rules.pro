# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep the application class
-keep class com.photoslide.PhotoSlideApp { *; }

# Keep the MainActivity
-keep class com.photoslide.MainActivity { *; }

# Keep all data classes
-keep class com.photoslide.data.model.** { *; }
-keep class com.photoslide.data.local.database.entity.** { *; }

# Keep Room entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Hilt modules
-keep class com.photoslide.di.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep ML Kit
-keep class com.google.mlkit.** { *; }

# Keep Coil
-keep class coil.** { *; }

# Keep Coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

# Keep Android
-keep class android.** { *; }
-keep class androidx.** { *; }

# Keep standard library
-keep class java.** { *; }
-keep class javax.** { *; }

# Remove logging
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
}

# Remove debug code
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullParameter(java.lang.Object, java.lang.String);
}