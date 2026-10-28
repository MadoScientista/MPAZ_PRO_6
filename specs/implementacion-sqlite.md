# Implementación de SQLite en MPAZ_PRO_6

Documento de contexto y plan para incorporar persistencia local con SQLite (Room)
y consumir el backend MPAZ ya funcional.

No modifica el contrato de `specs/endpoints`; ese archivo sigue siendo la fuente
de verdad de la API.

---

## 1. Contexto actual

La app es Android nativa (Kotlin + Jetpack Compose + Material 3) y hoy funciona
contra repositorios mock en memoria:

- `data/AppContainer.kt` construye un `MockStore` y cuatro repositorios mock:
  asignaturas, cursos, unidades y usuarios.
- `data/mock/MockStore.kt` mantiene listas en memoria y contraseñas sembradas.
- `data/remote/` y `data/remote/dtos/Responses.kt` son stubs reservados.
- No hay cliente HTTP ni serialización JSON.
- No hay base de datos local.
- `MainActivity.kt` crea `AppContainer()` sin `Context`.
- `Session.kt` guarda el usuario solo en memoria; se pierde al cerrar el proceso.
- `LoginViewModel.kt` autentica contra `UserRepository.authenticate()`, que hoy
  resuelve contra la semilla en memoria.
- `AppNavGraph.kt` siempre arranca en `LOGIN` y muestra credenciales demo.

El backend ya está operativo y expone:

- CRUD completo documentado en `specs/endpoints`.
- Autenticación nueva:

| Método | Ruta | Recibe | Retorna |
|---|---|---|---|
| `POST` | `/api/auth/login` | `{"username":"...","password":"..."}` | `200 OK` + `token`, `tokenType`, `expiresIn`, `usuario`; crea cookie `JSESSIONID`. Credenciales inválidas: `401` |
| `GET` | `/api/auth/me` | Cookie, `Authorization: Bearer <token>` o Basic | `200 OK` + `UsuarioResponse`; sin autenticación: `401` |
| `POST` | `/api/auth/logout` | Sin cuerpo | `204 No Content`; limpia la sesión HTTP. El JWT sigue válido hasta expirar |

Además, en `app/src/main/java/com/ccstudio/mpaz_pro_6/utils/V1__esquemas.sql`
está el DDL de referencia de las 17 tablas del backend, adaptado a SQLite:

- Enums como `varchar` + `CHECK`.
- Booleanos como `0/1`.
- Timestamps como epoch en milisegundos.
- FK simples y compuestas.
- Índices únicos, `CHECK` e índice parcial para una única `PRUEBA_FINAL` por unidad.

Ese archivo está dentro del árbol de fuentes Java, aunque Gradle lo ignora por no
ser `.kt`. Debe moverse a una ubicación de referencia fuera del código compilado.

---

## 2. Decisiones tomadas

| Tema | Decisión |
|---|---|
| Papel de SQLite | Caché offline del backend. Lecturas desde caché con respaldo remoto; escrituras online, sin cola de pendientes |
| Tecnología local | Room |
| Autenticación | Endpoints reales `/api/auth/*` con JWT `Bearer` |
| Stack de red | Retrofit + OkHttp + kotlinx.serialization |
| Persistencia del token | DataStore Preferences |
| Tablas | Las 17 entidades del contrato |
| Sincronización inicial | Catálogo + datos del usuario; asociaciones, intentos, respuestas e informes se cargan de forma lazy por pantalla |
| Base URL | `BuildConfig` configurable, default de emulador `http://10.0.2.2:8080/` |

### Fuera de alcance en esta entrega

- Cola de escrituras offline y resolución de conflictos.
- Sincronización incremental o por delta (el backend no expone endpoints para eso).
- Paginación (el contrato indica que ningún endpoint pagina).
- Cifrado del token en reposo (se usará DataStore Preferences, no cifrado).
- Cambios en `specs/endpoints`.

---

## 3. Requisitos técnicos

### 3.1 Dependencias

Agregar en `gradle/libs.versions.toml` y `app/build.gradle.kts`:

