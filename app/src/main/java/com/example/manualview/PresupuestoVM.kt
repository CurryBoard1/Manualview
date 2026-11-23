package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PresupuestoViewModel : ViewModel() {
    private val _state = MutableStateFlow(PresupuestoState())
    val state: StateFlow<PresupuestoState> = _state.asStateFlow()

    fun onIngresoChange(texto: String) {
        _state.update { it.copy(ingresoTexto = texto) }
    }

    fun onFormConceptoChange(concepto: String) {
        _state.update { it.copy(formConcepto = concepto) }
    }

    fun onFormMontoChange(monto: String) {
        _state.update { it.copy(formMontoTexto = monto) }
    }

    fun onFormCategoriaChange(categoria: Categoria) {
        _state.update { it.copy(formCategoria = categoria) }
    }

    fun onFormMenuChange(abierto: Boolean) {
        _state.update { it.copy(formMenuAbierto = abierto) }
    }

    fun onAgregarGasto() {
        val current = _state.value
        if (!current.formValido) return

        val nuevoGasto = Gasto(
            id = current.siguienteId,
            concepto = current.formConcepto.trim(),
            monto = current.formMonto!!,
            categoria = current.formCategoria
        )
        val nuevosGastos = current.gastos + listOf(nuevoGasto)
        _state.update {
            it.copy(
                gastos = nuevosGastos,
                siguienteId = current.siguienteId + 1,
                formConcepto = "",
                formMontoTexto = "",
                formCategoria = Categoria.COMIDA,
                formMenuAbierto = false
            )
        }
    }

    fun onEliminarGasto(gasto: Gasto) {
        _state.update { current ->
            current.copy(gastos = current.gastos.filter { it.id != gasto.id })
        }
    }
}
