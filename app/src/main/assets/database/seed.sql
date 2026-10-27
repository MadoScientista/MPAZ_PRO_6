-- =====================================================================
-- MPAZ PRO 6 - Datos de demo (SQLite)
-- Fuente: common/seed/DataInitializer.java (seccion 11 de la especificacion)
--
-- Los ids son explicitos para que las referencias entre tablas sean
-- deterministas. Los timestamps van como epoch en milisegundos, igual que
-- sqlite-jdbc con date_class=INTEGER.
--
-- La app no guarda contrasenas ni hashes: el login se resuelve contra
-- `/api/auth/login`. Este archivo solo precarga la cache offline con los datos
-- publicos de demostracion.
-- =====================================================================

-- ---------------------------------------------------------------------
-- A) Curso y usuarios
-- ---------------------------------------------------------------------

INSERT INTO curso (id, nombre, anio) VALUES
    (1, '6A', 2026);

INSERT INTO usuario (id, nombre, username, rol, curso_id) VALUES
    (1, 'Patricia Arancibia', 'utp', 'UTP', NULL),
    (2, 'Diego Salinas', 'docente', 'DOCENTE', NULL),
    (3, 'Camila Rivas', 'camila', 'ESTUDIANTE', 1),
    (4, 'Mateo Cortés', 'mateo', 'ESTUDIANTE', 1),
    (5, 'Javiera Muñoz', 'javiera', 'ESTUDIANTE', 1),
    (6, 'Benjamín Ortiz', 'benjamin', 'ESTUDIANTE', 1);

-- ---------------------------------------------------------------------
-- B) Asignaturas y unidades
-- ---------------------------------------------------------------------

INSERT INTO asignatura (id, nombre) VALUES
    (1, 'Lenguaje y Comunicación'),
    (2, 'Matematica'),
    (3, 'Ciencias Naturales'),
    (4, 'Historia, Geografía y Ciencias Sociales');

INSERT INTO unidad (id, asignatura_id, titulo, orden, estado, autorizada_por_id, fecha_autorizacion) VALUES
    (1, 1, 'Textos informativos: tema e idea principal', 1, 'PUBLICADO', 1, 1772442000000),
    (2, 2, 'Operaciones con números naturales y decimales', 1, 'AUTORIZADO', 1, 1772442300000),
    (3, 3, 'La materia y la energía en los seres vivos', 1, 'PUBLICADO', 1, 1772442600000),
    (4, 4, 'Ubicación temporal y espacial de hechos históricos', 1, 'PUBLICADO', 1, 1772442900000),
    (5, 1, 'Lectura de cuentos: personajes, ambiente y secuencia', 2, 'BORRADOR', NULL, NULL),
    (6, 2, 'Fracciones en recetas y medidas', 2, 'EN_REVISION', NULL, NULL);

-- ---------------------------------------------------------------------
-- C) Objetivos de aprendizaje (2 por unidad)
-- ---------------------------------------------------------------------

INSERT INTO objetivo_aprendizaje (id, unidad_id, codigo, descripcion, eje, texto_referencia) VALUES
    (1, 1, 'OA-LC-6A-01', 'Comprender el proposito y la idea principal de un texto informativo breve, distinguiendo tema y detalles.', 'Lectura y comprension', NULL),
    (2, 1, 'OA-LC-6A-02', 'Interpretar mensajes que combinan lenguaje verbal y no verbal, como titulos, subtitulos e imagenes de apoyo.', 'Comprension multimodal', NULL),
    (3, 2, 'OA-MA-6A-01', 'Resolver problemas de la vida diaria usando operaciones con numeros naturales y decimales.', 'Numeros y operaciones', NULL),
    (4, 2, 'OA-MA-6A-02', 'Reconocer y describir patrones numericos y geometricos presentes en objetos y figuras.', 'Patrones y formas', NULL),
    (5, 3, 'OA-CN-6A-01', 'Explicar como se organiza la materia en los seres vivos y que cambios sofre en un proceso observable.', 'Materia y energia', NULL),
    (6, 3, 'OA-CN-6A-02', 'Relacionar la luz con la produccion de energia en las plantas y su importancia para los otros seres vivos.', 'Luz y vida', NULL),
    (7, 4, 'OA-HG-6A-01', 'Ubicar en el tiempo y en el espacio hechos y fenomenos sociales, usando categorias simples.', 'Tiempo y espacio', NULL),
    (8, 4, 'OA-HG-6A-02', 'Reconocer la influencia de las actividades humanas en el ambiente de su comunidad.', 'Convivencia y ambiente', NULL);

