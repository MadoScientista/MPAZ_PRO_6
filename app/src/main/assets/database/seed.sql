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
    (1, 'Patricia Anilin', 'utp', 'UTP', NULL),
    (2, 'Diego Salinas', 'docente', 'DOCENTE', NULL),
    (3, 'Camila Rivas', 'camila', 'ESTUDIANTE', 1),
    (4, 'Mateo Cortes', 'mateo', 'ESTUDIANTE', 1),
    (5, 'Javiera Munoz', 'javiera', 'ESTUDIANTE', 1),
    (6, 'Benjamin Ortiz', 'benjamin', 'ESTUDIANTE', 1);

-- ---------------------------------------------------------------------
-- B) Asignaturas y unidades
-- ---------------------------------------------------------------------

INSERT INTO asignatura (id, nombre) VALUES
    (1, 'Lengua y Comunicacion'),
    (2, 'Matematica'),
    (3, 'Ciencias Naturales'),
    (4, 'Historia, Geografia y Ciencias Sociales');

INSERT INTO unidad (id, asignatura_id, titulo, orden, estado, autorizada_por_id, fecha_autorizacion) VALUES
    (1, 1, 'Lectura y comprehension de textos', 1, 'PUBLICADO', 1, 1772442000000),
    (2, 2, 'Numeros, operaciones y problemas', 1, 'AUTORIZADO', 1, 1772442300000),
    (3, 3, 'Materia y energia en los seres vivos', 1, 'PUBLICADO', 1, 1772442600000),
    (4, 4, 'Las sociedades en el tiempo y el espacio', 1, 'PUBLICADO', 1, 1772442900000),
    (5, 1, 'Comprension de textos narrativos', 2, 'BORRADOR', NULL, NULL),
    (6, 2, 'Ecosistemas y biodiversidad', 2, 'EN_REVISION', NULL, NULL);

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

