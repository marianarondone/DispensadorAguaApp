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
import com.dispensadoragua.data.Notificaciones
import com.dispensadoragua.viewmodel.ModelDispositivo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    ModelDispositivo: ModelDispositivo = viewModel()
) {
    val notificaciones by ModelDispositivo.notifications.collectAsState()
    val estadoDispositivo by ModelDispositivo.EstadoDispositivo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { ModelDispositivo.clearNotifications() }
                    ) {
                        Icon(Icons.Default.ClearAll, contentDescription = "Limpiar notificaciones")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaResumenAlertas(notificaciones, estadoDispositivo)

            Text(
                text = "Historial de Alertas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (notificaciones?.pocaAgua == true) {
                ItemNotificacion(
                    icono = Icons.Default.WaterDrop,
                    titulo = "Nivel de Agua Bajo",
                    descripcion = "El dispensador tiene poco agua.",
                    tiempo = notificaciones?.tiempoEjecucion ?: 0L,
                    severidad = ImportanciaNotificacion.WARNING
                )
            }

            if (estadoDispositivo?.tazaVacia() == true) {
                ItemNotificacion(
                    icono = Icons.Default.Warning,
                    titulo = "Agua Agotada",
                    descripcion = "El dispensador está vacío.",
                    tiempo = System.currentTimeMillis(),
                    severidad = ImportanciaNotificacion.CRITICAL
                )
            }

            if (notificaciones?.desconectado == true) {
                ItemNotificacion(
                    icono = Icons.Default.WifiOff,
                    titulo = "Dispositivo Desconectado",
                    descripcion = "El dispensador perdió conexión WiFi.",
                    tiempo = notificaciones?.tiempoEjecucion ?: 0L,
                    severidad = ImportanciaNotificacion.ERROR
                )
            }

            if (notificaciones?.error_code?.isNotEmpty() == true) {
                ItemNotificacion(
                    icono = Icons.Default.Error,
                    titulo = "Error del Sistema",
                    descripcion = "Código de error: ${notificaciones?.error_code}",
                    tiempo = notificaciones?.tiempoEjecucion ?: 0L,
                    severidad = ImportanciaNotificacion.ERROR
                )
            }

            if (notificaciones?.estadoAlerta() != true && estadoDispositivo?.tazaVacia() != true) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Sin alertas",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Todo está bien",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "No hay alertas activas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaResumenAlertas(
    notificacion: Notificaciones?,
    estadoDispositivo: com.dispensadoragua.data.EstadoDispositivo?
) {
    val alertasActivas = mutableListOf<String>()

    if (notificacion?.pocaAgua == true) alertasActivas.add("Agua baja")
    if (estadoDispositivo?.tazaVacia() == true) alertasActivas.add("Agua agotada")
    if (notificacion?.desconectado == true) alertasActivas.add("Desconectado")
    if (notificacion?.error_code?.isNotEmpty() == true) alertasActivas.add("Error del sistema")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (alertasActivas.isNotEmpty())
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (alertasActivas.isNotEmpty()) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                contentDescription = "Estado de alertas",
                modifier = Modifier.size(32.dp),
                tint = if (alertasActivas.isNotEmpty())
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = if (alertasActivas.isNotEmpty())
                        "${alertasActivas.size} Alerta(s) Activa(s)"
                    else
                        "Sin Alertas Activas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (alertasActivas.isNotEmpty()) {
                    Text(
                        text = alertasActivas.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemNotificacion(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    descripcion: String,
    tiempo: Long,
    severidad: ImportanciaNotificacion
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (severidad) {
                ImportanciaNotificacion.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                ImportanciaNotificacion.ERROR -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                ImportanciaNotificacion.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
                ImportanciaNotificacion.INFO -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                modifier = Modifier.size(28.dp),
                tint = when (severidad) {
                    ImportanciaNotificacion.CRITICAL, ImportanciaNotificacion.ERROR -> MaterialTheme.colorScheme.error
                    ImportanciaNotificacion.WARNING -> MaterialTheme.colorScheme.tertiary
                    ImportanciaNotificacion.INFO -> MaterialTheme.colorScheme.primary
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                if (tiempo > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    Text(
                        text = formato.format(Date(tiempo)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

enum class ImportanciaNotificacion {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}
