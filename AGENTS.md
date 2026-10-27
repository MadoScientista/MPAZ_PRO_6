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

**JDK de Gradle:** la elección del JDK es por máquina y no se versiona. No definir
`org.gradle.java.home` en `gradle.properties`: una ruta absoluta (Windows o Linux) rompe el sync en
el otro sistema operativo. Se elige en Android Studio → *Build Tools → Gradle → Gradle JDK* o se
fija en `~/.gradle/gradle.properties`. Ver `README.md`.

---

## 3. Contrato de API: `specs/endpoints`

`specs/endpoints` es la **fuente de verdad** del contrato. Antes de tocar cualquier capa de datos o cualquier
screen, leer la sección correspondiente.

`specs/components.md` es el **inventario de los componentes de UI**: para cada componente ya implementado
registra cómo se llama, qué recibe, qué muestra y qué hace. Antes de crear o modificar un componente,
leerlo y mantenerlo actualizado.

Convenciones del contrato (mapply en `data/error/ApiError.kt`):

| Código HTTP | Significado | `ApiError` |
|---|---|---|
| `200 OK` / `201 Created` | Éxito | — |
| `204 No Content` | Borrado correcto | — |
| `400 Bad Request` | Validación o regla de negocio violada | `ApiError.BadRequest` |
| `404 Not Found` | Referencia inexistente | `ApiError.NotFound` |
| `409 Conflict` | Duplicado o dependencia que impide borrar | `ApiError.Conflict` |

Base URL configurable en Gradle/entorno. Default de debug: `http://10.0.2.2:8080/` (emulador).
Para dispositivo físico, definir `MPAZ_BASE_URL` con la IP LAN del backend. Las llamadas usan
`Authorization: Bearer <token>`; no se usa la cookie `JSESSIONID`.

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
├── MpazApplication.kt         # crea el AppContainer por proceso
├── MainActivity.kt            # obtiene el contenedor y monta AppNavGraph
├── data/                      # CAPA DE DATOS
│   ├── model/                 # 1 data class por cada *Response del spec
│   │   └── requests/          # cuerpos de Create* y Update* por recurso
│   ├── enums/                 # enums del backend (nombre inglés, valor wire español)
│   ├── error/                 # ApiError, ApiCall y traducción HTTP/red
│   ├── repository/            # interfaces (contrato estable con la UI)
│   ├── local/                 # Room: entidades, DAOs, mappers y repositorios
│   ├── remote/                # Retrofit: MpazApi, DTOs y mappers wire
│   ├── auth/                  # RemoteAuthRepository
│   ├── session/               # Session + SessionStore (DataStore)
│   ├── preferences/           # tema claro/oscuro (ThemeMode + ThemePreferenceStore)
│   ├── sync/                  # InitialSync del catálogo
│   ├── mock/                  # mocks en memoria conservados para tests
│   └── AppContainer.kt        # composición de todas las dependencias
└── ui/                        # CAPA VISUAL
    ├── theme/                 # Color / Theme / Type (Material 3)
    ├── navigation/            # AppRoutes + AppNavGraph
    ├── common/                # piezas transversales de UI (UiState)
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
- `data/model/requests/` contiene los cuerpos de las operaciones de escritura: `Create*Request` con los
  campos obligatorios y `Update*Request` con **todos los campos opcionales** (`null` = "no enviar", porque
  el `PUT` es parcial).
- `data/enums/` es el **único** lugar donde viven los enums del dominio. Cada enum expone
  `wireValue` (el texto exacto que viaja al backend), `label` (el texto visible en la UI) y
  `fromWire(value)`, que devuelve `null` ante un valor desconocido para que el mapper no lance
  excepciones. `wireValue` nunca se adapta para mostrar; la UI usa `label`.