INSERT INTO contenido (id, oa_id, titulo, explicacion, ejemplos, instrucciones, orden, origen) VALUES
    (1, 1, 'De que se trata un texto informativo',
        'Un texto informativo explica algo de forma ordenada. Su idea principal responde a la pregunta de que trata, y los detalles la acompanan sin reemplazarla. Para encontrarla, conviene leer el titulo y el primer parrafo antes de seguir.',
        'Un texto sobre el ciclo del agua suele decir en su primera linea que tratara sobre ese proceso.',
        'Busca el titulo y la primera frase. Escribi en una linea de que trata el texto.',
        1, 'PROPIO'),
    (2, 2, 'Cuando la imagen completa al texto',
        'Muchos textos informativos incorporan imagenes, diagramas o infografias. La imagen no decora: aporta datos que el texto no repite. Para leerla hay que identificar que muestra, que rotulos tiene y que relacion propone.',
        'En un diagrama de etapas, cada recuadro es un paso y las flechas indican el orden.',
        'Mira la imagen y responde: que dato agrega al texto y donde lo encuentras.',
        1, 'PROPIO'),
    (3, 3, 'Elegir la operacion que corresponde',
        'Un problema de la vida diaria se resuelve identificando primero que se pide y despues eligiendo la operacion. Sumar acumula, restar quita, multiplicar repite y divide repartir. Elegir mal la operacion es la causa mas comun de error.',
        'Si hay 6 cajas con 8 utiles cada una y se necesitan 40, toca multiplicar y luego restar.',
        'Subraya que se pide antes de calcular. Anota la operacion que elegiste y por que.',
        1, 'PROPIO'),
    (4, 4, 'Patrones que se repiten',
        'Un patron es una secuencia que se repite con la misma regla. Se puede describir, continuar y anticipar. En figuras, el patron suele ser una regla de posicion o de forma que se aplica a cada elemento.',
        'Una fila de 3 cuadrados y un circulo que se repite cada cuatro figuras.',
        'Completa la secuencia y explica la regla con la que la completaste.',
        1, 'PROPIO'),
    (5, 5, 'La materia cambia de forma',
        'La materia es todo lo que ocupa espacio. En los seres vivos cambia de forma sin dejar de ser materia: el hielo se derrite, una hoja verde se pone amarilla, la madera se convierte en ceniza. Estos cambios se pueden describir y observar.',
        'Al dejar un cubo de hielo a temperatura ambiente, pasa de solido a liquido sin volverse otra sustancia.',
        'Describe un cambio de estado o de aspecto que hayas observado en una planta o en un objeto.',
        1, 'PROPIO'),
    (6, 6, 'De donde sale la energia de una planta',
        'Las plantas captan energia de la luz y la usan para fabricar su propio alimento. Ese alimento es la base de la cadena alimentaria: sin el, los animales no tendrian energia disponible.',
        'Por eso las areas con poca luz tienen menos vegetacion y menos animales asociados.',
        'Explica con tus palabras por que se dice que las plantas son la base de la cadena alimentaria.',
        1, 'PROPIO'),
    (7, 7, 'Ubicar un hecho en el tiempo y el lugar',
        'Ubicar un hecho significa ubicar cuando ocurrio y donde. El tiempo se ordena con categorias amplias y el lugar se ubica con un punto de referencia mas general. Un mismo hecho puede explicarse distinto segun desde que lugar se mire.',
        'La migracion del chancho de azucar se asocia al invierno en el hemisferio sur.',
        'Ubica un hecho que ocurrio en tu comuna y di por que importa saber donde y cuando.',
        1, 'PROPIO'),
    (8, 8, 'Como las personas cambian su entorno',
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

INSERT INTO actividad (id, unidad_id, titulo, tipo, orden) VALUES
    (1, 1, 'Explorar un texto informativo', 'ACTIVIDAD', 1),
    (2, 1, 'Desafio: reconstruir un aviso', 'DESAFIO', 2),
    (3, 1, 'Prueba final de Lengua y Comunicacion', 'PRUEBA_FINAL', 3),
    (4, 2, 'Explorar problemas de la vida diaria', 'ACTIVIDAD', 1),
    (5, 2, 'Desafio: encontrar el patron', 'DESAFIO', 2),
    (6, 2, 'Prueba final de Matematica', 'PRUEBA_FINAL', 3),
    (7, 3, 'Explorar los cambios de la materia', 'ACTIVIDAD', 1),
    (8, 3, 'Desafio: ordenar una cadena', 'DESAFIO', 2),
    (9, 3, 'Prueba final de Ciencias Naturales', 'PRUEBA_FINAL', 3),
    (10, 4, 'Explorar la ubicacion de un hecho', 'ACTIVIDAD', 1),
    (11, 4, 'Desafio: comparar dos lugares', 'DESAFIO', 2),
    (12, 4, 'Prueba final de Historia y Ciencias Sociales', 'PRUEBA_FINAL', 3);

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
    (4, 3, 1, 2, 'En una secuencia de figuras donde se repite un circulo cada cuatro elementos, que figura ocupara la posicion 10',
        'Escribe el numero de la posicion y el nombre de la figura.', 'MEDIA', 2),
    (5, 3, 1, 1, 'Un texto explica que las plantas usan la luz para fabricar su alimento. De que tipo de texto se trata',
        'Responde indicando el tipo y una caracteristica que lo delate.', 'ALTA', 1),
    (6, 3, 1, 2, 'Dos personas leen el mismo texto pero sacan conclusiones distintas. Cual es la razon mas probable',
        'Explica usando la idea de que los mensajes combinan texto e imagen.', 'ALTA', 2),
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
    (11, 4, 'Un cuadrado', 0),
    (12, 4, 'Un circulo', 1),
    (13, 4, 'Un triangulo', 0),
    (14, 5, 'Un texto informativo, porque explica un proceso de la naturaleza', 1),
    (15, 5, 'Un texto poetico, porque usa imagenes', 0),
    (16, 5, 'Un articulo de opinion, porque critica', 0),
    (17, 6, 'Leyeron partes distintas del mensaje completo', 1),
    (18, 6, 'El texto estaba mal escrito', 0),
    (19, 6, 'No tenia imagenes', 0),
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

-- El SQL de referencia no incluye `estado` en contenidos ni recursos. La app
-- lo agrega con valor por defecto y lo alinea con el estado de su unidad:
-- unidades 1, 3 y 4 publicadas; unidad 2 autorizada.
UPDATE contenido SET estado = 'PUBLICADO' WHERE oa_id IN (1, 2, 5, 6, 7, 8);
UPDATE contenido SET estado = 'AUTORIZADO' WHERE oa_id IN (3, 4);
UPDATE recurso_contenido SET estado = 'PUBLICADO' WHERE contenido_id IN (1, 2, 5, 6, 7, 8);
UPDATE recurso_contenido SET estado = 'AUTORIZADO' WHERE contenido_id IN (3, 4);
