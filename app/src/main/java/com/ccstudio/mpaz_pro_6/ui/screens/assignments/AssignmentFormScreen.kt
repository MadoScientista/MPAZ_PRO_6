package com.ccstudio.mpaz_pro_6.ui.screens.assignments

/**
 * Formulario de asignación.
 *
 * En modo creación pide `cursoId`, `unidadId`, `docenteId` y `fecha`; `maxIntentos`
 * y `pruebaFinalHabilitada` son opcionales y el backend aplica 1 y false por
 * defecto. El par curso-unidad es único.
 *
 * En modo edición (`PUT /api/asignaciones/{id}`) envía solo los campos modificados.
 */
