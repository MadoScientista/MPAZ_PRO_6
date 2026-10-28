# Flujo UTP

Documento de referencia del flujo de la jefa de UTP en la app. Describe las
pantallas, los datos que consume cada una y las reglas del contrato que aplica.

---

## 1. Objetivo

La UTP entra a la app, ve el estado general de su establecimiento, revisa las
unidades pendientes, consulta el material de una unidad y la autoriza o publica.
También gestiona asignaturas, cursos y usuarios.

---

## 2. Mapa de navegación

```
LoginScreen
   └── HomeUTPScreen
         ├── UnitListScreen
         │      └── UnitDetailScreen
         │             ├── ContentDetailScreen
         │             └── UnitAuthorizationScreen
         ├── SubjectListScreen ── SubjectFormScreen
         ├── CourseListScreen ─── CourseFormScreen
         └── UserListScreen ───── UserFormScreen
```

| Destino | Ruta | Pantalla |
|---|---|---|
| Inicio | `home` | `HomeUTPScreen` |
| Unidades | `units` | `UnitListScreen` |
| Detalle de unidad | `unitDetail/{unitId}` | `UnitDetailScreen` |
| Detalle de contenido | `contentDetail/{contentId}` | `ContentDetailScreen` |
| Autorización | `unitAuthorization/{unitId}` | `UnitAuthorizationScreen` |
| Asignaturas | `subjects` | `SubjectListScreen` |
| Formulario de asignatura | `subjectForm?subjectId={id}` | `SubjectFormScreen` |
| Cursos | `courses` | `CourseListScreen` |
| Formulario de curso | `courseForm?courseId={id}` | `CourseFormScreen` |
| Usuarios | `users` | `UserListScreen` |
| Formulario de usuario | `userForm?userId={id}` | `UserFormScreen` |

La barra inferior muestra cinco destinos raíz: **Inicio, Unidades, Asignaturas,
Cursos y Usuarios**. Se oculta en detalles, formularios y autorización.

---

## 3. Inicio (`HomeUTPScreen`)

Muestra:

- Saludo con el nombre de la UTP.
- Contador de **unidades pendientes**: solo `EN_REVISION`. Los `BORRADOR` son
  material de trabajo del docente y no se muestran a la UTP.
- Total de cursos.
- Total de asignaturas.
- Botón **Revisar unidades pendientes** → `units`.

Datos: `GET /api/unidades`, `GET /api/cursos`, `GET /api/asignaturas`.

---

## 4. Listado de unidades (`UnitListScreen`)

La UTP ve únicamente las unidades en `EN_REVISION`. Los `BORRADOR` pertenecen al
docente y no aparecen en la cola de revisión. La búsqueda por título o asignatura
y el filtro por asignatura son **client-side**.

Cada `UnitCard` muestra:

- Asignatura y estado.
- Título y nivel educativo.
- Cantidad de contenidos.
- Cantidad de actividades.
- Si la unidad tiene una `PRUEBA_FINAL`.
- Acciones **Ver detalle** y **Autorizar**.

La acción **Autorizar** se ofrece para unidades `EN_REVISION`. La pantalla de
autorización también permite publicar una unidad `AUTORIZADO`, pero esa acción no
se ofrece desde el listado.

Los conteos se resuelven en cliente:

- Objetivos de aprendizaje de la unidad → contenidos por `oaId`.
- Actividades de la unidad → total y detección de `PRUEBA_FINAL`.

Datos: `GET /api/unidades`, `GET /api/asignaturas`,
`GET /api/objetivos-aprendizaje`, `GET /api/contenidos`,
`GET /api/actividades`.

---

## 5. Detalle de unidad (`UnitDetailScreen`)

### 5.1 Resumen

- Encabezado con asignatura, título, nivel educativo y estado.
- Contador de contenidos.
- Contador de actividades.
- Indicador de prueba final (`Sí`/`No`).

### 5.2 Contenidos

Una `ContentCard` por contenido de la unidad, con:

- Título.
- Código del objetivo de aprendizaje asociado, cuando existe.
- Resumen de la explicación.
- Cantidad de recursos.
- Acción **Ver contenido** → `ContentDetailScreen`.

El botón **Crear nuevo contenido** (`ContentFormScreen`) está reservado exclusivamente
para el rol docente (`UserRole.DOCENTE`). Para la UTP y el estudiante la sección se mantiene
en modo lectura sin dicha acción.

