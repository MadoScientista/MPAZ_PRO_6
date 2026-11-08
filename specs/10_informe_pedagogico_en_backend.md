# Informe pedagógico preliminar y snapshot

**Estado:** implementado. La migración `V5__informe_pedagogico_fecha_corte.sql`
está aplicada, la entidad y los DTOs exponen `fechaCorte` y `origen`, y los dos
endpoints de §4 están disponibles. Verificado contra PostgreSQL 17 con un
dataset temporal: preview, creación, snapshot inmutable, corte futuro, usuario
sin resultados, usuario no estudiante y recursos inexistentes.

**Fuente del modelo:** `src/main/resources/db/migration/V5__informe_pedagogico_fecha_corte.sql`.

## 1. Objetivo

Permitir que un docente obtenga un consolidado preliminar del desempeño de un
estudiante en una unidad, a partir del último intento cerrado de cada actividad
de esa unidad, y que luego pueda emitir un informe pedagógico que conserve esos
conteos como una fotografía. Los intentos posteriores no modifican el informe.

## 2. Modelo

`informe_pedagogico` incorpora dos columnas de control:

| Columna | Tipo | Uso |
|---|---|---|
| `fecha_corte` | `timestamptz NOT NULL DEFAULT now()` | Instante máximo de los intentos incluidos. Queda fijo al crear el informe. |
| `origen` | `varchar(20) NOT NULL DEFAULT 'MANUAL'` | `AUTOMATICO` si se generó desde resultados; `MANUAL` si se creó o corrigió a mano. El `CHECK` restringe los valores. |

`detalle_informe_pedagogico.correctas` e `incorrectas` ya almacenan los conteos.
El informe no se recalcula en las lecturas: esa tabla es el snapshot por OA.

La separación entre `fecha` y `fecha_corte` es intencional:

- `fecha`: fecha de emisión del informe, editable por el docente.
- `fecha_corte`: corte técnico usado para seleccionar intentos, inmutable.

Así, cambiar la fecha del informe no altera el universo de intentos que lo
originó.

## 3. Reglas de cálculo

Para `(usuarioId, unidadId, fechaCorte)`:

1. **Cursos relevantes:** el intento debe pertenecer a un `usuario_curso` del
   estudiante. La unidad acota la actividad, de modo que no hace falta cruzar
   además con `asignatura_curso`.
2. **Actividades de la unidad:** `actividad_curso` cuya
   `actividad.contenido.unidad` sea la unidad indicada.
3. **Último intento por actividad:** se agrupa por `actividad_id` (no por
   `actividad_curso_id`, para no duplicar si la actividad está publicada en más
   de un curso) y se toma el intento con mayor `fecha_fin`. Solo se consideran
   intentos cerrados (`fecha_fin IS NOT NULL`) con `fecha_fin <= fechaCorte`.
4. **Preguntas evaluadas:** las de `pregunta_actividad` para la actividad del
   intento.
5. **Objetivos de aprendizaje:** se resuelven con
   `pregunta.recurso.contenido → objetivo_aprendizaje_contenido`, filtrando por
   la unidad.

### Reglas de corrección por defecto

- Una pregunta es **correcta** si tiene al menos una alternativa seleccionada y
  todas las seleccionadas tienen `alternativa.correcta = true`.
- Una pregunta es **incorrecta** si tiene al menos una alternativa seleccionada
  y al menos una tiene `alternativa.correcta = false`.
- Una pregunta **sin responder** no suma a `correctas` ni a `incorrectas`.
  Si el producto necesita reportarla, agregar `sin_responder` al detalle en una
  migración futura.
- Una pregunta reutilizada en dos actividades cuenta una vez por actividad.
- Una respuesta puede aportar a varios OA si su contenido está vinculado a más
  de un objetivo; eso es coherente con `objetivo_aprendizaje_contenido` N:M.

### Consulta de últimos intentos

En PostgreSQL se resuelve con `DISTINCT ON`:

```sql
SELECT DISTINCT ON (a.actividad_id) i.*
FROM intento i
JOIN actividad_curso ac ON ac.actividad_curso_id = i.actividad_curso_id
JOIN actividad a        ON a.actividad_id = ac.actividad_id
JOIN contenido c        ON c.contenido_id = a.contenido_id
JOIN usuario_curso uc   ON uc.curso_id = ac.curso_id AND uc.usuario_id = i.usuario_id
WHERE i.usuario_id = :usuarioId
  AND c.unidad_id  = :unidadId
  AND i.fecha_fin IS NOT NULL
  AND i.fecha_fin <= :fechaCorte
ORDER BY a.actividad_id, i.fecha_fin DESC, i.intento_id DESC;
```

## 4. Flujo implementado

