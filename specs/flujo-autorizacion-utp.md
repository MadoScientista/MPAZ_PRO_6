# Flujo de autorización de unidades - UTP

Este documento valida el flujo para autorizar/rechazar unidades desde el rol UTP, comparando lo implementado en la app con lo documentado en `specs/endpoints`.

## Alcance
- Rol: **UTP** (`UserRole.UTP`)
- Recurso: **Unidad de Aprendizaje** (`LearningUnit`, wire: `Unidad`)
- Endpoints relacionados: `GET/PUT/DELETE /api/unidades`, `POST/GET /api/informes-pedagogicos` (no directamente), gestión de usuarios para autorizante

**Regla de visualización:** El UTP solo visualiza unidades en estado `EN_REVISION`. Las unidades en `BORRADOR` no le corresponden hasta que sean enviadas a revisión por el docente.

## Estado actual en la app

### Pantallas
- `UnitListScreen` - Listado de unidades. Filtra por rol en cliente (no existe endpoint "pendientes de revisión"):
  - UTP → solo `EN_REVISION`
  - DOCENTE → `AUTORIZADO` y `PUBLICADO`
  - ESTUDIANTE → solo `PUBLICADO`
  El título, el subtítulo y el mensaje de vacío cambian según el rol (`tituloListado`, `subtituloListado`, `mensajeListadoVacio`). La acción **Autorizar** solo se muestra al rol UTP.
- `UnitDetailScreen` - Detalle de una unidad: cabecera con la asignatura real, información general, objetivos de aprendizaje, contenidos por objetivo, recursos por contenido y actividades. El botón **Autorizar / Revisar** solo se muestra al rol UTP.
- `UnitAuthorizationScreen` - Pantalla de autorización/publicación/rechazo. Solo el rol UTP ve los botones de acción; para los demás roles es de consulta y muestra el aviso "Solo la jefa de UTP puede autorizar o publicar unidades."

### ViewModels
- `UnitAuthorizationViewModel` - Carga la unidad y su asignatura, ejecuta `autorizar()` / `rechazar()` y expone `mensaje` (feedback) y `error`.
- `UnitListViewModel` - Carga y filtra las unidades por rol (`ContentStatus.esPendienteDeRevision()` para la UTP).
- `UnitDetailViewModel` - Carga la unidad, la asignatura y la estructura pedagógica. El I/O va en `Dispatchers.IO` y los contenidos/recursos se piden en paralelo.

### Repositorios
- `UnitRepository` - Tiene `list()`, `getById()`, `create()`, `update()`, `delete()`
- `UserRepository` - Para obtener el usuario UTP autenticado
- `Session` - Tiene el usuario con sesión iniciada

## Flujo esperado según el spec

### 1. Listado de unidades pendientes (UTP)
**Endpoint:** `GET /api/unidades` (puede filtrar por `?asignaturaId={id}`)

El UTP debe ver unidades que requieren revisión/autorización. Según el spec:
- Estados válidos: `BORRADOR`, `EN_REVISION`, `AUTORIZADO`, `PUBLICADO` (enum `ContentStatus`)
- **Regla UI:** "No hay endpoint de 'unidades pendientes de revisión'. El listado UTP filtra **únicamente por `EN_REVISION`** en cliente."

**Acción en app:** En `UnitListViewModel`, cuando `rol == UserRole.UTP` solo se muestran unidades con `ContentStatus.IN_REVIEW`. Los borradores (`DRAFT`) quedan a cargo del docente hasta que los envíe a revisión (`EN_REVISION`).

### 2. Revisar detalle de unidad
**Endpoint:** `GET /api/unidades/{id}`

UTP puede ver el detalle completo de la unidad antes de autorizar. La pantalla de detalle debe mostrar, **solo lectura**, la estructura pedagógica para revisión efectiva:

- **Objetivos de Aprendizaje** (`GET /api/objetivos-aprendizaje?unidadId={id}`): código, descripción, eje y texto de referencia.
- **Contenidos por objetivo** (`GET /api/contenidos?oaId={id}`): título, explicación, ejemplos, instrucciones, orden y origen. Agrupados por objetivo de aprendizaje.
- **Recursos de contenido** (`GET /api/recursos-contenido?contenidoId={id}`): tipo (`IMAGEN`/`AUDIO`), URL y descripción.
- **Actividades** (`GET /api/actividades?unidadId={id}`): título, tipo (`ACTIVIDAD`/`DESAFIO`/`PRUEBA_FINAL`), orden. Para revisión se muestran en modo solo lectura. 

**Regla:** El detalle debe agrupar por objetivo de aprendizaje. No se permite edición desde esta pantalla. Si la unidad en revisión no tiene objetivos/contenidos/actividades, se muestra un aviso pero **no se bloquea** la autorización desde el flujo UTP.

**Implementado:** `UnitDetailViewModel.cargar()` encadena unidad → asignatura → objetivos → contenidos → recursos → actividades. Cada llamada va en `Dispatchers.IO` y las que no dependen entre sí se lanzan con `async` + `awaitAll` (el detalle encadena objetivos → contenidos → recursos y pedirlo secuencial sería un N+1).

### 3. Autorizar unidad
**Endpoint:** `PUT /api/unidades/{id}`

Para pasar a `AUTORIZADO` o `PUBLICADO`, el spec indica:
> "Los estados `AUTORIZADO` y `PUBLISHED` requieren un autorizante con rol `UTP` y una `fechaAutorizacion`; la creación solo admite los estados iniciales. Un autorizante inexistente devuelve `404 Not Found`, y una autorización incompleta o un usuario sin rol `UTP` devuelve `400 Bad Request`."

**Cuerpo de actualización (parcial, PUT):**
```json
{
  "estado": "AUTORIZADO", // o "PUBLICADO"
  "autorizadaPorId": 123,  // ID del usuario UTP
  "fechaAutorizacion": "2026-10-21T10:30:00Z" // ISO-8601
}
```

**Notas importantes:**
- `PUT` es parcial: "aplicando solo los valores enviados" - solo enviar campos modificados
- Requiere `autorizadaPorId` (usuario con rol UTP) y `fechaAutorizacion` cuando se autoriza/publica
- El autorizante debe tener rol `UTP`

**Implementado:** `UnitAuthorizationViewModel.autorizar(autorizanteId)` calcula el estado destino a partir del estado actual (`DRAFT`/`IN_REVIEW` → `AUTHORIZED`, `AUTHORIZED` → `PUBLISHED`) y envía los tres campos. El `autorizanteId` sale de la sesión (`container.session.user`).

### 4. Rechazar unidad
**Endpoint:** `PUT /api/unidades/{id}`

Para rechazar/devolver a corrección, se cambia el estado a `BORRADOR`. **PENDIENTE DE CONFIRMAR CON BACKEND:** ¿Existe campo para observaciones/rechazo? El spec solo menciona `estado`, `autorizadaPorId`, `fechaAutorizacion` en la actualización.

**Flujo implementado:**
- Cambiar estado a `BORRADOR` (sin enviar `autorizadaPorId` ni `fechaAutorizacion`)
- Mostrar confirmación con `ConfirmDialog` antes de ejecutar
- Tras éxito, mostrar el mensaje "Unidad devuelta a borrador." y el botón **Volver al listado**

**Alternativa según reglas UI:**
> "Borrado siempre posible desde la UI, con explicación. El `409` del spec no se puede anticipar; se muestra un `ConfirmDialog` y, si el backend responde `Conflict`, un mensaje que diga *por qué*"

Para rechazo, solo cambio de estado con justificación mostrada al usuario (no necesariamente guardada en backend si no hay campo).

## Validaciones necesarias