| Artefacto / plugin | Uso |
|---|---|
| `com.google.devtools.ksp` | Procesador de anotaciones para Room |
| `androidx.room:room-runtime` | Runtime de Room |
| `androidx.room:room-ktx` | Corrutinas, `Flow` y transacciones |
| `androidx.room:room-compiler` (KSP) | Generación de DAOs y esquema |
| `androidx.room:room-testing` (`androidTest`) | Pruebas de esquema y migraciones |
| `org.jetbrains.kotlin.plugin.serialization` | Serialización JSON |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | JSON de la API |
| `com.squareup.retrofit2:retrofit` | Cliente HTTP |
| Conversor Retrofit para kotlinx.serialization | Conversión JSON |
| `com.squareup.okhttp3:okhttp` / `logging-interceptor` | Interceptores y logs de debug |
| `androidx.datastore:datastore-preferences` | Persistencia de token y sesión |

Las versiones exactas se fijan al implementar, verificando compatibilidad con:

- Kotlin `2.2.10`
- AGP `9.2.1`
- `compileSdk 37`, `minSdk 24`

Si KSP no tiene una versión compatible con AGP 9 / Kotlin 2.2.10, se debe resolver
antes de continuar; no se debe degradar Room silenciosamente.

### 3.2 Configuración de Gradle

- Activar `buildFeatures.buildConfig = true`.
- Definir `MPAZ_BASE_URL` desde una property de Gradle o variable de entorno.
- Default de debug: `http://10.0.2.2:8080/`.
- Configurar `ksp { arg("room.schemaLocation", "$projectDir/schemas") }`.
- Versionar el esquema exportado de Room y mantener `exportSchema = true`.

### 3.3 Manifest y red

- Agregar `<uses-permission android:name="android.permission.INTERNET" />`.
- Permitir HTTP en debug únicamente:
  - `src/debug/AndroidManifest.xml` con `usesCleartextTraffic="true"`, o
  - `network_security_config` limitado a `10.0.2.2`, `localhost` y la IP LAN usada.
- Release debe exigir HTTPS.
- La URL debe poder sobreescribirse para dispositivo físico, por ejemplo
  `http://192.168.1.50:8080/`.

### 3.4 Contexto de aplicación

Room y DataStore necesitan `Context`. Crear una `Application`:

```text
MpazApplication : Application
    val container: AppContainer by lazy { AppContainer(applicationContext) }
```

Registrarla en `AndroidManifest.xml` y hacer que `MainActivity` use
`(application as MpazApplication).container`. Esto garantiza una sola instancia
de base de datos y evita crear el contenedor dentro de `remember`.

---

## 4. Arquitectura propuesta

```text
com/ccstudio/mpaz_pro_6/
├── MpazApplication.kt
├── data/
│   ├── local/
│   │   ├── MpazDatabase.kt
│   │   ├── Converters.kt
│   │   ├── entity/              # 17 entidades Room
│   │   ├── dao/                 # DAOs por dominio
│   │   ├── mapper/              # entidad <-> modelo de data/model
│   │   └── repository/          # implementaciones offline-first
│   ├── remote/
│   │   ├── MpazApi.kt           # interfaz Retrofit
│   │   ├── ApiClient.kt         # OkHttp, JSON, base URL, interceptores
│   │   ├── dto/                 # respuestas, requests y auth
│   │   └── mapper/              # DTO <-> modelo
│   ├── session/
│   │   ├── Session.kt           # estado de sesión expuesto a la UI
│   │   └── SessionStore.kt      # persistencia DataStore
│   ├── sync/
│   │   └── InitialSync.kt       # sincronización inicial del catálogo
│   ├── repository/
│   │   └── AuthRepository.kt    # login, me, logout
│   └── AppContainer.kt          # composición de todas las dependencias
└── ui/
    └── screens/auth/            # LoginViewModel usa AuthRepository
```

Reglas:

- Los modelos de `data/model/` no se modifican para Room.
- Las entidades Room viven solo en `data/local/entity/`.
- Los repositorios mock se conservan para tests y previews, pero `AppContainer`
  deja de inyectarlos.
