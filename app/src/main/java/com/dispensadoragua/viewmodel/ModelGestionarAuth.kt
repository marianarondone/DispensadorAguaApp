package com.dispensadoragua.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dispensadoragua.repositorios.AutenticacionFirebase
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ModelGestionarAuth : ViewModel() {

    private val repositorio = AutenticacionFirebase()

    private val _estadoAutenticacion = MutableStateFlow<EstadoAutenticacion>(EstadoAutenticacion.Cargando)
    val estadoAutenticacion: StateFlow<EstadoAutenticacion> = _estadoAutenticacion.asStateFlow()

    private val _usuarioActual = MutableStateFlow<FirebaseUser?>(null)
    val usuarioActual: StateFlow<FirebaseUser?> = _usuarioActual.asStateFlow()

    init {
        verificarEstadoAutenticacion()
    }

    private fun verificarEstadoAutenticacion() {
        val usuario = repositorio.obtenerUsuarioActual()
        _usuarioActual.value = usuario
        _estadoAutenticacion.value = if (usuario != null) {
            EstadoAutenticacion.Autenticado
        } else {
            EstadoAutenticacion.NoAutenticado
        }
    }

    fun iniciarSesion(correo: String, contraseña: String) {
        viewModelScope.launch {
            _estadoAutenticacion.value = EstadoAutenticacion.Cargando

            val resultado = repositorio.iniciarSesion(correo, contraseña)

            resultado.onSuccess { usuario ->
                _usuarioActual.value = usuario
                _estadoAutenticacion.value = EstadoAutenticacion.Autenticado
            }.onFailure { error ->
                _estadoAutenticacion.value = EstadoAutenticacion.Error(
                    error.message ?: "Error de autenticación"
                )
            }
        }
    }

    fun registrarUsuario(correo: String, contraseña: String) {
        viewModelScope.launch {
            _estadoAutenticacion.value = EstadoAutenticacion.Cargando

            val resultado = repositorio.registrarUsuario(correo, contraseña)

            resultado.onSuccess { usuario ->
                _usuarioActual.value = usuario
                _estadoAutenticacion.value = EstadoAutenticacion.Autenticado
            }.onFailure { error ->
                _estadoAutenticacion.value = EstadoAutenticacion.Error(
                    error.message ?: "Error al registrar usuario"
                )
            }
        }
    }

    fun restablecerContraseña(correo: String) {
        viewModelScope.launch {
            _estadoAutenticacion.value = EstadoAutenticacion.Cargando

            val resultado = repositorio.restablecerContraseña(correo)

            resultado.onSuccess {
                _estadoAutenticacion.value = EstadoAutenticacion.CorreoRestablecimientoEnviado
            }.onFailure { error ->
                _estadoAutenticacion.value = EstadoAutenticacion.Error(
                    error.message ?: "Error al enviar correo"
                )
            }
        }
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
        _usuarioActual.value = null
        _estadoAutenticacion.value = EstadoAutenticacion.NoAutenticado
    }

    fun limpiarError() {
        if (_estadoAutenticacion.value is EstadoAutenticacion.Error) {
            _estadoAutenticacion.value = if (repositorio.usuarioAutenticado()) {
                EstadoAutenticacion.Autenticado
            } else {
                EstadoAutenticacion.NoAutenticado
            }
        }
    }
}

sealed class EstadoAutenticacion {
    object Cargando : EstadoAutenticacion()
    object Autenticado : EstadoAutenticacion()
    object NoAutenticado : EstadoAutenticacion()
    object CorreoRestablecimientoEnviado : EstadoAutenticacion()
    data class Error(val mensaje: String) : EstadoAutenticacion()
}
