package com.example.manualview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manualview.ui.theme.ManualviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManualviewTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        TareasScreen(viewModel = viewModel())
                    }
                }
            }
        }
    }
}

enum class Prioridad(val etiqueta: String, val fondo: Color, val texto: Color) {
    ALTA("Alta", Color(0xFFF9DEDC), Color(0xFF8C1D18)),
    MEDIA("Media", Color(0xFFFFEFB8), Color(0xFF6B4E00)),
    BAJA("Baja", Color(0xFFD8EDD5), Color(0xFF2E6B2E))
}

enum class Filtro(val etiqueta: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    HECHAS("Hechas")
}

data class Tarea(
    val id: Int,
    val titulo: String,
    val prioridad: Prioridad,
    val hecha: Boolean = false
)

@Composable
fun TareasScreen(viewModel: TareasViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text("Mis pendientes", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.textoNueva,
                onValueChange = { viewModel.onTextoNuevaChange(it) },
                placeholder = { Text("Nueva tarea") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = { viewModel.onAgregar() },
                enabled = state.textoNueva.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) { Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold) }
        }

        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Prioridad", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Prioridad.entries.forEach { p ->
                ChipSeleccion(p.etiqueta, state.prioridadNueva == p) { viewModel.onPrioridadNuevaChange(p) }
            }
        }

        Row(
            Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Filtro.entries.forEach { f ->
                ChipSeleccion(f.etiqueta, state.filtro == f) { viewModel.onFiltroChange(f) }
            }
        }

        HorizontalDivider(Modifier.padding(top = 8.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(state.tareasVisibles, key = { it.id }) { tarea ->
                TareaItem(
                    tarea = tarea,
                    onCheckedChange = { nuevo -> viewModel.onCheckedChange(tarea.id, nuevo) },
                    onDelete = { viewModel.onDelete(tarea.id) }
                )
                HorizontalDivider()
            }
            item {
                Text(
                    "${state.pendientes} pendientes de ${state.tareas.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        OutlinedButton(
            onClick = { viewModel.onMostrarDialogo(true) },
            enabled = state.hechas > 0,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Borrar completadas", fontWeight = FontWeight.Bold) }
    }

    if (state.mostrarDialogo) {
        ConfirmarBorradoDialog(
            cantidad = state.hechas,
            onConfirm = {
                viewModel.onBorrarCompletadas()
            },
            onDismiss = { viewModel.onMostrarDialogo(false) }
        )
    }
}

@Composable
private fun ChipSeleccion(texto: String, activo: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = activo,
        onClick = onClick,
        label = { Text(texto, fontSize = 16.sp) },
        leadingIcon = if (activo) {
            { Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        } else null
    )
}

@Composable
fun TareaItem(tarea: Tarea, onCheckedChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = tarea.hecha, onCheckedChange = onCheckedChange)
        Column(Modifier.weight(1f)) {
            Text(
                tarea.titulo,
                fontSize = 19.sp,
                textDecoration = if (tarea.hecha) TextDecoration.LineThrough else TextDecoration.None,
                color = if (tarea.hecha) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(50), color = tarea.prioridad.fondo) {
                Text(
                    tarea.prioridad.etiqueta,
                    color = tarea.prioridad.texto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                )
            }
        }
        IconButton(onClick = onDelete) {
            Text(
                "✕",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ConfirmarBorradoDialog(cantidad: Int, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrar completadas") },
        text = { Text("Se van a eliminar $cantidad tareas hechas. ¿Continuar?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Borrar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Preview(showBackground = true)
@Composable
fun TareaItemPreview() {
    ManualviewTheme {
        TareaItem(Tarea(1, "Leer capítulo 4", Prioridad.BAJA, true), {}, {})
    }
}
