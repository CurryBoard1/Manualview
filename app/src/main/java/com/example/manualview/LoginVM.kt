package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onRecordarmeChange(recordarme: Boolean) {
        _state.update { it.copy(recordarme = recordarme) }
    }

    fun onLogin() {
        val current = _state.value
        if (current.email == CORREO_OK && current.password == PASS_OK) {
            _state.update { it.copy(intentosFallidos = 0, estado = LoginEstado.EXITO) }
        } else {
            val nuevosIntentos = current.intentosFallidos + 1
            val nuevoEstado = if (nuevosIntentos >= MAX_INTENTOS) {
                LoginEstado.BLOQUEADO
            } else {
                LoginEstado.ERROR
            }
            _state.update { it.copy(intentosFallidos = nuevosIntentos, estado = nuevoEstado) }
        }
    }

    fun onLogout() {
        _state.value = LoginState()
    }
}

private const val CORREO_OK = "paciente@vida.mx"
private const val PASS_OK = "Vida2025"
private const val MAX_INTENTOS = 3
