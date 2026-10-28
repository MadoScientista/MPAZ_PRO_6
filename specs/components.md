# Componentes de UI

Resumen de los componentes ya implementados en `app/src/main/java/com/ccstudio/mpaz_pro_6/ui/components/`.
Todos son funciones `@Composable` top-level: no hay clases envoltorio.

Convenciones que se repiten en todos:

- `modifier: Modifier = Modifier` suele ser el último parámetro obligatorio, antes de los opcionales.
- Los callbacks de acción (`onXxx`) van al final de la lista de parámetros.
- Los componentes no hablan con repositorios ni con ViewModels: reciben datos y estado por parámetro.
- Los textos visibles están en español y los identificadores en inglés.
- Los colores salen siempre de `MaterialTheme.colorScheme`.

---

## Componentes comunes

Ubicación: `ui/components/common/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `AppBottomBar` | `destinations: List<BottomDestination>`, `selectedRoute: String?`, `onSelect: (BottomDestination) -> Unit`, `modifier` | Barra de navegación inferior con un ítem de icono y etiqueta por destino | Marca como seleccionado el destino indicado y emite el destino tocado; no conoce el rol ni el `NavController`, así que el llamador resuelve la navegación. En un detalle hijo, el llamador pasa la ruta de la pestaña padre para que la sección siga marcada. Usa los colores neutros del tema en ambos modos |
| `AppButton` | `text`, `onClick`, `modifier`, `enabled`, `variant` (`PRIMARY`, `OUTLINED`, `TEXT`), `containerColor`, `contentColor` | Botón con el estilo del tema según la variante | Ejecuta `onClick`; ocupa todo el ancho disponible |
| `AppTextField` | `value`, `onValueChange`, `label`, `modifier`, `placeholder`, `supportingText`, `isPassword`, `passwordVisible`, `singleLine`, `enabled`, `trailingIcon` | Campo `OutlinedTextField` con etiqueta y textos de apoyo, con esquinas de 12 dp (`TextFieldShape`) | Emite cada cambio de texto; aplica el modo contraseña y el icono final opcional |
| `AppTopBar` | `title` (slot `@Composable () -> Unit`), `modifier`, `onNavigateBack` (nullable), `actions` (slot `RowScope`) | Barra superior de 72 dp con título/identidad y acciones, montada por el `Scaffold` de `AppNavGraph` | Muestra el botón de retroceso solo si `onNavigateBack` no es `null`; en pestañas el slot recibe `ProfileHeader` y en detalles/formularios, un `Text` con el título. Usa `Surface` propia en lugar de `TopAppBar` para que el color cambie al instante con el tema. Colores de `LocalTopBarColors` |
| `ConfirmDialog` | `title`, `message`, `onConfirm`, `onDismiss`, `confirmLabel`, `dismissLabel` | Diálogo de confirmación con dos botones | Confirma o descarta; se usa antes de todo borrado |
| `EmptyState` | `message`, `modifier`, `description` (nullable) | Icono informativo, mensaje y descripción opcional | Solo presenta el estado vacío; no ofrece acciones |
| `ErrorState` | `error: ApiError`, `modifier`, `onRetry` (nullable) | Icono de advertencia y `error.message` | Muestra el motivo del fallo y un botón «Reintentar» si se pasa `onRetry` |
| `FilterChipRow` | `options: List<String>`, `selected: String`, `onSelect`, `modifier`, `disabledOptions: Set<String>` | Fila horizontal desplazable de `FilterChip` | Emite el valor del chip tocado; la pantalla decide qué significa. Las opciones de `disabledOptions` se muestran bloqueadas para que la pantalla explique por qué no están disponibles |
| `LoadingState` | `modifier`, `message` (por defecto `"Cargando..."`) | Indicador circular y mensaje | Presenta la espera de datos |
| `ProfileHeader` | `userName: String`, `role: UserRole`, `modifier` | Cabecera compacta para la barra superior: avatar circular de 40 dp con ícono de persona, saludo «Hola, nombre» y el rol debajo | La usa `AppTopBar` en todos los destinos de nivel superior (pestañas), en lugar del título. El nombre va en `titleMedium` y el rol en `bodySmall`; el avatar es decorativo. Toma los colores de `LocalTopBarColors` |
| `SearchField` | `value`, `onValueChange`, `modifier`, `placeholder`, `label` | Campo de búsqueda con lupa, botón de limpiar y esquinas de 12 dp (`TextFieldShape`) | Emite el texto; la búsqueda libre es siempre client-side |
| `SectionHeader` | `title`, `modifier`, `count` (nullable) | Título de sección y contador opcional | Separa visualmente secciones de una pantalla |
| `ThemeToggleButton` | `isDarkTheme`, `onToggleTheme`, `modifier` | Ícono con la acción disponible: luna cuando el tema es claro y sol cuando es oscuro | Alterna entre tema claro y oscuro; el estado lo resuelve `MainActivity` y lo persiste `ThemePreferenceStore` |
| `StatusChip` | `label`, `modifier`, `containerColor`, `contentColor` | Etiqueta de estado con formato de chip | Presenta un estado ya resuelto por la pantalla; por defecto usa el color neutro del tema y la pantalla elige el color semántico cuando corresponde |

Los estados (unidad, actividad, dificultad, informe, nivel por objetivo y rol) usan la paleta semántica `AppColors` (`success`, `warning`, `danger`, `info`, `accent`, `neutral`) que provee `MPAZ_PRO_6Theme`. No se reutilizan los roles container de Material para estados, así cada chip lleva su par `container`/`content` y no depende de `contentColorFor`.

---

## Asignaturas

Ubicación: `ui/components/subject/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `SubjectCard` | `subject: Subject`, `unitCount: Int`, `pendingCount: Int`, `onViewDetail`, `onEdit`, `onDelete`, `modifier`, `showActions` | Badge con el color de la materia, nombre, «N unidades», chip de unidades en revisión y acciones de editar/eliminar | Toda la tarjeta abre el detalle de la asignatura. El chip es `warning` si hay unidades `EN_REVISION` y `success` si no. El borrado siempre se ofrece: un `409 Conflict` se explica en la pantalla. Con `showActions = false` oculta los iconos de edición y borrado |
| `SubjectCarouselCard` | `subject: Subject`, `unitCount: Int`, `onClick`, `modifier` | Tarjeta compacta con badge circular del color de la materia, nombre, «N unidades» y chevron | Abre el listado de asignaturas al tocarla; se usa en el carrusel del panel de UTP. Con 0 unidades muestra «Sin unidades» |
| `SubjectForm` | `nombre`, `onNombreChange`, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Campo de nombre con contador y botones «Crear/Guardar» y «Cancelar» | Exige nombre no vacío de hasta 100 caracteres. La pantalla arma el `CreateSubjectRequest` o el `UpdateSubjectRequest` |

