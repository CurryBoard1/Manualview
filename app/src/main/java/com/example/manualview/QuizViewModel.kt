package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QuizViewModel : ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    fun onNombreChange(nuevoNombre: String) {
        _state.update { it.copy(nombre = nuevoNombre) }
    }

    fun seleccionarOpcion(index: Int) {
        if (!_state.value.respondida) {
            _state.update { it.copy(seleccionada = index) }
        }
    }

    fun confirmar() {
        val current = _state.value
        if (current.seleccionada != null && !current.respondida) {
            val acerto = current.seleccionada == current.preguntaActual.correcta
            _state.update {
                it.copy(
                    respondida = true,
                    puntaje = if (acerto) it.puntaje + 1 else it.puntaje
                )
            }
        }
    }

    fun siguiente() {
        val current = _state.value
        if (current.indicePregunta < preguntasList.size - 1) {
            _state.update {
                it.copy(
                    indicePregunta = it.indicePregunta + 1,
                    seleccionada = null,
                    respondida = false
                )
            }
        } else {
            _state.update { it.copy(pantalla = Pantalla.RESULTADO) }
        }
    }

    fun reintentar() {
        _state.update {
            it.copy(
                indicePregunta = 0,
                seleccionada = null,
                respondida = false,
                puntaje = 0,
                pantalla = Pantalla.QUIZ
            )
        }
    }

    fun cambiarJugador() {
        _state.update {
            it.copy(
                indicePregunta = 0,
                seleccionada = null,
                respondida = false,
                puntaje = 0,
                nombre = "",
                pantalla = Pantalla.INICIO
            )
        }
    }

    fun iniciarQuiz() {
        _state.update {
            it.copy(
                indicePregunta = 0,
                seleccionada = null,
                respondida = false,
                puntaje = 0,
                pantalla = Pantalla.QUIZ
            )
        }
    }
}
