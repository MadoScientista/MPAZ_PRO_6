package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.ApiError

/** Indica si un fallo permite responder con la caché local. */
internal fun Throwable.canUseCache(): Boolean =
    this is ApiError.Network || this is ApiError.Unknown

/** Devuelve la caché ante un fallo de red o servidor; si no hay, propaga el error. */
internal suspend fun <T> cachedValueOrError(
    error: Throwable,
    cache: suspend () -> T?
): Result<T> {
    if (!error.canUseCache()) {
        return Result.failure(error)
    }

    val cached = cache()
    return if (cached == null) {
        Result.failure(error)
    } else {
        Result.success(cached)
    }
}
