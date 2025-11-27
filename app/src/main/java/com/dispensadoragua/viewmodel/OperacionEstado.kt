package com.dispensadoragua.viewmodel

sealed class OperacionEstado {
    object Inactiva : OperacionEstado()
    object Cargando : OperacionEstado()
    data class Exito(val message: String) : OperacionEstado()
    data class ErrorOperacion(val message: String) : OperacionEstado()

}
