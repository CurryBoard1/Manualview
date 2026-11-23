package com.example.manualview

import java.util.Locale
import kotlin.math.floor

enum class Categoria(val nombre: String) {
    COMIDA("Comida"),
    TRANSPORTE("Transporte"),
    RENTA("Renta"),
    OCIO("Ocio"),
    OTROS("Otros")
}

data class Gasto(
    val id: Int,
    val concepto: String,
    val monto: Double,
    val categoria: Categoria
)

data class PresupuestoState(
    val ingresoTexto: String = "",
    val gastos: List<Gasto> = emptyList(),
    val siguienteId: Int = 1,
    val formConcepto: String = "",
    val formMontoTexto: String = "",
    val formCategoria: Categoria = Categoria.COMIDA,
    val formMenuAbierto: Boolean = false
) {
    val ingreso: Double get() = ingresoTexto.toDoubleOrNull() ?: 0.0
    val total: Double get() = gastos.sumOf { it.monto }
    val saldo: Double get() = ingreso - total
    val porcentaje: Float get() = if (ingreso > 0) (total / ingreso).toFloat() else 0f

    val topCategoria: Pair<Categoria, Double>? get() =
        gastos
            .groupBy { it.categoria }
            .mapValues { (_, lista) -> lista.sumOf { it.monto } }
            .maxByOrNull { it.value }
            ?.let { it.key to it.value }

    val fondoCard: Int get() = when {
        porcentaje > 1f -> 0xFFFBE4E1.toInt()
        porcentaje >= 0.8f -> 0xFFFFF5DC.toInt()
        else -> 0xFFF3EDF7.toInt()
    }

    val colorBarra: Int get() = when {
        porcentaje > 1f -> 0xFFB3261E.toInt()
        porcentaje >= 0.8f -> 0xFFF9A825.toInt()
        else -> 0xFF6200EE.toInt()
    }

    val formMonto: Double? get() = formMontoTexto.toDoubleOrNull()
    val formValido: Boolean get() {
        val monto = formMontoTexto.toDoubleOrNull()
        return formConcepto.isNotBlank() && monto != null && monto > 0
    }
}
