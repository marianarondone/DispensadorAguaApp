package com.dispensadoragua.otro

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificacionesFirebase : FirebaseMessagingService() {

    private val etiquetaLog = "FCMService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(etiquetaLog, "Nuevo token FCM: $token")
    }

    override fun onMessageReceived(mensaje: RemoteMessage) {
        super.onMessageReceived(mensaje)

        Log.d(etiquetaLog, "Mensaje recibido de: ${mensaje.from}")

        mensaje.notification?.let { notificacion ->
            Log.d(etiquetaLog, "Título: ${notificacion.title}")
            Log.d(etiquetaLog, "Cuerpo: ${notificacion.body}")

            val gestorNotificaciones = GestorNotificaciones(applicationContext)

            when {
                notificacion.title?.contains("Agua", ignoreCase = true) == true -> {
                    if (notificacion.body?.contains("agotada", ignoreCase = true) == true) {
                        gestorNotificaciones.mostrarNotificacionTazaVacia()
                    } else {
                        gestorNotificaciones.mostrarNotificacionAguaBaja(0)
                    }
                }
                notificacion.title?.contains("Desconectado", ignoreCase = true) == true -> {
                    gestorNotificaciones.mostrarNotificacionDesconectado()
                }
                notificacion.title?.contains("Error", ignoreCase = true) == true -> {
                    gestorNotificaciones.mostrarNotificacionError(notificacion.body ?: "Error desconocido")
                }
            }
        }

        if (mensaje.data.isNotEmpty()) {
            Log.d(etiquetaLog, "Datos del mensaje: ${mensaje.data}")
            handleDataPayload(mensaje.data)
        }
    }

    private fun handleDataPayload(datos: Map<String, String>) {
        val gestorNotificaciones = GestorNotificaciones(applicationContext)

        when (datos["type"]) {
            "low_water" -> {
                val nivel = datos["nivelAgua"]?.toIntOrNull() ?: 0
                gestorNotificaciones.mostrarNotificacionAguaBaja(nivel)
            }
            "tazaVacia" -> {
                gestorNotificaciones.mostrarNotificacionTazaVacia()
            }
            "desconectado" -> {
                gestorNotificaciones.mostrarNotificacionDesconectado()
            }
            "error" -> {
                val codigoError = datos["error_code"] ?: "UNKNOWN"
                gestorNotificaciones.mostrarNotificacionError(codigoError)
            }
        }
    }
}