- La UI sigue consumiendo las interfaces de `data/repository/`; no se cambia la
  firma de `Result<T>` ni el tipo `List<T>`.
- Se mantiene un solo `AppContainer` por proceso.

---

## 5. Modelo local

### 5.1 Mapeo de tablas

Los nombres de clase son en inglés y los nombres de tabla/columna replican el SQL
del backend en español mediante `@Entity(tableName = ...)` y
`@ColumnInfo(name = ...)`.

| Tabla SQL | Entidad Room | Modelo de dominio |
|---|---|---|
| `curso` | `CourseEntity` | `Course` |
| `usuario` | `UserEntity` | `User` |
| `asignatura` | `SubjectEntity` | `Subject` |
| `unidad` | `LearningUnitEntity` | `LearningUnit` |
| `objetivo_aprendizaje` | `LearningObjectiveEntity` | `LearningObjective` |
| `contenido` | `ContentEntity` | `Content` |
| `recurso_contenido` | `ContentResourceEntity` | `ContentResource` |
| `actividad` | `ActivityEntity` | `Activity` |
| `pregunta` | `QuestionEntity` | `Question` |
| `alternativa` | `AlternativeEntity` | `Alternative` |
| `asignacion` | `AssignmentEntity` | `Assignment` |
| `asignacion_oa` | `AssignmentObjectiveEntity` | `AssignmentObjective` |
| `asignacion_actividad` | `AssignmentActivityEntity` | `AssignmentActivity` |
| `intento` | `AttemptEntity` | `Attempt` |
| `respuesta_estudiante` | `StudentAnswerEntity` | `StudentAnswer` |
| `informe_pedagogico` | `PedagogicalReportEntity` | `PedagogicalReport` |
| `detalle_informe_oa` | `ReportObjectiveDetailEntity` | `ReportObjectiveDetail` |

### 5.2 Conversores

`Converters.kt` debe manejar:

- `Instant` ↔ `Long` (epoch en milisegundos, coherente con el SQL).
- `ContentStatus`, `ActivityType`, `ContentOrigin`, `ContentResourceType`,
  `Difficulty`, `ObjectiveLevel`, `ReportStatus`, `UserRole` ↔ `String` usando
  `wireValue` y `fromWire`.

`fromWire` devuelve `null` ante valores desconocidos; el mapper debe decidir si
descarta la fila o la reporta, sin lanzar una excepción global.

### 5.3 Ajustes respecto de `V1__esquemas.sql`

El SQL es la referencia, pero la copia local necesita estos ajustes:

1. **Omitir `usuario.password_hash`.** El backend nunca lo devuelve y el login es
   remoto. Cachear hashes o contraseñas en la app no aporta y es un riesgo.
2. **Agregar únicos que exige el contrato y no están en el script:**
   - `informe_pedagogico(estudiante_id, unidad_id)`: un informe por estudiante y
     unidad (`409 Conflict` al duplicar).
   - `actividad(unidad_id, titulo)`: título único dentro de la unidad.
3. **`detalle_informe_oa`:** la entidad necesita `id` e `informe_id`; el modelo
   `ReportObjectiveDetail` no los expone. El mapper los omite al dominio.
4. **PK compuestas:** `asignacion_oa` y `asignacion_actividad` usan
   `primaryKeys = ["asignacion_id", "oa_id"]` y
   `primaryKeys = ["asignacion_id", "actividad_id"]`.
5. **Índice parcial de `PRUEBA_FINAL`:** Room no lo expresa con anotaciones. Se
   crea con SQL crudo en `SeedLoader.createIndexes`, invocado desde
   `RoomDatabase.Callback.onCreate` (base nueva) y `onOpen` (cada apertura,
   incluida una base migrada y validada):

   ```sql
   CREATE UNIQUE INDEX ux_actividad_prueba_final_por_unidad
       ON actividad (unidad_id)
       WHERE tipo = 'PRUEBA_FINAL';
   ```

