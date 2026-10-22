package com.ccstudio.mpaz_pro_6.data.session

import com.ccstudio.mpaz_pro_6.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sesión local de la app.
 *
 * El backend no expone un endpoint de autenticación, así que la sesión se
 * resuelve en el dispositivo: aquí se guarda el usuario que inició sesión y su
 * rol, que es lo que decide el destino inicial de la navegación y qué acciones
 * habilita cada pantalla.
 */
class Session {

    private val _user = MutableStateFlow<User?>(null)

    /** Usuario con sesión iniciada, o `null` si no hay ninguna. */
    val user: StateFlow<User?> = _user.asStateFlow()

    /** Registra el usuario que inició sesión. */
    fun iniciar(user: User) {
        _user.value = user
    }

    /** Cierra la sesión. */
    fun cerrar() {
        _user.value = null
    }
}
