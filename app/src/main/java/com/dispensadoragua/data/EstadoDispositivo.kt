package com.dispensadoragua.data

data class EstadoDispositivo(
    var conexion: Boolean = false,
    var encendido: Boolean = false,
    var enUso: Boolean = false,
    var nivelAgua: Int = 100,
    var ultimaActualizacion: Long = 0L,
    var intensidadWifi: Int = 0
) {
    constructor() : this(false, false, false, 100, 0L, 0)

    fun obtenerEstado(): String {
        return when {
            !conexion -> "Desconectado"
            enUso -> "En Uso"
            encendido -> "Encendido"
            else -> "Apagado"
        }
    }

    fun nivelAguaBajo(): Boolean = nivelAgua < 20

    fun tazaVacia(): Boolean = nivelAgua <= 0

    fun obtenerIntensidadWifi(): String {
        return when {
            intensidadWifi >= -50 -> "Excelente"
            intensidadWifi >= -60 -> "Buena"
            intensidadWifi >= -70 -> "Regular"
            else -> "Débil"
        }
    }
}