6. **`CHECK` no expresables en Room:** las reglas de autorización de unidad,
   curso solo para estudiantes, fechas de intento, una alternativa correcta por
   pregunta, etc., se validan en repositorios/sync. Si se exige integridad total
   a nivel de BD, se pueden agregar triggers en el callback, con el costo de
   mantener SQL paralelo.
7. **Borrado en cascada:** el script no declara `ON DELETE CASCADE` para
   `recurso_contenido`; el backend lo resuelve en el Service. El repositorio
   local debe borrar los recursos antes que su contenido.
8. **Índices de FK:** Room advierte si una columna FK no está indexada. Se deben
   declarar índices equivalentes a los de la sección 7 del SQL.
9. **Relación actividad ↔ contenido:** `contenido` agrega `unidad_id`, derivada
   del objetivo de aprendizaje (nunca la elige ni la envía la app), y `actividad`
   agrega `contenido_id` opcional. `contenido_id` nulo significa que la actividad
   es transversal a la unidad. La integridad se refuerza con dos FK compuestas:
   `contenido(oa_id, unidad_id) → objetivo_aprendizaje(id, unidad_id)` y
   `actividad(contenido_id, unidad_id) → contenido(id, unidad_id)`. Ambas usan
   `ON DELETE RESTRICT`: un contenido con actividades no se puede borrar y una
   actividad no puede apuntar a un contenido de otra unidad.
   Como `contenido_id` admite `NULL`, la FK no se comprueba en las actividades
   transversales.
10. **Borrado de contenido con actividades:** el backend responde `409 Conflict`
    y el repositorio local lo replica contando las actividades asociadas antes de
    borrar. El repositorio de actividades traduce el contenido de otra unidad y la
    asociación simultánea con `desasociarContenido` a `ApiError.Conflict` y
    `ApiError.BadRequest` respectivamente.

### 5.4 Migración 1 → 2

La versión 2 de `MpazDatabase` agrega `contenido.unidad_id` y
`actividad.contenido_id`. SQLite no permite agregar una columna `NOT NULL` con FK
ni una FK compuesta con `ALTER TABLE`, así que `MIGRATION_1_2`:

1. Respaldar y borrar las tablas hijas de hoja a raíz
   (`respuesta_estudiante`, `intento`, `asignacion_actividad`, `alternativa`,
   `pregunta`, `recurso_contenido`), porque `ON DELETE RESTRICT` no se puede
   diferir dentro de la transacción exclusiva de Room.
2. Reconstruir `contenido` copiando `unidad_id` desde su objetivo de aprendizaje.
3. Reconstruir `actividad` con `contenido_id = NULL` en todas las filas
   existentes: la versión 1 no guardaba asociaciones y la migración no inventa
   datos.
4. Volver a crear las tablas hijas y restaurar sus filas.
5. Validar con `PRAGMA foreign_key_check`.

El índice parcial `ux_actividad_prueba_final_por_unidad` **no** se crea dentro de
la migración: Room valida el esquema después de `migrate()` y un índice extra que
no está declarado en `ActivityEntity` hace fallar la validación
(`Migration didn't properly handle: actividad`). Se crea en
`SeedLoader.createIndexes`, invocado desde `Callback.onOpen`, que corre después
de la validación y en cada apertura. La operación es idempotente
(`CREATE UNIQUE INDEX IF NOT EXISTS`).

El esquema exportado vive en
`app/schemas/com.ccstudio.mpaz_pro_6.data.local.MpazDatabase/{1,2}.json` y se
publica como asset de `androidTest` para que `MigrationTest` pueda crear la
versión 1 y validar la 2.

### 5.5 DAOs

Agrupar por dominio para evitar 17 archivos de DAO:

- `CatalogDao`: asignaturas y cursos.
- `UserDao`: usuarios.
- `UnitDao`: unidades.
- `CurriculumDao`: objetivos, contenidos, recursos, actividades, preguntas y
  alternativas.
- `AssignmentDao`: asignaciones y sus dos asociaciones.
- `AttemptDao`: intentos y respuestas.
- `ReportDao`: informes y detalles.

Cada DAO necesita, como mínimo:

