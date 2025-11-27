package com.dispensadoragua.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dispensadoragua.data.ControlarDispositivo
import com.dispensadoragua.data.EstadoDispositivo
import com.dispensadoragua.data.Notificaciones
import com.dispensadoragua.repositorios.GestionarFirebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ModelDispositivo : ViewModel() {

    private val repository = GestionarFirebase()

    private val _EstadoDispositivo = MutableStateFlow<EstadoDispositivo?>(null)
    val EstadoDispositivo: StateFlow<EstadoDispositivo?> = _EstadoDispositivo.asStateFlow()

    private val _ControlarDispositivo = MutableStateFlow<ControlarDispositivo?>(null)
    val ControlarDispositivo: StateFlow<ControlarDispositivo?> = _ControlarDispositivo.asStateFlow()

    private val _notifications = MutableStateFlow<Notificaciones?>(null)
    val notifications: StateFlow<Notificaciones?> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _estadoOperacion = MutableStateFlow<OperacionEstado>(OperacionEstado.Inactiva)
    val estadoOperacion: StateFlow<OperacionEstado> = _estadoOperacion.asStateFlow()

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        // Monitorear estado del dispositivo
        viewModelScope.launch {
            repository.obtenerEstadoDispositivo().collect { result ->
                result.onSuccess { status ->
                    _EstadoDispositivo.value = status
                    _isLoading.value = false
                    _errorMessage.value = null
                }.onFailure { error ->
                    _errorMessage.value = error.message
                    _isLoading.value = false
                }
            }
        }


        viewModelScope.launch {
            repository.obtenerControlarDispositivo().collect { result ->
                result.onSuccess { control ->
                    _ControlarDispositivo.value = control
                }.onFailure { error ->
                    _errorMessage.value = error.message
                }
            }
        }

        viewModelScope.launch {
            repository.obtenerNotificaciones().collect { result ->
                result.onSuccess { notification ->
                    _notifications.value = notification
                }.onFailure { error ->
                    _errorMessage.value = error.message
                }
            }
        }
    }


    fun updateControlarDispositivo(control: ControlarDispositivo) {
        viewModelScope.launch {
            _estadoOperacion.value = OperacionEstado.Cargando

            val result = repository.escribirControlarDispositivo(control)

            result.onSuccess {
                _estadoOperacion.value = OperacionEstado.Exito("Configuración actualizada")
            }.onFailure { error ->
                _estadoOperacion.value =
                    OperacionEstado.ErrorOperacion(error.message ?: "Error al actualizar")
            }
        }
    }

    fun toggleProgramming(enabled: Boolean) {
        val current = _ControlarDispositivo.value ?: ControlarDispositivo()
        updateControlarDispositivo(current.copy(activarProgramacion = enabled))
    }

    fun updateScheduleTime(time: String) {
        val current = _ControlarDispositivo.value ?: ControlarDispositivo()
        updateControlarDispositivo(current.copy(horarioEstablecido = time))
    }


    fun updateDispenseAmount(amount: Int) {
        val current = _ControlarDispositivo.value ?: ControlarDispositivo()
        updateControlarDispositivo(current.copy(cantidadDeAgua = amount))
    }

    fun dispenseWaterManually(amount: Int) {
        viewModelScope.launch {
            _estadoOperacion.value = OperacionEstado.Cargando

            val result = repository.dispensarAgua(amount)

            result.onSuccess {
                _estadoOperacion.value =
                    OperacionEstado.Exito("Agua dispensada: $amount ml")
            }.onFailure { error ->
                _estadoOperacion.value =
                    OperacionEstado.ErrorOperacion(error.message ?: "Error al dispensar")
            }
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.limpiarNotificaciones()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetOperacionEstado() {
        _estadoOperacion.value = OperacionEstado.Inactiva
    }

}