Los contenidos se resuelven en dos pasos porque el contrato no expone un filtro
por unidad:

1. `GET /api/objetivos-aprendizaje?unidadId={unitId}`.
2. `GET /api/contenidos` y filtro client-side por los `oaId` de la unidad.

La cantidad de recursos se obtiene de `GET /api/recursos-contenido` y se agrupa
por `contenidoId`.

### 5.3 Actividades

Una `ActivityCard` por actividad de la unidad, con:

- Tipo (`ACTIVIDAD`, `DESAFIO`, `PRUEBA_FINAL`).
- Orden y título.
- Cantidad de preguntas.

La revisión de la UTP es de solo consulta: la tarjeta no muestra acciones de
gestión. Las preguntas se cuentan desde `GET /api/preguntas`.

### 5.4 Autorización

El botón de estado solo se muestra a la UTP y solo cuando la unidad está
`EN_REVISION` (autorizar) o `AUTORIZADO` (publicar). Al confirmar, vuelve a la
pantalla anterior y avisa al listado para que recargue.

---

## 6. Detalle de contenido (`ContentDetailScreen`)

Pantalla de consulta:

- Título.
- Objetivo de aprendizaje (`OA: código`).
- Explicación completa.
- Ejemplos, cuando existen.
- Recursos del contenido.
- Instrucciones, cuando existen.

Los recursos se abren con `Intent.ACTION_VIEW`. Si la URL no se puede abrir, se
muestra un aviso y la pantalla sigue disponible. La UTP ve todos los recursos del
contenido; el estudiante solo los publicados.

Datos: `GET /api/contenidos/{id}`, `GET /api/objetivos-aprendizaje/{id}`,
`GET /api/recursos-contenido?contenidoId={id}`.

---

## 7. Autorización (`UnitAuthorizationScreen`)

- `EN_REVISION` → `AUTORIZADO`.
- `AUTORIZADO` → `PUBLICADO`.
- Un `BORRADOR` no se autoriza desde esta pantalla: pertenece al docente y debe
  pasar primero a `EN_REVISION`. El botón aparece deshabilitado y la pantalla
  explica el motivo.
- El backend exige un autorizante con rol `UTP` y una `fechaAutorizacion`.
- Si faltan datos, responde `400 Bad Request` y la pantalla muestra el motivo.
- Una unidad ya publicada no admite más cambios de estado.

---

## 8. Asignaturas, cursos y usuarios

Los tres listados están implementados con:

- Búsqueda client-side.
- Creación y edición mediante formularios.
- Borrado con `ConfirmDialog`.
- Explicación del `409 Conflict` cuando la entidad tiene dependencias.
- Formularios con validación de los límites del contrato.

### Asignaturas

- `SubjectListScreen` / `SubjectFormScreen`.
- Nombre obligatorio de hasta 100 caracteres.
- Duplicados → `409 Conflict`.
- Borrado con unidades asociadas → `409 Conflict`.

### Cursos

- `CourseListScreen` / `CourseFormScreen`.
- Nombre de hasta 50 caracteres y año entre 2000 y 2100.
- Filtro por año delegado al backend (`?anio`); búsqueda por nombre en cliente.
- Par nombre-año duplicado → `409 Conflict`.

### Usuarios

- `UserListScreen` / `UserFormScreen`.
- Filtro por rol client-side.
- Curso solo para estudiantes.
- Contraseña obligatoria al crear; vacía en edición significa “no cambiar”.
- Borrado con dependencias → `409 Conflict`.

---

## 9. Reglas aplicadas

| Regla | Aplicación |
|---|---|
| Sin paginación | Listas completas, sin scroll infinito |
| Búsqueda libre client-side | `SearchField` y `FilterChipRow` filtran en memoria |
| `409 Conflict` | Se muestra el motivo en la pantalla |
| Solo UTP autoriza/publica | La acción y la pantalla de autorización se limitan a ese rol |
| Borradores solo del docente | La UTP no ve unidades `BORRADOR` en su inicio, listado ni autorización |
| Barra inferior | Solo en los cinco destinos raíz |
| Tema | `ThemeToggleButton` en todas las pantallas reales |

---

## 10. Verificación

- `./gradlew compileDebugKotlin`
- `./gradlew test`
- `./gradlew assembleDebug`
- `./gradlew lintDebug`
- Prueba manual con un usuario UTP en modo local o contra el backend.
