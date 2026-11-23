package com.example.manualview

import kotlin.math.ceil

data class CuentaState(
    val montoTexto: String = "",
    val propinaPct: Int = 10,
    val personas: Int = 2,
    val redondear: Boolean = false
) {
    val monto: Double get() = montoTexto.toDoubleOrNull() ?: 0.0
    val montoInvalido: Boolean get() = montoTexto.isNotEmpty() && montoTexto.toDoubleOrNull() == null
    val propina: Double get() = monto * propinaPct / 100.0
    val total: Double get() = monto + propina
    val sinRedondear: Double get() = total / personas
    val porPersona: Double get() = if (redondear) ceil(sinRedondear) else sinRedondear
    val sobrante: Double? get() = if (redondear) porPersona * personas - total else null
}
