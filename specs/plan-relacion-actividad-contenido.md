# Plan de implementación — Relación actividad ↔ contenido

**Estado:** pendiente de implementar.
**Fuente de verdad del contrato:** `specs/endpoints` (secciones *Contenidos* y *Actividades*) y
`specs/mpaz_pro_modelo_definifivo.md` (decisión 13 y tablas `contenido` / `actividad`).

---

## 1. Objetivo

Agregar al modelo de la app la relación que ya define el backend:

```text
unidad 1 ─── N contenido
unidad 1 ─── N actividad
contenido 0..1 ─── N actividad
```

Reglas que deben quedar implementadas:

1. Todo `contenido` pertenece a una `unidad`, **copiada de la unidad de su OA**. La app nunca
   elige ni envía esa unidad: la deriva el backend y la respuesta la devuelve como `unidadId`.
2. Toda `actividad` pertenece a una `unidad` y, **opcionalmente**, a un `contenido` de esa misma
   unidad.
3. `actividad.contenidoId = null` significa **actividad transversal** de la unidad (por ejemplo,
   la `PRUEBA_FINAL`).
4. Una actividad no puede apuntar a un contenido de otra unidad. Esa regla se refuerza en la base
   local con la FK compuesta `(contenido_id, unidad_id) → contenido (id, unidad_id)`.
5. Un contenido con actividades asociadas no se puede eliminar (`409 Conflict`). Para liberarlo,
   primero hay que dejar esas actividades como transversales.

El contrato ya está actualizado; **no hay que modificar `specs/endpoints`**. El trabajo es alinear
con él la capa de datos, Room, repositorios, seed, UI y pruebas.

---

## 2. Punto de partida y brecha

| Capa | Situación actual | Cambio necesario |
|---|---|---|
| Dominio | `Content` no tiene `unidadId`; `Activity` no tiene `contenidoId` | Agregar ambos campos |
| Requests | `CreateActivityRequest` / `UpdateActivityRequest` no conocen contenido | Agregar `contenidoId` y `desasociarContenido` |
| DTO wire | `ContentDto` / `ActivityDto` no traen los campos nuevos | Agregarlos y mapearlos |
| Retrofit | `GET /api/actividades` solo acepta `unidadId` | Agregar `contenidoId` y `soloTransversales` |
| Room | `contenido` no tiene `unidad_id`; `actividad` no tiene `contenido_id` ni FK compuesta | Modificar entidades, DAOs, esquema y migración |
| Repositorios | No validan contenido de otra unidad ni filtran por contenido | Implementar reglas y filtros |
| Seed | Contenidos sin `unidad_id`; actividades sin `contenido_id` | Actualizar datos de demo |
| UI | `ActivityForm` no ofrece contenido; las tarjetas no lo muestran | Selector opcional y etiqueta de contenido/transversal |
| Pruebas | Solo cubren el modelo anterior | Agregar pruebas de mapeo, FK, filtros y migración |

---

## 3. Decisiones de diseño

### D1. `contenidoId` nulo = transversal

`Activity.contenidoId: Int?` se mantiene nulo cuando la actividad es transversal. No se crea un
booleano `esTransversal`: el contrato usa el nulo como significado y así lo refleja la FK compuesta
(con `contenido_id` nulo, SQLite no evalúa la FK).

### D2. `unidadId` del contenido es derivada y de solo lectura

`CreateContentRequest` y `UpdateContentRequest` **no** incorporan `unidadId`. La pantalla sigue
eligiendo el OA y el backend copia la unidad del OA. En modo local, `LocalContentRepository`
obtiene la unidad desde `LearningObjectiveEntity` al crear el contenido. En edición, la unidad no
cambia porque el OA no cambia.

### D3. `desasociarContenido` es la única forma de volver a transversal en edición

En el `PUT` parcial, `contenidoId = null` significa “no cambiar”. Para quitar el contenido se envía
`desasociarContenido: true`. La UI debe distinguir estos tres casos:

| Acción del usuario en edición | Cuerpo que se envía |
|---|---|
| No toca el contenido | Ni `contenidoId` ni `desasociarContenido` |
| Elige otro contenido | `contenidoId = {id}` |
| Elige “Sin contenido (transversal)” | `desasociarContenido = true` |

`contenidoId` y `desasociarContenido = true` juntos son `400 Bad Request`.

Si el usuario cambia el tipo a `PRUEBA_FINAL`, la pantalla limpia el contenido y, en edición, envía
`desasociarContenido = true` cuando la actividad tenía uno.

### D4. La `PRUEBA_FINAL` se propone siempre transversal

