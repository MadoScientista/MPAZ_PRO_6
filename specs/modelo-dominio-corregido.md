# Modelo de dominio corregido — MPAZ PRO 6.º

> **Estado:** documento histórico de trabajo. La fuente de verdad del alcance es
> el caso original de DSY1105.
>
> Correcciones respecto del caso original:
>
> - El MVP admite **máximo una unidad por asignatura**.
> - No exige los cursos 6A y 6B; basta un curso ficticio.
> - La autoría docente y la creación de asignaciones se mantienen como
>   extensiones del MVP, no como requisitos del caso.
> - El contenido y sus recursos tienen estado propio y acompañan el estado de
>   la unidad.
>
> `specs/endpoints` describe el contrato implementado; cuando haya conflicto,
> prevalece el caso original y esta cabecera de alcance.

---

## 1. Alcance del MVP

- Cursos: un curso ficticio; el seed actual usa **6A**.
- Asignaturas: **Lenguaje y Comunicación**, **Matemática**, **Ciencias Naturales**
  e **Historia, Geografía y Ciencias Sociales**.
- Material pedagógico base: viene del **seed/currículo**. La autoría docente se mantiene como extensión del MVP.
- UTP: revisa y autoriza; rol administrativo.
- Docente: selecciona objetivos, asigna contenidos/desafíos, configura intentos y
  valida informes.
- Estudiante: consume material, realiza intentos y ve sus respuestas. No accede al
  informe pedagógico.
- El MVP admite **máximo una unidad por asignatura**.

---

## 2. Roles

### UTP

- Revisa y autoriza unidades.
- Verifica que contenidos y actividades se relacionen con los objetivos.
- No crea material pedagógico en el flujo del docente.

### DOCENTE

- Puede enseñar varias asignaturas.
- Puede trabajar en uno o ambos cursos.
- En un curso y asignatura existe un solo docente.
- Selecciona objetivos de aprendizaje disponibles para una asignación.
- Asigna contenidos y desafíos existentes.
- Configura `maxIntentos` y `pruebaFinalHabilitada`.
- Revisa y valida los informes pedagógicos de sus estudiantes.
- En el alcance original no crea material; la app mantiene la autoría docente como extensión controlada por la UTP.

### ESTUDIANTE

- Pertenece a un curso.
- Accede a contenidos y actividades asignados.
- Realiza actividades y la prueba final dentro del límite configurado.
- Ve preguntas, respuestas correctas e incorrectas y sus intentos.
- No puede ver el informe pedagógico ni el análisis de fortalezas/refuerzo.

---

## 3. Relación docente-curso-asignatura

```text
docente_curso_asignatura
- id
- docente_id
- curso_id
- asignatura_id

UNIQUE (curso_id, asignatura_id)
```

Reglas:

- `docente_id` debe tener rol `DOCENTE`.
- Un docente puede tener muchas filas.
- Un curso y una asignatura solo pueden estar asociados a un docente.
- Cursos distintos pueden tener docentes distintos para la misma asignatura.
- El mismo docente puede dictar la misma asignatura en ambos cursos.

Esta tabla es la **única fuente de verdad** para responder qué asignaturas y
cursos tiene un docente. No se deriva de las asignaciones de unidades.

---

## 4. Entidades y relaciones

```text
Curso
Asignatura
DocenteCursoAsignatura
Unidad
ObjetivoAprendizaje
Contenido
RecursoContenido
Actividad
Pregunta
Alternativa
Asignacion
AsignacionObjetivo
AsignacionActividad
Intento
RespuestaEstudiante
InformePedagogico
DetalleInformeOa
Usuario
```

### Curso

- Existe al menos un curso ficticio (`6A` en el seed actual).
- Vienen del seed.
- El curso del estudiante se guarda en `Usuario.cursoId`.

### Asignatura

- Las cuatro del MVP.
- Vienen del seed.

### Unidad

- Pertenece a una asignatura.
- Viene del seed/currículo.
- El docente no la crea.
- UTP la revisa y autoriza.
- El MVP admite máximo una unidad por asignatura.