- `getById(id)`.
- Listados con filtros por FK (`unidad_id`, `oa_id`, `contenido_id`, etc.).
- `upsert(entity)` / `upsertAll(entities)`.
- `replaceAll(entities)` dentro de una transacción.
- `deleteAll()` para la sincronización completa.
- Consultas específicas de la UI (por asignatura, por curso, por docente, etc.).

### 5.6 Carga de la semilla local

`app/src/main/assets/database/seed.sql` contiene el catálogo inicial.
`SeedLoader` lo ejecuta al crear la base (`RoomDatabase.Callback.onCreate`) y
cuando `AppContainer.resetLocalData()` recrea la caché local.

El script **no se puede separar con `split(';')`**: hay literales que contienen
punto y coma. Por ejemplo, el contenido 8 incluye
`'Un parque bien cuidado mantiene arboles y deja el suelo permeable; uno descuidado pierde vegetacion.'`.
Cortar por `;` lanza `SQLiteException: unrecognized token` en el primer login,
porque Room abre la base y ejecuta la semilla.

`SeedLoader.load(context, db)` debe:

1. Leer el asset como UTF-8.
2. Crear los índices crudos (incluido el parcial de `PRUEBA_FINAL`) antes de la
   semilla.
3. Dividir con `SeedLoader.splitStatements(script)` y ejecutar cada sentencia con
   `db.execSQL`.

`splitStatements` respeta:

- `;` solo fuera de cadenas.
- Cadenas delimitadas por `'` y el escape `''`.
- Comentarios `--` de línea y `/* ... */` de bloque, que no forman parte de la
  sentencia.
- Sentencias vacías o con solo espacios se descartan.

La cobertura vive en:

- `app/src/test/java/com/ccstudio/mpaz_pro_6/data/local/SeedLoaderTest.kt`:
  splitter con `;` dentro de texto, comillas escapadas, comentarios y varias
  sentencias.
- `app/src/androidTest/java/com/ccstudio/mpaz_pro_6/data/local/MpazDatabaseInstrumentedTest.kt`:
  `semillaSeCargaSobreUnaBaseNueva` verifica los conteos, la unidad derivada de
  cada contenido, la unidad compartida entre actividad y contenido y que la
  `PRUEBA_FINAL` quede transversal; además cubre la FK compuesta de la relación y
  el borrado restringido de un contenido con actividades.
- `app/src/androidTest/java/com/ccstudio/mpaz_pro_6/data/local/MigrationTest.kt`:
  crea la versión 1 con filas insertadas a mano, ejecuta `MIGRATION_1_2` y
  comprueba la unidad derivada, la transversalidad y la conservación de las
  tablas hijas.

---

## 6. Autenticación y sesión

### 6.1 Modelo

Agregar a `data/model/`:

```text
AuthSession(
    token: String,
    tokenType: String,
    expiresIn: Long,
    usuario: User,
    expiresAt: Instant
)
```

El DTO de login conserva los nombres wire: `token`, `tokenType`, `expiresIn`,
`usuario`.

### 6.2 Flujo

1. `LoginViewModel` llama a `AuthRepository.login(username, password)`.
2. Si responde `200`, `SessionStore` guarda token, tipo, expiración y usuario en
   DataStore.
3. `Session` expone `StateFlow<SessionState>` con estados
   `Loading`, `Authenticated` y `Unauthenticated`.
4. `AppNavGraph` espera el bootstrap y elige `HOME` o `LOGIN` según el estado,
   sin mostrar el login a un usuario ya autenticado.
5. Al arrancar con token guardado:
   - Si `expiresAt` ya pasó: limpiar sesión.
   - Si sigue vigente: llamar a `GET /api/auth/me`.
   - `401`: limpiar sesión.
   - Error de red: mantener la sesión y permitir modo offline con la caché.
6. Logout:
   - Intentar `POST /api/auth/logout`.
   - Limpiar DataStore, sesión y caché local, aunque el backend falle.
   - Navegar a `LOGIN`.

### 6.3 HTTP

- Interceptor de OkHttp agrega `Authorization: Bearer <token>` salvo en
  `/api/auth/login`.
