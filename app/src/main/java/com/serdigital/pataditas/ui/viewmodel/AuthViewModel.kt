package com.serdigital.pataditas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serdigital.pataditas.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── Estado de autenticación ─────────────────────────────────────────────────

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class AuthUiState(
    val authState: AuthState = AuthState.Unauthenticated,
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) =
        _uiState.update { it.copy(email = email) }

    fun onPasswordChange(password: String) =
        _uiState.update { it.copy(password = password) }

    fun togglePasswordVisibility() =
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun signIn() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.Loading) }
            authRepository.signInWithEmail(state.email, state.password)
                .onSuccess {
                    _uiState.update {
                        it.copy(authState = AuthState.Authenticated(authRepository.currentUserId ?: ""))
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(authState = AuthState.Error(e.message ?: "Error al iniciar sesión"))
                    }
                }
        }
    }

    fun signUp() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.Loading) }
            authRepository.signUpWithEmail(state.email, state.password)
                .onSuccess {
                    _uiState.update {
                        it.copy(authState = AuthState.Authenticated(authRepository.currentUserId ?: ""))
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(authState = AuthState.Error(e.message ?: "Error al registrarse"))
                    }
                }
        }
    }

    fun sendPasswordReset() {
        val email = _uiState.value.email
        if (email.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.Loading) }
            authRepository.sendPasswordReset(email)
                .onSuccess {
                    _uiState.update { it.copy(authState = AuthState.Unauthenticated) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(authState = AuthState.Error(e.message ?: "Error al enviar email"))
                    }
                }
        }
    }

    fun clearError() =
        _uiState.update { it.copy(authState = AuthState.Unauthenticated) }
}