### ObjetivoAprendizaje

- Pertenece a una unidad.
- Incluye código oficial, descripción, eje y texto de referencia.

### Contenido

- Pertenece a un objetivo de aprendizaje.
- Viene del seed.
- Incluye explicación, ejemplos, instrucciones, orden, origen y estado.
- No se asocia directamente a una unidad ni a un curso.

### Actividad

- Pertenece a una unidad.
- Tipos: `ACTIVIDAD`, `DESAFIO`, `PRUEBA_FINAL`.
- Viene del seed.
- El docente la selecciona; no la crea.

### Pregunta

- Pertenece a una actividad.
- Tiene `unidadId` y `oaId`.
- El OA es el que permite consolidar el informe por objetivo.

### Asignación

```text
Asignacion
- id
- curso_id
- unidad_id
- max_intentos
- prueba_final_habilitada
- fecha
```

- Representa la unidad dentro de un curso.
- `docenteId` **no forma parte** de la asignación: se deriva de
  `docente_curso_asignatura` usando `curso_id + unidad.asignatura_id`.
- Si se necesita conservar historia, el informe pedagógico guarda el
  `docenteId` que lo generó/validó.
- `maxIntentos` y `pruebaFinalHabilitada` son propios de esa asignación.
- El docente puede actualizar esos campos y las asociaciones.

### AsignacionObjetivo

- Asocia una asignación con un OA de su unidad.
- Determina qué contenidos ve el estudiante: solo los contenidos de esos OA.

### AsignacionActividad

- Asocia una asignación con una actividad de su unidad.
- No admite `PRUEBA_FINAL`.
- Determina qué actividades ve el estudiante.

### Intento y RespuestaEstudiante

- El estudiante inicia intentos de actividades asignadas.
- La `PRUEBA_FINAL` depende de `pruebaFinalHabilitada` y `maxIntentos`.
- Las respuestas conservan `esCorrecta`.
- El estudiante puede consultar sus respuestas y resultados de intento.

### InformePedagogico

- Por estudiante y unidad.
- Guarda el docente responsable como snapshot.
- Solo el docente puede consultarlo y validarlo.
- El estudiante no tiene acceso.

---

## 5. Visibilidad

### Docente

- Solo ve cursos y asignaturas de `docente_curso_asignatura`.
- Solo ve unidades de esas asignaturas en sus cursos.
- Puede seleccionar OAs y actividades existentes.
- Ve y valida informes de sus estudiantes.

### Estudiante

- Ve las unidades asignadas a su curso.
- Ve solo los contenidos de los OAs seleccionados en la asignación.
- Ve solo las actividades seleccionadas en la asignación, más la prueba final si
  está habilitada.
- Ve sus intentos, preguntas y respuestas.
- No ve el informe pedagógico.

### Contenidos y actividades

- El contenido se filtra por `AsignacionObjetivo`.
- La actividad se filtra por `AsignacionActividad`.
- No se agrega `asignacion_contenido` en esta etapa.
- Si más adelante se necesita seleccionar contenidos concretos dentro de un OA,
  se evalúa una relación adicional.

---

## 6. Endpoints corregidos

### Docente-curso-asignatura

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/usuarios/{id}/docencias` | Lista los pares curso-asignatura del docente. Solo rol `DOCENTE`. |
| GET | `/api/docencias` | Lista todas las docencias. Solo UTP/administración. |

Respuesta:

```json
[
  {
    "id": 1,
    "docenteId": 2,
    "cursoId": 1,
    "asignaturaId": 1
  }
]
```

### Asignaciones

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/asignaciones` | Filtros `?cursoId`, `?unidadId` y, de forma derivada, `?docenteId`. |
| GET | `/api/asignaciones/{id}` | Detalle de una asignación. |
| PUT | `/api/asignaciones/{id}` | Docente actualiza `maxIntentos`, `pruebaFinalHabilitada` y `fecha`. |
| DELETE | `/api/asignaciones/{id}` | Sujeta a las reglas de conflicto existentes. |

