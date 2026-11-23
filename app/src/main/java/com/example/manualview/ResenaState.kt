package com.example.manualview

private val ETIQUETAS = listOf("Familiar", "Económico", "Rápido", "Romántico", "Vegetariano")
private val DESCRIPCION = listOf("Sin calificar", "Malo", "Regular", "Bueno", "Muy bueno", "Excelente")
private const val MAX_COMENTARIO = 200

data class ResenaState(
    val comida: Int = 0,
    val servicio: Int = 0,
    val ambiente: Int = 0,
    val comentario: String = "",
    val etiquetas: Set<String> = emptySet(),
    val publicada: Boolean = false
) {
    val completo: Boolean get() = comida > 0 && servicio > 0 && ambiente > 0
    val general: Double get() = (comida + servicio + ambiente) / 3.0
    val puedePublicar: Boolean get() = completo && comentario.trim().length >= 20
}

val todasLasEtiquetas: List<String> get() = ETIQUETAS
val descripciones: List<String> get() = DESCRIPCION
val maxComentario: Int get() = MAX_COMENTARIO
