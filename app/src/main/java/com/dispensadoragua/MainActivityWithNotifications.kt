package com.dispensadoragua

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dispensadoragua.ui.*
import com.dispensadoragua.ui.theme.DispensadorAguaAppTheme
import com.dispensadoragua.otro.GestorNotificaciones
import com.dispensadoragua.viewmodel.EstadoAutenticacion
import com.dispensadoragua.viewmodel.ModelGestionarAuth
import com.dispensadoragua.viewmodel.ModelDispositivo
import com.dispensadoragua.viewmodel.ModelNotificaciones

class MainActivityConNotificaciones : ComponentActivity() {

    private lateinit var gestorNotificaciones: GestorNotificaciones

    private val lanzadorPermisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { otorgado: Boolean ->
        if (otorgado) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gestorNotificaciones = GestorNotificaciones(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                lanzadorPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            DispensadorAguaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavegacionConNotificaciones()
                }
            }
        }
    }
}

@Composable
fun NavegacionConNotificaciones() {
    val controladorNavegacion = rememberNavController()
    val viewAutenticacion: ModelGestionarAuth = viewModel()
    val viewDispositivo: ModelDispositivo = viewModel()
    val viewNotificaciones: ModelNotificaciones = viewModel()

    val estadoAutenticacion by viewAutenticacion.estadoAutenticacion.collectAsState()
    val EstadoDispositivo by viewDispositivo.EstadoDispositivo.collectAsState()
    val datosNotificaciones by viewDispositivo.notifications.collectAsState()

    LaunchedEffect(EstadoDispositivo) {
        EstadoDispositivo?.let { estado ->
            viewNotificaciones.procesarEstadoDispositivo(estado)
        }
    }

    LaunchedEffect(datosNotificaciones) {
        datosNotificaciones?.let { noti ->
            viewNotificaciones.procesarDatosNotificacion(noti)
        }
    }

    val destinoInicial = if (estadoAutenticacion is EstadoAutenticacion.Autenticado) "inicio" else "login"

    NavHost(
        navController = controladorNavegacion,
        startDestination = destinoInicial
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    controladorNavegacion.navigate("inicio") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                ModelGestionarAuth = viewAutenticacion
            )
        }

        composable("inicio") {
            HomeScreen(
                navegarMonitoreo = { controladorNavegacion.navigate("monitor") },
                navegarControl = { controladorNavegacion.navigate("control") },
                navegarNotificaciones = { controladorNavegacion.navigate("notificaciones") },
                ModelGestionarAuth = viewAutenticacion,
                ModelDispositivo = viewDispositivo
            )
        }

        composable("monitor") {
            MonitorScreen(
                onNavigateBack = { controladorNavegacion.popBackStack() },
                ModelDispositivo = viewDispositivo
            )
        }

        composable("control") {
            ControlScreen(
                onNavigateBack = { controladorNavegacion.popBackStack() },
                ModelDispositivo = viewDispositivo
            )
        }

        composable("notificaciones") {
            NotificationsScreen(
                onNavigateBack = { controladorNavegacion.popBackStack() },
                ModelDispositivo = viewDispositivo
            )
        }
    }

    LaunchedEffect(estadoAutenticacion) {
        if (estadoAutenticacion is EstadoAutenticacion.NoAutenticado &&
            controladorNavegacion.currentDestination?.route != "login"
        ) {
            controladorNavegacion.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
