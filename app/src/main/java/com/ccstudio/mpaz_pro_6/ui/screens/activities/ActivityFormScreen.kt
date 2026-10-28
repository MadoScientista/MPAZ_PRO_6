package com.ccstudio.mpaz_pro_6.ui.screens.activities

/**
 * Formulario de actividad.
 *
 * En modo creación pide `unidadId`, `titulo` y `tipo`; `contenidoId` y `orden` son
 * opcionales. El contenido, si se elige, debe ser de la misma unidad y la pantalla
 * filtra la lista al cambiar de unidad. El título es único dentro de la unidad y
 * solo se admite una `PRUEBA_FINAL` por unidad: el selector de tipo debe avisar
 * cuando la prueba final ya está en uso. La `PRUEBA_FINAL` se propone transversal.
 *
 * En modo edición, la pantalla distingue «no cambiar el contenido»
 * (`contenidoId = null`), «asociar» (`contenidoId = {id}`) y «volver a transversal»
 * (`desasociarContenido = true`).
 */
