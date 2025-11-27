package com.dispensadoragua.repositorios

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AutenticacionFirebase {

    private val autenticacion = FirebaseAuth.getInstance()
    private val ETIQUETA_LOG = "AutenticacionFirebase"

    fun obtenerUsuarioActual(): FirebaseUser? = autenticacion.currentUser

    fun usuarioAutenticado(): Boolean = autenticacion.currentUser != null

    suspend fun iniciarSesion(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(ETIQUETA_LOG, "Intentando iniciar sesión con: $email")
            val resultado = autenticacion
                .signInWithEmailAndPassword(email, password)
                .await()

            if (resultado.user != null) {
                Log.d(ETIQUETA_LOG, "Inicio de sesión exitoso: ${resultado.user?.email}")
                Result.success(resultado.user!!)
            } else {
                Log.e(ETIQUETA_LOG, "Error: Usuario nulo después de autenticación")
                Result.failure(Exception("Error de autenticación"))
            }

        } catch (e: Exception) {
            Log.e(ETIQUETA_LOG, "Error en inicio de sesión", e)
            Result.failure(e)
        }
    }

    suspend fun registrarUsuario(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(ETIQUETA_LOG, "Intentando registrar usuario: $email")
            val resultado = autenticacion
                .createUserWithEmailAndPassword(email, password)
                .await()

            if (resultado.user != null) {
                Log.d(ETIQUETA_LOG, "Registro exitoso: ${resultado.user?.email}")
                Result.success(resultado.user!!)
            } else {
                Log.e(ETIQUETA_LOG, "Error: Usuario nulo después de registro")
                Result.failure(Exception("Error de registro"))
            }

        } catch (e: Exception) {
            Log.e(ETIQUETA_LOG, "Error en registro", e)
            Result.failure(e)
        }
    }

    suspend fun restablecerContraseña(email: String): Result<Unit> {
        return try {
            Log.d(ETIQUETA_LOG, "Enviando email de recuperación a: $email")
            autenticacion.sendPasswordResetEmail(email).await()
            Log.d(ETIQUETA_LOG, "Email de recuperación enviado")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(ETIQUETA_LOG, "Error enviando email de recuperación", e)
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        Log.d(ETIQUETA_LOG, "Cerrando sesión del usuario: ${autenticacion.currentUser?.email}")
        autenticacion.signOut()
        Log.d(ETIQUETA_LOG, "Sesión cerrada")
    }
}
