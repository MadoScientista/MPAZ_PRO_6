# AGENTS.md — MPAZ_PRO_6

Instrucciones para cualquier agente (o persona) que trabaje en este repositorio.

---

## 1. Qué es este proyecto

**MPAZ_PRO_6** es una app **Android nativa** (Kotlin + Jetpack Compose + Material 3) que consume una API REST
de planificación pedagogical (**MPAZ**), servida por un backend Spring Boot que vive **fuera de este repositorio**.

- Módulo Gradle único: `:app`
- `applicationId` / `namespace`: `com.ccstudio.mpaz_pro_6`
- Paquete raíz de código: `app/src/main/java/com/ccstudio/mpaz_pro_6/`
- `minSdk 24`, `compileSdk 37`, `targetSdk 37`, Java 11
- **No hay backend en este repo.** El único contrato disponible es `specs/endpoints`.

### Los tres roles del dominio

| Rol (wire) | Identificador en el código | Qué hace en la app |
|---|---|---|
| `UTP` | `UserRole.UTP` | Jefe de UTP. Crea asignaturas y cursos, reviza y autoriza/publica unidades, gestiona usuarios. |
| `DOCENTE` | `UserRole.DOCENTE` | Crea objetivos, contenidos, actividades y preguntas; asigna unidades a cursos; revisa informes de sus estudiantes. |
| `ESTUDIANTE` | `UserRole.ESTUDIANTE` | Resuelve actividades asignadas, registra intentos y respuestas, consulta sus informes. |

---

## 2. Comandos

```bash
./gradlew assembleDebug          # compila el APK de debug
./gradlew compileDebugKotlin     # solo compila Kotlin (check más rápido)
./gradlew test                   # tests unitarios
./gradlew lintDebug              # lint de Android
./gradlew installDebug           # instala en el dispositivo conectado
```

En Windows usar `gradlew.bat`. Todo lo que se ejecute debe ir con el *workdir* en la raíz del repo.

---

## 3. Contrato de API: `specs/endpoints`

`specs/endpoints` es la **fuente de verdad** del contrato. Antes de tocar cualquier capa de datos o cualquier
screen, leer la sección correspondiente.

Convenciones del contrato (mapply en `data/error/ApiError.kt`):

| Código HTTP | Significado | `ApiError` |
|---|---|---|
| `200 OK` / `201 Created` | Éxito | — |
| `204 No Content` | Borrado correcto | — |
| `400 Bad Request` | Validación o regla de negocio violada | `ApiError.BadRequest` |
| `404 Not Found` | Referencia inexistente | `ApiError.NotFound` |
| `409 Conflict` | Duplicado o dependencia que impide borrar | `ApiError.Conflict` |

Base URL local del backend: `http://localhost:8080`.

### Glosario: dominio español → identificador inglés

El backend y el spec usan español; **el código usa inglés**. No inventar otros nombres.

| Spec / backend | Código | Endpoint |
|---|---|---|
| `Asignatura` | `Subject` | `/api/asignaturas` |
| `Curso` | `Course` | `/api/cursos` |
| `Unidad` | `LearningUnit` | `/api/unidades` |
| `ObjetivoAprendizaje` | `LearningObjective` | `/api/objetivos-aprendizaje` |
| `Contenido` | `Content` | `/api/contenidos` |
| `RecursoContenido` | `ContentResource` | `/api/recursos-contenido` |
| `Actividad` | `Activity` | `/api/actividades` |
| `Pregunta` | `Question` | `/api/preguntas` |
| `Alternativa` | `Alternative` | `/api/alternativas` |
| `Asignacion` | `Assignment` | `/api/asignaciones` |
| `AsignacionOa` | `AssignmentObjective` | `/api/asignaciones/{id}/objetivos-aprendizaje` |
| `AsignacionActividad` | `AssignmentActivity` | `/api/asignaciones/{id}/actividades` |
| `Intento` | `Attempt` | `/api/intentos` |
| `RespuestaEstudiante` | `StudentAnswer` | `/api/intentos/{id}/respuestas` |
| `InformePedagogico` | `PedagogicalReport` | `/api/informes-pedagogicos` |
| `DetalleInformeOa` | `ReportObjectiveDetail` | `/api/informes-pedagogicos/{id}/objetivos-aprendizaje` |
| `Usuario` | `User` | `/api/usuarios` |
| `Rol` | `UserRole` | `UTP` / `DOCENTE` / `ESTUDIANTE` |
| `EstadoUnidad` / `EstadoContenido` | `ContentStatus` | `BORRADOR` / `EN_REVISION` / `AUTORIZADO` / `PUBLICADO` |
| `TipoActividad` | `ActivityType` | `ACTIVIDAD` / `DESAFIO` / `PRUEBA_FINAL` |
| `OrigenMaterial` | `ContentOrigin` | `PROPIO` / `PUBLICO` / `AUTORIZADO` |
| `Dificultad` | `Difficulty` | `BAJA` / `MEDIA` / `ALTA` |
| `TipoRecurso` | `ContentResourceType` | `IMAGEN` / `AUDIO` |
| `EstadoInforme` | `ReportStatus` | `GENERADO` / `PENDIENTE_VALIDACION` / `VALIDADO` |
| `NivelOa` | `ObjectiveLevel` | `FORTALEZA` / `REFUERZO` |

