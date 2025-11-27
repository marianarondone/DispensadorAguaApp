package com.dispensadoragua.repositorios

import android.util.Log
import com.dispensadoragua.data.ControlarDispositivo
import com.dispensadoragua.data.EstadoDispositivo
import com.dispensadoragua.data.Notificaciones
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GestionarFirebase {

    private val baseDatos = FirebaseDatabase.getInstance()
    private val ETIQUETA = "GestionarFirebase"

    fun obtenerEstadoDispositivo(): Flow<Result<EstadoDispositivo>> = callbackFlow {
        val referencia = baseDatos.getReference("EstadoDispositivo")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val estado = snapshot.getValue(EstadoDispositivo::class.java)
                    if (estado != null) {
                        Log.d(ETIQUETA, "Estado actualizado: $estado")
                        trySend(Result.success(estado))
                    } else {
                        trySend(Result.failure(Exception("Sin datos disponibles")))
                    }
                } catch (e: Exception) {
                    Log.e(ETIQUETA, "Error procesando estado", e)
                    trySend(Result.failure(e))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ETIQUETA, "Lectura cancelada", error.toException())
                trySend(Result.failure(error.toException()))
            }
        }

        referencia.addValueEventListener(listener)

        awaitClose {
            referencia.removeEventListener(listener)
            Log.d(ETIQUETA, "listener eliminado para estadoDispositivo")
        }
    }

    fun obtenerControlarDispositivo(): Flow<Result<ControlarDispositivo>> = callbackFlow {
        val referencia = baseDatos.getReference("ControlarDispositivo")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val control = snapshot.getValue(ControlarDispositivo::class.java)
                    if (control != null) {
                        Log.d(ETIQUETA, "Control actualizado: $control")
                        trySend(Result.success(control))
                    } else {
                        trySend(Result.failure(Exception("Sin datos de control")))
                    }
                } catch (e: Exception) {
                    Log.e(ETIQUETA, "Error procesando control", e)
                    trySend(Result.failure(e))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ETIQUETA, "Lectura cancelada", error.toException())
                trySend(Result.failure(error.toException()))
            }
        }

        referencia.addValueEventListener(listener)

        awaitClose {
            referencia.removeEventListener(listener)
            Log.d(ETIQUETA, "listener eliminado para ControlarDispositivo")
        }
    }

    fun obtenerNotificaciones(): Flow<Result<Notificaciones>> = callbackFlow {
        val referencia = baseDatos.getReference("Notificaciones")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val notificacion = snapshot.getValue(Notificaciones::class.java)
                    if (notificacion != null) {
                        Log.d(ETIQUETA, "Notificación actualizada: $notificacion")
                        trySend(Result.success(notificacion))
                    } else {
                        trySend(Result.failure(Exception("Sin datos de notificaciones")))
                    }
                } catch (e: Exception) {
                    Log.e(ETIQUETA, "Error procesando notificación", e)
                    trySend(Result.failure(e))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ETIQUETA, "Lectura cancelada", error.toException())
                trySend(Result.failure(error.toException()))
            }
        }

        referencia.addValueEventListener(listener)

        awaitClose {
            referencia.removeEventListener(listener)
            Log.d(ETIQUETA, "listener eliminado para Notificaciones")
        }
    }

    suspend fun escribirControlarDispositivo(control: ControlarDispositivo): Result<Unit> {
        return try {
            val referencia = baseDatos.getReference("ControlarDispositivo")

            control.ultimaInstruccion = System.currentTimeMillis()

            referencia.setValue(control).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun dispensarAgua(cantidad: Int): Result<Unit> {
        return try {
            val referencia = baseDatos.getReference("manual_dispense")
            val comando = mapOf(
                "amount" to cantidad,
                "timestamp" to System.currentTimeMillis()
            )

            referencia.setValue(comando).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun limpiarNotificaciones(): Result<Unit> {
        return try {
            val referencia = baseDatos.getReference("Notificaciones")
            val notificacionVacia = Notificaciones()

            referencia.setValue(notificacionVacia).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