La UI, al elegir `PRUEBA_FINAL`, limpia el contenido y deshabilita el selector. No se agrega una
restricción de base que el contrato no declare; es una convención de la pantalla y del seed. El
repositorio local **no** normaliza el contenido: guarda el `contenidoId` que recibe, igual que el
backend, para que el modo `localOnly` no diverja del modo remoto.

### D5. La FK compuesta es la última defensa

Los repositorios validan antes de guardar para devolver mensajes claros, pero la FK compuesta de
Room evita que un dato inconsistente entre por otra vía (sync, seed o migración).

---

## 4. Cambios por capa

### 4.1 Modelo de dominio

**`data/model/Activity.kt`**

```kotlin
data class Activity(
    val id: Int,
    val unidadId: Int,
    val contenidoId: Int?,
    val titulo: String,
    val tipo: ActivityType,
    val orden: Int
)
```

Actualizar el KDoc: `contenidoId` nulo = actividad transversal; si tiene valor, el contenido es de
la misma unidad. Al no llevar valor por defecto, hay que agregar `contenidoId = null` en las
construcciones de `Activity` de pruebas y previews (por ejemplo, `HomeStudentScreenTest`).

**`data/model/Content.kt`**

```kotlin
data class Content(
    val id: Int,
    val oaId: Int,
    val unidadId: Int,
    val titulo: String,
    val explicacion: String,
    val ejemplos: String?,
    val instrucciones: String?,
    val orden: Int,
    val origen: ContentOrigin,
    val estado: ContentStatus
)
```

Actualizar el KDoc: `unidadId` es la unidad del OA, copiada por el backend, y no se edita.

### 4.2 Requests

**`data/model/requests/CreateActivityRequest.kt`**

```kotlin
data class CreateActivityRequest(
    val unidadId: Int,
    val titulo: String,
    val tipo: ActivityType,
    val contenidoId: Int? = null,
    val orden: Int? = null
)
```

**`data/model/requests/UpdateActivityRequest.kt`**

```kotlin
data class UpdateActivityRequest(
    val titulo: String? = null,
    val tipo: ActivityType? = null,
    val orden: Int? = null,
    val contenidoId: Int? = null,
    val desasociarContenido: Boolean? = null
)
```

`CreateContentRequest` y `UpdateContentRequest` **no cambian**. El `unidadId` no se envía.

### 4.3 Capa wire: DTO, API y mappers

**`data/remote/dto/RemoteDtos.kt`**

- `ContentDto`: agregar `val unidadId: Int` (obligatorio en la respuesta).
- `ActivityDto`: agregar `val contenidoId: Int? = null`.
- `CreateActivityRequestDto`: agregar `val contenidoId: Int? = null`.
- `UpdateActivityRequestDto`: agregar `val contenidoId: Int? = null` y
  `val desasociarContenido: Boolean? = null`.
- No agregar `unidadId` a `CreateContentRequestDto` ni a `UpdateContentRequestDto`.

**`data/remote/MpazApi.kt`**

```kotlin
@GET("api/actividades")
suspend fun getActivities(
    @Query("unidadId") unitId: Int? = null,
    @Query("contenidoId") contentId: Int? = null,
    @Query("soloTransversales") onlyTransversal: Boolean? = null
): List<ActivityDto>
```

El repositorio enviará `soloTransversales` solo cuando sea `true`; así no se manda `false` al
backend. `ApiClient` usa `explicitNulls = false`, por lo que los `null` de los DTO de escritura no
se serializan.

**`data/remote/mapper/DtoMappers.kt`**

- `ContentDto.toDomain()` → `unidadId = unidadId`.
- `Content.toEntity()` / `ContentEntity.toDomain()` → transportar `unidadId`.
- `ActivityDto.toDomain()` → `contenidoId = contenidoId`.
- `Activity.toEntity()` / `ActivityEntity.toDomain()` → transportar `contenidoId`.
- `CreateActivityRequest.toDto()` → `contenidoId = contenidoId`.
- `UpdateActivityRequest.toDto()` → `contenidoId = contenidoId` y
  `desasociarContenido = desasociarContenido`.

### 4.4 Room: entidades, DAOs y migración

**`data/local/entity/CurriculumEntities.kt`**

`ContentEntity`:

- Agregar `@ColumnInfo(name = "unidad_id") val unidadId: Int`.
- Mantener la FK simple `oa_id → objetivo_aprendizaje.id`.
- Agregar la FK compuesta `(oa_id, unidad_id) → objetivo_aprendizaje (id, unidad_id)`.
- Agregar índice único `(id, unidad_id)` (lo necesita la FK compuesta de `actividad`).
- Agregar índice `(unidad_id)` para consultas por unidad.
- Agregar índice `(oa_id, unidad_id)` para la FK compuesta.

