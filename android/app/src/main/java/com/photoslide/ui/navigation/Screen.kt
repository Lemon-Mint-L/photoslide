package com.photoslide.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AIScan : Screen("ai_scan")
    object Album : Screen("album")
    object Analysis : Screen("analysis")
    object Settings : Screen("settings")
    object Compare : Screen("compare/{photoGroupId}") {
        fun createRoute(photoGroupId: Long) = "compare/$photoGroupId"
    }
    object PhotoDetail : Screen("photo/{photoId}") {
        fun createRoute(photoId: Long) = "photo/$photoId"
    }
}