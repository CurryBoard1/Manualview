package com.example.manualview

data class TareasState(
    val tareas: List<Tarea> = emptyList(),
    val siguienteId: Int = 1,
    val textoNueva: String = "",
    val prioridadNueva: Prioridad = Prioridad.ALTA,
    val filtro: Filtro = Filtro.TODAS,
    val mostrarDialogo: Boolean = false
) {
    val pendientes: Int get() = tareas.count { !it.hecha }
    val hechas: Int get() = tareas.size - pendientes
    val tareasVisibles: List<Tarea> get() = when (filtro) {
        Filtro.TODAS -> tareas
        Filtro.PENDIENTES -> tareas.filter { !it.hecha }
        Filtro.HECHAS -> tareas.filter { it.hecha }
    }.sortedBy { it.prioridad }
}