`ActivityEntity`:

- Agregar `@ColumnInfo(name = "contenido_id") val contenidoId: Int?`.
- Mantener la FK `unidad_id → unidad.id`.
- Agregar la FK simple `contenido_id → contenido.id` y la FK compuesta
  `(contenido_id, unidad_id) → contenido (id, unidad_id)`, igual que `QuestionEntity` declara
  simple + compuesta.
- Agregar índices `(contenido_id)` y `(contenido_id, unidad_id)`.
- Mantener el índice único `(id, unidad_id)`, el único `(unidad_id, titulo)` y el índice parcial de
  `PRUEBA_FINAL` que crea `SeedLoader`.

`LearningObjectiveEntity` ya declara el único `(id, unidad_id)` que necesita la FK compuesta de
`contenido`; no hay que modificarlo. El backend describe la FK de `actividad` como `NO ACTION`; en
Room se usa `RESTRICT`, igual que el resto de la base, porque ambas se comprueban en el acto y
`RESTRICT` no se difiere.

**`data/local/dao/CurriculumDao.kt`**

Agregar consultas equivalentes a:

```kotlin
@Query("SELECT * FROM actividad WHERE unidad_id = :unitId AND contenido_id = :contentId ORDER BY orden")
suspend fun getActivitiesByUnitAndContent(unitId: Int, contentId: Int): List<ActivityEntity>

@Query("SELECT * FROM actividad WHERE unidad_id = :unitId AND contenido_id IS NULL ORDER BY orden")
suspend fun getTransversalActivitiesByUnit(unitId: Int): List<ActivityEntity>

@Query("SELECT COUNT(*) FROM actividad WHERE contenido_id = :contentId")
suspend fun countActivitiesByContent(contentId: Int): Int
```

`deleteContentCascade` debe seguir borrando recursos, pero **primero** el repositorio local
verificará que no haya actividades asociadas. No se debe dejar que la FK produzca un mensaje
genérico.

**Versión de base y migración**

- Subir `MpazDatabase.version` de `1` a `2`.
- Crear `MIGRATION_1_2` y registrarla con `.addMigrations(MIGRATION_1_2)` en
  `MpazDatabase.build(...)`.
- SQLite no permite agregar una columna `NOT NULL` con FK ni una FK compuesta con `ALTER TABLE`;
  hay que reconstruir `contenido` y `actividad`.

**Por qué no basta con `defer_foreign_keys`**

Room ejecuta la migración dentro de una transacción (`BEGIN EXCLUSIVE`). En SQLite:

- `PRAGMA foreign_keys = OFF` se ignora dentro de una transacción.
- `PRAGMA defer_foreign_keys = ON` difiere las comprobaciones de FK hasta el `COMMIT`, pero una
  acción `ON DELETE RESTRICT` **no es diferible**: se comprueba en el acto.
- Todas las FK de la app usan `ON DELETE RESTRICT`.

El estado de `foreign_keys` durante la migración depende de la conexión y no es un contrato
estable, así que la migración no debe depender de que estén apagadas. La forma segura es desarmar
el árbol de tablas hijas, reconstruir los padres y volver a montar las hijas.

**Procedimiento seguro de `MIGRATION_1_2`**

Todo ocurre dentro de la transacción de Room; si un paso falla, se revierte y la base queda en v1.

1. Respaldar como tablas planas y borrar de hoja a raíz. El orden importa:
   `respuesta_estudiante` → `intento` → `asignacion_actividad` → `alternativa` → `pregunta` →
   `recurso_contenido`.

   ```sql
   CREATE TABLE __migracion_respuesta_estudiante AS SELECT * FROM respuesta_estudiante;
   DROP TABLE respuesta_estudiante;
   -- repetir con intento, asignacion_actividad, alternativa, pregunta y recurso_contenido
   ```

   `CREATE TABLE ... AS SELECT` conserva nombres y valores, que es todo lo que hace falta para
   reinsertar. No conserva restricciones ni índices: se recrean al final.

