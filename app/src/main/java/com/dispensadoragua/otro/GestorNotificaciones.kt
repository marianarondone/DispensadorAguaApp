package com.dispensadoragua.otro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class GestorNotificaciones(private val contexto: Context) {

    companion object {
        const val ID_CANAL = "dispensador_agua_canal"
        const val NOMBRE_CANAL = "Alertas del Dispensador"
        const val DESCRIPCION_CANAL = "Notificaciones sobre el estado del dispensador de agua"

        const val ID_NOTIF_AGUA_BAJA = 1
        const val ID_NOTIF_AGUA_VACIA = 2
        const val ID_NOTIF_DESCONECTADO = 3
        const val ID_NOTIF_ERROR = 4
    }

    init {
        crearCanalNotificaciones()
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(ID_CANAL, NOMBRE_CANAL, importancia).apply {
                description = DESCRIPCION_CANAL
                enableVibration(true)
                enableLights(true)
            }

            val gestorNotificaciones = contexto.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            gestorNotificaciones.createNotificationChannel(canal)
        }
    }

    fun mostrarNotificacionAguaBaja(nivelAgua: Int) {
        val notificacion = NotificationCompat.Builder(contexto, ID_CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Nivel de Agua Bajo")
            .setContentText("El dispensador tiene solo $nivelAgua% de agua. Recarga pronto.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(contexto).notify(ID_NOTIF_AGUA_BAJA, notificacion)
    }

    fun mostrarNotificacionTazaVacia() {
        val notificacion = NotificationCompat.Builder(contexto, ID_CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🚨 Agua Agotada")
            .setContentText("El dispensador está vacío. Rellénalo inmediatamente.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(contexto).notify(ID_NOTIF_AGUA_VACIA, notificacion)
    }

    fun mostrarNotificacionDesconectado() {
        val notificacion = NotificationCompat.Builder(contexto, ID_CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("📡 Dispositivo Desconectado")
            .setContentText("El dispensador ha perdido la conexión. Revisa la red WiFi.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(contexto).notify(ID_NOTIF_DESCONECTADO, notificacion)
    }

    fun mostrarNotificacionError(codigoError: String) {
        val notificacion = NotificationCompat.Builder(contexto, ID_CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("❌ Error del Sistema")
            .setContentText("Error detectado: $codigoError. Revisa el dispositivo.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(contexto).notify(ID_NOTIF_ERROR, notificacion)
    }

    fun cancelarTodasNotificaciones() {
        NotificationManagerCompat.from(contexto).cancelAll()
    }
}