---

## Cursos

Ubicación: `ui/components/course/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `CourseCard` | `course: Course`, `studentCount: Int`, `onViewDetail`, `onEdit`, `onDelete`, `modifier`, `showActions` | Badge con icono de curso, nombre, «año · N estudiantes», iconos de editar/eliminar y botón «Ver detalle» | Abre el detalle del curso desde el botón. El borrado siempre se ofrece: un `409 Conflict` se explica en la pantalla. Con `showActions = false` oculta los iconos de edición y borrado |
| `CourseCarouselCard` | `course: Course`, `studentCount: Int`, `onClick`, `modifier` | Tarjeta compacta con badge, nombre, «año · N estudiantes» y chevron | Abre el detalle del curso al tocarla; se usa en los carruseles de los paneles de UTP y docente |
| `CourseForm` | `nombre`, `anio`, `onNombreChange`, `onAnioChange`, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Campos de nombre y año con contador/ayuda y botones | Exige nombre (máx. 50) y año entre 2000 y 2100. En edición la pantalla envía solo lo modificado |

---

## Unidades

Ubicación: `ui/components/unit/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `UnitCard` | `unit: LearningUnit`, `subjectName`, `onViewDetail`, `onAuthorize` (nullable), `modifier`, `contentCount` (nullable), `activityCount` (nullable), `hasFinalTest` (nullable) | Chip de asignatura, título, nivel educativo, `UnitStatusChip`, resumen opcional de contenidos/actividades/prueba final y botones «Ver detalle» y «Autorizar» | Ejecuta la acción correspondiente. El resumen solo se muestra cuando la pantalla entrega los conteos; la pantalla decide cuándo mostrar la tarjeta según el rol y el estado |
| `UnitCarouselCard` | `unit: LearningUnit`, `subjectName`, `onClick`, `modifier` | Tarjeta compacta con asignatura (punto de su color), estado, título y nivel, sin botones | Abre el detalle de la unidad al tocarla; se usa en los carruseles de los paneles de UTP y docente |
| `UnitForm` | `subjects`, `asignaturaId`, `nivelEducativo`, `titulo`, `orden`, `estado`, callbacks de cada campo, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Selector de asignatura (campo y menú de 12 dp), campos de nivel, título y orden, estado y botones | Valida título (máx. 200), nivel (máx. 50) y orden (> 0). La asignatura no se cambia en edición; la creación fija `BORRADOR`; los estados se gestionan desde autorización |
| `UnitStatusChip` | `status: ContentStatus`, `modifier` | Chip con la etiqueta visible (`Borrador`, `En revisión`, `Autorizado`, `Publicado`) | Asigna la familia semántica según el estado: neutral, warning, info y success |
| `SubjectLabel` | `subjectName: String`, `modifier` | Punto de color de la materia + nombre en gris | Deriva el color del nombre (mapa de palabras clave y hash de respaldo). No dibuja el punto para «Sin asignatura» ni «Unidad a revisar». El texto usa `onSurfaceVariant` para no competir con el título de la unidad |
| `UnitSummaryHeader` | `unit: LearningUnit`, `subjectName`, `modifier` | Asignatura, título grande, nivel educativo y estado | Encabeza el detalle de una unidad |

