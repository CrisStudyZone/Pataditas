package com.serdigital.pataditas.data.remoteConfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object FirebaseConfigUtils {
    fun getRemoteConfigInstance(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            // 0 segundos para que en el examen los cambios impacten en tiempo real
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaultValues = mapOf(
            "kick_button_text" to "Registrar Patada",
            "show_examen_buttons" to true
        )
        remoteConfig.setDefaultsAsync(defaultValues)

        return remoteConfig
    }
}