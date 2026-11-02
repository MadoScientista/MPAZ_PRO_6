# Capa DTO y mappers MapStruct

**Alcance:** contratos de entrada y salida de la API para las 26 entidades del
modelo, junto con los mappers que los convierten desde y hacia JPA.

**Fuentes:**
- Entidades JPA: `src/main/java/com/example/mpaz_pro_6springboot/<feature>/model/`.
- Servicios existentes: `src/main/java/com/example/mpaz_pro_6springboot/<feature>/service/`.
- Relaciones y reglas: `specs/1.relaciones_modelo_mpaz.md` y `AGENTS.md`.

La capa DTO no modifica el esquema ni los servicios. Los controladores siguen
pendientes; este documento define el contrato que deberán usar.

## 1. Estructura

Cada feature mantiene sus DTOs y mappers en su propio paquete:

```text
<feature>/
  dto/
    request/    XxxRequest, XxxUpdateRequest
    response/   XxxResponse, XxxResumen
  mapper/       XxxMapper
common/
  mapper/
    ResumenMapper      entidad -> resumen anidado
    EntityRefMapper    id -> referencia JPA (stub)
```

| Feature | Paquete |
|---|---|
| Identidad | `identidad` |
| Currículo | `curriculo` |
| Docencia | `docencia` |
| Evaluación | `evaluacion` |
| Informe | `informe` |

Se generaron **116 archivos Java**:

- 26 `XxxRequest` de creación.
- 18 `XxxUpdateRequest` (solo entidades con operación de actualización).
- 26 `XxxResponse`.
- 18 `XxxResumen` usados como objetos anidados en respuestas.
- 26 `XxxMapper`.
- 2 helpers compartidos (`ResumenMapper`, `EntityRefMapper`).

## 2. Convenciones

### 2.1. Requests

- Son `record` inmutables con Bean Validation.
- Las claves foráneas se envían como `Long` con sufijo `Id`:
  `rolId`, `nivelId`, `contenidoId`, `estadoId`, etc.
- En creación, las referencias obligatorias usan `@NotNull @Positive`.
- En actualización, todos los campos son opcionales y no llevan `@NotNull`;
  `@Positive`, `@Size` y `@PositiveOrZero` aceptan `null`.
- Los textos obligatorios usan `@NotBlank`; los catálogos limitan la longitud
  según la columna (`VARCHAR`).
- Los campos `TEXT` (`descripcion`, `enunciado`, `instrucciones`) no llevan
  `@Size`: el esquema no impone un máximo.
- Las fechas usan `java.time.Instant`.

### 2.2. Responses

- Son `record` de solo lectura; nunca incluyen `passwordHash`.
- Las relaciones se exponen como objetos anidados `XxxResumen` con `id` y los
  campos mínimos para mostrar (nombre, código, letra, etc.).
- Los resúmenes no se anidan indefinidamente: por ejemplo `CursoResumen`
  incluye `NivelResumen` y `SeccionResumen`, que son hojas.
- Las respuestas no exponen entidades JPA ni colecciones bidireccionales.

### 2.3. Update

- `XxxUpdateRequest` aplica actualización parcial:
  `NullValuePropertyMappingStrategy.IGNORE`.
- `updateEntity(request, @MappingTarget entity)` parchea solo campos escalares.
- Para flujos que pasan por los servicios existentes (que reciben una entidad
  `datos`), usar `toEntity(updateRequest)`. Ese método también crea referencias
  con solo el id para las relaciones incluidas en el update.
- Los cambios de estado se hacen con métodos explícitos del servicio, no con
  el update genérico:
  - `Usuario.activo` -> `UsuarioService.cambiarActivo`.
  - `Alternativa.correcta` -> `AlternativaService.cambiarCorrecta`.
  - `Actividad.habilitada` -> `ActividadService.cambiarHabilitada`.
  - `Intento.fechaFin` -> `IntentoService.cerrar`.
  - `InformePedagogico.unidad` no se puede cambiar; `InformePedagogicoUpdateRequest`
    no incluye `unidadId`.

## 3. MapStruct

### 3.1. Configuración

Todos los mappers usan:

```java
@Mapper(
        componentModel = "spring",
        uses = {ResumenMapper.class, EntityRefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
```

La configuración de annotation processors ya existe en `pom.xml` y respeta el
orden Lombok -> MapStruct -> `lombok-mapstruct-binding`. Los mappers se generan
en `target/generated-sources/annotations/...` y se registran como beans Spring.

### 3.2. Métodos generados

Cada `XxxMapper` expone, según corresponda:

```java
XxxResponse toResponse(Xxx entity);
List<XxxResponse> toResponseList(List<Xxx> entities);

Xxx toEntity(XxxRequest request);              // creación
Xxx toEntity(XxxUpdateRequest request);        // actualización vía servicio
void updateEntity(XxxUpdateRequest request, @MappingTarget Xxx entity);
```