2. Reconstruir `contenido`:
   - Crear `contenido_nuevo` con `unidad_id INTEGER NOT NULL`, la FK simple
     `oa_id → objetivo_aprendizaje.id` y la FK compuesta
     `(oa_id, unidad_id) → objetivo_aprendizaje(id, unidad_id)`. Los índices se crean después del
     renombre, con los nombres exactos que espera Room.
   - Copiar derivando la unidad del OA, sin inventar valores:

     ```sql
     INSERT INTO contenido_nuevo (id, oa_id, unidad_id, titulo, explicacion, ejemplos,
                                  instrucciones, orden, origen, estado)
     SELECT c.id, c.oa_id, oa.unidad_id, c.titulo, c.explicacion, c.ejemplos,
            c.instrucciones, c.orden, c.origen, c.estado
     FROM contenido AS c
     JOIN objetivo_aprendizaje AS oa ON oa.id = c.oa_id;
     ```

   - Comparar la cantidad copiada con `SELECT COUNT(*) FROM contenido` antes de borrar la tabla
     vieja; si no coincide, lanzar una excepción para que Room revierta.
   - `DROP TABLE contenido`, `ALTER TABLE contenido_nuevo RENAME TO contenido` y recrear los
     índices, incluido el único `(id, unidad_id)` que necesita la FK compuesta de `actividad`.

3. Reconstruir `actividad`:
   - Crear `actividad_nuevo` con `contenido_id INTEGER` anulable, la FK
     `unidad_id → unidad.id` y las FK de contenido `contenido_id → contenido.id` y
     `(contenido_id, unidad_id) → contenido(id, unidad_id)`. Los índices se crean después del
     renombre.
   - Copiar todas las filas con `contenido_id = NULL`. Es la única verdad disponible: v1 no tenía
     la relación, así que **no se infiere ninguna asociación**.
   - `DROP TABLE actividad`, renombrar y recrear **todos** los índices, incluido
     `ux_actividad_prueba_final_por_unidad`.

4. Recrear las hijas con el SQL exacto que Room genera en `2.json`, en orden inverso:
   `recurso_contenido` → `pregunta` → `alternativa` → `asignacion_actividad` → `intento` →
   `respuesta_estudiante`. Copiar desde las tablas `__migracion_*` y recrear sus índices con los
   nombres y definiciones de `2.json`, incluidos los únicos compuestos.

5. Borrar las tablas `__migracion_*` y ejecutar `PRAGMA foreign_key_check`; si devuelve filas,
   lanzar una excepción para revertir.

6. No llamar a `SeedLoader.load` desde la migración: duplicaría o pisaría datos del usuario. La
   semilla actualizada solo se aplica en una base nueva y en `resetLocalData()`.

- Las filas existentes de `actividad` quedan como transversales. Es el resultado seguro: la
  migración no inventa relaciones. El seed actualizado restaura las asociaciones de demo cuando se
  ejecuta `resetLocalData()`.
- Al compilar, se generará `app/schemas/com.ccstudio.mpaz_pro_6.data.local.MpazDatabase/2.json`;
  ese archivo debe commitearse junto con el cambio. Para escribir el SQL de la migración, compilar
  primero con las entidades ya cambiadas y `MIGRATION_1_2` vacía, y usar el SQL, los índices y los
  nombres de `2.json` como molde.

**Alternativa descartada: migración destructiva**

`fallbackToDestructiveMigration()` evitaría el procedimiento, pero borra la base local y no vuelve
a ejecutar `SeedCallback.onCreate`; la app quedaría con las tablas vacías hasta un
`resetLocalData()` explícito. No se adopta.

**`data/local/MpazDatabase.kt`**

Además de la versión y la migración, revisar que `SeedLoader.createIndexes` siga creando el índice
parcial de `PRUEBA_FINAL` en bases nuevas. La migración debe crear el mismo índice para bases
existentes.

### 4.5 Seed

**`app/src/main/assets/database/seed.sql`**

- `INSERT INTO contenido` debe incluir `unidad_id` y cada fila debe coincidir con la unidad de su
  OA. Ejemplo de asignación:

  | Contenido | OA | Unidad |
  |---|---|---|
  | 1, 2 | 1, 2 | 1 |
  | 3, 4 | 3, 4 | 2 |
  | 5, 6 | 5, 6 | 3 |
  | 7, 8 | 7, 8 | 4 |

- `INSERT INTO actividad` debe incluir `contenido_id`:
  - Actividad 1 → contenido 1; actividad 2 → contenido 2; actividad 3 (prueba final) → `NULL`.
  - Actividad 4 → contenido 3; actividad 5 → contenido 4; actividad 6 (prueba final) → `NULL`.
  - Actividad 7 → contenido 5; actividad 8 → contenido 6; actividad 9 (prueba final) → `NULL`.
  - Actividad 10 → contenido 7; actividad 11 → contenido 8; actividad 12 (prueba final) → `NULL`.

- Mantener el resto de datos y el orden de inserts. Un `resetLocalData()` debe cargar la relación
  nueva sin pasos manuales.

### 4.6 Repositorios

**`data/repository/ActivityRepository.kt`**

Ampliar el listado:

```kotlin
suspend fun list(
    unitId: Int? = null,
    contentId: Int? = null,
    onlyTransversal: Boolean = false
): Result<List<Activity>>
```

