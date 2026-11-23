package com.example.manualview

data class Pregunta(val texto: String, val opciones: List<String>, val correcta: Int)

enum class Pantalla { INICIO, QUIZ, RESULTADO }

val preguntasList = listOf(
    Pregunta(
        "¿Qué palabra clave declara una variable que no puede cambiar su valor en Kotlin?",
        listOf("var", "val", "let", "static"), 1
    ),
    Pregunta(
        "¿Cuál es el resultado de 7 % 3?",
        listOf("2", "1", "0", "3"), 1
    ),
    Pregunta(
        "¿Qué tipo de dato conviene usar para guardar el valor 3.14?",
        listOf("Int", "Double", "String", "Boolean"), 1
    ),
    Pregunta(
        "¿Qué estructura repite un bloque mientras se cumpla una condición?",
        listOf("if", "when", "while", "class"), 2
    ),
    Pregunta(
        "¿Qué índice tiene el primer elemento de una lista?",
        listOf("0", "1", "-1", "Depende de la lista"), 0
    )
)

data class QuizState(
    val pantalla: Pantalla = Pantalla.INICIO,
    val nombre: String = "",
    val indicePregunta: Int = 0,
    val seleccionada: Int? = null,
    val respondida: Boolean = false,
    val puntaje: Int = 0
) {
    val preguntaActual: Pregunta get() = preguntasList[indicePregunta]
    val totalPreguntas: Int get() = preguntasList.size
}
