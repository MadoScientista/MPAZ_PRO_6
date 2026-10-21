package com.ccstudio.mpaz_pro_6.data.mock

/**
 * Datos en memoria que alimentan la app mientras no exista el backend.
 *
 * Debe contener una semilla por cada entidad del dominio (asignaturas, cursos,
 * unidades, objetivos de aprendizaje, contenidos, recursos, actividades, preguntas,
 * alternativas, asignaciones, intentos, respuestas, informes y usuarios) con datos
 * coherentes entre sí: por ejemplo, las actividades referencian unidades existentes
 * y los intentos referencian actividades reales.
 *
 * Es temporal: se elimina cuando `data/remote/` asuma el rol de fuente de datos.
 */