Documentar que `contentId` y `onlyTransversal` requieren `unitId` y que son excluyentes.

**`data/local/repository/LocalCurriculumRepositories.kt`**

`LocalContentRepository.create`:

- Buscar el OA; si no existe → `ApiError.NotFound`.
- Crear `ContentEntity` con `unidadId = objective.unidadId`.
- No aceptar una unidad enviada por la UI.

`LocalContentRepository.delete`:

- Si `dao.countActivitiesByContent(id) > 0`, devolver
  `ApiError.Conflict("No se puede eliminar: tiene actividades asociadas.")`.
- Solo después borrar recursos y contenido.

`LocalActivityRepository.create`:

- Validar que la unidad exista.
- Si `contenidoId != null`, validar que el contenido exista (`NotFound`) y que
  `content.unidadId == request.unidadId` (`Conflict`).
- Mantener las reglas de título único y de una `PRUEBA_FINAL` por unidad.
- No normalizar `contenidoId` cuando el tipo es `PRUEBA_FINAL`: la convención de transversalidad
  vive en la pantalla y en el seed, y el repositorio debe guardar lo mismo que el backend.

`LocalActivityRepository.update`:

- Si `contenidoId != null && desasociarContenido == true` → `ApiError.BadRequest`.
- Si `desasociarContenido == true` → `contenidoId = null`.
- Si `contenidoId != null` → validar existencia y misma unidad.
- Si no se envía ninguno de los dos, conservar el contenido actual.
- Mantener la validación de `PRUEBA_FINAL`.

`LocalActivityRepository.list`:

- `contentId != null && onlyTransversal` → `BadRequest`.
- `(contentId != null || onlyTransversal) && unitId == null` → `BadRequest`.
- Si `unitId` no existe → `NotFound`.
- Si `contentId` no existe → `NotFound`; si existe pero no es de la unidad → `Conflict`.
- Consultar con `getActivitiesByUnitAndContent` o `getTransversalActivitiesByUnit`.

`localCall` traduce `require` a `ApiError.BadRequest`; por eso, para que un contenido de otra unidad
o un borrado en uso devuelvan `Conflict`, hay que lanzar `ApiError.Conflict(...)` explícitamente y no
usar `require` en esos dos casos.

**`data/local/repository/OfflineFirstActivityRepository.kt`**

- Pasar los tres filtros a `api.getActivities(...)`.
- En el fallback desde Room, aplicar la misma consulta filtrada; no devolver una lista que no
  corresponda al filtro.
- `create` y `update` no necesitan lógica adicional: el backend valida; el mapeo guarda el
  resultado en Room.

**`data/local/repository/OfflineFirstContentRepository.kt`**

- No requiere cambios de lógica: el mapper transporta `unidadId` y el repositorio lo persiste.
- Verificar que el `upsert` no pierda el campo.

**`data/sync/InitialSync.kt`**

- No requiere cambios: ya descarga contenidos antes de actividades y el mapeo nuevo transporta los
  campos. Confirmar el orden de borrado/inserción para no violar la FK compuesta.

### 4.7 UI

La UI actual de creación de actividades y contenidos es un esqueleto
(`ActivityFormScreen.kt` y `ContentFormScreen.kt` solo tienen KDoc). El plan deja los componentes
listos para cuando se implementen esas pantallas.

**`ui/components/activity/ActivityForm.kt`**

Agregar parámetros:

```kotlin
contents: List<Content>,
contenidoId: Int?,
onContenidoChange: (Int?) -> Unit,
```

- Mostrar un selector con la primera opción **“Sin contenido (transversal)”**, con el patrón
  `ExposedDropdownMenuBox` que ya usan `UnitForm` y `UserForm`.
- Recibir `contents` ya filtrado por la unidad: la pantalla es quien filtra la lista y limpia
  `contenidoId` cuando cambia la unidad; el formulario no conoce la unidad.
- Deshabilitar el selector si `isReadOnly`, `isSaving` o si el tipo es `PRUEBA_FINAL`.
- Mostrar un texto de apoyo: “Una actividad transversal no depende de un contenido; la prueba final
  se propone sin contenido.”
- En modo edición, la pantalla decide entre `contenidoId` y `desasociarContenido` según la tabla de
  la decisión D3.

**`ui/components/activity/ActivityCard.kt`**

- Agregar `contentTitle: String? = null`.
- Mostrar “Contenido: {título}” cuando `contentTitle` exista; “Transversal” cuando
  `activity.contenidoId == null`; y “Contenido no disponible” cuando la actividad tenga contenido
  pero el rol no pueda verlo (por ejemplo, un contenido en borrador para el estudiante).