-- ---------------------------------------------------------------------
-- D) Contenidos y recursos (1 por OA)
-- ---------------------------------------------------------------------

INSERT INTO contenido (id, oa_id, unidad_id, titulo, explicacion, ejemplos, instrucciones, orden, origen) VALUES
    (1, 1, 1, 'Identificar el tema y la idea principal',
        'Un texto informativo explica algo de forma ordenada. Su idea principal responde a la pregunta de que trata, y los detalles la acompanan sin reemplazarla. Para encontrarla, conviene leer el titulo y el primer parrafo antes de seguir.',
        'Un texto sobre el ciclo del agua suele decir en su primera linea que tratara sobre ese proceso.',
        'Busca el titulo y la primera frase. Escribi en una linea de que trata el texto.',
        1, 'PROPIO'),
    (2, 2, 1, 'Interpretar títulos, imágenes y diagramas',
        'Muchos textos informativos incorporan imagenes, diagramas o infografias. La imagen no decora: aporta datos que el texto no repite. Para leerla hay que identificar que muestra, que rotulos tiene y que relacion propone.',
        'En un diagrama de etapas, cada recuadro es un paso y las flechas indican el orden.',
        'Mira la imagen y responde: que dato agrega al texto y donde lo encuentras.',
        1, 'PROPIO'),
    (3, 3, 2, 'Elegir operaciones para resolver problemas',
        'Un problema de la vida diaria se resuelve identificando primero que se pide y despues eligiendo la operacion. Sumar acumula, restar quita, multiplicar repite y divide repartir. Elegir mal la operacion es la causa mas comun de error.',
        'Si hay 6 cajas con 8 utiles cada una y se necesitan 40, toca multiplicar y luego restar.',
        'Subraya que se pide antes de calcular. Anota la operacion que elegiste y por que.',
        1, 'PROPIO'),
    (4, 4, 2, 'Reconocer y continuar patrones',
        'Un patron es una secuencia que se repite con la misma regla. Se puede describir, continuar y anticipar. En figuras, el patron suele ser una regla de posicion o de forma que se aplica a cada elemento.',
        'Una fila de 3 cuadrados y un circulo que se repite cada cuatro figuras.',
        'Completa la secuencia y explica la regla con la que la completaste.',
        1, 'PROPIO'),
    (5, 5, 3, 'Observar cambios de la materia',
        'La materia es todo lo que ocupa espacio. En los seres vivos cambia de forma sin dejar de ser materia: el hielo se derrite, una hoja verde se pone amarilla, la madera se convierte en ceniza. Estos cambios se pueden describir y observar.',
        'Al dejar un cubo de hielo a temperatura ambiente, pasa de solido a liquido sin volverse otra sustancia.',
        'Describe un cambio de estado o de aspecto que hayas observado en una planta o en un objeto.',
        1, 'PROPIO'),
    (6, 6, 3, 'La luz como fuente de energía para las plantas',
        'Las plantas captan energia de la luz y la usan para fabricar su propio alimento. Ese alimento es la base de la cadena alimentaria: sin el, los animales no tendrian energia disponible.',
        'Por eso las areas con poca luz tienen menos vegetacion y menos animales asociados.',
        'Explica con tus palabras por que se dice que las plantas son la base de la cadena alimentaria.',
        1, 'PROPIO'),
    (7, 7, 4, 'Ordenar hechos en una línea de tiempo y ubicarlos en un mapa',
        'Ubicar un hecho significa ubicar cuando ocurrio y donde. El tiempo se ordena con categorias amplias y el lugar se ubica con un punto de referencia mas general. Un mismo hecho puede explicarse distinto segun desde que lugar se mire.',
        'Las lluvias de invierno pueden cambiar el caudal de un río y las actividades de una comunidad.',
        'Ubica un hecho que ocurrio en tu comuna y di por que importa saber donde y cuando.',
        1, 'PROPIO'),
    (8, 8, 4, 'Actividades humanas y cambios en el entorno',
        'Las actividades humanas, como construir, cultivar o sacar minerales, modifican el ambiente. Los cambios pueden mejorar o deteriorar, y el efecto depende de como se haga la actividad.',
        'Un parque bien cuidado mantiene arboles y deja el suelo permeable; uno descuidado pierde vegetacion.',
        'Describe una actividad de tu barrio y que cambio produce en el ambiente.',
        1, 'PROPIO');

