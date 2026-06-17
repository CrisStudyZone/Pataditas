package com.serdigital.pataditas.domain.repository

import com.serdigital.pataditas.domain.model.DailyStats
import com.serdigital.pataditas.domain.model.HourlyActivity
import com.serdigital.pataditas.domain.model.KickSession
import com.serdigital.pataditas.domain.model.Note
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interfaz del repositorio de sesiones.
 * Permite implementaciones locales (Room) y remotas (Firestore) de forma intercambiable.
 */
interface KickSessionRepository {

    fun getAllSessions(): Flow<List<KickSession>>

    fun getSessionsByDay(date: LocalDate): Flow<List<KickSession>>

    /** Obtiene sesiones de los últimos N días para estadísticas */
    fun getSessionsLastDays(days: Int): Flow<List<KickSession>>

    suspend fun getSessionById(id: Long): KickSession?

    suspend fun saveSession(session: KickSession): Long

    suspend fun updateSession(session: KickSession)

    suspend fun deleteSession(id: Long)

    /** Para sincronización futura con Firestore */
    suspend fun syncPendingSessions()
}

/**
 * Interfaz del repositorio de notas.
 */
interface NoteRepository {

    fun getAllNotes(): Flow<List<Note>>

    suspend fun getNoteById(id: Long): Note?

    suspend fun saveNote(note: Note): Long

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(id: Long)
}

/**
 * Interfaz para estadísticas (puede ser calculada local o remota).
 */
interface StatsRepository {

    fun getDailyStats(days: Int): Flow<List<DailyStats>>

    fun getHourlyActivity(): Flow<List<HourlyActivity>>
}

/**
 * Interfaz de autenticación — preparada para Firebase Auth.
 * NO implementada todavía.
 */
interface AuthRepository {

    val isLoggedIn: Boolean

    val currentUserId: String?

    suspend fun signInWithEmail(email: String, password: String): Result<Unit>

    suspend fun signUpWithEmail(email: String, password: String): Result<Unit>

    suspend fun sendPasswordReset(email: String): Result<Unit>

    suspend fun signOut()
}
