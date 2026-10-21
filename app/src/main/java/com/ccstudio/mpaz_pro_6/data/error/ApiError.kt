package com.ccstudio.mpaz_pro_6.data.error

/**
 * Errores que puede devolver el backend.
 *
 * Mapea los códigos HTTP documentados en `specs/endpoints`: `400 Bad Request` a
 * `BadRequest`, `404 Not Found` a `NotFound` y `409 Conflict` a `Conflict`.
 *
 * `Conflict` es el caso más frecuente en la UI: ocurre al eliminar una unidad, un
 * objetivo o una actividad que todavía tienen elementos asociados, y al duplicar
 * nombres o códigos. Debe permitir que la pantalla muestre un mensaje que explique
 * el motivo del rechazo y no un error genérico.
 */