- No se usa la cookie `JSESSIONID`; el JWT es suficiente.
- Un `401` en cualquier endpoint limpia la sesión y notifica a la navegación.
- El token no se revoca en el backend: al cerrar sesión solo se elimina del
  dispositivo.

### 6.4 Cambios de UI

- `LoginViewModel.factory(authRepository)`.
- `LoginScreen` deja de mostrar credenciales demo.
- `AppNavGraph` usa el estado de sesión y expone logout vía `AuthRepository`.
- Los mensajes de error de credenciales salen de `ApiError.Unauthorized` o del
  mensaje del backend.

---

## 7. Repositorios y sincronización

### 7.1 Política offline-first

Las implementaciones de `data/local/repository/` mantienen las interfaces
actuales:

- `list()` / `getById()`:
  1. Intentar la llamada remota.
  2. Si responde, hacer upsert en Room y devolver el dato.
  3. Si falla por red o error de servidor, devolver la caché si existe.
  4. Si no hay caché, devolver el `ApiError` original.
- `create()` / `update()` / `delete()`:
  1. Llamar al backend.
  2. Guardar la respuesta o aplicar el borrado en Room.
  3. Si falla, propagar el `ApiError`. No se encola nada.
- Listados con filtro: solo `upsert`, nunca borrar, para no romper FK.
- Listados sin filtro dentro de la sincronización completa: reemplazo total.

### 7.2 Sincronización inicial

`InitialSync` se ejecuta tras un login exitoso o al arrancar con sesión válida.
Descarga y reemplaza en una transacción, en este orden:

1. `curso`
2. `usuario`
3. `asignatura`
4. `unidad`
5. `objetivo_aprendizaje`
6. `contenido`
7. `recurso_contenido`
8. `actividad`
9. `pregunta`
10. `alternativa`
11. `asignacion`

Para reemplazar sin violar FK:

- Borrar en orden inverso.
- Insertar en el orden anterior.
- Usar una transacción de Room.
- Si el backend devuelve listas vacías, respetarlas: son la verdad del servidor.
- Si la sincronización falla, conservar la caché anterior y reportar el error sin
  bloquear la navegación.

Las asociaciones se cargan de forma lazy:

- `asignacion_oa` y `asignacion_actividad` al abrir una asignación.
- `intento` y `respuesta_estudiante` al abrir una actividad o un intento.
- `informe_pedagogico` y `detalle_informe_oa` al abrir informes.

Esto evita recorrer todas las asignaciones, intentos e informes en el arranque.

### 7.3 Composición en `AppContainer`

`AppContainer(context)` debe construir y exponer:

- `MpazDatabase`.
- `SessionStore` y `Session`.
- `MpazApi` / `ApiClient`.
- `AuthRepository`.
- `InitialSync`.
- Las 14 implementaciones de repositorio.
- `session`.

`MainActivity` deja de crear el contenedor y lo obtiene de `MpazApplication`.

---

## 8. Plan de trabajo

### Fase 0 — Preparación

- [ ] Resolver versiones compatibles de KSP, Room, Retrofit y serialización.
- [ ] Agregar dependencias y plugins.
- [ ] Activar `buildConfig` y `MPAZ_BASE_URL`.
- [ ] Agregar `INTERNET` y configuración de cleartext de debug.
- [ ] Crear `MpazApplication` y ajustar `MainActivity`.
- [ ] Mover `V1__esquemas.sql` a `app/schemas/reference/`.

### Fase 1 — Base local

- [ ] Crear `MpazDatabase` versión 1 con las 17 entidades.
- [ ] Implementar `Converters`.
- [ ] Crear DAOs y mappers.
- [ ] Declarar FK, PK compuestas, únicos e índices.
- [ ] Crear el índice parcial de `PRUEBA_FINAL` en el callback.
- [ ] Exportar y versionar el esquema de Room.

### Fase 2 — Red