### 3.3. Relaciones como referencias

`EntityRefMapper` convierte un `Long` en una entidad JPA con solo el id:

```java
Usuario usuario = usuarioDesdeId(3L);
// Usuario.builder().id(3L).build()
```

Los mappers de entrada usan estas referencias. Así los servicios existentes
pueden seguir resolviendo la entidad real con `findById` sin cambiar su firma.

> **Importante:** una referencia de `EntityRefMapper` es un stub de transporte.
> No debe persistirse directamente ni usarse fuera de una transacción que la
> resuelva contra el repositorio.

Ejemplo generado para `Contenido`:

```java
Contenido contenido = Contenido.builder()
        .descripcion(request.descripcion())
        .estado(estadoAutorizacionDesdeId(request.estadoId()))
        .unidad(unidadDesdeId(request.unidadId()))
        .build();
```

`ContenidoService.crear` resuelve `estado` y `unidad` con `findById` y construye
la entidad definitiva. En actualización, `ContenidoService.actualizar` compara
`unidadId` y rechaza el cambio porque la columna es inmutable.

### 3.4. Resúmenes centralizados

`ResumenMapper` concentra todas las conversiones entidad -> resumen para evitar
duplicarlas en cada mapper de feature. `UsuarioResumen.nombreCompleto` se arma
con las partes disponibles (`pnombre`, `snombre`, `apaterno`, `amaterno`), sin
dobles espacios.

## 4. DTOs por feature

### 4.1. Identidad

| Entidad | Creación | Actualización | Respuesta | Mapper |
|---|---|---|---|---|
| `Rol` | `RolRequest` | `RolUpdateRequest` | `RolResponse` | `RolMapper` |
| `Nivel` | `NivelRequest` | `NivelUpdateRequest` | `NivelResponse` | `NivelMapper` |
| `Seccion` | `SeccionRequest` | `SeccionUpdateRequest` | `SeccionResponse` | `SeccionMapper` |
| `Curso` | `CursoRequest` | `CursoUpdateRequest` | `CursoResponse` | `CursoMapper` |
| `Usuario` | `UsuarioRequest` | `UsuarioUpdateRequest` | `UsuarioResponse` | `UsuarioMapper` |

Campos relevantes:

- `UsuarioRequest`: datos personales, `nombreUsuario`, `password` (texto plano),
  `rolId`, `activo`. `UsuarioResponse` nunca incluye password.
- `CursoRequest`: `annio`, `nivelId`, `seccionId`.
- `CursoUpdateRequest`: los tres campos opcionales; la combinación única se
  valida en `CursoService`.

### 4.2. Currículo

| Entidad | Creación | Actualización | Respuesta | Mapper |
|---|---|---|---|---|
| `Asignatura` | `AsignaturaRequest` | `AsignaturaUpdateRequest` | `AsignaturaResponse` | `AsignaturaMapper` |
| `EstadoAutorizacion` | `EstadoAutorizacionRequest` | `EstadoAutorizacionUpdateRequest` | `EstadoAutorizacionResponse` | `EstadoAutorizacionMapper` |
| `TipoActividad` | `TipoActividadRequest` | `TipoActividadUpdateRequest` | `TipoActividadResponse` | `TipoActividadMapper` |
| `TipoRecurso` | `TipoRecursoRequest` | `TipoRecursoUpdateRequest` | `TipoRecursoResponse` | `TipoRecursoMapper` |
| `Unidad` | `UnidadRequest` | `UnidadUpdateRequest` | `UnidadResponse` | `UnidadMapper` |
| `ObjetivoAprendizaje` | `ObjetivoAprendizajeRequest` | `ObjetivoAprendizajeUpdateRequest` | `ObjetivoAprendizajeResponse` | `ObjetivoAprendizajeMapper` |
| `ObjetivoAprendizajeUnidad` | `ObjetivoAprendizajeUnidadRequest` | — | `ObjetivoAprendizajeUnidadResponse` | `ObjetivoAprendizajeUnidadMapper` |
| `Contenido` | `ContenidoRequest` | `ContenidoUpdateRequest` | `ContenidoResponse` | `ContenidoMapper` |
| `Recurso` | `RecursoRequest` | `RecursoUpdateRequest` | `RecursoResponse` | `RecursoMapper` |
| `Actividad` | `ActividadRequest` | `ActividadUpdateRequest` | `ActividadResponse` | `ActividadMapper` |
| `Pregunta` | `PreguntaRequest` | `PreguntaUpdateRequest` | `PreguntaResponse` | `PreguntaMapper` |
| `Alternativa` | `AlternativaRequest` | `AlternativaUpdateRequest` | `AlternativaResponse` | `AlternativaMapper` |
| `AlternativaPregunta` | `AlternativaPreguntaRequest` | — | `AlternativaPreguntaResponse` | `AlternativaPreguntaMapper` |
| `PreguntaActividad` | `PreguntaActividadRequest` | — | `PreguntaActividadResponse` | `PreguntaActividadMapper` |
| `ObjetivoAprendizajeContenido` | `ObjetivoAprendizajeContenidoRequest` | — | `ObjetivoAprendizajeContenidoResponse` | `ObjetivoAprendizajeContenidoMapper` |