---

## Objetivos de aprendizaje

Ubicación: `ui/components/learningobjective/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `LearningObjectiveCard` | `objective: LearningObjective`, `onEdit`, `onDelete`, `modifier`, `unitTitle` (nullable) | Chip con el código (o «Sin código»), unidad opcional, descripción, eje, texto de referencia opcional y botones «Editar» y «Eliminar» | Presenta el objetivo y delega la edición o el borrado. El borrado siempre se ofrece: un `409 Conflict` se explica en la pantalla |
| `LearningObjectiveForm` | `codigo`, `descripcion`, `eje`, `textoReferencia`, callbacks de cada campo, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Campos de código, descripción, eje y texto de referencia con contadores y botones | Exige descripción y eje; valida código (máx. 50), eje (máx. 100) y texto de referencia (máx. 500). La unidad la gestiona la pantalla |

---

## Contenidos y recursos

Ubicación: `ui/components/content/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `ContentCard` | `content: Content`, `resourceCount: Int`, `onViewContent`, `modifier`, `objectiveLabels: List<String>`, `onEdit` (nullable), `onDelete` (nullable) | Título, códigos de objetivos, resumen de la explicación (3 líneas), cantidad de recursos y botón «Ver contenido» | Delega el detalle. Las acciones de edición y borrado son opcionales: en una pantalla de solo consulta se omiten. Al eliminar, el backend borra en cascada los recursos |
| `ContentForm` | `titulo`, `explicacion`, `ejemplos`, `instrucciones`, `orden`, `origen`, callbacks de cada campo, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Campos de título, explicación, ejemplos, instrucciones, orden, chips de origen y botones | Exige título (máx. 200) y explicación; valida orden (> 0). Ejemplos, instrucciones y orden son opcionales. El OA lo gestiona la pantalla |
| `ContentResourceCard` | `resource: ContentResource`, `onOpen`, `modifier`, `onEdit` (nullable), `onDelete` (nullable) | Chip de tipo (`Imagen`/`Audio`), descripción opcional, URL y botón «Abrir enlace» | Abre el enlace o delega la edición/borrado. Las acciones de edición y borrado son opcionales: en una pantalla de solo consulta se omiten |
| `ContentResourceForm` | `tipo`, `url`, `descripcion`, `origen`, callbacks de cada campo, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Chips de tipo y origen, campos de URL y descripción con contadores y botones | Exige URL y valida URL/descripción (máx. 500). El contenido asociado lo gestiona la pantalla |

---

## Actividades

Ubicación: `ui/components/activity/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `ActivityTypeChip` | `activityType: ActivityType`, `modifier` | Chip con la etiqueta visible (`Actividad`, `Desafío`, `Prueba final`) | Asigna el color del tema según el tipo; la `PRUEBA_FINAL` usa el color de error por sus reglas propias |
| `StudentActivityCard` | `activity: Activity`, `unitTitle`, `status: StudentActivityStatus`, `attemptsCount`, `onClick`, `modifier` | Tipo, orden, título, unidad, chip de estado (`Pendiente`, `En progreso`, `Completada`), cantidad de intentos y texto de acción (`Resolver`, `Continuar`, `Ver resultado`) | Presenta la actividad al estudiante y emite el toque; la pantalla decide si abre el intento o el resultado |
| `StudentActivityCarouselCard` | `activity: Activity`, `unitTitle`, `status: StudentActivityStatus`, `onClick`, `modifier` | Tarjeta compacta con tipo, orden, estado, título, unidad y acción (`Resolver` o `Continuar`) | Abre el intento de la actividad al tocarla; se usa en el carrusel de actividades pendientes del panel del estudiante |
| `ActivityCard` | `activity: Activity`, `questionsCount: Int`, `modifier`, `unitTitle` (nullable), `onViewDetail` (nullable), `onEdit` (nullable), `onDelete` (nullable), `detailLabel` | Chip de tipo, orden, título, unidad opcional, cantidad de preguntas y botón opcional de detalle | Presenta la actividad y delega las acciones. En modo consulta se omiten las acciones; `detailLabel` permite mostrar «Resolver» al estudiante. El borrado siempre se ofrece cuando la pantalla lo habilita: un `409 Conflict` se explica en la pantalla |
| `ActivityForm` | `titulo`, `tipo`, `orden`, `contents`, `contenidoId`, callbacks de cada campo, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error`, `hasFinalTest`, `isContentLocked` | Campos de título y orden, chips de tipo con `Prueba final` bloqueada si ya existe una y botones | Exige título (máx. 200) y valida orden (> 0 si se escribe). La unidad la gestiona la pantalla. Al crear, el orden vacío deja que el backend aplique `1`; al editar, vacío conserva el valor actual. Con `isContentLocked` el contenido queda fijo y la prueba final no se ofrece |