- `data/repository/` define una interfaz por recurso, con funciones `suspend` que devuelven `Result<T>`.
  El fallo **siempre** es un `ApiError` (`BadRequest`, `Unauthorized`, `NotFound`, `Conflict`,
  `Network`, `Unknown`). **La UI nunca conoce la implementación.**
- `data/local/repository/` implementa las interfaces con política offline-first: lectura remota,
  `upsert` en Room y respaldo desde caché solo ante `Network` o `Unknown`. Las escrituras son
  online y nunca se encolan. Una lista vacía del backend no se reemplaza por caché.
- `data/local/` es el único lugar con entidades Room. Los modelos de dominio no se anotan para Room.
- `data/remote/` contiene `MpazApi`, `ApiClient`, DTOs wire y mappers. No se usa desde la UI.
- `data/mock/` se conserva para tests y previews; `AppContainer` no lo inyecta en la app.
- `data/auth/` y `data/session/` manejan `/api/auth/*`, el token JWT en DataStore y el estado de
  sesión (`Loading`, `Authenticated`, `Unauthenticated`). `Session` expone `StateFlow<SessionState>`.
- `data/preferences/` guarda preferencias ajenas a la sesión. `ThemePreferenceStore` persiste
  `SYSTEM`, `LIGHT` o `DARK` en su propio DataStore para sobrevivir a logout y reset.
- `data/AppContainer.kt` es el **único** lugar donde se decide la implementación de cada repositorio.
  No se usa un contenedor de inyección externo (Hilt, Koin).
- La contraseña nunca forma parte del modelo `User` ni de la caché local. Solo existe en el cuerpo
  del login y en `MockStore.passwords` para pruebas.

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
11. **Offline-first solo para lectura.** Si el remoto falla con `Network` o `Unknown`, la UI recibe
    la caché Room si existe; si no, recibe el `ApiError` original. Escrituras siempre online.
12. **La sesión manda la navegación.** `AppNavGraph` no muestra login con sesión válida. Un `401`
    limpia el token y vuelve a login; un fallo de red al validar `/auth/me` conserva la sesión.
13. **Caché compartida por usuario.** Logout, reset y cambio de usuario limpian la base local;
    `InitialSync` reemplaza el catálogo de forma transaccional tras autenticar.

---

## 5. Brechas conocidas del contrato

Pendientes de confirmar con el backend. **No inventar valores** para estos casos:

| Gap | Consecuencia |
|---|---|
| Los endpoints `/api/auth/*` no aparecen en `specs/endpoints`; están descritos en `specs/implementacion-sqlite.md`. | La app usa login JWT, `/auth/me` y logout; si cambia el contrato, actualizar DTOs y `RemoteAuthRepository`. |
| No hay endpoint de "unidades pendientes de revisión". | El listado UTP filtra por `DRAFT` o `EN_REVISION` en cliente. |
| No hay listado de estudiantes por curso. | El docente ve el listado de usuarios completo. |
| Las "reglas 1, 2, 4, 7, 9, 11" del modelo no están en el repo. | Los mensajes se apoyan solo en `specs/endpoints`. |
| `InitialSync` descarga el catálogo completo sin distinguir rol. | Si el backend restringe algún endpoint, la sync falla y se conserva la caché anterior. |

---

## 6. Reglas de trabajo para agentes

- **No hacer commit ni push** salvo que se pida explícitamente. Si se pide, revisar antes
  `git status`, `git diff` y `git log --oneline -10`, y subir solo lo intencionado.
- **No borrar ni reescribir código funcional existente** sin motivo explícito. Hoy hay 5 pantallas
  y 13 componentes con implementación real (el vertical slice UTP: `LoginScreen`, `HomeUTPScreen`,
  `UnitListScreen`, `UnitDetailScreen`, `UnitAuthorizationScreen`, más `UnitCard`, `UnitStatusChip`,
  `UnitSummaryHeader` y las piezas de `ui/components/common/`), además de la capa de datos completa:
  son la referencia de estilo de la app.
