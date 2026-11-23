package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InscripcionViewModel : ViewModel() {
    private val _state = MutableStateFlow(InscripcionState())
    val state: StateFlow<InscripcionState> = _state.asStateFlow()

    fun onNombreChange(nombre: String) {
        _state.update { it.copy(nombre = nombre) }
    }

    fun onCorreoChange(correo: String) {
        _state.update { it.copy(correo = correo) }
    }

    fun onEdadChange(edad: String) {
        _state.update { it.copy(edad = edad) }
    }

    fun onTallerChange(taller: String) {
        _state.update { it.copy(taller = taller) }
    }

    fun onAceptaAvisoChange(acepta: Boolean) {
        _state.update { it.copy(aceptaAviso = acepta) }
    }

    fun onEnviar() {
        _state.update { current ->
            val errores = validarErrores(current)
            if (errores.isEmpty()) {
                current.copy(intentoEnviar = true, inscrito = true)
            } else {
                current.copy(intentoEnviar = true)
            }
        }
    }

    fun onNuevaInscripcion() {
        _state.value = InscripcionState()
    }

    val errNombre: String?
        get() = if (state.value.intentoEnviar) validarNombre(state.value.nombre) else null

    val errCorreo: String?
        get() = if (state.value.intentoEnviar) validarCorreo(state.value.correo) else null

    val errEdad: String?
        get() = if (state.value.intentoEnviar) validarEdad(state.value.edad) else null

    val errAviso: String?
        get() = if (state.value.intentoEnviar) validarAviso(state.value.aceptaAviso) else null

    private fun validarErrores(s: InscripcionState): List<String> {
        val errores = mutableListOf<String>()
        validarNombre(s.nombre)?.let { errores.add(it) }
        validarCorreo(s.correo)?.let { errores.add(it) }
        validarEdad(s.edad)?.let { errores.add(it) }
        validarAviso(s.aceptaAviso)?.let { errores.add(it) }
        return errores
    }

    private fun validarNombre(nombre: String): String? =
        if (nombre.trim().length < 3) "Escribe tu nombre completo (mínimo 3 caracteres)" else null

    private fun validarCorreo(correo: String): String? {
        val arroba = correo.indexOf('@')
        val punto = correo.indexOf('.', arroba + 1)
        val bien = arroba > 0 && punto > arroba + 1 && punto < correo.length - 1
        return if (bien) null else "Ingresa un correo válido"
    }

    private fun validarEdad(edad: String): String? {
        val n = edad.toIntOrNull()
        return if (n == null || n < 12 || n > 99) "La edad debe estar entre 12 y 99" else null
    }

    private fun validarAviso(acepta: Boolean): String? =
        if (acepta) null else "Debes aceptar el aviso de privacidad"
}