- El docente solo puede operar sobre asignaciones de sus cursos-asignaturas.
- La creación base de asignaciones queda en el seed/administración; la app mantiene
  la creación docente como extensión.

### Asociaciones

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/asignaciones/{id}/objetivos-aprendizaje` | OAs asociados. |
| POST | `/api/asignaciones/{id}/objetivos-aprendizaje/{oaId}` | Asocia un OA de la unidad. |
| DELETE | `/api/asignaciones/{id}/objetivos-aprendizaje/{oaId}` | Quita la asociación. |
| GET | `/api/asignaciones/{id}/actividades` | Actividades asociadas. |
| POST | `/api/asignaciones/{id}/actividades/{actividadId}` | Asocia una actividad de la unidad. |
| DELETE | `/api/asignaciones/{id}/actividades/{actividadId}` | Quita la asociación. |

Solo el docente de la asignación puede modificar estas asociaciones.

### Informes

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/informes-pedagogicos` | Solo `DOCENTE`. |
| GET | `/api/informes-pedagogicos/{id}` | Solo el docente responsable. |
| PATCH | `/api/informes-pedagogicos/{id}/validacion` | Solo el docente responsable. |

El estudiante no tiene endpoints de informes pedagógicos.

### Material

- `GET` de asignaturas, unidades, OAs, contenidos y actividades: permitido según
  la visibilidad del rol.
- `POST`, `PUT` y `DELETE` de material base: administración/seed. La autoría docente
  se mantiene como extensión controlada por la UTP.

---

## 7. Seed mínimo

- Cursos: al menos un curso ficticio (`6A` en el seed actual).
- Asignaturas: las cuatro del caso.
- Docentes: al menos uno con varias asignaturas y cursos.
- Docencias: filas que relacionan docentes con curso y asignatura.
- Unidades: máximo una por asignatura.
- OAs: por unidad.
- Contenidos: por OA.
- Actividades: por unidad, con y sin prueba final.
- Preguntas y alternativas: por actividad.
- Estudiantes: ficticios, asignados al curso del seed.
- Asignaciones: por curso/unidad.
- Asociaciones OA/actividad: configuradas para demostrar el flujo.

---

## 8. Decisiones explícitas

1. El material base viene del seed/currículo.
2. La autoría docente es una extensión del MVP y requiere revisión de la UTP.
3. El docente se relaciona con cursos y asignaturas mediante
   `docente_curso_asignatura`.
4. En un curso/asignatura solo hay un docente.
5. El estudiante no accede al informe pedagógico.
6. Los contenidos se filtran por los OAs seleccionados.
7. Las actividades se filtran por las actividades seleccionadas.
8. `Asignacion.docenteId` se elimina; el docente se deriva de la docencia del
   curso/asignatura.
9. El informe conserva el docente responsable como snapshot.
10. La restricción de máximo una unidad por asignatura forma parte del MVP.

---

## 9. Cambios respecto de `specs/endpoints`

- Se agrega `docente_curso_asignatura`.
- Se agrega el endpoint de docencias del docente.
- Se elimina `docenteId` de `Asignacion`.
- Se restringe la creación de material.
- Se elimina el acceso del estudiante a informes.
- Se explicita el filtrado de contenidos por OA.
- Se documentan los límites de rol.

---

## 10. Fuera de alcance inmediato

- Crear unidades.
- La autoría docente se mantiene como extensión; su detalle vive en `AGENTS.md` y `specs/endpoints`.
- Gestión administrativa completa de cursos, asignaturas y usuarios.
- Diagnósticos automáticos o IA.

---

## 11. Criterios de aceptación del contrato

- Un docente puede recuperar sus pares curso-asignatura.
- Un docente no puede acceder a cursos/asignaturas que no le corresponden.
- Un curso/asignatura no puede tener dos docentes.
- El docente puede seleccionar OAs y actividades de sus asignaciones.
- El estudiante solo ve contenidos de OAs seleccionados y actividades asignadas.
- El estudiante no puede acceder a informes.
- El docente puede validar informes de sus estudiantes.
- El seed permite demostrar el flujo completo con el curso ficticio `6A`.