INSERT INTO recurso_contenido (id, contenido_id, tipo, url, descripcion, origen) VALUES
    (1, 1, 'IMAGEN', 'recursos/texto-informativo-organizacion.png', 'Diagrama con las cuatro partes de un texto informativo: titulo, introduccion, desarrollo y cierre.', 'PROPIO'),
    (2, 2, 'IMAGEN', 'recursos/infografia-ciclo-agua.png', 'Infografia de seis recuadros con las etapas del ciclo del agua.', 'PROPIO'),
    (3, 3, 'IMAGEN', 'recursos/jerarquia-operaciones.png', 'Organigrama que ordena las operaciones segun la accion que realizar.', 'PROPIO'),
    (4, 4, 'IMAGEN', 'recursos/patrones-figuras.png', 'Franja con cinco figuras de una secuencia y la regla escrita al lado.', 'PROPIO'),
    (5, 5, 'AUDIO', 'recursos/auditivo-cambio-estado.mp3', 'Transcripcion de una explicacion de 90 segundos sobre cambios de estado.', 'PROPIO'),
    (6, 6, 'IMAGEN', 'recursos/cadena-alimentaria.png', 'Cadena de cuatro niveles con flechas que unen sol, planta, insecto y ave.', 'PROPIO'),
    (7, 7, 'IMAGEN', 'recursos/mapa-comuna-marcadores.png', 'Mapa simple de la comuna con cuatro puntos de referencia y una leyenda.', 'PROPIO'),
    (8, 8, 'AUDIO', 'recursos/auditivo-entorno-comunitario.mp3', 'Transcripcion de una entrevista breve a dos vecinos sobre su plaza.', 'PROPIO');

-- ---------------------------------------------------------------------
-- E) Actividades (ACTIVIDAD, DESAFIO y PRUEBA_FINAL por unidad)
-- ---------------------------------------------------------------------

INSERT INTO actividad (id, unidad_id, contenido_id, titulo, tipo, orden) VALUES
    (1, 1, 1, 'Actividad: tema e idea principal de un texto', 'ACTIVIDAD', 1),
    (2, 1, 2, 'Desafío: interpretar un afiche informativo', 'DESAFIO', 2),
    (3, 1, NULL, 'Evaluación: textos informativos', 'PRUEBA_FINAL', 3),
    (4, 2, 3, 'Actividad: resolver problemas con operaciones', 'ACTIVIDAD', 1),
    (5, 2, 4, 'Desafío: descubrir la regla de una secuencia', 'DESAFIO', 2),
    (6, 2, NULL, 'Evaluación: operaciones y patrones', 'PRUEBA_FINAL', 3),
    (7, 3, 5, 'Actividad: cambios de estado', 'ACTIVIDAD', 1),
    (8, 3, 6, 'Desafío: construir una cadena alimentaria', 'DESAFIO', 2),
    (9, 3, NULL, 'Evaluación: materia y energía', 'PRUEBA_FINAL', 3),
    (10, 4, 7, 'Actividad: leer una línea de tiempo', 'ACTIVIDAD', 1),
    (11, 4, 8, 'Desafío: cambios en el entorno local', 'DESAFIO', 2),
    (12, 4, NULL, 'Evaluación: tiempo, espacio y comunidad', 'PRUEBA_FINAL', 3);

-- ---------------------------------------------------------------------
-- F) Preguntas y alternativas de las actividades 1, 4, 7 y 10,
--    y de la prueba final de la unidad 1 (actividad 3)
-- ---------------------------------------------------------------------

