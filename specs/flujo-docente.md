# Flujo Docente

Documento de referencia del flujo del docente (`UserRole.DOCENTE`) en la app.
Describe las pantallas, los datos que consume cada una, la navegación entre
componentes y las reglas aplicadas.

---

## 1. Objetivo

El docente ingresa a la app para gestionar la planificación de sus cursos, revisar
y crear unidades y contenidos, administrar asignaciones e informes de sus
estudiantes.

---

## 2. Mapa de navegación

```
LoginScreen
   └── HomeTeacherScreen
         ├── CourseDetailScreen ── SubjectDetailScreen (Unidades de la asignatura)
         │                                └── UnitDetailScreen
         ├── UnitListScreen ────── UnitDetailScreen
         │                               ├── ContentDetailScreen ── ContentFormScreen
         │                               └── ActivityFormScreen
         ├── AssignmentListScreen ─ AssignmentDetailScreen
         └── ReportListScreen ───── ReportDetailScreen
```

| Destino | Ruta | Pantalla |
|---|---|---|
| Inicio | `home` | `HomeTeacherScreen` |
| Cursos | `courses` | `CourseListScreen` |
| Detalle de curso | `courseDetail/{courseId}` | `CourseDetailScreen` |
| Detalle de asignatura | `subjectDetail/{subjectId}?courseId={courseId}` | `SubjectDetailScreen` |
| Unidades | `units` | `UnitListScreen` |
| Detalle de unidad | `unitDetail/{unitId}` | `UnitDetailScreen` |
| Asignaciones | `assignments` | `AssignmentListScreen` |
| Detalle de asignación | `assignmentDetail/{assignmentId}` | `AssignmentDetailScreen` |
| Informes | `reports` | `ReportListScreen` |
| Detalle de informe | `reportDetail/{reportId}` | `ReportDetailScreen` |

La barra inferior del docente muestra cuatro destinos raíz: **Inicio, Unidades,
Asignaciones e Informes**.

---

## 3. Inicio (`HomeTeacherScreen`)

Muestra:

- Resumen con tarjetas contador de **Unidades**, **Actividades** e **Informes**.
- Carrusel de **Cursos**: tarjetas compactas (`CourseCarouselCard`) con nombre,
  año y cantidad de estudiantes. Al presionar una tarjeta, navega directamente a
  la vista del curso (`CourseDetailScreen`).
- Carrusel de **Asignaturas**: tarjetas compactas (`SubjectCarouselCard`) con
  nombre de la materia y cantidad de unidades. Al presionar una tarjeta, navega
  a la vista de la asignatura (`SubjectDetailScreen`).
- Carrusel de **Unidades en curso**: unidades asignadas al docente (`UnitCarouselCard`).

Datos:
`GET /api/asignaciones?docenteId={id}`,
`GET /api/usuarios/{id}/cursos`,
`GET /api/unidades`,
`GET /api/asignaturas`,
`GET /api/actividades`,
`GET /api/informes-pedagogicos`.

---

## 4. Detalle de Curso (`CourseDetailScreen`)

Al seleccionar un curso desde el carrusel de Inicio o desde el listado de cursos,
se abre la pantalla de detalle del curso, organizada en dos secciones principales:

### 4.1 Encabezado

Muestra el nombre del curso (ej. "6A"), el año académico y la cantidad de
estudiantes matriculados en él.

### 4.2 Sección 1: Asignaturas del curso

- Muestra las asignaturas que tienen unidades asignadas al curso mediante
  `SubjectCard` (en modo consulta, `showActions = false`).
- Muestra el nombre de la materia, su ícono representativo y el número de
  unidades asociadas.
- **Acción**: Al pinchar cualquier tarjeta de asignatura, navega a la vista de
  unidades de esa asignatura (`SubjectDetailScreen`).

### 4.3 Sección 2: Estudiantes del curso

- Se presentan los estudiantes pertenecientes al curso en tarjetas horizontales
  dispuestas en una fila desplazable (`LazyRow`).
- Cada tarjeta horizontal (`StudentCourseCard`) contiene:
  - **Izquierda**: Ícono de estudiante (`Icons.Filled.Person`) contenido en un
    contenedor circular.
  - **Derecha del ícono**: Nombre completo del estudiante (`student.nombre`)
    arriba, y su nombre de usuario abajo precedido por un arroba (`@username`).

Datos:
`GET /api/cursos/{id}`,
`GET /api/usuarios` (filtrado por `rol == UserRole.ESTUDIANTE` y `cursoId == {id}`),
`GET /api/asignaciones?cursoId={id}`,
`GET /api/unidades`,
`GET /api/asignaturas`.

---

## 5. Vista de Unidades de la Asignatura (`SubjectDetailScreen`)

Se abre al seleccionar una asignatura desde el detalle del curso (`CourseDetailScreen`)
o desde el listado general de asignaturas.

Muestra:

- Encabezado con el nombre de la asignatura, el curso al que pertenece (si se navega desde un curso, ej. "Matemática — 6A" y "Curso 6A (2026)") y la cantidad total de unidades.
- Listado vertical de tarjetas de unidad (`UnitCard`). Para el docente, las tarjetas solo ofrecen el botón «Ver detalle» (incluso si la unidad está en estado `EN_REVISION`, la acción «Autorizar» o «Publicar» queda reservada a la UTP).
- Al seleccionar una unidad, el docente navega a `UnitDetailScreen` para revisar
  sus contenidos y actividades, o agregar nuevo material.

Datos:
`GET /api/asignaturas/{id}`,
`GET /api/unidades?asignaturaId={id}`.

---

## 6. Reglas aplicadas

| Regla | Aplicación |
|---|---|
| Carrusel de Cursos en Home | Tocar una tarjeta de curso abre directamente `CourseDetailScreen` |
| Vista de Curso con 2 Secciones | Sección 1: Asignaturas del curso · Sección 2: Estudiantes en tarjetas horizontales |
| Tarjeta de Estudiante | Ícono a la izquierda, nombre arriba a la derecha y `@username` abajo a la derecha |
| Navegación Asignatura → Unidades | Tocar la tarjeta de asignatura abre la vista de sus unidades (`SubjectDetailScreen`) |
| Acciones de Unidad para Docente | Las tarjetas `UnitCard` para el docente muestran exclusivamente el botón «Ver detalle». Las acciones de autorizar y publicar están reservadas al rol UTP |
| Tema | Soporte completo de tema claro/oscuro con `ThemeToggleButton` |

---

## 7. Verificación

- `./gradlew compileDebugKotlin`
- `./gradlew test`
- `./gradlew assembleDebug`
- `./gradlew lintDebug`
