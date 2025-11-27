package com.dispensadoragua.data

data class Notificaciones(
    var pocaAgua: Boolean = false,
    var desconectado: Boolean = false,
    var error_code: String = "",
    var tiempoEjecucion: Long = 0L
) {
    constructor() : this(false, false, "", 0L)

    fun getTipoNotificacion(): String {
        return when {
            desconectado -> "Dispositivo Desconectado"
            error_code.isNotEmpty() -> "Error del Sistema: $error_code"
            pocaAgua -> "Nivel de Agua Bajo"
            else -> "Sin Alertas"
        }
    }

    fun estadoAlerta(): Boolean = pocaAgua || desconectado || error_code.isNotEmpty()
}
