package com.example.manualview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    var state by mutableStateOf(QuizState())
        private set

    fun onNombreChange(nuevoNombre: String) {
        state = state.copy(nombre = nuevoNombre)
    }

    fun seleccionarOpcion(index: Int) {
        if (!state.respondida) {
            state = state.copy(seleccionada = index)
        }
    }

    fun confirmar() {
        if (state.seleccionada != null && !state.respondida) {
            val acerto = state.seleccionada == state.preguntaActual.correcta
            state = state.copy(
                respondida = true,
                puntaje = if (acerto) state.puntaje + 1 else state.puntaje
            )
        }
    }

    fun siguiente() {
        if (state.indicePregunta < preguntasList.size - 1) {
            state = state.copy(
                indicePregunta = state.indicePregunta + 1,
                seleccionada = null,
                respondida = false
            )
        } else {
            state = state.copy(pantalla = Pantalla.RESULTADO)
        }
    }

    fun reintentar() {
        state = state.copy(
            indicePregunta = 0,
            seleccionada = null,
            respondida = false,
            puntaje = 0,
            pantalla = Pantalla.QUIZ
        )
    }

    fun cambiarJugador() {
        state = state.copy(
            indicePregunta = 0,
            seleccionada = null,
            respondida = false,
            puntaje = 0,
            nombre = "",
            pantalla = Pantalla.INICIO
        )
    }

    fun iniciarQuiz() {
        state = state.copy(
            indicePregunta = 0,
            seleccionada = null,
            respondida = false,
            puntaje = 0,
            pantalla = Pantalla.QUIZ
        )
    }
}
