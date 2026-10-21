package com.ccstudio.mpaz_pro_6.ui.screens.units

/**
 * Detalle de una unidad.
 *
 * Consume `GET /api/unidades/{id}` y debe mostrar el contenido agrupado en objetivos
 * de aprendizaje, contenidos y actividades, con acceso al formulario de edición y al
 * de autorización.
 *
 * Las acciones disponibles dependen del `UnitStatus` y del rol del usuario: en
 * `BORRADOR` solo la UTP edita; en `AUTORIZADO` la UTP publica; en `PUBLISHED` todos
 * solo leen.
 */
