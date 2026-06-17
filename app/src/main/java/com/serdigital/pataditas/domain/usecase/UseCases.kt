package com.serdigital.pataditas.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.serdigital.pataditas.domain.model.KickSession
import com.serdigital.pataditas.domain.model.Note
import com.serdigital.pataditas.domain.repository.KickSessionRepository
import com.serdigital.pataditas.domain.repository.NoteRepository
import com.serdigital.pataditas.domain.repository.StatsRepository
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

// ─── Sesiones ────────────────────────────────────────────────────────────────

class GetAllSessionsUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    operator fun invoke() = repository.getAllSessions()
}

class GetSessionsByDayUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    operator fun invoke(date: LocalDate) = repository.getSessionsByDay(date)
}

class SaveSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    suspend operator fun invoke(session: KickSession): Long =
        repository.saveSession(session)
}

class UpdateSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    suspend operator fun invoke(session: KickSession) =
        repository.updateSession(session)
}

class DeleteSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteSession(id)
}

class StartSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(): Long {
        val now = LocalDateTime.now()
        val session = KickSession(
            startTime = now,
            endTime = null,
            kickCount = 0,
            kickTimestamps = emptyList(),
            date = now,
            durationSeconds = 0
        )
        return repository.saveSession(session)
    }
}

class AddKickToSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    suspend operator fun invoke(sessionId: Long, kickTimestamp: Long) {
        val session = repository.getSessionById(sessionId) ?: return
        val updated = session.copy(
            kickCount = session.kickCount + 1,
            kickTimestamps = session.kickTimestamps + kickTimestamp
        )
        repository.updateSession(updated)
    }
}

class EndSessionUseCase @Inject constructor(
    private val repository: KickSessionRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(sessionId: Long) {
        val session = repository.getSessionById(sessionId) ?: return
        val now = LocalDateTime.now()
        val durationSeconds = Duration.between(session.startTime, now).seconds
        val updated = session.copy(
            endTime = now,
            durationSeconds = durationSeconds
        )
        repository.updateSession(updated)
    }
}

// ─── Notas ───────────────────────────────────────────────────────────────────

class GetAllNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke() = repository.getAllNotes()
}

class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Long = repository.saveNote(note)
}

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) = repository.updateNote(note)
}

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteNote(id)
}

// ─── Estadísticas ────────────────────────────────────────────────────────────

class GetDailyStatsUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(days: Int = 5) = repository.getDailyStats(days)
}

class GetHourlyActivityUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke() = repository.getHourlyActivity()
}
