package com.example.manualview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manualview.ui.theme.ManualviewTheme

private val rojoBorde = Color(0xFFB3261E)
private val rojoFondo = Color(0xFFF9DEDC)
private val verdeBorde = Color(0xFF2E7D32)
private val verdeFondo = Color(0xFFDDEFD9)

@Composable
fun QuizScreen(viewModel: QuizViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state.pantalla) {
        Pantalla.INICIO -> InicioScreen(
            nombre = state.nombre,
            onNombreChange = viewModel::onNombreChange,
            onComenzar = viewModel::iniciarQuiz
        )

        Pantalla.QUIZ -> PreguntaScreen(
            state = state,
            onSeleccionar = viewModel::seleccionarOpcion,
            onConfirmar = viewModel::confirmar,
            onSiguiente = viewModel::siguiente
        )

        Pantalla.RESULTADO -> ResultadoScreen(
            nombre = state.nombre,
            puntaje = state.puntaje,
            total = state.totalPreguntas,
            onReintentar = viewModel::reintentar,
            onCambiarJugador = viewModel::cambiarJugador
        )
    }
}

@Composable
fun InicioScreen(
    nombre: String,
    onNombreChange: (String) -> Unit,
    onComenzar: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quiz de programación", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "5 preguntas de fundamentos. ¿Cómo te llamas?",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = { Text("Tu nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onComenzar,
            enabled = nombre.trim().length >= 2,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Comenzar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun PreguntaScreen(
    state: QuizState,
    onSeleccionar: (Int) -> Unit,
    onConfirmar: () -> Unit,
    onSiguiente: () -> Unit
) {
    val pregunta = state.preguntaActual
    val numero = state.indicePregunta + 1
    val total = state.totalPreguntas
    val acerto = state.seleccionada == pregunta.correcta

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Quiz de programación", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Pregunta $numero de $total",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Puntaje: ${state.puntaje}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { numero / total.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )

        Spacer(Modifier.height(24.dp))
        Text(
            pregunta.texto,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(Modifier.height(24.dp))

        pregunta.opciones.forEachIndexed { i, opcion ->
            val esCorrecta = state.respondida && i == pregunta.correcta
            val esMala = state.respondida && i == state.seleccionada && i != pregunta.correcta

            val fondo = when {
                esCorrecta -> verdeFondo
                esMala -> rojoFondo
                else -> Color.Transparent
            }
            val borde = when {
                esCorrecta -> BorderStroke(2.dp, verdeBorde)
                esMala -> BorderStroke(2.dp, rojoBorde)
                else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            }

            Surface(
                color = fondo,
                shape = RoundedCornerShape(16.dp),
                border = borde,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable(enabled = !state.respondida) { onSeleccionar(i) }
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.seleccionada == i,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = if (esMala) rojoBorde else MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        opcion,
                        fontSize = 18.sp,
                        fontWeight = if (esCorrecta || esMala || (!state.respondida && state.seleccionada == i))
                            FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                    if (esCorrecta) Text("✓", fontSize = 20.sp, color = verdeBorde, fontWeight = FontWeight.Bold)
                    if (esMala) Text("✕", fontSize = 20.sp, color = rojoBorde, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (state.respondida) {
            Spacer(Modifier.height(8.dp))
            Surface(
                color = if (acerto) verdeFondo else rojoFondo,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (acerto) "¡Correcto!"
                    else "Incorrecto. La respuesta correcta es '${pregunta.opciones[pregunta.correcta]}'.",
                    color = if (acerto) Color(0xFF1B5E20) else Color(0xFF8C1D18),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilledTonalButton(
                onClick = onConfirmar,
                enabled = state.seleccionada != null && !state.respondida,
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
            ) {
                Text("Confirmar", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onSiguiente,
                enabled = state.respondida,
                modifier = Modifier
                    .weight(3f)
                    .height(52.dp)
            ) {
                Text(
                    if (numero == total) "Ver resultados" else "Siguiente",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ResultadoScreen(
    nombre: String,
    puntaje: Int,
    total: Int,
    onReintentar: () -> Unit,
    onCambiarJugador: () -> Unit
) {
    val mensaje = when {
        puntaje <= 2 -> "Sigue practicando"
        puntaje <= 4 -> "Buen trabajo"
        else -> "Perfecto"
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "¡Muy bien, $nombre!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "$puntaje de $total",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            mensaje,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onReintentar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Reintentar", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onCambiarJugador,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Cambiar jugador", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizPreview() {
    ManualviewTheme {
        QuizScreen()
    }
}
