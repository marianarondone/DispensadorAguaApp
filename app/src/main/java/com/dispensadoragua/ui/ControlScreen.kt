package com.dispensadoragua.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dispensadoragua.viewmodel.ModelDispositivo
import com.dispensadoragua.viewmodel.OperacionEstado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
    onNavigateBack: () -> Unit,
    ModelDispositivo: ModelDispositivo = viewModel()
) {
    val estadoControl by ModelDispositivo.ControlarDispositivo.collectAsState()
    val estadoOperacion by ModelDispositivo.estadoOperacion.collectAsState()

    var programacionActiva by remember { mutableStateOf(estadoControl?.activarProgramacion ?: false) }
    var horarioProgramado by remember { mutableStateOf(estadoControl?.horarioEstablecido ?: "08:00") }
    var cantidadDeAguaProgramada by remember { mutableStateOf((estadoControl?.cantidadDeAgua ?: 250).toString()) }
    var cantidadDeAguaManual by remember { mutableStateOf("250") }

    LaunchedEffect(estadoControl) {
        estadoControl?.let {
            programacionActiva = it.activarProgramacion
            horarioProgramado = it.horarioEstablecido
            cantidadDeAguaProgramada = it.cantidadDeAgua.toString()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estadoOperacion) {
        when (estadoOperacion) {
            is OperacionEstado.Exito -> {
                snackbarHostState.showSnackbar(
                    message = (estadoOperacion as OperacionEstado.Exito).message
                )
                ModelDispositivo.resetOperacionEstado()
            }
            is OperacionEstado.ErrorOperacion -> {
                snackbarHostState.showSnackbar(
                    message = (estadoOperacion as OperacionEstado.ErrorOperacion).message
                )
                ModelDispositivo.resetOperacionEstado()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control del Dispensador", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.elevatedCardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(
                                "Programación Automática",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                if (programacionActiva) "Activada" else "Desactivada",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                            )
                        }

                        Switch(
                            checked = programacionActiva,
                            onCheckedChange = { activado ->
                                programacionActiva = activado
                                ModelDispositivo.toggleProgramming(activado)
                            }
                        )
                    }

                    if (programacionActiva) {

                        OutlinedTextField(
                            value = horarioProgramado,
                            onValueChange = { horarioProgramado = it },
                            label = { Text("Hora (HH:mm)") },
                            leadingIcon = { Icon(Icons.Default.Schedule, null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = cantidadDeAguaProgramada,
                            onValueChange = { nuevoValor ->
                                val valorInt = nuevoValor.toIntOrNull()
                                if (valorInt != null && valorInt in 50..500) {
                                    cantidadDeAguaProgramada = nuevoValor
                                } else if (nuevoValor.isEmpty()) {
                                    cantidadDeAguaProgramada = ""
                                }
                            },
                            label = { Text("Cantidad (ml)") },
                            leadingIcon = { Icon(Icons.Default.WaterDrop, null) },
                            supportingText = { Text("Rango permitido: 50-500 ml") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val cantidad = cantidadDeAguaProgramada.toIntOrNull() ?: 250
                                ModelDispositivo.updateScheduleTime(horarioProgramado)
                                ModelDispositivo.updateDispenseAmount(cantidad)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = estadoOperacion !is OperacionEstado.Cargando
                        ) {
                            if (estadoOperacion is OperacionEstado.Cargando) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text("Guardar Configuración")
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.elevatedCardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "Dispensación Manual",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = cantidadDeAguaManual,
                        onValueChange = { nuevoValor ->
                            val valorInt = nuevoValor.toIntOrNull()
                            if (valorInt != null && valorInt in 50..500) {
                                cantidadDeAguaManual = nuevoValor
                            } else if (nuevoValor.isEmpty()) {
                                cantidadDeAguaManual = ""
                            }
                        },
                        label = { Text("Cantidad (ml)") },
                        leadingIcon = { Icon(Icons.Default.WaterDrop, null) },
                        supportingText = { Text("Rango permitido: 50-500 ml") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val cantidad = cantidadDeAguaManual.toIntOrNull() ?: 250
                            ModelDispositivo.dispenseWaterManually(cantidad)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = cantidadDeAguaManual.isNotEmpty() &&
                                estadoOperacion !is OperacionEstado.Cargando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dispensar Ahora")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Los cambios se aplican inmediatamente al dispositivo.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
