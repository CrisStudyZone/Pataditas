package com.serdigital.pataditas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.serdigital.pataditas.data.local.Converters



/**
 * Entidad Room para sesiones de conteo de patadas.
 * Diseñada para sincronización futura con Firestore (campo remoteId).
 */
@Entity(tableName = "kick_sessions")
@TypeConverters(Converters::class)
data class KickSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: String? = null,
    val startTime: Long = 0,
    val endTime: Long? = null,
    val kickCount: Int = 0,
    val kickTimestamps: List<Long> = emptyList(),
    val date: Long = 0,
    val durationSeconds: Long = 0,
    val notes: String? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = 0
)

/**
 * Entidad Room para notas de la usuaria.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: String? = null,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

/**
 * Entidad para registro de contracciones
 */
@Entity(tableName = "contractions")
data class ContractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long?,
    val durationSeconds: Long?,
    /** Intervalo desde el inicio de la contracción anterior (en segundos) */
    val intervalFromPreviousSeconds: Long?,
    val sessionId: String, // agrupa contracciones de una misma sesión de trabajo de parto
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
