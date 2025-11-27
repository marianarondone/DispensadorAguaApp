package com.dispensadoragua.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dispensadoragua.viewmodel.EstadoAutenticacion
import com.dispensadoragua.viewmodel.ModelGestionarAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    ModelGestionarAuth: ModelGestionarAuth = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isregistrarUsuarioMode by rememberSaveable { mutableStateOf(false) }
    var showrestablecerContraseña by rememberSaveable { mutableStateOf(false) }

    val EstadoAutenticacion by ModelGestionarAuth.estadoAutenticacion.collectAsState()

    LaunchedEffect(EstadoAutenticacion) {
        if (EstadoAutenticacion is EstadoAutenticacion.Autenticado) {
            onLoginSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Logo minimalista
                    Text(
                        text = "💧",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = if (isregistrarUsuarioMode) "Crear Cuenta" else "Iniciar Sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 4.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Dispensador de Agua",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    if (!isregistrarUsuarioMode) {
                        TextButton(
                            onClick = { showrestablecerContraseña = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("¿Olvidaste tu contraseña?")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isregistrarUsuarioMode) ModelGestionarAuth.registrarUsuario(email, password)
                            else ModelGestionarAuth.iniciarSesion(email, password)
                        },
                        enabled = email.isNotBlank() && password.length >= 6 && EstadoAutenticacion !is EstadoAutenticacion.Cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        if (EstadoAutenticacion is EstadoAutenticacion.Cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(if (isregistrarUsuarioMode) "Registrarse" else "Ingresar")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { isregistrarUsuarioMode = !isregistrarUsuarioMode; ModelGestionarAuth.limpiarError() }) {
                        Text(
                            if (isregistrarUsuarioMode)
                                "¿Ya tienes cuenta? Inicia sesión"
                            else
                                "¿No tienes cuenta? Regístrate"
                        )
                    }

                    if (EstadoAutenticacion is EstadoAutenticacion.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text((EstadoAutenticacion as EstadoAutenticacion.Error).mensaje) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                labelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }

                    if (EstadoAutenticacion is EstadoAutenticacion.CorreoRestablecimientoEnviado) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("Se envió un correo para restablecer la contraseña") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        if (showrestablecerContraseña) {
            AlertDialog(
                onDismissRequest = { showrestablecerContraseña = false },
                title = { Text("Recuperar Contraseña") },
                text = {
                    Column {
                        Text("Ingresa tu correo y te enviaremos un enlace para restablecer tu contraseña.")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo Electrónico") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            ModelGestionarAuth.restablecerContraseña(email)
                            showrestablecerContraseña = false
                        },
                        enabled = email.isNotBlank()
                    ) { Text("Enviar") }
                },
                dismissButton = {
                    TextButton(onClick = { showrestablecerContraseña = false }) { Text("Cancelar") }
                }
            )
        }
    }
}
