package com.ccstudio.mpaz_pro_6.data.local.repository

import android.database.sqlite.SQLiteConstraintException
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import kotlinx.coroutines.CancellationException

/** Calcula el siguiente id local a partir de los registros existentes. */
internal fun <T> List<T>.nextId(selector: (T) -> Int): Int =
    (maxOfOrNull(selector) ?: 0) + 1

/** Ejecuta una operación local y traduce sus fallos al contrato de [ApiError]. */
internal suspend fun <T> localCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: ApiError) {
        Result.failure(error)
    } catch (error: SQLiteConstraintException) {
        Result.failure(
            ApiError.Conflict(
                error.message ?: "La operación entra en conflicto con datos existentes."
            )
        )
    } catch (error: IllegalArgumentException) {
        Result.failure(ApiError.BadRequest(error.message ?: "Los datos no son válidos."))
    } catch (error: Throwable) {
        Result.failure(ApiError.Unknown(error.message ?: "Ocurrió un error local inesperado."))
    }

/** Devuelve el valor o un `NotFound` con el nombre del recurso. */
internal fun <T : Any> T?.orNotFound(resource: String): Result<T> =
    if (this == null) {
        Result.failure(ApiError.NotFound("$resource no encontrado."))
    } else {
        Result.success(this)
    }
