package com.example.manualview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManualviewTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        InscripcionScreen(viewModel = viewModel())
                    }
                }
            }
        }
    }
}

@Composable
fun InscripcionScreen(viewModel: InscripcionViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.inscrito) {
        ConfirmacionScreen(
            nombre = state.nombre,
            correo = state.correo,
            taller = state.taller,
            onNueva = { viewModel.onNuevaInscripcion() }
        )
    } else {
        InscripcionForm(
            nombre = state.nombre,
            onNombreChange = { viewModel.onNombreChange(it) },
            correo = state.correo,
            onCorreoChange = { viewModel.onCorreoChange(it) },
            edad = state.edad,
            onEdadChange = { viewModel.onEdadChange(it) },
            taller = state.taller,
            onTallerChange = { viewModel.onTallerChange(it) },
            aceptaAviso = state.aceptaAviso,
            onAceptaAvisoChange = { viewModel.onAceptaAvisoChange(it) },
            errNombre = viewModel.errNombre,
            errCorreo = viewModel.errCorreo,
            errEdad = viewModel.errEdad,
            errAviso = viewModel.errAviso,
            cuposTaller = state.cuposTaller,
            onEnviar = { viewModel.onEnviar() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscripcionForm(
    nombre: String, onNombreChange: (String) -> Unit,
    correo: String, onCorreoChange: (String) -> Unit,
    edad: String, onEdadChange: (String) -> Unit,
    taller: String, onTallerChange: (String) -> Unit,
    aceptaAviso: Boolean, onAceptaAvisoChange: (Boolean) -> Unit,
    errNombre: String?, errCorreo: String?, errEdad: String?, errAviso: String?,
    cuposTaller: Int,
    onEnviar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Inscripción a talleres", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        Text(
            "Casa del Lago · cupo limitado",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))

        CampoTexto("Nombre completo", nombre, onNombreChange, errNombre)
        CampoTexto(
            "Correo", correo, onCorreoChange, errCorreo,
            KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        CampoTexto(
            "Edad", edad, onEdadChange, errEdad,
            KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        ExposedDropdownMenuBox(
            expanded = menuAbierto,
            onExpandedChange = { menuAbierto = it },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            OutlinedTextField(
                value = taller,
                onValueChange = {},
                readOnly = true,
                label = { Text("Taller") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                talleres.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t) },
                        onClick = {
                            onTallerChange(t)
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = aceptaAviso, onCheckedChange = onAceptaAvisoChange)
            Text("Acepto el aviso de privacidad", fontSize = 17.sp)
        }
        if (errAviso != null) {
            Text(
                errAviso,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onEnviar,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Inscribirme", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

        Spacer(Modifier.height(16.dp))
        Text(
            "Quedan $cuposTaller lugares en este taller",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    )
}

@Composable
fun ConfirmacionScreen(nombre: String, correo: String, taller: String, onNueva: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("¡Inscripción lista!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaResumen("Nombre", nombre)
                FilaResumen("Correo", correo)
                FilaResumen("Taller", taller)
            }
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = onNueva,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Nueva inscripción", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun FilaResumen(titulo: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(titulo, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun InscripcionPreview() {
    ManualviewTheme {
        InscripcionScreen(viewModel = InscripcionViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmacionPreview() {
    ManualviewTheme {
        ConfirmacionScreen(
            nombre = "Ana López",
            correo = "ana.lopez@correo.com",
            taller = "Fotografía",
            onNueva = {}
        )
    }
}