- Actualizar el KDoc: la actividad ya no solo trae `unidadId`.

**`ui/components/activity/StudentActivityCard.kt`**

- Opcional: agregar `contentTitle: String? = null` y mostrarlo bajo la unidad. No es obligatorio
  para compilar, pero ayuda a que el estudiante entienda el agrupamiento.

**`ui/screens/units/UnitDetailViewModel.kt`**

- Filtrar contenidos por `it.unidadId == unitId` (además de la validación por OA).
- No cambiar la lógica de visibilidad por rol: `contenidos` sigue siendo la lista que el rol puede
  ver.

**`ui/screens/units/UnitDetailScreen.kt`**

- Construir el mapa con la lista `contenidos` que ya llega filtrada por rol:
  `val contentTitles = contenidos.associate { it.id to it.titulo }`.
- Pasar `contentTitle = activity.contenidoId?.let(contentTitles::get)` a `ActivityCard`. Así el
  estudiante no ve el título de un contenido en borrador.
- Opcional (segunda iteración): agrupar visualmente las actividades por contenido, con una sección
  “Transversales” al final. La versión mínima es mostrar la etiqueta en cada tarjeta.

**`ui/screens/activities/ActivityListScreen.kt` y `ActivityFormScreen.kt`**

- Actualizar el KDoc cuando se implementen: la lista admite `unidadId`, `contenidoId` y
  `soloTransversales`; el formulario incluye el contenido opcional.
- No es necesario crear estas pantallas en esta fase si siguen como esqueleto; sí deben quedar sus
  contratos documentados.

**`ui/components/content/ContentCard.kt` y `ContentForm.kt`**

- Actualizar el KDoc: `Content` ahora trae `unidadId`, derivada del OA. La unidad no se muestra como
  campo editable ni se elige en el formulario.

**`ui/screens/contents/ContentFormScreen.kt`**

- Mantener el OA como único selector. No agregar un selector de unidad: la unidad se deriva.

### 4.8 Documentación

- Actualizar `specs/components.md`:
  - `ActivityForm`: nuevos parámetros `contents`, `contenidoId`, `onContenidoChange`; regla de
    `PRUEBA_FINAL` transversal.
  - `ActivityCard`: `contentTitle` y etiqueta de transversal.
  - `StudentActivityCard`: si se agrega `contentTitle`.
  - `ContentForm` / `ContentCard`: aclarar que `Content` ya trae `unidadId` y que la unidad no se
    elige en el formulario.
- Actualizar `specs/implementacion-sqlite.md` con:
  - `contenido.unidad_id` y `actividad.contenido_id`.
  - La FK compuesta nueva.
  - La migración `1 → 2`.
- No modificar `specs/endpoints`: ya describe la relación y los filtros.

---

## 5. Orden de implementación

### Fase 1 — Dominio y contrato wire

1. Agregar `Content.unidadId` y `Activity.contenidoId`.
2. Actualizar los requests de actividad.
3. Actualizar DTO, `MpazApi` y mappers.
4. Compilar: `./gradlew compileDebugKotlin`.

**Listo cuando:** `./gradlew compileDebugKotlin` pasa y los mapeos nuevos quedan cubiertos por las
pruebas de la fase 5.

### Fase 2 — Room, migración y seed

1. Actualizar `ContentEntity` y `ActivityEntity` con FKs e índices.
2. Agregar consultas al `CurriculumDao`.
3. Subir la versión a 2 y escribir `MIGRATION_1_2`.
4. Actualizar `seed.sql`.
5. Compilar para generar `2.json` y revisar que el esquema exportado contenga las FK compuestas.
6. Probar la migración con una base v1 real.

**Listo cuando:** una base v1 migra sin perder filas y el seed carga contenidos con unidad y
actividades con contenido/transversal.

### Fase 3 — Repositorios y reglas

1. Implementar creación/edición/borrado local con las validaciones de la sección 4.6.
2. Implementar los filtros del listado de actividades.
3. Ajustar el fallback offline-first.
4. Verificar que el modo `localOnly` y el modo remoto se comporten igual.

**Listo cuando:** crear una actividad con contenido de otra unidad devuelve un error explicable y
no guarda; borrar un contenido en uso devuelve `Conflict`.

### Fase 4 — UI y documentación

1. Extender `ActivityForm`.
2. Mostrar contenido/transversal en `ActivityCard`.
3. Resolver títulos en `UnitDetailViewModel` y pasarlos a la pantalla.
4. Actualizar `specs/components.md` e `specs/implementacion-sqlite.md`.

**Listo cuando:** la relación se puede elegir y ver sin romper las pantallas existentes.

### Fase 5 — Pruebas y cierre