---

## Usuarios

Ubicación: `ui/components/user/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `UserCard` | `user: User`, `onEdit`, `onDelete`, `modifier`, `courseName` (nullable), `showActions` | Badge con icono de persona, nombre y subtítulo `@usuario · rol` (más el curso si es estudiante), con iconos de editar/eliminar | Ejecuta la acción de edición o borrado; no tiene detalle. El nombre del curso lo resuelve la pantalla. Con `showActions = false` oculta los iconos para listados de solo lectura |
| `UserForm` | `nombre`, `username`, `password`, `rol`, `cursoId`, `courses`, callbacks de cada campo, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Campos de nombre, usuario y contraseña, chips de rol y selector de curso para estudiantes (campo y menú de 12 dp) | Valida el `username` (3–100 caracteres) y exige contraseña al crear. Al cambiar a un rol que no es estudiante limpia el curso. En edición la contraseña vacía no se envía |

---

## Informes

Ubicación: `ui/components/report/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `ReportCard` | `report: PedagogicalReport`, `studentName`, `unitName`, `onViewDetail`, `modifier`, `showActions`, `showStudentName` | Estudiante, unidad, chip de estado, fechas de generación/validación y botón «Ver informe» | Abre el detalle del informe. Los nombres los resuelve la pantalla. Con `showActions = false` oculta el botón para listados de solo lectura; con `showStudentName = false` usa la unidad como título, para la vista del estudiante |
| `ReportCarouselCard` | `report: PedagogicalReport`, `studentName`, `unitName`, `onClick`, `modifier` | Tarjeta compacta con chip de estado, estudiante, unidad y fecha de generación | Abre el detalle del informe al tocarla; se usa en el carrusel de informes por validar del panel del docente |
| `ReportStatusChip` | `status: ReportStatus`, `modifier` | Chip con la etiqueta visible (`Generado`, `Pendiente de validación`, `Validado`) | Asigna el color del tema según el estado; se comparte entre el listado y el detalle del informe |
| `ReportObjectiveLevelCard` | `detail: ReportObjectiveDetail`, `modifier`, `objectiveCode` (nullable), `objectiveDescription` (nullable) | Código del OA, chip de nivel (`Fortaleza`/`Refuerzo`), barra de progreso y correctas sobre total | Presenta el nivel ya calculado por el backend; con `total = 0` muestra «Sin preguntas respondidas» |

---

## Preguntas y alternativas

