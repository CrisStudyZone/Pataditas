package com.serdigital.pataditas.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig

// Theme.kt o un nuevo archivo RemoteTheme.kt
object TemaRemoto {
    private val remoteConfig = Firebase.remoteConfig

    val colorPrimario: Color
        get() = when (remoteConfig.getString("app_theme_color")) {
            "rosa" -> RosadoSuave
            else -> CieloSuave
        }
}