package com.example.manualview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManualviewTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
                        CarritoScreen(viewModel = viewModel())
                    }
                }
            }
        }
    }
}

private val verde = Color(0xFF2E7D32)

fun dinero(x: Double): String = "$" + String.format(Locale.US, "%,.2f", x)

@Composable
fun CarritoScreen(viewModel: CarritoViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tienda Don Pepe", style = MaterialTheme.typography.headlineMedium)
            Box(
                Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${state.articulos}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            state.catalogo.forEachIndexed { i, producto ->
                ProductoItem(
                    producto = producto,
                    cantidad = state.cantidades[i],
                    onCantidadChange = { viewModel.onCantidadChange(i, it) }
                )
                HorizontalDivider()
            }

            OutlinedTextField(
                value = state.cupon,
                onValueChange = { viewModel.onCuponChange(it) },
                label = { Text("Cupón") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            Spacer(Modifier.height(12.dp))
            ResumenCarrito(
                articulos = state.articulos,
                subtotal = state.subtotal,
                descuento = state.descuento,
                descCupon = state.descCupon,
                envio = state.envio,
                total = state.total
            )
            Spacer(Modifier.height(12.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.onVaciar() },
                modifier = Modifier.height(52.dp)
            ) { Text("Vaciar", fontWeight = FontWeight.Bold) }
            Button(
                onClick = { viewModel.onConfirmar() },
                enabled = state.articulos > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) { Text("Confirmar pedido", fontWeight = FontWeight.Bold) }
        }
    }

    if (state.confirmado) {
        AlertDialog(
            onDismissRequest = { viewModel.onConfirmadoCerrado() },
            title = { Text("Pedido confirmado") },
            text = { Text("Son ${state.articulos} artículos. Total a pagar: ${dinero(state.total)}") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onConfirmadoCerrado()
                    viewModel.onNuevaCompra()
                }) { Text("Listo") }
            }
        )
    }
}

@Composable
fun ProductoItem(producto: Producto, cantidad: Int, onCantidadChange: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Text(
                "${dinero(producto.precio)} c/u",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        OutlinedIconButton(
            onClick = { onCantidadChange(cantidad - 1) },
            enabled = cantidad > 0,
            modifier = Modifier.size(38.dp)
        ) { Text("−", fontSize = 20.sp) }
        Text(
            "$cantidad",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center
        )
        OutlinedIconButton(
            onClick = { onCantidadChange(cantidad + 1) },
            enabled = cantidad < 10,
            modifier = Modifier.size(38.dp)
        ) { Text("+", fontSize = 20.sp) }
    }
}

@Composable
fun ResumenCarrito(
    articulos: Int,
    subtotal: Double,
    descuento: Double,
    descCupon: Double,
    envio: Double,
    total: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FilaResumen("Artículos", "$articulos")
            FilaResumen("Subtotal", dinero(subtotal))
            if (descuento > 0) {
                FilaResumen("Descuento 10 %", "−" + dinero(descuento), verde)
            } else {
                FilaResumen("Descuento 10 %", dinero(0.0))
            }
            if (descCupon > 0) {
                FilaResumen("Cupón DONPEPE", "−" + dinero(descCupon), verde)
            }
            if (articulos > 0 && envio == 0.0) {
                FilaResumen("Envío", "GRATIS", verde)
            } else {
                FilaResumen("Envío", dinero(envio))
            }

            HorizontalDivider(Modifier.padding(vertical = 6.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOTAL", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(
                    dinero(total),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FilaResumen(etiqueta: String, valor: String, colorValor: Color = Color.Unspecified) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 17.sp)
        Text(valor, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = colorValor)
    }
}

@Preview(showBackground = true)
@Composable
fun ResumenCarritoPreview() {
    MaterialTheme { ResumenCarrito(6, 231.5, 23.15, 0.0, 0.0, 208.35) }
}
