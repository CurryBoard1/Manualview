package com.example.manualview

enum class LoginEstado {
    INACTIVO,
    ERROR,
    BLOQUEADO,
    EXITO
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val recordarme: Boolean = false,
    val intentosFallidos: Int = 0,
    val estado: LoginEstado = LoginEstado.INACTIVO
) {
    val mensajeError: String? get() = when (estado) {
        LoginEstado.ERROR -> "Correo o contraseña incorrectos\n(intento $intentosFallidos de $MAX_INTENTOS)"
        LoginEstado.BLOQUEADO -> "Cuenta bloqueada\nDemasiados intentos fallidos, intenta de nuevo más tarde."
        else -> null
    }

    val bloqueado: Boolean get() = estado == LoginEstado.BLOQUEADO
}

private const val CORREO_OK = "paciente@vida.mx"
private const val PASS_OK = "Vida2025"
private const val MAX_INTENTOS = 3
