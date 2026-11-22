package com.example.manualview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manualview.ui.theme.ManualviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManualviewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                    ) {
                        LoginScreen()
                    }
                }
            }
        }
    }
}

private const val CORREO_OK = "paciente@vida.mx"
private const val PASS_OK = "Vida2025"
private const val MAX_INTENTOS = 3

enum class LoginEstado {
    INACTIVO,
    ERROR,
    BLOQUEADO,
    EXITO
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var recordarme by remember { mutableStateOf(false) }
    var intentosFallidos by remember { mutableIntStateOf(0) }
    var estado by remember { mutableStateOf(LoginEstado.INACTIVO) }

    val mensajeError = when (estado) {
        LoginEstado.ERROR -> "Correo o contraseña incorrectos\n(intento $intentosFallidos de $MAX_INTENTOS)"
        LoginEstado.BLOQUEADO -> "Cuenta bloqueada\nDemasiados intentos fallidos, intenta de nuevo más tarde."
        else -> null
    }

    when (estado) {
        LoginEstado.EXITO -> HomeScreen(
            email = email,
            onLogout = {
                email = ""
                password = ""
                intentosFallidos = 0
                estado = LoginEstado.INACTIVO
            }
        )
        else -> LoginForm(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            recordarme = recordarme,
            onRecordarmeChange = { recordarme = it },
            mensajeError = mensajeError,
            bloqueado = estado == LoginEstado.BLOQUEADO,
            onLogin = {
                if (email == CORREO_OK && password == PASS_OK) {
                    intentosFallidos = 0
                    estado = LoginEstado.EXITO
                } else {
                    intentosFallidos++
                    estado = if (intentosFallidos >= MAX_INTENTOS) {
                        LoginEstado.BLOQUEADO
                    } else {
                        LoginEstado.ERROR
                    }
                }
            }
        )
    }
}

@Composable
fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    recordarme: Boolean,
    onRecordarmeChange: (Boolean) -> Unit,
    mensajeError: String?,
    bloqueado: Boolean,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFEADDFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Clínica Vida",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Consulta tus citas médicas",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = recordarme,
                onCheckedChange = onRecordarmeChange
            )
            Text(
                text = "Recordarme",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (mensajeError != null) {
            val partes = mensajeError.split("\n")
            Surface(
                color = Color(0xFFF9DEDC),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = partes[0],
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8C1D18)
                    )
                    if (partes.size > 1) {
                        Text(
                            text = partes[1],
                            fontSize = 14.sp,
                            color = Color(0xFF8C1D18)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = onLogin,
            enabled = !bloqueado,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Entrar",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { /* Olvidaste tu contraseña */ }) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Contraseña") },
        singleLine = true,
        visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                Text(
                    text = if (mostrarPassword) "👁" else "🙈",
                    fontSize = 18.sp
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    email: String,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = email,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Cerrar sesión",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ManualviewTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ManualviewTheme {
        HomeScreen("paciente@vida.mx", onLogout = {})
    }
}