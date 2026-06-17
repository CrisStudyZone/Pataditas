package com.serdigital.pataditas.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serdigital.pataditas.domain.model.ActiveSession
import com.serdigital.pataditas.domain.model.KickSession
import com.serdigital.pataditas.domain.usecase.AddKickToSessionUseCase
import com.serdigital.pataditas.domain.usecase.EndSessionUseCase
import com.serdigital.pataditas.domain.usecase.GetSessionsByDayUseCase
import com.serdigital.pataditas.domain.usecase.StartSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

// ─── Estados UI ──────────────────────────────────────────────────────────────

sealed class CounterState {
    data object Waiting : CounterState()
    data class Counting(val activeSession: ActiveSession) : CounterState()
    data class Finished(val session: KickSession) : CounterState()
}

data class HomeUiState(
    val counterState: CounterState = CounterState.Waiting,
    val todaySessions: List<KickSession> = emptyList(),
    val totalKicksToday: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

// ─── ViewModel ───────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startSessionUseCase: StartSessionUseCase,
    private val addKickToSessionUseCase: AddKickToSessionUseCase,
    private val endSessionUseCase: EndSessionUseCase,
    private val getSessionsByDayUseCase: GetSessionsByDayUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var activeSessionId: Long? = null
    private var sessionStartTimeMs: Long = 0L
    private val kickTimestamps = mutableListOf<Long>()

    init {
        loadTodaySessions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTodaySessions() {
        viewModelScope.launch {
            getSessionsByDayUseCase(LocalDate.now())
                .collect { sessions ->
                    val completed = sessions.filter { !it.isActive }
                    _uiState.update { state ->
                        state.copy(
                            todaySessions = completed,
                            totalKicksToday = completed.sumOf { it.kickCount }
                        )
                    }
                }
        }
    }

    /**
     * Maneja el tap principal:
     * - Si en espera → inicia sesión
     * - Si contando → registra patada
     */
    fun onMainButtonTap() {
        when (_uiState.value.counterState) {
            is CounterState.Waiting,
            is CounterState.Finished -> startNewSession()
            is CounterState.Counting -> registerKick()
        }
    }

    /**
     * Manejo de long press → finaliza sesión activa.
     */
    fun onMainButtonLongPress() {
        if (_uiState.value.counterState is CounterState.Counting) {
            finishSession()
        }
    }

    private fun startNewSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val sessionId = startSessionUseCase()
                activeSessionId = sessionId
                sessionStartTimeMs = System.currentTimeMillis()
                kickTimestamps.clear()

                val activeSession = ActiveSession(
                    sessionId = sessionId,
                    startTime = sessionStartTimeMs,
                    kickCount = 0,
                    kickTimestamps = emptyList(),
                    elapsedSeconds = 0
                )

                _uiState.update {
                    it.copy(
                        counterState = CounterState.Counting(activeSession),
                        isLoading = false
                    )
                }

                startTimer()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun registerKick() {
        val sessionId = activeSessionId ?: return
        val timestamp = System.currentTimeMillis()
        kickTimestamps.add(timestamp)

        viewModelScope.launch {
            addKickToSessionUseCase(sessionId, timestamp)
        }

        val currentState = _uiState.value.counterState
        if (currentState is CounterState.Counting) {
            _uiState.update {
                it.copy(
                    counterState = CounterState.Counting(
                        currentState.activeSession.copy(
                            kickCount = kickTimestamps.size,
                            kickTimestamps = kickTimestamps.toList()
                        )
                    )
                )
            }
        }
    }

    private fun finishSession() {
        val sessionId = activeSessionId ?: return
        timerJob?.cancel()

        viewModelScope.launch {
            endSessionUseCase(sessionId)
            val currentState = _uiState.value.counterState
            if (currentState is CounterState.Counting) {
                // Crear sesión de dominio simplificada para mostrar en UI
                val finished = KickSession(
                    id = sessionId,
                    startTime = LocalDateTime.now().minusSeconds(
                        currentState.activeSession.elapsedSeconds
                    ),
                    endTime = LocalDateTime.now(),
                    kickCount = kickTimestamps.size,
                    kickTimestamps = kickTimestamps.toList(),
                    date = LocalDateTime.now(),
                    durationSeconds = currentState.activeSession.elapsedSeconds
                )
                _uiState.update {
                    it.copy(counterState = CounterState.Finished(finished))
                }
                activeSessionId = null
                kickTimestamps.clear()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val elapsed = (System.currentTimeMillis() - sessionStartTimeMs) / 1000
                val currentState = _uiState.value.counterState
                if (currentState is CounterState.Counting) {
                    _uiState.update {
                        it.copy(
                            counterState = CounterState.Counting(
                                currentState.activeSession.copy(elapsedSeconds = elapsed)
                            )
                        )
                    }
                }
            }
        }
    }

    fun resetToWaiting() {
        timerJob?.cancel()
        _uiState.update { it.copy(counterState = CounterState.Waiting) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
