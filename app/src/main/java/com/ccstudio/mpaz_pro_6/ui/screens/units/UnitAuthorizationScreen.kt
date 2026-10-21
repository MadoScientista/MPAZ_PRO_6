package com.ccstudio.mpaz_pro_6.ui.screens.units

/**
 * Pantalla de autorización y publicación de una unidad.
 *
 * Debe permitir pasar una unidad de `BORRADOR` a `AUTORIZADO` y de `AUTORIZADO` a
 * `PUBLISHED`, indicando el usuario autorizante (con rol `UTP`) y la fecha de
 * autorización.
 *
 * El backend exige ambos datos: una autorización incompleta o un autorizante sin
 * rol `UTP` devuelve `400 Bad Request`.
 */