Decisiones específicas:

- **OA multi-unidad:** `ObjetivoAprendizajeUnidadRequest` recibe
  `objetivoAprendizajeId` y `unidadId`. La pareja es única y el OA puede
  aparecer en varias unidades.
- **Contenido:** `ContenidoRequest` recibe `unidadId` y `estadoId`.
  `ContenidoUpdateRequest` acepta `unidadId` solo para que el servicio detecte
  un intento de cambio y responda con `400`; la unidad es inmutable.
- **Actividad:** el request recibe `contenidoId`; la unidad se hereda del
  contenido. `habilitada` solo se cambia con `ActividadService.cambiarHabilitada`.
- **Pregunta y Alternativa:** ambas referencian un `recursoId` obligatorio.
- **Alternativa:** `correcta` se define al crear y se cambia con
  `AlternativaService.cambiarCorrecta`.
- **Tablas puente:** no tienen update porque la pareja se crea o se elimina.
  La FK compuesta de `ObjetivoAprendizajeContenido` sigue validando que el OA y
  el contenido pertenezcan a la misma unidad.

### 4.3. Docencia

| Entidad | Creación | Actualización | Respuesta | Mapper |
|---|---|---|---|---|
| `AsignaturaCurso` | `AsignaturaCursoRequest` | — | `AsignaturaCursoResponse` | `AsignaturaCursoMapper` |
| `UsuarioCurso` | `UsuarioCursoRequest` | — | `UsuarioCursoResponse` | `UsuarioCursoMapper` |

`UsuarioCursoRequest` no distingue rol ni tipo de participación; esa regla se
valida en servicio.

### 4.4. Evaluación

| Entidad | Creación | Actualización | Respuesta | Mapper |
|---|---|---|---|---|
| `Intento` | `IntentoRequest` | — | `IntentoResponse` | `IntentoMapper` |
| `Respuesta` | `RespuestaRequest` | — | `RespuestaResponse` | `RespuestaMapper` |

- `IntentoRequest` recibe `usuarioId`, `actividadId` y fechas opcionales. El
  cierre usa `IntentoService.cerrar`; no hay update genérico.
- `RespuestaRequest` recibe `intentoId`, `preguntaId` y `alternativaId`. La FK
  compuesta de `respuesta` y las validaciones de `RespuestaService` garantizan
  que la alternativa pertenezca a la pregunta y que la pregunta pertenezca a la
  actividad del intento.

### 4.5. Informe

| Entidad | Creación | Actualización | Respuesta | Mapper |
|---|---|---|---|---|
| `InformePedagogico` | `InformePedagogicoRequest` | `InformePedagogicoUpdateRequest` | `InformePedagogicoResponse` | `InformePedagogicoMapper` |
| `DetalleInformePedagogico` | `DetalleInformePedagogicoRequest` | `DetalleInformePedagogicoUpdateRequest` | `DetalleInformePedagogicoResponse` | `DetalleInformePedagogicoMapper` |

- `InformePedagogicoRequest` recibe `usuarioId`, `unidadId`, `fecha` y
  `descripcion`. El servicio valida que el usuario tenga rol `ESTUDIANTE`.
- `InformePedagogicoUpdateRequest` no incluye `unidadId`: la unidad del informe
  no se puede cambiar.
- `DetalleInformePedagogicoUpdateRequest` solo permite `correctas` e
  `incorrectas`; el informe y el OA no se reasignan.

## 5. Resúmenes anidados

