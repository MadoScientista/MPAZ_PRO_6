# Plan de Implementación: Gestión de Informes Pedagógicos (Rol Docente)

Implementación de la construcción y gestión de **Informes Pedagógicos** para el rol de **Docente** en la vista de *Informes*, alineada con las especificaciones actualizadas `1.relaciones_modelo_mpaz.md`, `2_dtos.md` y `10_informe_pedagogico_en_backend.md`.

---

## User Review Required

> [!IMPORTANT]
> - **Migración de Base de Datos Room (Versión 5 → 6):** Se añadirá `fecha_corte` (`TEXT`) y `origen` (`TEXT`, default `'MANUAL'`) a la entidad `InformePedagogicoEntity`. Se incluirá la migración `MIGRATION_5_6` y se actualizará `seedInitialData`.
> - **Algoritmo de Cálculo Preliminar:** Se evaluarán los últimos intentos cerrados del estudiante (`fechaFin <= fechaCorte`) por actividad de la unidad. Una pregunta se considera **correcta** si todas las alternativas seleccionadas son verdaderas; **incorrecta** si al menos una seleccionada es falsa; las no respondidas se omiten. Las respuestas se desglosan por Objetivo de Aprendizaje (`OA`).
> - **Inmutabilidad del Snapshot:** Una vez emitido el informe (origen `AUTOMÁTICO` o `MANUAL`), la cabecera y el detalle por OA quedan guardados como una fotografía inalterable ante futuros intentos.

---

## Proposed Changes

### Data & Domain Layer

#### [MODIFY] [InformeEntities.kt](file:///C:/Users/Sam/Desktop/Github_MadoScientista/MPAZ_PRO_6/app/src/main/java/com/ccstudio/mpaz_pro_6/data/local/entity/informe/InformeEntities.kt)
- Añadir campos `fechaCorte: String` (ISO-8601) y `origen: String` (`"AUTOMATICO"` / `"MANUAL"`) a `InformePedagogicoEntity`.

#### [MODIFY] [InformeDtos.kt](file:///C:/Users/Sam/Desktop/Github_MadoScientista/MPAZ_PRO_6/app/src/main/java/com/ccstudio/mpaz_pro_6/data/dto/informe/InformeDtos.kt)
- Actualizar `InformePedagogicoResponse`, `InformePedagogicoRequest` y `InformePedagogicoResumen` para incluir `fechaCorte` y `origen`.
- Añadir DTOs de informe preliminar: `GenerarInformeRequest`, `InformePreliminarResponse`, `ActividadEvaluadaResumen`, `DetallePreliminarResponse`, `InformePedagogicoDetalladoResponse`.

#### [MODIFY] [MpazDatabase.kt](file:///C:/Users/Sam/Desktop/Github_MadoScientista/MPAZ_PRO_6/app/src/main/java/com/ccstudio/mpaz_pro_6/data/local/MpazDatabase.kt)
- Incrementar `version = 6` en `@Database`.
- Agregar `MIGRATION_5_6` (`ALTER TABLE informe_pedagogico ADD COLUMN fecha_corte TEXT NOT NULL DEFAULT ''`, `ALTER TABLE informe_pedagogico ADD COLUMN origen TEXT NOT NULL DEFAULT 'MANUAL'`).
- Actualizar `seedInitialData` para incluir `fecha_corte` y `origen` en inserts si aplica.

#### [MODIFY] [InformeDao.kt](file:///C:/Users/Sam/Desktop/Github_MadoScientista/MPAZ_PRO_6/app/src/main/java/com/ccstudio/mpaz_pro_6/data/local/dao/InformeDao.kt)
- Agregar clase `InformePedagogicoConDetalles` / queries para consultar informes emitidos por usuario o unidad con información completa de estudiante y unidad.
- Agregar métodos para eliminar informes y sus detalles.

---

### UI Layer & ViewModel

#### [NEW] [InformesViewModel.kt](file:///C:/Users/Sam/Desktop/Github_MadoScientista/MPAZ_PRO_6/app/src/main/java/com/ccstudio/mpaz_pro_6/ui/docente/InformesViewModel.kt)
- Gestionar carga de cursos asignados al docente (`docenciaDao.getAsignaturasCursoByDocenteId`).
- Cargar lista de estudiantes del curso seleccionado (`docenciaDao.getEstudiantesByCursoId`).
- Cargar unidades del curso/asignatura (`curriculoDao.getUnidadesByAsignaturaId`).
- Algoritmo de cálculo de informe preliminar (`calcularInformePreliminar`):
  - Obtiene actividades de la unidad.
  - Para cada actividad, busca el último intento cerrado (`fechaFin <= fechaCorte`).
  - Correlaciona respuestas con alternativas correctas/incorrectas.
  - Agrupa por `OA` (`objetivo_aprendizaje`) calculando correctas, incorrectas y porcentaje.
- Emitir informe automático (`guardarInformeDesdeResultados`) o manual.
- Eliminar informes existentes.

#### [NEW] [DocenteInformesScreen.kt](file:///C:/Users/Sam/Desktop/Github_MadoScientista/MPAZ_PRO_6/app/src/main/java/com/ccstudio/mpaz_pro_6/ui/docente/DocenteInformesScreen.kt)
- Selector de Curso, Estudiante y Unidad.
- Pestañas/Secciones:
  1. **Informes Emitidos:** Tarjetas de informes existentes con fecha, corte técnico, origen (`AUTOMÁTICO`/`MANUAL`), estudiante y unidad. Modal de desglose de detalles por OA y botón de eliminar.
  2. **Generar Informe (Vista Previa & Emisión):**
     - Botón "Generar Vista Previa" con selector de `fechaCorte`.
     - Resumen preliminar: actividades evaluadas y desgloses de rendimiento por OA (tarjetas de avance con porcentaje).
     - Formulario de fecha de emisión y observaciones/descripción.
     - Botón "Emitir / Guardar Informe".

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/Sam/Desktop/Github_MadoScientista/MPAZ_PRO_6/app/src/main/java/com/ccstudio/mpaz_pro_6/ui/home/HomeScreen.kt)
- Reemplazar `GenericSectionScreen(title = NavDestination.Informes.title)` por `DocenteInformesScreen()` cuando el usuario en rol `DOCENTE` navega a `NavDestination.Informes.route`.

---

## Verification Plan

### Automated Build Verification
- Ejecutar `gradle_build("app:compileDebugKotlin")` para verificar que los cambios en Room, DTOs, DAOs y Composables compilen sin errores.

### Manual Verification
- Autenticarse como docente (e.g. usuario `docente` / `mpaz123`).
- Ir a la pestaña **Informes**.
- Seleccionar un curso, estudiante y unidad.
- Probar la generación de vista previa preliminar.
- Emitir un informe y comprobar que aparece en la lista de informes emitidos con el tag `AUTOMÁTICO` y el desglose correcto por OA.
- Eliminar un informe emitido y verificar su actualización.