---

## 4. Arquitectura

### 4.1 Árbol de directorios

```
com/ccstudio/mpaz_pro_6/
├── MainActivity.kt
├── data/                      # CAPA DE DATOS
│   ├── model/                 # 1 data class por cada *Response del spec
│   │                          # (LearningUnit, no Unit: ver 4.3)
│   ├── enums/                 # enums del backend (nombre inglés, valor wire español)
│   ├── error/                 # ApiError
│   ├── repository/            # interfaces (contrato estable con la UI)
│   ├── mock/                  # ⚠ TEMPORAL: implementaciones en memoria
│   └── remote/                # ⚠ RESERVADO: cliente HTTP + DTOs + mappers
└── ui/                        # CAPA VISUAL
    ├── theme/                 # Color / Theme / Type (Material 3)
    ├── navigation/            # AppRoutes + AppNavGraph
    ├── common/                # piezas transversales de UI
    ├── screens/               # screens, agrupadas por dominio
    └── components/            # componentes, agrupados por dominio
```

### 4.2 Reglas de ubicación (obligatorias)

1. **Una screen vive en `ui/screens/<dominio>/`, nunca en `ui/components/`.** El nombre termina en `Screen`.
2. **Un componente vive en `ui/components/<dominio>/`.** El nombre **no** termina en `Screen`.
3. **Un componente usado por 2 o más dominios sube a `ui/components/common/`.**
4. **Un modelo de datos vive en `data/model/`, nunca dentro de `ui/`.**
5. **Una screen no habla con el repositorio.** Recibe estado por parámetro y expone `onXxx: () -> Unit`.
   Si una screen necesita estado propio persistente, usa un `ViewModel` que vive junto a ella
   (`ui/screens/<dominio>/<Dominio>ViewModel.kt`).
6. **Un componente nunca importa desde `ui/screens/`.** La dependencia va en sentido contrario.

### 4.3 Nomenclatura

- **Identificadores (clases, funciones, variables): inglés.** `UnitListScreen`, `LearningObjectiveCard`, `unitId`.
- **Comentarios, KDoc y textos visibles al usuario: español.** Es la regla que aplica a todo el repositorio.
- **Una palabra por concepto, sin abreviaturas crudas**: `Subject`, no `Subj`. `AssignmentObjective`, no `AsgOa`.
- **Nunca nombrar una clase `Unit`.** En Kotlin, importar una clase `Unit` sombrea `kotlin.Unit` y rompe
  los tipos de retorno `-> Unit` de cualquier archivo que la importe. Por eso el modelo de la unidad
  de aprendizaje se llama `LearningUnit` (las screens y componentes sí usan el prefijo `Unit`:
  `UnitListScreen`, `UnitCard`). El mismo criterio aplica a `Nothing`, `Nothing?` y `Companion`.
- **Nombres de screen = `<Entidad><Acción>Screen`**: `UnitListScreen`, `UnitFormScreen`, `UnitDetailScreen`.
- **Nombres de componente = `<Entidad><Variante>`**: `UnitCard`, `UnitForm`, `UnitStatusChip`.
- **Enums**: constante en inglés, valor wire en español (`UnitStatus.AUTHORIZED` → `"AUTORIZADO"`).

### 4.4 Contrato de datos

- `data/model/` refleja **1:1** los `*Response` del spec. Un campo del modelo = un campo de la respuesta.
- `data/enums/` es el **único** lugar donde viven los enums del dominio. Cada enum expone
  `wireValue` (el texto exacto que viaja al backend) y `fromWire(value)`, que devuelve `null`
  ante un valor desconocido para que el mapper no lance excepciones.
- `data/repository/` define una interfaz por recurso, con funciones `suspend` que devuelven `Result<T>`
  o lanzan `ApiError`. **La UI nunca conoce la implementación.**
- `data/mock/` implementa esas interfaces con datos en memoria. **Es temporal**: cuando exista el backend,
  se implementa la misma interfaz en `data/remote/` y se cambia la inyección. **La UI no cambia.**
- `data/remote/` está reservado. No se escribe hasta que exista el backend.

### 4.5 Patrones de Compose que ya rigen el repo

- Estado local de pantalla: `rememberSaveable` para filtros y textos de búsqueda.
  (Referencia: `ui/screens/units/UnitListScreen.kt`.)
- Pantallas: `@Composable fun <Name>Screen(..., modifier: Modifier = Modifier)` top-level.
  **Sin clases envoltorio** (la antigua `class Login { fun FormScreen }` queda atrás).