INSERT INTO pregunta (id, actividad_id, unidad_id, oa_id, enunciado, criterio_respuesta, dificultad, orden) VALUES
    (1, 3, 1, 1, 'Lee el siguiente texto sobre el ciclo del agua y responde: el texto informa principalmente sobre',
        'Responde en una frase. Basta con nombrar el tema del texto.', 'BAJA', 1),
    (2, 3, 1, 1, 'Cual de estas partes de un texto informativo aparece primero y anuncia de que se tratara',
        'Marca una sola alternativa y explica en una linea por que.', 'BAJA', 2),
    (3, 3, 1, 2, 'Observa la infografia que acompana al texto. Que dato aporta la imagen que el texto no repite',
        'Menciona un dato concreto de la imagen.', 'MEDIA', 1),
    (4, 3, 1, 2, '¿Qué función cumple un subtítulo en un texto informativo?',
        'Explica cómo ayuda a organizar o comprender la información.', 'MEDIA', 2),
    (5, 3, 1, 1, '¿Qué característica permite distinguir un texto informativo de un cuento?',
        'Menciona una característica del propósito o de la organización del texto.', 'ALTA', 1),
    (6, 3, 1, 2, '¿Cómo puede ayudar una imagen a comprender un texto informativo?',
        'Relaciona un dato visual con la información escrita.', 'ALTA', 2),
    (7, 1, 1, 1, 'Cual es la idea principal de un texto informativo',
        'Responde con una frase que resuma el tema.', 'BAJA', 1),
    (8, 1, 1, 2, 'Que parte del texto anuncia de que se tratara',
        'Marca una sola alternativa.', 'BAJA', 2),
    (9, 4, 2, 3, 'Que operacion conviene para repartir una cantidad en partes iguales',
        'Marca una sola alternativa.', 'BAJA', 1),
    (10, 4, 2, 4, 'Que regla sigue una secuencia que repite un circulo cada cuatro figuras',
        'Marca una sola alternativa.', 'MEDIA', 2),
    (11, 7, 3, 5, 'Que cambio observable muestra que la materia cambia de forma',
        'Marca una sola alternativa y justifica con un ejemplo.', 'BAJA', 1),
    (12, 7, 3, 6, 'De donde obtienen energia las plantas',
        'Marca una sola alternativa.', 'MEDIA', 2),
    (13, 10, 4, 7, 'Que datos permiten ubicar un hecho en el tiempo y el espacio',
        'Marca una sola alternativa.', 'BAJA', 1),
    (14, 10, 4, 8, 'Como puede cambiar una actividad humana el ambiente de una comunidad',
        'Marca una sola alternativa y menciona un efecto.', 'MEDIA', 2);

INSERT INTO alternativa (id, pregunta_id, texto, es_correcta) VALUES
    (1, 1, 'las formas de empezar el dia', 0),
    (2, 1, 'el proceso por el que el agua recorre la tierra', 1),
    (3, 1, 'las plantas y su alimento', 0),
    (4, 1, 'las partes de un texto', 0),
    (5, 2, 'El desarrollo', 0),
    (6, 2, 'El titulo', 1),
    (7, 2, 'La conclusion', 0),
    (8, 3, 'Un dato sobre etapas del agua que el texto no detalla', 1),
    (9, 3, 'El numero de palabras del texto', 0),
    (10, 3, 'El nombre de quien lo escribio', 0),
    (11, 4, 'Presenta un tema específico dentro del texto', 1),
    (12, 4, 'Reemplaza toda la información del texto', 0),
    (13, 4, 'Indica siempre la opinión del autor', 0),
    (14, 5, 'Su propósito principal es informar o explicar un tema', 1),
    (15, 5, 'Siempre narra hechos imaginarios con personajes', 0),
    (16, 5, 'Su único propósito es expresar sentimientos', 0),
    (17, 6, 'La imagen puede aportar datos que complementan el texto', 1),
    (18, 6, 'La imagen siempre contradice el texto', 0),
    (19, 6, 'Las imágenes no aportan información', 0),
    (20, 7, 'De que trata el texto y cual es su idea principal', 1),
    (21, 7, 'Cuantas palabras tiene el texto', 0),
    (22, 7, 'Quien escribio el texto', 0),
    (23, 8, 'El titulo', 1),
    (24, 8, 'La ultima frase', 0),
    (25, 8, 'Las letras grandes', 0),
    (26, 9, 'Dividir', 1),
    (27, 9, 'Sumar', 0),
    (28, 9, 'Multiplicar', 0),
    (29, 10, 'Se repite cada cuatro posiciones', 1),
    (30, 10, 'Cambia de color en cada figura', 0),
    (31, 10, 'No sigue ninguna regla', 0),
    (32, 11, 'El hielo pasa de solido a liquido', 1),
    (33, 11, 'El hielo deja de ser materia', 0),
    (34, 11, 'El agua se convierte en otra sustancia', 0),
    (35, 12, 'De la luz', 1),
    (36, 12, 'De los insectos que come', 0),
    (37, 12, 'No necesita energia', 0),
    (38, 13, 'Cuando ocurrio y donde ocurrio', 1),
    (39, 13, 'Solo el nombre del protagonista', 0),
    (40, 13, 'El color del mapa', 0),
    (41, 14, 'Construir puede cambiar el suelo y la vegetacion', 1),
    (42, 14, 'Las actividades humanas no afectan el ambiente', 0),
    (43, 14, 'Solo el clima cambia el ambiente', 0);

