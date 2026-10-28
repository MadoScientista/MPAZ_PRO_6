# Flujo del docente

Documento de referencia del flujo `DOCENTE` de MPAZ PRO 6.º. La fuente de
verdad del alcance es el caso original de DSY1105.

## 1. Alcance

El docente:

- inicia sesión y aterriza en `HomeTeacherScreen`;
- consulta sus cursos y asignaturas desde `docente_curso_asignatura`;
- abre una unidad existente;
- selecciona los OAs que se asignan al curso;
- asigna contenidos y actividades existentes;
- configura `maxIntentos` y la prueba final por asignación;
- revisa y valida informes de sus estudiantes.

El docente **no crea unidades ni OAs**.

La app mantiene como extensión:

- crear contenidos dentro de una unidad existente;
- crear actividades, preguntas y alternativas;
- editar actividades;
- crear asignaciones.

Estas extensiones no reemplazan el flujo oficial de seleccionar y asignar
material existente.

## 2. Navegación principal

```text
LoginScreen
  └── HomeTeacherScreen
        ├── Asignaturas
        │     └── SubjectDetailScreen
        │           └── UnitDetailScreen
        │                 ├── ContentDetailScreen
        │                 │     ├── ActivityDetailScreen
        │                 │     └── ActivityFormScreen
        │                 └── ActivityPreviewScreen
        ├── Cursos
        │     └── CourseDetailScreen
        ├── Asignaciones
        │     └── AssignmentDetailScreen
        └── Informes
              └── ReportDetailScreen
```

## 3. Unidad

`UnitDetailScreen` muestra:

- unidad y asignatura;
- contenidos;
- actividades regulares por contenido;
- prueba final;
- máximo de intentos de la prueba final para el curso seleccionado.

El docente puede:

- abrir un contenido;
- previsualizar una actividad;
- gestionar preguntas y alternativas si la unidad está `BORRADOR`;
- editar una actividad si la unidad está `BORRADOR`;
- crear contenido si la unidad está `BORRADOR`;
- enviar la unidad a revisión.

La UTP conserva la autorización y publicación.

## 4. Asignaciones

La asignación es por curso y unidad. `maxIntentos` y
`pruebaFinalHabilitada` pertenecen a esa asignación, no a la unidad global.

El detalle de la asignación permite asociar:

- OAs de la unidad;
- actividades de la unidad.

El estudiante ve los contenidos de los OAs asociados y las actividades
asociadas, más la prueba final si está habilitada.

## 5. Informes

El docente puede:

- consultar los informes de sus estudiantes;
- validar un informe pendiente.

El estudiante no tiene acceso a los informes pedagógicos.

## 6. Reglas que no se deben romper

- Una unidad por asignatura como máximo.
- El docente no crea unidades.
- La UTP revisa, autoriza y publica.
- El contenido y los recursos tienen estado.
- El estudiante no ve material no publicado ni no asignado.
- El estudiante no accede a informes.
- `maxIntentos` se interpreta por asignación y curso.