1. Completar pruebas JVM e instrumentadas.
2. Ejecutar `./gradlew test` y las pruebas instrumentadas en emulador/dispositivo.
3. Revisar `git diff` y confirmar que no se tocaron `specs/endpoints` ni configuración de máquina.

---

## 6. Pruebas

### 6.1 JVM

- `DtoMappersTest`:
  - `ContentDto.toDomain()` conserva `unidadId`.
  - `Content.toEntity()` / `ContentEntity.toDomain()` conservan `unidadId`.
  - `ActivityDto.toDomain()` conserva `contenidoId` nulo y no nulo.
  - `CreateActivityRequest.toDto()` envía `contenidoId`.
  - `UpdateActivityRequest.toDto()` envía `desasociarContenido`.
- Nueva prueba de serialización (`ActivityRelationSerializationTest` o similar):
  - `UpdateActivityRequestDto(contenidoId = null, desasociarContenido = null)` no incluye ninguno de
    los dos campos en el JSON.
  - `UpdateActivityRequestDto(desasociarContenido = true)` incluye
    `"desasociarContenido":true`.
  - `CreateActivityRequestDto(contenidoId = 3)` incluye `"contenidoId":3`.
- `SeedLoaderTest` puede seguir igual; no depende de las columnas nuevas.

### 6.2 Instrumentadas (Room)

Para que `MigrationTestHelper` encuentre los esquemas exportados, agregar en `app/build.gradle.kts`:

```kotlin
sourceSets {
    getByName("androidTest").assets.srcDirs(files("$projectDir/schemas"))
}
```

Sin esa línea, la prueba de migración falla al abrir `1.json` / `2.json` desde los assets de
`androidTest`.

Actualizar las construcciones posicionales existentes de `ActivityEntity` (por ejemplo, la de
`MpazDatabaseInstrumentedTest`) para incluir `contenidoId = null` después de `unidadId`.

La prueba de migración debe insertar sus propias filas v1 por SQL. No puede reutilizar
`SeedLoader.load`, porque el `seed.sql` actualizado ya incluye `unidad_id` y `contenido_id`, columnas
que no existen en la versión 1.

Agregar a `MpazDatabaseInstrumentedTest`:

1. Insertar OA, contenido y actividad de la misma unidad → se guarda.
2. Insertar contenido con `unidad_id` distinta a la de su OA → la FK compuesta lo rechaza.
3. Insertar actividad con `contenido_id` de otra unidad → la FK compuesta la rechaza.
4. Insertar actividad con `contenido_id = NULL` → se guarda como transversal.
5. Borrar un contenido referenciado por una actividad → la FK lo rechaza.
6. Cargar el seed y verificar:
   - Todos los contenidos tienen `unidad_id` igual a la unidad de su OA.
   - Las actividades 1, 2, 4, 5, 7, 8, 10 y 11 tienen contenido; las pruebas finales tienen
     `contenido_id` nulo.
7. Crear una base v1 con datos, aplicar `MIGRATION_1_2` y verificar:
   - Las filas de `contenido` conservan su `unidad_id` derivada.
   - Las actividades existentes quedan transversales.
   - Las tablas hijas conservan sus filas y `PRAGMA foreign_key_check` no devuelve filas.
   - El índice parcial de `PRUEBA_FINAL` sigue existiendo.

### 6.3 Pruebas manuales

- `./gradlew installDebug` en modo `localOnly`.
- Crear una actividad con contenido de la misma unidad → aparece con “Contenido: …”.
- Crear una actividad sin contenido → aparece como “Transversal”.
- Intentar asociar un contenido de otra unidad → mensaje de conflicto.
- Intentar borrar un contenido con actividades → mensaje que explica el `409`.
- Hacer logout/reset y comprobar que el seed restaura las asociaciones.

---

## 7. Criterios de aceptación

- [ ] `Content` expone `unidadId` y `Activity` expone `contenidoId`.
- [ ] `CreateActivityRequest` acepta `contenidoId` opcional.
- [ ] `UpdateActivityRequest` distingue “no cambiar”, “asociar” y “desasociar”.
- [ ] `ContentDto` y `ActivityDto` mapean los campos nuevos.
- [ ] `GET /api/actividades` acepta `contenidoId` y `soloTransversales`.
- [ ] Room tiene las FK compuestas y los índices necesarios.
- [ ] La migración `1 → 2` conserva las filas y se prueba.
- [ ] El seed asigna unidad a todos los contenidos y contenido a las actividades no transversales.
- [ ] El modo local rechaza contenido de otra unidad y borrado de contenido en uso.
- [ ] La UI permite elegir contenido y muestra “Transversal” cuando no lo hay.
- [ ] `specs/components.md` e `specs/implementacion-sqlite.md` quedan actualizados.
- [ ] Pasan `./gradlew test`, `compileDebugKotlin` y las pruebas instrumentadas.