-- ---------------------------------------------------------------------
-- G) Asignaciones del docente
-- ---------------------------------------------------------------------

INSERT INTO asignacion (id, curso_id, unidad_id, docente_id, max_intentos, prueba_final_habilitada, fecha) VALUES
    (1, 1, 1, 2, 2, 1, 1775462400000),
    (2, 1, 2, 2, 2, 0, 1775462700000),
    (3, 1, 3, 2, 1, 0, 1775463000000),
    (4, 1, 4, 2, 1, 0, 1775463300000);

INSERT INTO asignacion_oa (asignacion_id, unidad_id, oa_id) VALUES
    (1, 1, 1),
    (1, 1, 2),
    (2, 2, 3),
    (2, 2, 4),
    (3, 3, 5),
    (3, 3, 6),
    (4, 4, 7),
    (4, 4, 8);

INSERT INTO asignacion_actividad (asignacion_id, unidad_id, actividad_id) VALUES
    (1, 1, 1),
    (2, 2, 4),
    (3, 3, 7),
    (4, 4, 10);

-- ---------------------------------------------------------------------
-- H) Intentos y respuestas de ejemplo
--    Camila: 2 intentos (el ultimo completado), Mateo: 1 completado,
--    Javiera: 1 en progreso, Benjamin: sin intentos.
-- ---------------------------------------------------------------------

INSERT INTO intento (id, estudiante_id, actividad_id, numero, fecha_inicio, fecha_fin) VALUES
    (1, 3, 3, 1, 1777888800000, 1777889640000),
    (2, 3, 3, 2, 1778080800000, 1778081580000),
    (3, 4, 3, 1, 1777978800000, 1777979940000),
    (4, 5, 3, 1, 1778169600000, NULL);

INSERT INTO respuesta_estudiante (id, intento_id, pregunta_id, alternativa_id, es_correcta) VALUES
    (1, 2, 1, 2, 1),
    (2, 2, 2, 6, 1),
    (3, 2, 3, 8, 1),
    (4, 2, 4, 11, 0),
    (5, 2, 5, 15, 0),
    (6, 2, 6, 19, 0),
    (7, 3, 1, 1, 0),
    (8, 3, 2, 5, 0),
    (9, 3, 3, 8, 1),
    (10, 3, 4, 12, 1),
    (11, 3, 5, 14, 1),
    (12, 3, 6, 18, 0);

-- ---------------------------------------------------------------------
-- I) Informe de ejemplo de Camila en la unidad 1
--    OA 1 -> 2 correctas de 3 -> REFUERZO
--    OA 2 -> 1 correcta de 3 -> REFUERZO
-- ---------------------------------------------------------------------

INSERT INTO informe_pedagogico (id, estudiante_id, unidad_id, docente_id, fecha_generacion, estado, fecha_validacion) VALUES
    (1, 3, 1, 2, 1778230800000, 'PENDIENTE_VALIDACION', NULL);

INSERT INTO detalle_informe_oa (id, informe_id, oa_id, total_preguntas, correctas, nivel) VALUES
    (1, 1, 1, 3, 2, 'REFUERZO'),
    (2, 1, 2, 3, 1, 'REFUERZO');



-- ---------------------------------------------------------------------
-- J) Unidades en BORRADOR y EN_REVISION con contenido y actividades
--    para probar el detalle de unidad y la revisión de UTP.
-- ---------------------------------------------------------------------

