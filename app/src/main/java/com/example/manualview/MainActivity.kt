package com.example.manualview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manualview.ui.theme.ManualviewTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManualviewTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        ResenaScreen(viewModel = viewModel(), onBack = { finish() })
                    }
                }
            }
        }
    }
}

private val dorado = Color(0xFFF9A825)

@Composable
fun ResenaScreen(viewModel: ResenaViewModel, onBack: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text("Escribe tu reseña", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(12.dp))

        Text("Restaurante El Mezquite", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Califica tu experiencia", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))

        FilaCalificacion("Comida", state.comida) { viewModel.onComidaChange(it) }
        FilaCalificacion("Servicio", state.servicio) { viewModel.onServicioChange(it) }
        FilaCalificacion("Ambiente", state.ambiente) { viewModel.onAmbienteChange(it) }

        if (state.completo) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Calificación general",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            String.format(Locale.US, "%.1f", state.general),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("★", fontSize = 22.sp, color = dorado)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        ComentarioField(state.comentario, { viewModel.onComentarioChange(it) }, maxComentario)

        FlowRow(
            Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            todasLasEtiquetas.forEach { e ->
                val marcada = e in state.etiquetas
                FilterChip(
                    selected = marcada,
                    onClick = { viewModel.onToggleEtiqueta(e) },
                    label = { Text(e, fontSize = 16.sp) },
                    leadingIcon = if (marcada) {
                        { Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    } else null
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.onPublicar() },
            enabled = state.puedePublicar,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Publicar", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

        if (state.publicada) {
            Spacer(Modifier.height(16.dp))
            ResumenResena(state.general, state.comentario, state.etiquetas)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun FilaCalificacion(nombre: String, rating: Int, onChange: (Int) -> Unit) {
    Text(nombre, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RatingBar(rating, onChange)
        Text(
            if (rating > 0) descripciones[rating] else "",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// si tocas la estrella que ya estaba marcada se quita la calificación (rating = 0).
// Lo dejé aquí dentro para que cualquier pantalla que use RatingBar ya lo traiga.
@Composable
fun RatingBar(rating: Int, onRatingChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier) {
        for (i in 1..5) {
            Text(
                "★",
                fontSize = 32.sp,
                color = if (i <= rating) dorado else Color(0xFFCAC4D0),
                modifier = Modifier
                    .clickable { onRatingChange(if (i == rating) 0 else i) }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun ComentarioField(texto: String, onChange: (String) -> Unit, maximo: Int = 200) {
    Column {
        OutlinedTextField(
            value = texto,
            onValueChange = { if (it.length <= maximo) onChange(it) },
            label = { Text("Comentario") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "${texto.length}/$maximo",
            color = if (texto.length >= 180) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun ResumenResena(general: Double, comentario: String, etiquetas: Set<String>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4E7)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("¡Reseña publicada!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Calificación general: ${String.format(Locale.US, "%.1f", general)} ★")
            Text(comentario)
            if (etiquetas.isNotEmpty()) {
                Text(
                    etiquetas.joinToString(" · "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
    ManualviewTheme { RatingBar(rating = 4, onRatingChange = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun ResenaScreenPreview() {
    ManualviewTheme {
        ResenaScreen(viewModel = ResenaViewModel())
    }
}