```text
1. GET /api/informes-pedagogicos/preliminar?usuarioId&unidadId[&fechaCorte]
   └─ Solo lectura. Calcula los últimos intentos cerrados hasta fechaCorte.
      Devuelve el `fechaCorte` usado, las actividades consideradas y los
      conteos por OA.

2. El docente revisa el preliminar y define fecha y descripción.

3. POST /api/informes-pedagogicos/desde-resultados
   { usuarioId, unidadId, fecha, descripcion, fechaCorte }
   └─ Recalcula en el servidor con el mismo fechaCorte (no confía en los
      números del preview) y persiste cabecera + detalles en una transacción.

4. Los intentos posteriores quedan fuera porque el cálculo solo ocurre en el
   POST y los GET del informe leen los conteos persistidos.
```

El `POST` debe recibir el `fechaCorte` que devolvió el `preview`. Así, si el
estudiante cierra un intento entre ambos pasos, ese intento no se cuela en el
informe. Si el docente quiere un corte más reciente, vuelve a pedir el preview.

### Endpoints

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/informes-pedagogicos/preliminar` | Consolidado efímero por estudiante y unidad. Parámetros: `usuarioId`, `unidadId`, `fechaCorte` opcional (ISO-8601). |
| `POST` | `/api/informes-pedagogicos/desde-resultados` | Crea el informe y persiste el snapshot. Responde `201` con la cabecera y sus detalles. |

Reglas de la capa HTTP:

- `fechaCorte` ausente equivale a `now()`. Un corte futuro se rechaza con `400`.
- `usuarioId` o `unidadId` inexistentes responden `404`.
- Un usuario sin rol `ESTUDIANTE` responde `400`.
- Un `POST` sin objetivos con resultados responde `400` y no persiste nada.
- `GenerarInformeRequest` valida `usuarioId` y `unidadId` con `@NotNull` y
  `@Positive`.

### DTOs

- `InformePreliminarResponse`: `usuario`, `unidad`, `fechaCorte`,
  `actividades`, `detalles`, `totalCorrectas`, `totalIncorrectas`.
- `ActividadEvaluadaResumen`: `actividadCursoId`, `actividadId`,
  `instrucciones`, `intentoId`, `fechaFin`.
- `DetallePreliminarResponse`: `objetivoAprendizaje`, `correctas`,
  `incorrectas`, `total`, `porcentaje`.
- `InformePedagogicoDetalladoResponse`: `informe` (`InformePedagogicoResponse`)
  y `detalles` (`DetalleInformePedagogicoResponse`).
- `GenerarInformeRequest`: `usuarioId`, `unidadId`, `fecha`, `descripcion`,
  `fechaCorte`.

`InformePedagogicoResponse` incorpora `fechaCorte` y `origen`. El `PUT` del
informe no modifica ninguna de las dos.

## 5. Estado de implementación

| Elemento | Ubicación |
|---|---|
| Enum `OrigenInforme` | `informe/model/OrigenInforme.java` |
| `InformePedagogico.fechaCorte` y `.origen` | `informe/model/InformePedagogico.java` |
| Cálculo y persistencia | `informe/service/InformePreliminarService.java` |
| Resolución de estudiante/unidad y defaults | `informe/service/InformePedagogicoService.java` |
| Endpoints | `informe/controller/InformePedagogicoController.java` |
| Últimos intentos por unidad y corte | `evaluacion/repository/IntentoRepository.java` |
| Respuestas con alternativa resuelta | `evaluacion/repository/RespuestaRepository.java` |
| Preguntas por actividad | `curriculo/repository/PreguntaActividadRepository.java` |
| OA por contenido de la unidad | `curriculo/repository/ObjetivoAprendizajeContenidoRepository.java` |

`InformePreliminarService.generarDesdeResultados` recibe entidades ya resueltas
desde `calcular(...)`, crea la cabecera con `origen = AUTOMATICO` y guarda los
detalles con `saveAll` en la misma transacción. Los conteos persistidos son el
snapshot; no existe tabla de auditoría de intentos.

## 6. Riesgos y decisiones abiertas

- **`IntentoRequest` acepta `fechaInicio` y `fechaFin`.** `IntentoService.crear`
  persiste ambos tal cual, lo que permite crear un intento ya cerrado con una
  fecha arbitraria. Para que el corte sea confiable, el servidor debería fijar
  `fechaInicio = now()` y `fechaFin = null`, y cerrar solo con
  `POST /api/intentos/{id}/cerrar`.
- **Preguntas sin responder:** se excluyen por defecto. Si se quieren reportar,
  agregar `sin_responder` a `detalle_informe_pedagogico`.
- **Corrección estricta:** se exige que todas las alternativas seleccionadas
  sean correctas. Si se prefiere "basta una correcta", ajustar la regla en el
  servicio y documentarla.
- **Informes automáticos:** si se permite editar los conteos de un informe
  `AUTOMATICO`, la fotografía deja de ser inmutable. Recomendado bloquear el
  `PUT` de detalles para ese origen o registrar la corrección.
- **Auditoría:** los conteos persistidos bastan para el requisito actual. Si se
  necesita saber exactamente qué intentos alimentaron un informe, agregar una
  tabla puente `informe_intento` en una migración posterior.