INSERT INTO objetivo_aprendizaje (id, unidad_id, codigo, descripcion, eje, texto_referencia) VALUES
    (9, 5, 'OA-LEN-6B-01', 'Analizar narraciones identificando personajes, ambiente y acontecimientos principales.', 'Lectura', NULL),
    (10, 5, 'OA-LEN-6B-02', 'Describir cómo las acciones de los personajes contribuyen al desarrollo de una narración.', 'Lectura', NULL),
    (11, 6, 'OA-MAT-6B-01', 'Representar fracciones y explicar su significado en situaciones cotidianas.', 'Números y operaciones', NULL),
    (12, 6, 'OA-MAT-6B-02', 'Resolver problemas que involucren fracciones de una cantidad y equivalencias sencillas.', 'Números y operaciones', NULL);

INSERT INTO contenido (id, oa_id, unidad_id, titulo, explicacion, ejemplos, instrucciones, orden, origen) VALUES
    (9, 9, 5, 'Personajes, ambiente y acontecimientos',
     'En un cuento, los personajes participan en los acontecimientos y actúan en un lugar y un tiempo determinados. Para comprender la historia, identifica quiénes participan, dónde ocurre y qué hechos cambian la situación inicial.',
     'En un cuento ambientado en una escuela, una estudiante encuentra una mochila perdida y busca a su dueño.',
     'Lee un cuento breve e identifica al personaje principal, el ambiente y dos acontecimientos importantes.',
     1, 'PROPIO'),
    (10, 10, 5, 'Cómo las decisiones cambian una historia',
     'Las decisiones de los personajes producen consecuencias y permiten que la historia avance. Explicar una acción implica señalar qué hizo el personaje y qué ocurrió después.',
     'Si un personaje decide pedir ayuda, puede resolver un problema que no lograba solucionar por sí solo.',
     'Elige una decisión de un personaje y explica qué consecuencia tuvo en la historia.',
     2, 'PROPIO'),
    (11, 11, 6, 'Fracciones en recetas y medidas',
     'Una fracción representa partes iguales de una unidad. El denominador indica en cuántas partes iguales se divide la unidad y el numerador cuántas de esas partes se consideran.',
     'Si una receta requiere media taza de leche, se utiliza una de dos partes iguales de una taza.',
     'Representa con un dibujo 1/2, 1/3 y 3/4 de una unidad e indica qué significan el numerador y el denominador.',
     1, 'PROPIO'),
    (12, 12, 6, 'Calcular una fracción de una cantidad',
     'Para calcular una fracción sencilla de una cantidad, divide la cantidad por el denominador y multiplica el resultado por el numerador.',
     'Un cuarto de 20 galletas son 20 ÷ 4 = 5 galletas.',
     'Resuelve un problema que pida calcular la mitad, un tercio o un cuarto de una cantidad y explica tus pasos.',
     2, 'PROPIO');

INSERT INTO recurso_contenido (id, contenido_id, tipo, url, descripcion, origen) VALUES
    (9, 9, 'IMAGEN', 'recursos/guia-personajes-ambiente.png', 'Guía de lectura con una tabla para registrar personajes, ambiente y acontecimientos.', 'PROPIO'),
    (10, 10, 'IMAGEN', 'recursos/secuencia-cuento.png', 'Secuencia de tres viñetas que muestra una decisión y sus consecuencias.', 'PROPIO'),
    (11, 11, 'IMAGEN', 'recursos/fracciones-recetas.png', 'Representaciones visuales de medios, tercios y cuartos en recipientes y alimentos.', 'PROPIO'),
    (12, 12, 'IMAGEN', 'recursos/problemas-fraccion-cantidad.png', 'Ficha de problemas cotidianos para calcular fracciones de una cantidad.', 'PROPIO');

INSERT INTO actividad (id, unidad_id, contenido_id, titulo, tipo, orden) VALUES
    (13, 5, 9, 'Actividad: identificar elementos de un cuento', 'ACTIVIDAD', 1),
    (14, 5, 10, 'Desafío: explicar una decisión del personaje', 'DESAFIO', 2),
    (15, 5, NULL, 'Evaluación: comprensión de narraciones', 'PRUEBA_FINAL', 3),
    (16, 6, 11, 'Actividad: representar fracciones', 'ACTIVIDAD', 1),
    (17, 6, 12, 'Desafío: calcular fracciones de cantidades', 'DESAFIO', 2),
    (18, 6, NULL, 'Evaluación: fracciones en situaciones cotidianas', 'PRUEBA_FINAL', 3);