Ubicación: `ui/components/question/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `QuestionCard` | `question: Question`, `alternativesCount`, `onEdit`, `onDelete`, `modifier`, `activityTitle` (nullable) | Chip de dificultad, orden, enunciado, actividad opcional, criterio de respuesta opcional, cantidad de alternativas y botones «Editar» y «Eliminar» | Presenta la pregunta y delega la edición o el borrado. El borrado siempre se ofrece: un `409 Conflict` se explica en la pantalla |
| `QuestionForm` | `enunciado`, `criterioRespuesta`, `dificultad`, `orden`, callbacks de cada campo, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Campos de enunciado y criterio, chips de dificultad (`BAJA`/`MEDIA`/`ALTA`), campo de orden y botones | Exige enunciado y valida el orden (> 0). El criterio y el orden son opcionales. No incluye actividad, unidad ni OA: los gestiona la pantalla |
| `AlternativeRow` | `alternative: Alternative`, `onEdit`, `onDelete`, `modifier`, `isReadOnly` | Texto de la alternativa, chip «Correcta»/«Incorrecta» y acciones «Editar»/«Eliminar» | Presenta una alternativa de la lista. En modo de solo lectura oculta las acciones |
| `AlternativeForm` | `texto`, `esCorrecta`, `onTextoChange`, `onEsCorrectaChange`, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Campo de texto con contador, casilla «Es la alternativa correcta» y botones | Valida texto no vacío y máximo 500 caracteres. Refleja el valor de `esCorrecta`; la selección exclusiva la aplica la pantalla |

Nota: el chip de dificultad y el chip de corrección son funciones privadas dentro de `QuestionCard` y `AlternativeRow`; no se usan fuera de su archivo.

---

## Asignaciones

Ubicación: `ui/components/assignment/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `AssignmentCard` | `assignment: Assignment`, `courseName`, `unitTitle`, `teacherName`, `onViewDetail`, `onEdit`, `onDelete`, `modifier`, `showActions`, `onClick` | Chip del curso, título de la unidad, docente, fecha, chip de prueba final habilitada/deshabilitada, límite de intentos y botones «Ver detalle», «Editar» y «Eliminar» | Presenta la asignación y delega las acciones. El borrado siempre se ofrece: un `409 Conflict` se explica en la pantalla. Con `showActions = false` oculta las acciones para listados de solo lectura; con `onClick` no nulo, toda la tarjeta abre el detalle |
| `AssignmentForm` | `courses`, `units`, `teachers`, `cursoId`, `unidadId`, `docenteId`, `fecha`, `maxIntentos`, `pruebaFinalHabilitada`, callbacks de cada campo, `onSubmit`, `onCancel`, `modifier`, `isEditMode`, `isReadOnly`, `isSaving`, `error` | Selectores de curso, unidad y docente (campos y menús de 12 dp), selector de fecha, campo de máximo de intentos, interruptor de prueba final y botones | Exige curso, unidad, docente y fecha; valida `maxIntentos` (> 0 cuando se escribe). En edición bloquea curso, unidad y docente porque el `PUT` no los admite. Solo ofrece docentes con rol `DOCENTE`. La fecha conserva la hora del valor actual |
| `ActivitySelector` | `activities`, `associatedActivityIds: Set<Int>`, `onAssociationChange`, `modifier`, `isReadOnly`, `isSaving` | Lista con casilla por actividad, tipo, orden y chip de `Prueba final` | Emite el nuevo estado de asociación. Las pruebas finales se muestran identificadas y con la casilla deshabilitada porque el backend las rechaza con `400 Bad Request` |
| `ObjectiveSelector` | `objectives`, `associatedObjectiveIds: Set<Int>`, `onAssociationChange`, `modifier`, `isReadOnly`, `isSaving` | Lista con casilla por objetivo, código (o «Sin código»), descripción y eje | Emite el nuevo estado de asociación. La pantalla lo traduce a `POST` o `DELETE` en `/api/asignaciones/{id}/objetivos-aprendizaje/{oaId}` |

---

## Intentos

Ubicación: `ui/components/attempt/`.

| Componente | Recibe | Muestra | Qué hace |
|---|---|---|---|
| `AnswerOption` | `alternative: Alternative`, `modifier`, `isSelected`, `isCorrect` (nullable), `enabled`, `onSelect` (nullable) | Texto de la alternativa, selección radial y chip «Correcta»/«Incorrecta» cuando se conoce el resultado | Emite la alternativa tocada mediante `Modifier.selectable` con rol `RadioButton` y `testTag("alternative-{id}")`. Con `onSelect` nulo queda como solo lectura y conserva la marca de selección. El estado de corrección llega congelado desde el backend y no se recalcula |
| `AttemptProgress` | `attempt: Attempt`, `answeredCount`, `totalQuestions`, `modifier` | Número de intento, chip «En progreso»/«Completado», barra de progreso y contador de preguntas respondidas | Deduce la finalización de `attempt.fechaFin == null` y normaliza el conteo para no salir del rango |
| `AttemptQuestionCard` | `question: Question`, `alternatives`, `selectedAlternativeId`, `onSelectAlternative`, `modifier`, `questionNumber` (nullable), `isReadOnly`, `isLocked`, `showCorrectness`, `enabled` | Enunciado, criterio de respuesta opcional y alternativas con `AnswerOption` | Mantiene la selección exclusiva delegando en la pantalla. Agrupa las alternativas con `Modifier.selectableGroup()`. En solo lectura desactiva las opciones; `isLocked` marca una respuesta ya registrada y bloquea su cambio; `showCorrectness` revela el resultado congelado de cada alternativa |

