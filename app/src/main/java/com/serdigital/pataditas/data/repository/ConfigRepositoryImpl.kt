package com.serdigital.pataditas.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.serdigital.pataditas.data.remoteConfig.FirebaseConfigUtils
import com.serdigital.pataditas.domain.model.CampaignTheme
import com.serdigital.pataditas.domain.repository.ConfigRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject


class ConfigRepositoryImpl : ConfigRepository {
    private val remoteConfig: FirebaseRemoteConfig = FirebaseConfigUtils.getRemoteConfigInstance()

    override fun fetchAndActivate(): Flow<Boolean> = callbackFlow {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                trySend(task.isSuccessful)
                close()
            }
        awaitClose { /* No-op */ }
    }

    override fun getCampaignTheme(): CampaignTheme {
        // Leemos el String plano que viene de la consola de Firebase
        val jsonString = remoteConfig.getString("campaign_theme")

        return try {
            val jsonObject = JSONObject(jsonString)
            val activeCampaign = jsonObject.getString("active_campaign")

            // Si no hay campaña activa, devolvemos el estado vacío de forma segura
            if (activeCampaign == "NONE") {
                return CampaignTheme(activeCampaign = "NONE", imageUrl = "")
            }

            // Buscamos dentro del objeto "campaigns" la campaña que esté activa
            val campaignsObject = jsonObject.getJSONObject("campaigns")
            val activeCampaignData = campaignsObject.getJSONObject(activeCampaign)
            val imageUrl = activeCampaignData.getString("image_url")

            CampaignTheme(
                activeCampaign = activeCampaign,
                imageUrl = imageUrl
            )
        } catch (e: Exception) {
            CampaignTheme(activeCampaign = "NONE", imageUrl = "")
        }
    }

    override fun shouldShowExamenButtons(): Boolean = remoteConfig.getBoolean("show_examen_buttons")

    override fun isChristmasThemeEnabled(): Boolean {
        return remoteConfig.getBoolean("show_christmas_theme")
    }
}