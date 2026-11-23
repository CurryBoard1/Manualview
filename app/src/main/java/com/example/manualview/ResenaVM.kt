package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ResenaViewModel : ViewModel() {
    private val _state = MutableStateFlow(ResenaState())
    val state: StateFlow<ResenaState> = _state.asStateFlow()

    fun onComidaChange(rating: Int) {
        _state.update { it.copy(comida = rating) }
    }

    fun onServicioChange(rating: Int) {
        _state.update { it.copy(servicio = rating) }
    }

    fun onAmbienteChange(rating: Int) {
        _state.update { it.copy(ambiente = rating) }
    }

    fun onComentarioChange(texto: String) {
        _state.update { it.copy(comentario = texto) }
    }

    fun onToggleEtiqueta(etiqueta: String) {
        _state.update { current ->
            val nuevasEtiquetas = if (etiqueta in current.etiquetas) {
                current.etiquetas - etiqueta
            } else {
                current.etiquetas + etiqueta
            }
            current.copy(etiquetas = nuevasEtiquetas)
        }
    }

    fun onPublicar() {
        _state.update { it.copy(publicada = true) }
    }
}