| Acción | Validación | Respuesta esperada |
|---|---|---|
| Autorizar sin UTP logueado | Debe haber sesión con rol UTP | Prevenir acción en UI |
| Autorizar | Enviar `estado`, `autorizadaPorId`, `fechaAutorizacion` | `200 OK` con `UnidadResponse` actualizado |
| Autorizar con usuario no-UTP | Backend valida rol | `400 Bad Request` |
| Rechazar | Cambiar estado (posiblemente a `EN_REVISION`) | `200 OK` |

**Implementado en la UI:** las tres primeras filas se previenen en la pantalla, no solo en el backend:
- La pantalla de autorización recibe `rol: UserRole` y **no renderiza** los botones `Autorizar unidad` / `Rechazar y devolver a borrador` si el rol no es UTP; en su lugar muestra el aviso de que solo la UTP puede autorizar.
- El botón `Autorizar` de `UnitCard` y el botón `Autorizar / Revisar` de `UnitDetailScreen` solo se muestran con rol UTP.
- El `MockUnitRepository` valida además que el autorizante exista (`404`) y tenga rol UTP (`400`).

## Verificación en el dispositivo

Recorrido probado con `adb install -r` + `uiautomator dump` (SM-S921B, 1080x2340):

| Paso | Resultado |
|---|---|
| Login UTP → "Revisar unidades en revisión" | Listado con 3 unidades `EN_REVISION`, botón `Autorizar` visible |
| "Ver detalle" de "Ecosistemas y biodiversity" | Cabecera con la asignatura real, objetivos, contenidos, recursos y actividades |
| Scroll hasta "Actividades" | **Sin crash** (antes: `Key "1" was already used`) |
| "Autorizar / Revisar" | Pantalla de autorización con la acción disponible según el estado |
| "Autorizar unidad" | Estado `AUTORIZADO` + mensaje "Unidad autorizada." + botón "Volver al listado" |
| "Volver al listado" | Listado refrescado: 3 → 2 unidades |
| "Rechazar y devolver a borrador" | `ConfirmDialog`; al confirmar, estado `BORRADOR` + "Unidad devuelta a borrador." |
| Login docente (marco) | Listado "Mis unidades" **sin** botón `Autorizar`; detalle **sin** "Autorizar / Revisar" |

## Pendientes por verificar

1. **Campo de observaciones/rechazo** - ¿Existe en `UpdateLearningUnitRequest`? Revisar modelo `data/model/requests/UpdateLearningUnitRequest.kt`
2. **Transiciones de estados permitidas** - ¿Puede pasar de `AUTORIZADO` a `PUBLICADO`? ¿Puede rechazar desde `EN_REVIEW`?
3. **Comportamiento de `fechaAutorizacion` al rechazar** - ¿Se mantiene o se limpia? (la app no los envía al rechazar: el `PUT` es parcial)
4. **Permisos** - ¿Solo UTP puede autorizar? Backend lo valida.
5. **Publicar desde el listado** - como la UTP solo ve `EN_REVISION`, la transición `AUTORIZADO` → `PUBLICADO` no es alcanzable desde el listado actual. Pendiente de decidir si el listado UTP debe incluir las unidades `AUTORIZADO` que puede publicar.

## Próximos pasos para implementar

1. Implementar `autorizarUnidad()` y `rechazarUnidad()` en ViewModel — **hecho**
2. Conectar botones en `UnitAuthorizationScreen` — **hecho**
3. Añadir diálogos de confirmación — **hecho**
4. Mostrar feedback y navegar al listado — **hecho**
5. **Ampliar detalle de unidad**: cargar objetivos, contenidos por objetivo, recursos y actividades para revisión efectiva (solo lectura) — **hecho**
6. Implementar la transición a `PUBLICADO` desde la UI del listado (ver pendiente 5)

## Notas
- El `PUT` es parcial - solo enviar campos necesarios
- Fecha en formato ISO-8601 (el backend espera `fechaAutorizacion` como fecha/hora)
- Usar `data/mock/MockData` para probar flujo en desarrollo
- `java.time.Instant` funciona con `minSdk 24` porque el proyecto activa
  `isCoreLibraryDesugaringEnabled` en `app/build.gradle.kts`
