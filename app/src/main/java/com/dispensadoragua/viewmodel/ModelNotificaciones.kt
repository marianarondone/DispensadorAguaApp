package com.dispensadoragua.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dispensadoragua.data.EstadoDispositivo
import com.dispensadoragua.data.Notificaciones
import com.dispensadoragua.otro.GestorNotificaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ModelNotificaciones(application: Application) : AndroidViewModel(application) {

    private val gestorNotificaciones = GestorNotificaciones(application.applicationContext)

    private val _notificacionesMostradas = MutableStateFlow<Set<String>>(emptySet())
    val notificacionesMostradas: StateFlow<Set<String>> = _notificacionesMostradas.asStateFlow()

    fun procesarEstadoDispositivo(estado: EstadoDispositivo) {
        viewModelScope.launch {

            if (estado.tazaVacia() && !_notificacionesMostradas.value.contains("taza_vacia")) {
                gestorNotificaciones.mostrarNotificacionTazaVacia()
                agregarNotificacionMostrada("taza_vacia")
            }

            else if (estado.nivelAguaBajo() &&
                !_notificacionesMostradas.value.contains("agua_baja_${estado.nivelAgua}")
            ) {
                gestorNotificaciones.mostrarNotificacionAguaBaja(estado.nivelAgua)
                agregarNotificacionMostrada("agua_baja_${estado.nivelAgua}")
            }

            if (!estado.conexion && !_notificacionesMostradas.value.contains("desconectado")) {
                gestorNotificaciones.mostrarNotificacionDesconectado()
                agregarNotificacionMostrada("desconectado")
            }

            if (estado.conexion && _notificacionesMostradas.value.contains("desconectado")) {
                removerNotificacionMostrada("desconectado")
            }

            if (estado.nivelAgua >= 20) {
                _notificacionesMostradas.value
                    .filter { it.startsWith("agua_baja") }
                    .forEach { removerNotificacionMostrada(it) }
            }
        }
    }

    fun procesarDatosNotificacion(notificacion: Notificaciones) {
        viewModelScope.launch {
            if (notificacion.error_code.isNotEmpty() &&
                !_notificacionesMostradas.value.contains("error_${notificacion.error_code}")
            ) {
                gestorNotificaciones.mostrarNotificacionError(notificacion.error_code)
                agregarNotificacionMostrada("error_${notificacion.error_code}")
            }
        }
    }

    private fun agregarNotificacionMostrada(clave: String) {
        _notificacionesMostradas.value = _notificacionesMostradas.value + clave
    }

    private fun removerNotificacionMostrada(clave: String) {
        _notificacionesMostradas.value = _notificacionesMostradas.value - clave
    }

    fun limpiarTodasLasNotificaciones() {
        gestorNotificaciones.cancelarTodasNotificaciones()
        _notificacionesMostradas.value = emptySet()
    }

    fun reiniciarEstadoNotificaciones() {
        _notificacionesMostradas.value = emptySet()
    }
}
