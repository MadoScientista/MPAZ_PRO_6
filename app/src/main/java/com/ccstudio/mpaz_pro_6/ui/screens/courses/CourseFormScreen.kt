package com.ccstudio.mpaz_pro_6.ui.screens.courses

/**
 * Formulario de curso.
 *
 * En modo creación pide `nombre` (hasta 50 caracteres) y `anio` (entre 2000 y 2100).
 * En modo edición (`PUT /api/cursos/{id}`) envía solo los campos modificados. El par
 * nombre-año duplicado devuelve `409 Conflict`.
 */
