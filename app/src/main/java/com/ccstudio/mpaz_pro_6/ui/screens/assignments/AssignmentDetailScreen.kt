package com.ccstudio.mpaz_pro_6.ui.screens.assignments

/**
 * Detalle de una asignación.
 *
 * Debe mostrar los objetivos de aprendizaje y las actividades asociados, obtenidos
 * con `GET /api/asignaciones/{id}/objetivos-aprendizaje` y
 * `GET /api/asignaciones/{id}/actividades`, y permitir asociar o desasociar cada uno.
 *
 * La `PRUEBA_FINAL` no se puede asociar como actividad de la asignación: el selector
 * debe marcarla y excluirla. Su habilitación depende de `pruebaFinalHabilitada` y
 * del límite `maxIntentos`.
 */
