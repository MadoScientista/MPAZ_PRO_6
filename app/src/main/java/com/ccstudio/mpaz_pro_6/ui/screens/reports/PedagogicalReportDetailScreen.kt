package com.ccstudio.mpaz_pro_6.ui.screens.reports

/**
 * Detalle de un informe pedagógico.
 *
 * Consume `GET /api/informes-pedagogicos/{id}` y el detalle por objetivo de
 * aprendizaje con `GET /api/informes-pedagogicos/{id}/objetivos-aprendizaje`.
 *
 * El nivel de cada objetivo se muestra como `FORTALEZA` cuando
 * `correctas * 100.0 / total >= 70` y como `REFUERZO` en caso contrario. El docente
 * además puede validar el informe con `PATCH /api/informes-pedagogicos/{id}/validacion`.
 */
