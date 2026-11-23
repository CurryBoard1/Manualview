package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CarritoViewModel : ViewModel() {
    private val _state = MutableStateFlow(CarritoState())
    val state: StateFlow<CarritoState> = _state.asStateFlow()

    fun onCantidadChange(indice: Int, nuevaCantidad: Int) {
        _state.update { current ->
            val nuevasCantidades = current.cantidades.toMutableList()
            nuevasCantidades[indice] = nuevaCantidad.coerceIn(0, 10)
            current.copy(cantidades = nuevasCantidades)
        }
    }

    fun onCuponChange(cupon: String) {
        _state.update { current ->
            current.copy(cupon = cupon.uppercase())
        }
    }

    fun onConfirmar() {
        _state.update { current ->
            current.copy(confirmado = true)
        }
    }

    fun onVaciar() {
        _state.update { current ->
            val vacias = List(current.cantidades.size) { 0 }
            current.copy(cantidades = vacias, cupon = "", confirmado = false)
        }
    }

    fun onConfirmadoCerrado() {
        _state.update { current ->
            current.copy(confirmado = false)
        }
    }

    fun onNuevaCompra() {
        _state.value = CarritoState()
    }
}
