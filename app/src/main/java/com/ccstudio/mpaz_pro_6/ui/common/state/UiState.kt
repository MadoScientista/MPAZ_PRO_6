package com.ccstudio.mpaz_pro_6.ui.common.state

import com.ccstudio.mpaz_pro_6.data.error.ApiError

/**
 * Estado genérico de una carga de datos.
 *
 * Modela los tres casos que la UI debe distinguir: cargando, con datos y con
 * error. El error conserva el `ApiError` para que la pantalla pueda explicar el
 * motivo (por ejemplo, un `Conflict` al eliminar) en vez de mostrar un fallo
 * genérico.
 */
sealed interface UiState<out T> {

    /** La pantalla espera los datos de un endpoint. */
    data object Loading : UiState<Nothing>

    /** La pantalla tiene datos que mostrar. */
    data class Success<out T>(val data: T) : UiState<T>

    /** La carga falló y el motivo está en [error]. */
    data class Error(val error: ApiError) : UiState<Nothing>

    /** Datos disponibles, o `null` si la pantalla está cargando o falló. */
    val dataOrNull: T?
        get() = (this as? Success)?.data
}

/** Indica si el estado corresponde a una carga en curso. */
val UiState<*>.isLoading: Boolean
    get() = this is UiState.Loading

/** Indica si el estado corresponde a un fallo. */
val UiState<*>.isError: Boolean
    get() = this is UiState.Error
