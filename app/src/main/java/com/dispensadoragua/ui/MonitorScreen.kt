package com.dispensadoragua.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dispensadoragua.data.EstadoDispositivo
import com.dispensadoragua.viewmodel.ModelDispositivo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorScreen(
    onNavigateBack: () -> Unit,
    ModelDispositivo: ModelDispositivo = viewModel()
) {
    val EstadoDispositivo by ModelDispositivo.EstadoDispositivo.collectAsState()
    val isLoading by ModelDispositivo.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoreo en Tiempo Real") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "onNavigateBack")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TarjetaActualizacionAutomatica()
                TarjetaConexion(EstadoDispositivo)
                TarjetaNivelAgua(EstadoDispositivo)
                TarjetaInformacionTecnica(EstadoDispositivo)
                TarjetaEstadoUso(EstadoDispositivo)
            }
        }
    }
}

@Composable
fun TarjetaActualizacionAutomatica() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Actualización automática en tiempo real",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun TarjetaConexion(estado: EstadoDispositivo?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (estado?.conexion == true)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (estado?.conexion == true) Icons.Default.Wifi else Icons.Default.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (estado?.conexion == true)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = if (estado?.conexion == true) "Conectado" else "Desconectado",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Señal WiFi: ${estado?.obtenerIntensidadWifi() ?: "N/A"} (${estado?.intensidadWifi ?: 0} dBm)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun TarjetaNivelAgua(estado: EstadoDispositivo?) {
    val nivel = estado?.nivelAgua ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                nivel <= 0 -> MaterialTheme.colorScheme.errorContainer
                nivel < 20 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nivel de Agua",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$nivel%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        nivel <= 0 -> MaterialTheme.colorScheme.error
                        nivel < 20 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { nivel / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = when {
                    nivel <= 0 -> MaterialTheme.colorScheme.error
                    nivel < 20 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                nivel <= 0 -> Text(
                    text = "🚨 Agua agotada - Recarga inmediatamente",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
                nivel < 20 -> Text(
                    text = "⚠️ Nivel bajo - Recarga pronto",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )
                else -> Text(
                    text = "✓ Nivel adecuado",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TarjetaInformacionTecnica(estado: EstadoDispositivo?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Información Técnica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FilaMonitor(
                icono = Icons.Default.Power,
                etiqueta = "Alimentación",
                valor = if (estado?.encendido == true) "Encendido" else "Apagado"
            )

            FilaMonitor(
                icono = Icons.Default.SignalCellularAlt,
                etiqueta = "Señal WiFi (RSSI)",
                valor = "${estado?.intensidadWifi ?: 0} dBm"
            )

            if (estado?.ultimaActualizacion != null && estado.ultimaActualizacion > 0) {
                val formato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val actualizacion = formato.format(Date(estado.ultimaActualizacion))

                FilaMonitor(
                    icono = Icons.Default.Schedule,
                    etiqueta = "Última Actualización",
                    valor = actualizacion
                )
            }
        }
    }
}

@Composable
fun TarjetaEstadoUso(estado: EstadoDispositivo?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (estado?.enUso == true)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (estado?.enUso == true) Icons.Default.WaterDrop else Icons.Default.Pause,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (estado?.enUso == true)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = if (estado?.enUso == true) "Dispensando Agua" else "Inactivo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (estado?.enUso == true)
                        "El dispositivo está dispensando agua actualmente"
                    else
                        "El dispositivo está en espera",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun FilaMonitor(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = etiqueta,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
