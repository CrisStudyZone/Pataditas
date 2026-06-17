package com.serdigital.pataditas.domain.model

import java.time.LocalDateTime

/**
 * Modelo de dominio para una sesión de conteo.
 * Desacoplado de Room y de Firebase.
 */
data class KickSession(
    val id: Long = 0,
    val remoteId: String? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val kickCount: Int,
    val kickTimestamps: List<Long>,
    val date: LocalDateTime,
    val durationSeconds: Long,
    val notes: String? = null,
    val isSynced: Boolean = false
) {
    val durationFormatted: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }

    val isActive: Boolean
        get() = endTime == null

    val averageIntervalSeconds: Double?
        get() {
            if (kickTimestamps.size < 2) return null
            val intervals = kickTimestamps.zipWithNext { a, b -> (b - a) / 1000.0 }
            return intervals.average()
        }
}

/**
 * Modelo de dominio para notas.
 */
data class Note(
    val id: Long = 0,
    val remoteId: String? = null,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isSynced: Boolean = false
)

/**
 * Modelo para estadísticas diarias.
 */
data class DailyStats(
    val date: LocalDateTime,
    val totalKicks: Int,
    val sessionCount: Int,
    val averageKicksPerSession: Double,
    val mostActiveHour: Int?,
    val firstSession: KickSession?,
    val lastSession: KickSession?
)

/**
 * Actividad agrupada por franja horaria (para el mapa de calor).
 */
data class HourlyActivity(
    val hour: Int,
    val totalKicks: Int,
    val sessionCount: Int
)

/**
 * Estado de la sesión activa en tiempo real.
 */
data class ActiveSession(
    val sessionId: Long?,
    val startTime: Long,
    val kickCount: Int,
    val kickTimestamps: List<Long>,
    val elapsedSeconds: Long
)