-- Preguntas de las actividades de práctica de las unidades en preparación.
INSERT INTO pregunta (id, actividad_id, unidad_id, oa_id, enunciado, criterio_respuesta, dificultad, orden) VALUES
    (15, 13, 5, 9, 'En un cuento, ¿qué información permite reconocer el ambiente?', 'Identifica el lugar o el tiempo en que ocurren los hechos.', 'BAJA', 1),
    (16, 13, 5, 9, '¿Qué acontecimiento modifica la situación inicial del cuento?', 'Señala un hecho concreto de la narración.', 'MEDIA', 2),
    (17, 14, 5, 10, 'Un personaje decide contar la verdad aunque teme una consecuencia. ¿Qué deberías explicar para analizar su decisión?', 'Relaciona la decisión con una consecuencia de la historia.', 'MEDIA', 1),
    (18, 16, 6, 11, 'En la fracción 3/4, ¿qué indica el número 4?', 'Explica que la unidad se divide en cuatro partes iguales.', 'BAJA', 1),
    (19, 16, 6, 11, '¿Qué fracción representa una de tres partes iguales?', 'Identifica un tercio como 1/3.', 'BAJA', 2),
    (20, 17, 6, 12, '¿Cuánto es un cuarto de 20 lápices?', 'Divide 20 entre 4 y obtiene 5 lápices.', 'MEDIA', 1);

INSERT INTO alternativa (id, pregunta_id, texto, es_correcta) VALUES
    (44, 15, 'El lugar y el tiempo en que suceden los hechos', 1),
    (45, 15, 'La cantidad de páginas del libro', 0),
    (46, 15, 'El nombre de la editorial', 0),
    (47, 16, 'Un hecho importante que cambia lo que ocurre en la historia', 1),
    (48, 16, 'La lista de capítulos del libro', 0),
    (49, 16, 'El color de la portada', 0),
    (50, 17, 'Explicar qué hizo el personaje y qué consecuencia tuvo', 1),
    (51, 17, 'Describir solamente su apariencia física', 0),
    (52, 17, 'Contar cuántas páginas tiene el cuento', 0),
    (53, 18, 'En cuántas partes iguales se divide la unidad', 1),
    (54, 18, 'Cuántas unidades completas hay', 0),
    (55, 18, 'Qué operación se usa siempre', 0),
    (56, 19, '1/3', 1),
    (57, 19, '3/1', 0),
    (58, 19, '1/2', 0),
    (59, 20, '5 lápices', 1),
    (60, 20, '4 lápices', 0),
    (61, 20, '10 lápices', 0);

-- Las unidades 5 y 6 siguen sin asignaciones: permiten probar la preparación
-- docente y la revisión UTP sin que aparezcan como publicadas para estudiantes.
UPDATE contenido SET estado = 'BORRADOR' WHERE unidad_id = 5;
UPDATE recurso_contenido SET estado = 'BORRADOR' WHERE contenido_id IN (9, 10);
UPDATE contenido SET estado = 'EN_REVISION' WHERE unidad_id = 6;
UPDATE recurso_contenido SET estado = 'EN_REVISION' WHERE contenido_id IN (11, 12);

-- El SQL de referencia no incluye `estado` en contenidos ni recursos. La app
-- lo agrega con valor por defecto y lo alinea con el estado de su unidad:
-- unidades 1, 3 y 4 publicadas; unidad 2 autorizada.
UPDATE contenido SET estado = 'PUBLICADO' WHERE oa_id IN (1, 2, 5, 6, 7, 8);
UPDATE contenido SET estado = 'AUTORIZADO' WHERE oa_id IN (3, 4);
UPDATE recurso_contenido SET estado = 'PUBLICADO' WHERE contenido_id IN (1, 2, 5, 6, 7, 8);
UPDATE recurso_contenido SET estado = 'AUTORIZADO' WHERE contenido_id IN (3, 4);
