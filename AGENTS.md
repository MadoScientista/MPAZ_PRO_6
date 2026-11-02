# AGENTS.md - Guía de Proyecto para Agentes de IA

Este documento contiene las directrices de arquitectura, convenciones de código, flujo de trabajo y reglas del proyecto **MPAZ_PRO_6** para agentes de IA que colaboren en el desarrollo.

---

## 1. Visión General del Proyecto

- **Nombre del proyecto:** MPAZ_PRO_6
- **Package Base:** `com.ccstudio.mpaz_pro_6`
- **Tipo de Aplicación:** Android App (Jetpack Compose + Room + Retrofit)
- **SDK Objetivo:** `compileSdk` / `targetSdk` = 37, `minSdk` = 24

### 1.1. Stack Tecnológico
- **UI:** Jetpack Compose (Material3, Navigation Compose, Activity Compose)
- **Base de Datos Local:** Room DB 2.8.5 con KSP (Kotlin Symbol Processing)
- **Red & API:** Retrofit 3.0 + OkHttp 5.5 + `kotlinx.serialization`
- **Preferencias:** DataStore Preferences
- **Compatibilidad de Fechas:** `java.time` mediante Core Library Desugaring (`minSdk 24`)

---

## 2. Estructura y Áreas Funcionales (26 Entidades)

El modelo de datos y la arquitectura están divididos en **5 características principales (features)**:

| Feature | Descripción | Entidades (26) | DTOs & DAOs |
|---|---|---|---|
| **Identidad** (`identidad`) | Gestión de roles, niveles, secciones, cursos y usuarios | `Rol`, `Nivel`, `Seccion`, `Curso`, `Usuario` | `IdentidadDao`, DTOs de creación, actualización y resumen |
| **Currículo** (`curriculo`) | Gestión académica, contenidos, actividades, preguntas y recursos | `Asignatura`, `EstadoAutorizacion`, `TipoActividad`, `TipoRecurso`, `Unidad`, `ObjetivoAprendizaje`, `ObjetivoAprendizajeUnidad`, `Contenido`, `Recurso`, `Actividad`, `Pregunta`, `Alternativa`, `AlternativaPregunta`, `PreguntaActividad`, `ObjetivoAprendizajeContenido` | `CurriculoDao`, DTOs por entidad |
| **Docencia** (`docencia`) | Vinculación entre cursos, asignaturas y usuarios | `AsignaturaCurso`, `UsuarioCurso` | `DocenciaDao`, DTOs puente |
| **Evaluación** (`evaluacion`) | Registro de intentos y respuestas de actividades | `Intento`, `Respuesta` | `EvaluacionDao`, DTOs de intento y respuesta |
| **Informe** (`informe`) | Informes pedagógicos y desglose por objetivo | `InformePedagogico`, `DetalleInformePedagogico` | `InformeDao`, DTOs de informe y detalle |

---

## 3. Organización de Paquetes

```text
com.ccstudio.mpaz_pro_6/
├── data/
│   ├── dto/
│   │   ├── identidad/      # DTOs y Resúmenes de Identidad
│   │   ├── curriculo/      # DTOs y Resúmenes de Currículo
│   │   ├── docencia/       # DTOs de Docencia
│   │   ├── evaluacion/     # DTOs y Resúmenes de Evaluación
│   │   └── informe/        # DTOs y Resúmenes de Informe
│   ├── local/
│   │   ├── MpazDatabase.kt  # Base de datos Room (Versión 1, exportSchema = true)
│   │   ├── dao/
│   │   │   ├── IdentidadDao.kt
│   │   │   ├── CurriculoDao.kt
│   │   │   ├── DocenciaDao.kt
│   │   │   ├── EvaluacionDao.kt
│   │   │   └── InformeDao.kt
│   │   └── entity/
│   │       ├── identidad/  # RolEntity, NivelEntity, SeccionEntity, CursoEntity, UsuarioEntity
│   │       ├── curriculo/  # AsignaturaEntity, UnidadEntity, ContenidoEntity, ActividadEntity, etc.
│   │       ├── docencia/   # AsignaturaCursoEntity, UsuarioCursoEntity
│   │       ├── evaluacion/ # IntentoEntity, RespuestaEntity
│   │       └── informe/    # InformePedagogicoEntity, DetalleInformePedagogicoEntity
│   └── mapper/
│       └── ResumenMapper.kt # Mapeadores entre entidades y DTOs/Resúmenes
├── ui/                     # Pantallas, componentes y ViewModel en Compose
└── MainActivity.kt
```

---

## 4. Reglas Principales del Modelo y Negocio

1. **Inmutabilidad de Unidades:**
   - `Contenido.unidadId` es inmutable. Si se requiere mover un contenido, debe crearse uno nuevo.
   - `InformePedagogico.unidadId` no se puede modificar una vez emitido el informe.
2. **Restricciones de Unicidad (`UNIQUE`):**
   - `seccion`: `letra` es única.
   - `curso`: `(annio, nivel_id, seccion_id)` es único.
   - `usuario`: `nombre_usuario` es único.
   - Tablas intermedias (`asignatura_curso`, `usuario_curso`, `objetivo_aprendizaje_unidad`, `alternativa_pregunta`, `pregunta_actividad`, `objetivo_aprendizaje_contenido`, `respuesta`, `detalle_informe_pedagogico`): la combinación de claves debe ser **única**.
3. **Resúmenes anidados:**
   - Las respuestas de consulta no exponen entidades Room ni colecciones cíclicas. Usan DTOs `XxxResumen` (e.g., `CursoResumen`, `UnidadResumen`, `UsuarioResumen`).
4. **Seguridad y Passwords:**
   - `UsuarioResponse` y `UsuarioResumen` **nunca** exponen la contraseña ni `passwordHash`.
   - `UsuarioRequest` sobrescribe `toString()` para imprimir `password='***'` e impedir fugas en logs.

---

## 5. Reglas de Modificación de Código para Agentes de IA

- **HERRAMIENTAS DEL IDE:**
  - Usar siempre las herramientas quirúrgicas `write_file`, `replace_file_content` o `multi_replace_file_content`.
  - **PROHIBIDO** ejecutar comandos de consola como `sed`, `awk`, `perl` o redirecciones (`>`) para editar archivos en disco (esto desincroniza los buffers de memoria del IDE).
- **VERIFICACIÓN DE COMPILACIÓN:**
  - Tras realizar cambios significativos en Room o DTOs, validar la compilación ejecutando:
    `gradle_build("app:compileDebugKotlin")` o `gradle_build("app:assembleDebug")`.
- **CONSISTENCIA DE CONVENCIONES:**
  - Respetar los nombres de clases, entidades y paquetes definidos en la especificación (`specs/1.relaciones_modelo_mpaz.md` y `specs/2_dtos.md`).
