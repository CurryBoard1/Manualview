package com.example.manualview

private val cupos = mapOf("Pintura" to 8, "Fotografía" to 6, "Guitarra" to 4)
val talleres = cupos.keys.toList()

data class InscripcionState(
    val nombre: String = "",
    val correo: String = "",
    val edad: String = "",
    val taller: String = talleres.firstOrNull() ?: "",
    val aceptaAviso: Boolean = false,
    val intentoEnviar: Boolean = false,
    val inscrito: Boolean = false
) {
    val cuposTaller: Int get() = cupos[taller] ?: 0
}
