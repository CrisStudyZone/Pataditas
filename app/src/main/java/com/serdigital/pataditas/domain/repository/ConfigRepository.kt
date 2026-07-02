package com.serdigital.pataditas.domain.repository

import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun fetchAndActivate(): Flow<Boolean>
    fun getCampaignTheme(): com.serdigital.pataditas.domain.model.CampaignTheme
    fun shouldShowExamenButtons(): Boolean
    fun isChristmasThemeEnabled(): Boolean
}