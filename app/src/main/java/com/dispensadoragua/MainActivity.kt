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

class MainActivity : ComponentActivity() {

    private lateinit var gestorNotificaciones: GestorNotificaciones

    private val lanzadorPermisosNotificaciones = registerForActivityResult(
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
                lanzadorPermisosNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            DispensadorAguaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavegacionApp()
                }
            }
        }
    }
}

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val ModelGestionarAuth: ModelGestionarAuth = viewModel()
    val ModelDispositivo: ModelDispositivo = viewModel()

    val estadoAutenticacion by ModelGestionarAuth.estadoAutenticacion.collectAsState()

    val destinoInicial =
        if (estadoAutenticacion is EstadoAutenticacion.Autenticado) "inicio"
        else "login"

    NavHost(
        navController = navController,
        startDestination = destinoInicial
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("inicio") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                ModelGestionarAuth = ModelGestionarAuth
            )
        }

        composable("inicio") {
            HomeScreen(
                navegarMonitoreo = { navController.navigate("monitoreo") },
                navegarControl = { navController.navigate("control") },
                navegarNotificaciones = { navController.navigate("notificaciones") },
                ModelGestionarAuth = ModelGestionarAuth,
                ModelDispositivo = ModelDispositivo
            )
        }

        composable("monitoreo") {
            MonitorScreen(
                onNavigateBack = { navController.popBackStack() },
                ModelDispositivo = ModelDispositivo
            )
        }

        composable("control") {
            ControlScreen(
                onNavigateBack = { navController.popBackStack() },
                ModelDispositivo = ModelDispositivo
            )
        }

        composable("notificaciones") {
            NotificationsScreen(
                onNavigateBack = { navController.popBackStack() },
                ModelDispositivo = ModelDispositivo
            )
        }
    }

    LaunchedEffect(estadoAutenticacion) {
        if (
            estadoAutenticacion is EstadoAutenticacion.NoAutenticado &&
            navController.currentDestination?.route != "login"
        ) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
