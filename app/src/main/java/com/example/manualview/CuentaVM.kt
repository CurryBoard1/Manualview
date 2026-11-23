package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CuentaViewModel : ViewModel() {
    private val _state = MutableStateFlow(CuentaState())
    val state: StateFlow<CuentaState> = _state.asStateFlow()

    fun onMontoChange(texto: String) {
        _state.update { it.copy(montoTexto = texto) }
    }

    fun onPropinaChange(pct: Int) {
        _state.update { it.copy(propinaPct = pct) }
    }

    fun onPersonasChange(n: Int) {
        _state.update { it.copy(personas = n.coerceIn(1, 20)) }
    }

    fun onRedondearChange(activado: Boolean) {
        _state.update { it.copy(redondear = activado) }
    }
}
