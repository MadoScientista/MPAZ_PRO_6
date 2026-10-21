package com.ccstudio.mpaz_pro_6.ui.screens.units

/**
 * Formulario de unidad.
 *
 * En modo creación (`POST /api/unidades`) pide `asignaturaId`, `titulo` y `estado`
 * (siempre `BORRADOR`); `nivelEducativo` y `orden` son opcionales y toman por
 * defecto "Sexto Basico" y 1.
 *
 * En modo edición (`PUT /api/unidades/{id}`) envía solo los campos modificados. Los
 * estados `AUTORIZADO` y `PUBLISHED` exigen un autorizante con rol `UTP` y una
 * `fechaAutorizacion`, por lo que esa parte se resuelve en la pantalla de
 * autorización.
 */