| Resumen | Campos |
|---|---|
| `RolResumen` | `id`, `nombre` |
| `NivelResumen` | `id`, `nombre` |
| `SeccionResumen` | `id`, `letra` |
| `CursoResumen` | `id`, `annio`, `nivel`, `seccion` |
| `UsuarioResumen` | `id`, `nombreUsuario`, `nombreCompleto` |
| `AsignaturaResumen` | `id`, `nombre` |
| `EstadoAutorizacionResumen` | `id`, `nombre` |
| `TipoActividadResumen` | `id`, `nombre` |
| `TipoRecursoResumen` | `id`, `nombre` |
| `UnidadResumen` | `id`, `nombre`, `asignatura` |
| `ObjetivoAprendizajeResumen` | `id`, `codigo`, `descripcion` |
| `ContenidoResumen` | `id`, `descripcion` |
| `RecursoResumen` | `id`, `nombre`, `tipoRecurso` |
| `ActividadResumen` | `id`, `instrucciones`, `habilitada` |
| `PreguntaResumen` | `id`, `enunciado` |
| `AlternativaResumen` | `id`, `enunciado`, `correcta` |
| `IntentoResumen` | `id`, `fechaInicio`, `fechaFin` |
| `InformePedagogicoResumen` | `id`, `fecha` |

## 6. Integración con servicios

### 6.1. Creación

```java
@PostMapping("/unidades")
public UnidadResponse crear(@Valid @RequestBody UnidadRequest request) {
    Unidad datos = unidadMapper.toEntity(request);
    Unidad creada = unidadService.crear(datos);
    return unidadMapper.toResponse(creada);
}
```

El mapper deja `asignatura` y `estado` como referencias con id. El servicio las
resuelve, valida y persiste; la respuesta se arma dentro de la transacción para
poder navegar las relaciones lazy.

### 6.2. Actualización

```java
@PutMapping("/unidades/{id}")
public UnidadResponse actualizar(
        @PathVariable Long id,
        @Valid @RequestBody UnidadUpdateRequest request) {
    Unidad cambios = unidadMapper.toEntity(request);
    Unidad actualizada = unidadService.actualizar(id, cambios);
    return unidadMapper.toResponse(actualizada);
}
```

`toEntity(updateRequest)` produce una entidad dispersa: solo los campos no
nulos y referencias con id. Los servicios existentes ya ignoran los campos
nulos.

Alternativa para parches solo escalares:

```java
Unidad unidad = unidadService.obtener(id);
unidadMapper.updateEntity(request, unidad);
return unidadMapper.toResponse(unidad);
```

`updateEntity` no aplica cambios de relación; si el update incluye `xxxId`, usar
`toEntity(updateRequest)` con el servicio.

### 6.3. Listados

```java
@GetMapping("/unidades")
public List<UnidadResponse> listar() {
    return unidadMapper.toResponseList(unidadService.listar());
}
```

### 6.4. Contraseñas

`UsuarioRequest.password` es un campo de escritura. El mapper **no** lo copia a
`passwordHash`. Además, `UsuarioRequest` y `UsuarioUpdateRequest` sobrescriben
`toString()` para imprimir `password=***` y evitar fugas en logs. Mientras la
autenticación siga pendiente, la integración debe hashear explícitamente:

```java
Usuario usuario = usuarioMapper.toEntity(request);
usuario.setPasswordHash(passwordEncoder.encode(request.password()));
Usuario creado = usuarioService.crear(usuario);
```

Cuando se implemente autenticación, el hashing debe quedar en el servicio o en
un `UserDetailsService`, no en el controlador.

## 7. Validación y errores

- Los controladores deben usar `@Valid` en el cuerpo del request.
- La validación de Bean Validation produce `400`.
- Las reglas de negocio siguen en los servicios:
  - unidad de contenido inmutable;
  - OA ofrecido en la unidad del contenido o del informe;
  - una sola alternativa correcta por pregunta, si se adopta esa regla;
  - límite de intentos y disponibilidad de la actividad;
  - usuario con rol `ESTUDIANTE` para informes;
  - unicidad de parejas en tablas puente;
  - borrados con hijos se traducen a `409`.
- El manejo global con `@RestControllerAdvice` sigue pendiente. Cuando se
  agregue, debe mapear `ReglaNegocioException` -> `400`,
  `RecursoNoEncontradoException` -> `404` y `ConflictoException` -> `409`.

## 8. Compilación

```powershell
.\mvnw.cmd clean compile
```

Los mappers se regeneran en:

```text
target/generated-sources/annotations/com/example/mpaz_pro_6springboot/<feature>/mapper/
```

Si un mapper no aparece o MapStruct reporta propiedades sin mapear:

1. Verificar que el campo del request y el de la entidad se llamen igual o que
   exista un `@Mapping` explícito.
2. Verificar que el tipo de retorno del resumen exista en `ResumenMapper`.
3. Ejecutar `.\mvnw.cmd clean compile` para forzar la regeneración.

## 9. Pendientes

- Controladores REST y rutas `/api/...`.
- `@RestControllerAdvice` con el mapeo de excepciones indicado en §7.
- Autenticación y autorización por rol; hashing BCrypt de contraseñas.
- Tests de integración con Testcontainers/PostgreSQL para validar DTOs, mappers
  y reglas de negocio.
- Datos seed (`V3__seed_*.sql`).
