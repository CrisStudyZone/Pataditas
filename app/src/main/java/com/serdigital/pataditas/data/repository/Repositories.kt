package com.serdigital.pataditas.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.serdigital.pataditas.data.local.dao.KickSessionDao
import com.serdigital.pataditas.data.local.dao.NoteDao
import com.serdigital.pataditas.data.mapper.toDomain
import com.serdigital.pataditas.data.mapper.toEntity
import com.serdigital.pataditas.domain.repository.AuthRepository
import com.serdigital.pataditas.domain.repository.KickSessionRepository
import com.serdigital.pataditas.domain.repository.NoteRepository
import com.serdigital.pataditas.domain.repository.StatsRepository
import com.serdigital.pataditas.domain.model.DailyStats
import com.serdigital.pataditas.domain.model.HourlyActivity
import com.serdigital.pataditas.domain.model.KickSession
import com.serdigital.pataditas.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KickSessionRepositoryImpl @Inject constructor(
    private val dao: KickSessionDao
) : KickSessionRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllSessions(): Flow<List<KickSession>> =
        dao.getAllSessions().map { list -> list.map { it.toDomain() } }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getSessionsByDay(date: LocalDate): Flow<List<KickSession>> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return dao.getSessionsByDay(startOfDay, endOfDay)
            .map { list -> list.map { it.toDomain() } }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getSessionsLastDays(days: Int): Flow<List<KickSession>> {
        val fromDate = LocalDate.now().minusDays(days.toLong())
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getSessionsFromDate(fromDate)
            .map { list -> list.map { it.toDomain() } }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getSessionById(id: Long): KickSession? =
        dao.getSessionById(id)?.toDomain()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveSession(session: KickSession): Long =
        dao.insertSession(session.toEntity())

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateSession(session: KickSession) =
        dao.updateSession(session.toEntity())

    override suspend fun deleteSession(id: Long) =
        dao.deleteSessionById(id)

    /** Placeholder para sincronización futura con Firestore */
    override suspend fun syncPendingSessions() {
        // TODO: implementar con Firebase Firestore
        // val unsynced = dao.getUnsyncedSessions()
        // firestoreService.uploadSessions(unsynced)
        // unsynced.forEach { dao.markAsSynced(it.id, generatedRemoteId) }
    }
}

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NoteRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllNotes(): Flow<List<Note>> =
        dao.getAllNotes().map { list -> list.map { it.toDomain() } }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getNoteById(id: Long): Note? =
        dao.getNoteById(id)?.toDomain()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveNote(note: Note): Long =
        dao.insertNote(note.toEntity())

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateNote(note: Note) =
        dao.updateNote(note.toEntity())

    override suspend fun deleteNote(id: Long) =
        dao.deleteNoteById(id)
}

@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val kickSessionRepository: KickSessionRepository
) : StatsRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getDailyStats(days: Int): Flow<List<DailyStats>> =
        kickSessionRepository.getSessionsLastDays(days).map { sessions ->
            sessions
                .groupBy { it.date.toLocalDate() }
                .map { (date, daySessions) ->
                    val completedSessions = daySessions.filter { !it.isActive }
                    val totalKicks = completedSessions.sumOf { it.kickCount }
                    val avgKicks = if (completedSessions.isNotEmpty())
                        totalKicks.toDouble() / completedSessions.size else 0.0

                    // Hora de mayor actividad
                    val mostActiveHour = completedSessions
                        .flatMap { s -> s.kickTimestamps.map { ts ->
                            Instant.ofEpochMilli(ts)
                                .atZone(ZoneId.systemDefault()).hour
                        }}
                        .groupingBy { it }
                        .eachCount()
                        .maxByOrNull { it.value }?.key

                    DailyStats(
                        date = date.atStartOfDay(),
                        totalKicks = totalKicks,
                        sessionCount = completedSessions.size,
                        averageKicksPerSession = avgKicks,
                        mostActiveHour = mostActiveHour,
                        firstSession = completedSessions.minByOrNull { it.startTime },
                        lastSession = completedSessions.maxByOrNull { it.startTime }
                    )
                }
                .sortedByDescending { it.date }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getHourlyActivity(): Flow<List<HourlyActivity>> =
        kickSessionRepository.getSessionsLastDays(30).map { sessions ->
            val allTimestamps = sessions.flatMap { it.kickTimestamps }
            val grouped = allTimestamps
                .map { ts ->
                    Instant.ofEpochMilli(ts)
                        .atZone(ZoneId.systemDefault()).hour
                }
                .groupingBy { it }
                .eachCount()

            (0..23).map { hour ->
                HourlyActivity(
                    hour = hour,
                    totalKicks = grouped[hour] ?: 0,
                    sessionCount = sessions.count { session ->
                        session.startTime.hour == hour
                    }
                )
            }
        }
}

/**
Implementación original utilizada antes de integrar Firebase Authentication.
Se conserva únicamente como referencia para el TP.

@Singleton
class StubAuthRepositoryImpl @Inject constructor() : AuthRepository {

    override val isLoggedIn: Boolean = false
    override val currentUserId: String? = null

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        Result.failure(NotImplementedError("Firebase Auth no implementado aún"))

    override suspend fun signUpWithEmail(email: String, password: String): Result<Unit> =
        Result.failure(NotImplementedError("Firebase Auth no implementado aún"))

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        Result.failure(NotImplementedError("Firebase Auth no implementado aún"))

    override suspend fun signOut() { /* no-op */ }

}*/
