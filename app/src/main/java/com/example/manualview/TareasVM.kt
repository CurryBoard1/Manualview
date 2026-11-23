package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TareasViewModel : ViewModel() {
    private val _state = MutableStateFlow(TareasState())
    val state: StateFlow<TareasState> = _state.asStateFlow()

    fun onTextoNuevaChange(texto: String) {
        _state.update { it.copy(textoNueva = texto) }
    }

    fun onPrioridadNuevaChange(prioridad: Prioridad) {
        _state.update { it.copy(prioridadNueva = prioridad) }
    }

    fun onFiltroChange(filtro: Filtro) {
        _state.update { it.copy(filtro = filtro) }
    }

    fun onAgregar() {
        val current = _state.value
        if (current.textoNueva.isBlank()) return

        val nuevaTarea = Tarea(current.siguienteId, current.textoNueva.trim(), current.prioridadNueva)
        val nuevasTareas = current.tareas + listOf(nuevaTarea)
        _state.update {
            it.copy(
                tareas = nuevasTareas,
                siguienteId = current.siguienteId + 1,
                textoNueva = ""
            )
        }
    }

    fun onCheckedChange(tareaId: Int, nueva: Boolean) {
        _state.update { current ->
            val indice = current.tareas.indexOfFirst { it.id == tareaId }
            if (indice == -1) return@update current
            val nuevasTareas = current.tareas.toMutableList()
            nuevasTareas[indice] = nuevasTareas[indice].copy(hecha = nueva)
            current.copy(tareas = nuevasTareas)
        }
    }

    fun onDelete(tareaId: Int) {
        _state.update { current ->
            current.copy(tareas = current.tareas.filter { it.id != tareaId })
        }
    }

    fun onBorrarCompletadas() {
        _state.update { current ->
            current.copy(
                tareas = current.tareas.filter { !it.hecha },
                mostrarDialogo = false
            )
        }
    }

    fun onMostrarDialogo(mostrar: Boolean) {
        _state.update { it.copy(mostrarDialogo = mostrar) }
    }
}