- Componentes: `@Composable fun <Name>(...)`, sin parámetros obligatorios de estado global.
- Actions: lambdas `onXxx: () -> Unit` al final de la lista de parámetros.
- Material 3 + `RoundedCornerShape(16.dp)` en tarjetas, `12.dp` de espaciado vertical,
  `16.dp` de padding de pantalla.
- Colores: siempre desde `MaterialTheme.colorScheme`. Nunca colores literales.

### 4.6 Reglas de UI derivadas del contrato

Estas reglas existen para que la app se comporte igual que el backend:

1. **Borrado siempre posible desde la UI, con explicación.** El `409` del spec no se puede anticipar;
   se muestra un `ConfirmDialog` y, si el backend responde `Conflict`, un mensaje que diga *por qué*
   ("no se puede eliminar: tiene objetivos asociados").
2. **Crear y editar son formularios distintos.** El `PUT` es parcial: el spec dice *"aplicando solo los
   valores enviados"*. Los `*FormScreen` de edición deben enviar **únicamente los campos modificados**.
3. **El estado de la unidad gobierna la visibilidad:**
   `DRAFT` → editable por UTP, oculta al estudiante · `IN_REVIEW` → espera la revisión de la UTP ·
   `AUTHORIZED` → visible al docente, UTP puede publicar ·
   `PUBLISHED` → visible al estudiante, solo lectura.
   El backend usa el mismo enum (`ContentStatus`) para la unidad, el contenido y el
   recurso de contenido.
4. **`Attempt` en progreso se deduce de `fechaFin == null`**, no de un campo aparte.
5. **Una `PRUEBA_FINAL` no se puede asociar como actividad** de una asignación (`400` en el spec):
   el selector debe marcarla y excluirla. Su habilitación depende de `pruebaFinalHabilitada` y `maxIntentos`.
6. **Los filtros de la UI son solo los documentados** (`?anio`, `?asignaturaId`, `?unidadId`, `?oaId`,
   `?cursoId`, `?docenteId`, `?estudianteId`, `?actividadId`, `?preguntaId`, `?contenidoId`).
   **La búsqueda por texto libre es client-side.**
7. **El nivel por OA se calcula con umbral 70 %**: `FORTALEZA` si `correctas * 100.0 / total >= 70`,
   si no `REFUERZO`.
8. **`esCorrecta` viene congelado en la respuesta** (`StudentAnswer`): no se recalcula en la app.
9. **Solo hay una alternativa correcta por pregunta** → el editor usa selección exclusiva, no múltiple.
10. **Ningún endpoint pagina.** Las listas se cargan completas: no implementar scroll infinito.

---

## 5. Brechas conocidas del contrato

Pendientes de confirmar con el backend. **No inventar valores** para estos casos:

| Gap | Consecuencia |
|---|---|
| No existe endpoint de login/auth; solo hay CRUD de `/api/usuarios` con `username`/`password`. | `LoginScreen` es UI pura; la sesión se resuelve en local. |
| No hay endpoint de "unidades pendientes de revisión". | El listado UTP filtra por `DRAFT` o `EN_REVISION` en cliente. |
| No hay listado de estudiantes por curso. | El docente ve el listado de usuarios completo. |
| Las "reglas 1, 2, 4, 7, 9, 11" del modelo no están en el repo. | Los mensajes se apoyan solo en `specs/endpoints`. |

---

## 6. Reglas de trabajo para agentes

- **No hacer commit ni push** salvo que se pida explícitamente. Si se pide, revisar antes
  `git status`, `git diff` y `git log --oneline -10`, y subir solo lo intencionado.
- **No borrar ni reescribir código funcional existente** sin motivo explícito. Hoy hay 3 pantallas
  y 2 componentes con implementación real (`LoginScreen`, `UnitListScreen`, `UnitCard`,
  `UnitStatusChip`), además de la capa de datos completa: son la referencia de estilo de la app.
- **Al crear un archivo nuevo**, dejar en él un comentario en español que describa **qué debe contener**,
  mientras la implementación no esté escrita.
- **Respetar el idioma**: identificadores en inglés, comentarios y textos de UI en español.
- **No agregar dependencias** sin avisar. Hoy faltan `navigation-compose` y `lifecycle-viewmodel-compose`,
  que se añadirán cuando se implemente la navegación real.
- **No tocar** `specs/endpoints` salvo para corregir una errata del contrato.

---

## 7. Estado actual del proyecto

La **capa de datos está completa**: los 8 enums de `data/enums/` y los 17 modelos de
`data/model/` implementan el contrato completo del backend. Faltan por escribir las interfaces de
`data/repository/`, las implementaciones en memoria de `data/mock/` y el cliente de `data/remote/`
(reservado hasta que exista el backend).

La **capa visual está esqueleto**: la estructura de directorios está completa y cada archivo documenta
su contenido previsto, pero la implementación de las pantallas nuevas está pendiente. Lo único
implementado hoy es el flujo `LoginScreen → UnitListScreen → UnitCard` (con `UnitStatusChip`),
alimentado con datos ficticios en `MainActivity`.
