package com.example.manualview

data class Producto(val nombre: String, val precio: Double)

data class CarritoState(
    val catalogo: List<Producto> = listOf(
        Producto("SSD 6.7 KB  ", 24.0),
        Producto("RAM 2 TB", 27.5),
        Producto("ACEITE EN POLVO 1 KG", 52.0),
        Producto("500 ROBUX", 38.0)
    ),
    val cantidades: List<Int> = listOf(0, 0, 0, 0),
    val cupon: String = "",
    val confirmado: Boolean = false
) {
    val articulos: Int get() = cantidades.sum()
    val subtotal: Double get() = catalogo.indices.sumOf { catalogo[it].precio * cantidades[it] }
    val descuento: Double get() = if (subtotal >= 200) subtotal * 0.10 else 0.0
    val subtotalConDescuento: Double get() = subtotal - descuento
    val descCupon: Double get() = if (cupon == "DONPEPE" && articulos > 0) 20.0 else 0.0
    val envio: Double get() = when {
        articulos == 0 -> 0.0
        subtotalConDescuento >= 150 -> 0.0
        else -> 25.0
    }
    val total: Double get() = (subtotalConDescuento - descCupon).coerceAtLeast(0.0) + envio
}
