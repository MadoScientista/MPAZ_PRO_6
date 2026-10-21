package com.ccstudio.mpaz_pro_6.ui.screens.learningobjectives

/**
 * Formulario de objetivo de aprendizaje.
 *
 * En modo creación pide `unidadId`, `descripcion` y `eje`; `codigo` y
 * `textoReferencia` son opcionales. El código es único dentro de la unidad y su
 * duplicado devuelve `409 Conflict`.
 *
 * En modo edición (`PUT /api/objetivos-aprendizaje/{id}`) envía solo los campos
 * modificados; la unidad asociada se mantiene.
 */