- [ ] Crear DTOs wire de respuestas, requests y auth.
- [ ] Crear `MpazApi` con todos los endpoints de `specs/endpoints`.
- [ ] Crear `ApiClient` con JSON, base URL, logging de debug e interceptor Bearer.
- [ ] Crear mappers DTO ↔ dominio.
- [ ] Agregar `ApiError.Unauthorized` y mapear códigos HTTP/red.

### Fase 3 — Auth

- [ ] Crear `AuthRepository`.
- [ ] Extender `Session` y crear `SessionStore` con DataStore.
- [ ] Implementar bootstrap, validación con `/me`, expiración y logout.
- [ ] Actualizar `LoginViewModel`, `LoginScreen` y `AppNavGraph`.
- [ ] Manejar `401` limpiando sesión y volviendo a login.

### Fase 4 — Repositorios offline-first

- [ ] Implementar las 14 interfaces en `data/local/repository/`.
- [ ] Implementar la política de remoto → caché y fallback.
- [ ] Mantener los mocks solo para tests y previews.
- [ ] Reemplazar las inyecciones en `AppContainer`.
- [ ] Deprecar `UserRepository.authenticate` en el flujo de UI sin romper los
      tests existentes.

### Fase 5 — Sincronización

- [ ] Crear `InitialSync` con el orden de dependencias.
- [ ] Ejecutarla tras login y en arranque con sesión válida.
- [ ] Implementar carga lazy de asociaciones, intentos, respuestas e informes.
- [ ] Limpiar la caché al cerrar sesión o cambiar de usuario.

### Fase 6 — Pruebas y cierre

- [ ] Tests JVM de converters, mappers, errores y fallback de repositorios.
- [ ] Tests instrumentados de Room: FK, únicos, PK compuestas, índice parcial y
      reemplazo transaccional.
- [ ] Mantener los tests actuales de repositorios mock.
- [ ] Ejecutar `./gradlew compileDebugKotlin`, `./gradlew test`,
      `./gradlew lintDebug` y `./gradlew assembleDebug`.
- [ ] Ejecutar pruebas instrumentadas en emulador o dispositivo.
- [ ] Actualizar `AGENTS.md` para reflejar la nueva capa local, auth y red.

---

## 9. Riesgos y puntos abiertos

| Riesgo | Mitigación |
|---|---|
| KSP incompatible con AGP 9.2.1 / Kotlin 2.2.10 | Verificar versiones antes de escribir el esquema; no continuar con una combinación no soportada |
| Room no reproduce `CHECK` ni índices parciales | Índice parcial por callback; reglas restantes en repositorios/sync |
| Sincronización inicial pesada | Listas completas sin paginación; se puede dividir por rol o por demanda más adelante |
| Borrados no reflejados hasta la próxima sync completa | Reemplazo total en `InitialSync`; documentar la limitación |
| Token no cifrado en DataStore | Aceptado; migrar a Keystore/EncryptedSharedPreferences si se exige más seguridad |
| `localhost` no sirve en emulador ni dispositivo | `MPAZ_BASE_URL` configurable y default `10.0.2.2` |
| Caché compartida entre usuarios | Limpiar la base al cerrar sesión o detectar cambio de usuario |
| Semilla SQL con `;` dentro de literales | Cargar con un splitter que respeta cadenas y comentarios; nunca usar `split(';')`; cubrir con test JVM e instrumentado |
| El backend puede cambiar el contrato | `specs/endpoints` sigue siendo la fuente de verdad; actualizar DTOs y mappers con el contrato |

---

## 10. Criterio de aceptación

La implementación se considera completa cuando:

1. La app compila e inicia sin mocks en `AppContainer`.
2. El login usa `/api/auth/login` y el token sobrevive al cierre de la app.
3. Las pantallas actuales (unidades, autorización, home UTP) funcionan contra el
   backend y muestran datos cacheados si no hay red.
4. Las escrituras se reflejan en el backend y en Room.
5. Room exporta el esquema versión 1 y las pruebas de integridad pasan.
6. La semilla local carga sin errores, incluso con `;` dentro de literales.
7. `./gradlew test`, `./gradlew lintDebug` y `./gradlew assembleDebug` terminan
   correctamente.
