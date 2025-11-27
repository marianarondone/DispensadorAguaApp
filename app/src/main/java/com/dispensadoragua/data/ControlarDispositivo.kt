package com.dispensadoragua.data

data class ControlarDispositivo(
    var activarProgramacion: Boolean = false,
    var horarioEstablecido: String = "08:00",
    var cantidadDeAgua: Int = 250,
    var ultimaInstruccion: Long = 0L
) {
    constructor() : this(false, "08:00", 250, 0L)

    fun validarHorario(): Boolean {
        val reg = Regex("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")
        return reg.matches(horarioEstablecido)
    }

    fun validarCantidadDeAgua(): Boolean = cantidadDeAgua in 50..500
}
