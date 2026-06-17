package com.serdigital.pataditas.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serdigital.pataditas.domain.model.DailyStats
import com.serdigital.pataditas.domain.model.HourlyActivity
import com.serdigital.pataditas.domain.model.KickSession
import com.serdigital.pataditas.domain.model.Note
import com.serdigital.pataditas.domain.usecase.DeleteNoteUseCase
import com.serdigital.pataditas.domain.usecase.DeleteSessionUseCase
import com.serdigital.pataditas.domain.usecase.GetAllNotesUseCase
import com.serdigital.pataditas.domain.usecase.GetAllSessionsUseCase
import com.serdigital.pataditas.domain.usecase.GetDailyStatsUseCase
import com.serdigital.pataditas.domain.usecase.GetHourlyActivityUseCase
import com.serdigital.pataditas.domain.usecase.SaveNoteUseCase
import com.serdigital.pataditas.domain.usecase.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

// ─── History ViewModel ───────────────────────────────────────────────────────

data class HistoryUiState(
    val sessions: List<KickSession> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllSessionsUseCase: GetAllSessionsUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            getAllSessionsUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { sessions ->
                    _uiState.update {
                        it.copy(sessions = sessions.filter { s -> !s.isActive }, isLoading = false)
                    }
                }
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            deleteSessionUseCase(id)
        }
    }
}

// ─── Stats ViewModel ─────────────────────────────────────────────────────────

data class StatsUiState(
    val dailyStats: List<DailyStats> = emptyList(),
    val hourlyActivity: List<HourlyActivity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getDailyStatsUseCase: GetDailyStatsUseCase,
    private val getHourlyActivityUseCase: GetHourlyActivityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            combine(
                getDailyStatsUseCase(5),
                getHourlyActivityUseCase()
            ) { daily, hourly ->
                StatsUiState(dailyStats = daily, hourlyActivity = hourly, isLoading = false)
            }
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { state -> _uiState.update { state } }
        }
    }
}

// ─── Notes ViewModel ─────────────────────────────────────────────────────────

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            getAllNotesUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { notes -> _uiState.update { it.copy(notes = notes, isLoading = false) } }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveNote(title: String, content: String, existingId: Long = 0) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            if (existingId > 0) {
                updateNoteUseCase(
                    Note(
                        id = existingId,
                        title = title.ifBlank { "Sin título" },
                        content = content,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            } else {
                saveNoteUseCase(
                    Note(
                        title = title.ifBlank { "Sin título" },
                        content = content,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch { deleteNoteUseCase(id) }
    }
}
