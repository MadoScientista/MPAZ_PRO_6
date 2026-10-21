package com.ccstudio.mpaz_pro_6.ui.screens.users

/**
 * Listado de usuarios.
 *
 * Consume `GET /api/usuarios`. Debe filtrar por rol (`UserRole`) de forma client-side
 * y ofrecer el acceso a la creación y a la edición.
 *
 * El borrado siempre se ofrece, pero el backend responde `409 Conflict` si el
 * usuario tiene intentos, informes, unidades autorizadas o asignaciones.
 */