- **Al crear un archivo nuevo**, dejar en él un comentario en español que describa **qué debe contener**,
  mientras la implementación no esté escrita.
- **Mantener `specs/components.md` al día.** Al implementar, cambiar o eliminar un componente,
  actualizar su sección en el inventario.
- **Respetar el idioma**: identificadores en inglés, comentarios y textos de UI en español.
- **No agregar dependencias** sin avisar. Ya están `navigation-compose` y `lifecycle-viewmodel-compose`
  (navegación y ViewModels); cualquier otra debe avisarse antes.
- **No tocar** `specs/endpoints` salvo para corregir una errata del contrato.
- **No versionar configuración de máquina**: `.idea/`, `local.properties` y rutas absolutas
  (JDK, SDK, dispositivos) no se commitean. Cada IDE las regenera; ver `README.md`.

---

## 7. Estado actual del proyecto

La **capa de datos está completa**: los 8 enums, los 17 modelos, los 24 cuerpos de request y las
14 interfaces implementan el contrato. Room tiene las 17 entidades, 7 DAOs, conversores, mappers,
semilla desde `assets/database/seed.sql` e índice parcial de `PRUEBA_FINAL`. Las 14 interfaces
tienen implementación offline-first en `data/local/repository/`; `AppContainer` las expone todas
junto con `SessionStore`, `Session`, `AuthRepository`, `InitialSync` y el cliente Retrofit.

La **autenticación es real**: `LoginViewModel` usa `/api/auth/login`, el token persiste en DataStore,
`Session` restaura y valida con `/auth/me`, un `401` limpia la sesión y un fallo de red conserva la
sesión. Logout y reset limpian la caché; el reset solo se muestra en debug.

La **capa visual mantiene el vertical slice UTP**: `LoginScreen → HomeUTPScreen → UnitListScreen →
UnitDetailScreen → UnitAuthorizationScreen`, ahora con datos del backend y respaldo en Room. El
docente aterriza en `HomeTeacherScreen`, con sus cursos, unidades asignadas, actividades e informes
pendientes; el estudiante aterriza en `HomeStudentScreen` con sus unidades y actividades pendientes.
El docente ya cuenta con una **barra de navegación inferior** (`AppBottomBar`) con cuatro destinos:
Inicio, Unidades, Asignaciones e Informes. El UTP ya muestra sus cinco destinos: Inicio, Unidades,
Asignaturas, Cursos y Usuarios, con CRUD completo en Asignaturas, Cursos y Usuarios (crear, editar y
borrar con confirmación y explicación del `409 Conflict`). El estudiante tiene cuatro destinos:
Inicio, Unidades, Actividades e Informes. Desde Inicio puede iniciar o continuar un intento y ver su
resultado; en el detalle de una unidad ve el resumen, los contenidos y las actividades. La UTP y el
docente ven todo el material en modo lectura; el estudiante ve solo contenidos publicados y
actividades asignadas, y puede resolverlas. La autorización solo se ofrece a la UTP en `EN_REVISION`
o `AUTORIZADO`. En Actividades ve el estado de cada una y accede a su resultado; en Informes consulta
sus informes pedagógicos y el detalle por objetivo de aprendizaje en modo lectura. Todas las
pantallas reales exponen el botón de tema claro/oscuro (`ThemeToggleButton`), cuya elección persiste
en `data/preferences/`.
El resto de pantallas sigue como esqueleto documentado.

Los tests JVM cubren mocks, conversores, mappers DTO, errores HTTP/red y fallback de caché. Las
pruebas instrumentadas de Room cubren FK, únicos, PK compuesta, índice parcial, transacción y seed;
las pruebas de UI de Compose cubren el cambio de alternativa, el scroll del Inicio y las acciones de
la tarjeta de informe. Todas las pruebas instrumentadas requieren emulador o dispositivo y todavía
no se ejecutaron localmente.