---

## 8. Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| El backend aún no devuelve `unidadId` o `contenidoId` | Coordinar el despliegue del contrato actualizado; el DTO exige `unidadId`. Validar con `GET /api/contenidos/1` antes de la integración |
| La migración de SQLite falla por las FK | No usar `defer_foreign_keys`: `RESTRICT` no se difiere. Respaldar y borrar las hijas de hoja a raíz, reconstruir `contenido` y `actividad`, recrear las hijas desde `2.json` y probar con `MigrationTestHelper` sobre una base v1 |
| Se pierden asociaciones al migrar | Es esperado: una v1 no tenía la relación. Las actividades quedan transversales y el seed/reset restaura la demo |
| El fallback offline devuelve actividades que no cumplen el filtro | Aplicar las mismas consultas filtradas en Room y probar cada combinación |
| El contenido se borra y deja actividades huérfanas | La FK `RESTRICT` + la validación explícita del repositorio devuelven `Conflict` con mensaje claro |
| La UI envía `contenidoId = null` esperando desasociar | Documentar y probar el uso de `desasociarContenido = true`; el formulario debe emitir el request correcto |
| El seed o el sync violan la FK compuesta | Cargar contenidos antes que actividades y validar cada `unidad_id` contra su OA |

---

## 9. Fuera de alcance

- No se crean endpoints nuevos ni se modifican `specs/endpoints`.
- No se cambia la relación de `recurso_contenido`, `pregunta`, `alternativa`, `intento` ni
  `informe_pedagogico`.
- No se implementa paginación.
- No se elimina el campo `estado` de `Content`; este plan solo agrega la unidad.
- No se implementan las pantallas de creación/edición completas si siguen siendo esqueleto; solo se
  dejan los componentes y contratos preparados.

---

## 10. Resumen de archivos a tocar

| Archivo | Cambio |
|---|---|
| `data/model/Activity.kt` | `contenidoId` |
| `data/model/Content.kt` | `unidadId` |
| `data/model/requests/CreateActivityRequest.kt` | `contenidoId` |
| `data/model/requests/UpdateActivityRequest.kt` | `contenidoId`, `desasociarContenido` |
| `data/remote/dto/RemoteDtos.kt` | Campos nuevos en DTO |
| `data/remote/MpazApi.kt` | Filtros de actividades |
| `data/remote/mapper/DtoMappers.kt` | Mapeos nuevos |
| `data/repository/ActivityRepository.kt` | Firma de `list` |
| `data/local/entity/CurriculumEntities.kt` | Columnas, FKs e índices |
| `data/local/dao/CurriculumDao.kt` | Consultas por unidad/contenido/transversal |
| `data/local/mapper/EntityMappers.kt` | Mapeos nuevos |
| `data/local/repository/LocalCurriculumRepositories.kt` | Reglas locales |
| `data/local/repository/OfflineFirstActivityRepository.kt` | Filtros y fallback |
| `data/local/MpazDatabase.kt` | Versión 2, migración e índice parcial |
| `app/src/main/assets/database/seed.sql` | Datos de demo actualizados |
| `ui/components/activity/ActivityForm.kt` | Selector de contenido |
| `ui/components/activity/ActivityCard.kt` | Etiqueta contenido/transversal |
| `ui/components/activity/StudentActivityCard.kt` | Etiqueta opcional |
| `ui/components/content/ContentCard.kt` / `ContentForm.kt` | KDoc de `unidadId` derivada |
| `ui/screens/units/UnitDetailViewModel.kt` | Filtro de contenidos por unidad |
| `ui/screens/units/UnitDetailScreen.kt` | Paso del título de contenido |
| `ui/screens/activities/*` | KDoc del contrato actualizado |
| `specs/components.md` | Inventario actualizado |
| `specs/implementacion-sqlite.md` | Esquema y migración documentados |
| `app/schemas/.../2.json` | Esquema Room generado y versionado |
| `app/build.gradle.kts` | Esquemas exportados como assets de `androidTest` para `MigrationTestHelper` |
| `app/src/test/.../DtoMappersTest.kt` | Pruebas de mapeo |
| `app/src/androidTest/.../MpazDatabaseInstrumentedTest.kt` | Pruebas de FK y seed |
| `app/src/androidTest/.../MigrationTest.kt` | Prueba de migración (nuevo) |
| `app/src/androidTest/.../HomeStudentScreenTest.kt` | `contenidoId = null` en la actividad de prueba |
