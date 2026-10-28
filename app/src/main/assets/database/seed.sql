-- =====================================================================
-- MPAZ PRO 6 - Datos de demostración (SQLite)
-- Nivel: 6.º básico | Año: 2026
--
-- Una unidad por asignatura. Las unidades 1, 4 y 9 están publicadas; la
-- unidad 12 (Historia) queda en BORRADOR para demostrar la autoría docente.
-- Los OAs, contenidos, actividades, preguntas y alternativas se extraen del
-- seed curricular de 6.º básico, recortados a las cuatro unidades del modelo.
--
-- Los estados se sincronizan explícitamente al final: el esquema Room 4 no
-- tiene `contenido.unidad_id` y no puede ejecutar los UPDATE del seed remoto.
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
-- B) Asignaturas, docencias y unidades
-- ---------------------------------------------------------------------

INSERT INTO asignatura (id, nombre) VALUES
    (1, 'Lengua y Comunicacion'),
    (2, 'Matematica'),
    (3, 'Ciencias Naturales'),
    (4, 'Historia, Geografia y Ciencias Sociales');

INSERT INTO docencia (id, docente_id, curso_id, asignatura_id) VALUES
    (1, 2, 1, 1),
    (2, 2, 1, 2),
    (3, 2, 1, 3),
    (4, 2, 1, 4);

INSERT INTO unidad (id, asignatura_id, titulo, orden, estado, autorizada_por_id, fecha_autorizacion) VALUES
    (1, 1, 'Lectura - Comprension', 1, 'PUBLICADO', 1, 1772409600000),
    (4, 2, 'Numeros y operaciones', 1, 'PUBLICADO', 1, 1772409600000),
    (9, 3, 'Ciencias de la Vida', 1, 'PUBLICADO', 1, 1772668800000),
    (12, 4, 'Historia', 1, 'BORRADOR', NULL, NULL);

-- ---------------------------------------------------------------------
-- C) Objetivos de aprendizaje de las cuatro unidades
-- ---------------------------------------------------------------------
INSERT INTO objetivo_aprendizaje (id, unidad_id, codigo, descripcion, eje, texto_referencia) VALUES
    (1, 1, 'LE06 OA 01', 'Leer de manera fluida textos variados apropiados a su edad: pronunciando las palabras con precisión; respetando la prosodia indicada por todos los signos de puntuación; decodificando de manera automática la mayoría de las palabras del texto.', 'Lectura - Comprensión', NULL),
    (2, 1, 'LE06 OA 02', 'Comprender textos aplicando estrategias de comprensión lectora; por ejemplo: relacionar la información del texto con sus experiencias y conocimientos; releer lo que no fue comprendido; formular preguntas sobre lo leído y responderlas; organizar la información en esquemas o mapas conceptuales; resumir.', 'Lectura - Comprensión', NULL),
    (3, 1, 'LE06 OA 03', 'Leer y familiarizarse con un amplio repertorio de literatura para aumentar su conocimiento del mundo, desarrollar su imaginación y reconocer su valor social y cultural; por ejemplo: poemas, cuentos folclóricos y de autor, fábulas, leyendas, mitos, novelas, historietas, entre otros.', 'Lectura - Comprensión', NULL),
    (4, 1, 'LE06 OA 04', 'Analizar aspectos relevantes de las narraciones leídas para profundizar su comprensión: identificar las acciones principales del relato y explicar cómo influyen en el desarrollo de la historia; explicar las actitudes y reacciones de los personajes de acuerdo con sus motivaciones y las situaciones que viven; describir el ambiente y las costumbres representadas en el texto y explicar su influencia en las acciones del relato; relacionar el relato, si es pertinente, con la época y el lugar en que se ambienta; interpretar el lenguaje figurado; expresar opiniones sobre las actitudes y acciones de los personajes y fundamentarlas con ejemplos del texto; llegar a conclusiones sustentadas en la información del texto; comparar textos de autores diferentes y justificar su preferencia por alguno.', 'Lectura - Comprensión', NULL),
    (5, 1, 'LE06 OA 05', 'Analizar aspectos relevantes de diversos poemas para profundizar su comprensión: explicar cómo el lenguaje poético empleado por el autor apela a los sentidos, sugiere estados de ánimo y crea imágenes en el lector; identificar personificaciones, comparaciones e hipérboles y explicar su significado dentro del poema; analizar cómo los efectos sonoros (aliteración y onomatopeya) utilizados por el poeta refuerzan lo dicho.', 'Lectura - Comprensión', NULL),
    (6, 1, 'LE06 OA 06', 'Leer independientemente y comprender textos no literarios (cartas, biografías, relatos históricos, libros y artículos informativos, noticias, etc.) para ampliar su conocimiento del mundo y formarse una opinión: extraer información explícita e implícita; hacer inferencias a partir de la información del texto y de sus experiencias y conocimientos; relacionar la información de imágenes, gráficos, tablas, mapas o diagramas con el texto; interpretar expresiones en lenguaje figurado; comparar información entre dos textos del mismo tema; formular una opinión sobre algún aspecto de la lectura y fundamentarla con información del texto o conocimientos previos.', 'Lectura - Comprensión', NULL),
    (7, 1, 'LE06 OA 07', 'Evaluar críticamente la información presente en textos de diversa procedencia: determinar quién es el emisor, cuál es su propósito y a quién dirige el mensaje; evaluar si un texto entrega suficiente información para responder una pregunta o cumplir un propósito; comparar la información que se entrega sobre una misma noticia en distintas fuentes.', 'Lectura - Comprensión', NULL),
    (8, 1, 'LE06 OA 08', 'Sintetizar, registrar y ordenar las ideas principales de textos leídos para satisfacer propósitos como estudiar, hacer una investigación, recordar detalles, entre otros.', 'Lectura - Comprensión', NULL),
    (9, 1, 'LE06 OA 09', 'Desarrollar el gusto por la lectura, leyendo habitualmente diversos textos.', 'Lectura - Comprensión', NULL),
    (10, 1, 'LE06 OA 10', 'Asistir habitualmente a la biblioteca para satisfacer diversos propósitos (seleccionar textos, investigar sobre un tema, informarse sobre actualidad, etc.), adecuando su comportamiento y cuidando el material para permitir el trabajo y la lectura de los demás.', 'Lectura - Comprensión', NULL),
    (11, 1, 'LE06 OA 11', 'Buscar y comparar información sobre un tema, utilizando fuentes como internet, enciclopedias, libros y prensa, entre otras, para llevar a cabo una investigación.', 'Lectura - Comprensión', NULL),
    (12, 1, 'LE06 OA 12', 'Aplicar estrategias para determinar el significado de palabras nuevas: claves contextuales; raíces y afijos; preguntar a otro; diccionarios, enciclopedias e internet.', 'Lectura - Comprensión', NULL),
    (32, 4, 'MA06 OA 01', 'Demostrar que comprenden los factores y múltiplos: determinar los múltiplos y factores de números naturales menores de 100; identificar números primos y compuestos; resolver problemas que involucran múltiplos.', 'Números y operaciones', NULL),
    (33, 4, 'MA06 OA 02', 'Realizar cálculos que involucren las cuatro operaciones en el contexto de la resolución de problemas, utilizando la calculadora en ámbitos superiores a 10 000.', 'Números y operaciones', NULL),
    (34, 4, 'MA06 OA 03', 'Demostrar que comprenden el concepto de razón de manera concreta, pictórica y simbólica, en forma manual y/o usando software educativo.', 'Números y operaciones', NULL),
    (35, 4, 'MA06 OA 04', 'Demostrar que comprenden el concepto de porcentaje de manera concreta, pictórica y simbólica, de forma manual y/o usando software educativo.', 'Números y operaciones', NULL),
    (36, 4, 'MA06 OA 05', 'Demostrar que comprenden las fracciones y números mixtos: identificar y determinar equivalencias entre fracciones impropias y números mixtos usando material concreto y representaciones pictóricas, de manera manual y/o con software educativo; representar estos números en la recta numérica.', 'Números y operaciones', NULL),
    (37, 4, 'MA06 OA 06', 'Resolver adiciones y sustracciones de fracciones propias e impropias y números mixtos con numeradores y denominadores de hasta dos dígitos.', 'Números y operaciones', NULL),
    (38, 4, 'MA06 OA 07', 'Demostrar que comprenden la multiplicación y la división de decimales por números naturales de un dígito, múltiplos de 10 y decimales hasta la milésima, de manera concreta, pictórica y simbólica.', 'Números y operaciones', NULL),
    (39, 4, 'MA06 OA 08', 'Resolver problemas rutinarios y no rutinarios que involucren adiciones y sustracciones de fracciones propias, impropias, números mixtos o decimales hasta la milésima.', 'Números y operaciones', NULL),
    (56, 9, 'CN06 OA 01', 'Explicar, a partir de una investigación experimental, los requerimientos de agua, dióxido de carbono y energía lumínica para la producción de azúcar y liberación de oxígeno en la fotosíntesis, comunicando sus resultados y los aportes de científicos en este campo a través del tiempo.', 'Ciencias de la Vida', NULL),
    (57, 9, 'CN06 OA 02', 'Representar, por medio de modelos, la transferencia de energía y materia desde los organismos fotosintéticos a otros seres vivos por medio de cadenas y redes alimentarias en diferentes ecosistemas.', 'Ciencias de la Vida', NULL),
    (58, 9, 'CN06 OA 03', 'Analizar los efectos de la actividad humana sobre las redes alimentarias.', 'Ciencias de la Vida', NULL),
    (59, 9, 'CN06 OA 04', 'Identificar y describir las funciones de las principales estructuras del sistema reproductor humano femenino y masculino.', 'Ciencias de la Vida', NULL),
    (60, 9, 'CN06 OA 05', 'Describir y comparar los cambios que se producen en la pubertad en mujeres y hombres, reconociéndola como una etapa del desarrollo humano.', 'Ciencias de la Vida', NULL),
    (61, 9, 'CN06 OA 06', 'Reconocer los beneficios de realizar actividad física en forma regular y de cuidar la higiene corporal en el período de la pubertad.', 'Ciencias de la Vida', NULL),
    (62, 9, 'CN06 OA 07', 'Investigar y comunicar los efectos nocivos de algunas drogas para la salud, proponiendo conductas de protección.', 'Ciencias de la Vida', NULL),
    (74, 12, 'HI06 OA 01', 'Explicar los múltiples antecedentes de la independencia de las colonias americanas y reconocer que la independencia de Chile se enmarca en un proceso continental.', 'Historia', NULL),
    (75, 12, 'HI06 OA 02', 'Explicar el desarrollo del proceso de independencia de Chile, considerando actores y bandos que se enfrentaron, hombres y mujeres destacados, avances y retrocesos de la causa patriota y acontecimientos significativos, como el cabildo abierto de 1810 y la formación de la Primera Junta Nacional de Gobierno, la elección del primer Congreso Nacional, las batallas de Rancagua, Chacabuco y Maipú y la Declaración de la Independencia, entre otros.', 'Historia', NULL),
    (76, 12, 'HI06 OA 03', 'Describir hitos y procesos de la organización de la república, incluyendo las dificultades y desafíos que implicó organizar en Chile una nueva forma de gobierno, el surgimiento de grupos con diferentes ideas políticas (conservadores y liberales), las características de la Constitución de 1833 y el impacto de las reformas realizadas por los liberales en la segunda mitad del siglo XIX.', 'Historia', NULL),
    (77, 12, 'HI06 OA 04', 'Investigar aspectos culturales del siglo XIX, como los avances en educación y la fundación de instituciones, el aporte de intelectuales y científicos nacionales (por ejemplo, Diego Barros Arana, Benjamín Vicuña Mackenna y José Victorino Lastarria) y extranjeros (por ejemplo, Andrés Bello, Claudio Gay, Charles Darwin y María Graham), las primeras mujeres en obtener títulos universitarios y el impacto de la llegada del ferrocarril y otros avances tecnológicos.', 'Historia', NULL),
    (78, 12, 'HI06 OA 05', 'Describir cómo se conformó el territorio de Chile durante el siglo XIX, considerando colonizaciones europeas, la incorporación de Isla de Pascua, la ocupación de la Araucanía, la Guerra del Pacífico y diversos conflictos bélicos, entre otros factores.', 'Historia', NULL),
    (79, 12, 'HI06 OA 06', 'Caracterizar los principales aspectos que definieron el período de riqueza aportada por la explotación del salitre, considerando la expansión económica y el inicio de la cuestión social.', 'Historia', NULL),
    (80, 12, 'HI06 OA 07', 'Explicar y dar ejemplos de la progresiva democratización de la sociedad durante el siglo XX, considerando el acceso creciente al voto, la participación de la mujer en la vida pública y el acceso a la educación y la cultura, entre otros.', 'Historia', NULL),
    (81, 12, 'HI06 OA 08', 'Comparar diferentes visiones sobre el quiebre de la democracia en Chile, el régimen o dictadura militar y el proceso de recuperación de la democracia a fines del siglo XX, considerando los distintos actores, experiencias y puntos de vista, y el consenso actual con respecto al valor de la democracia.', 'Historia', NULL),
    (82, 12, 'HI06 OA 09', 'Explicar y dar ejemplos de aspectos que se mantienen y aspectos que han cambiado o se han desarrollado en la sociedad chilena a lo largo de su historia.', 'Historia', NULL);

-- ---------------------------------------------------------------------
-- D) Contenidos y recursos
-- ---------------------------------------------------------------------
INSERT INTO contenido (id, oa_id, titulo, explicacion, ejemplos, instrucciones, orden, origen) VALUES
    (1, 2, 'Estrategias de comprensión lectora', 'Comprender un texto requiere construir significado: conectar sus ideas con conocimientos previos, releer pasajes poco claros, formular preguntas y resumir la información central. Un esquema ayuda a separar las ideas principales de los detalles.', 'Al leer una noticia sobre el agua, se puede resumir la idea central y anotar dos datos que la apoyan.', 'Lee un texto breve. Formula una pregunta, responde con información del texto y escribe un resumen de dos oraciones.', 1, 'PROPIO'),
    (4, 35, 'Comprender y calcular porcentajes', 'Un porcentaje expresa una cantidad como parte de 100. Puede representarse con una cuadrícula de cien partes, como fracción con denominador 100 o con un número seguido del símbolo %.', 'Si 25 de 100 casillas están marcadas, está representado el 25 %. En una encuesta de 20 estudiantes, 5 equivalen al 25 %.', 'Representa 10 %, 25 % y 50 % de una cuadrícula de cien. Luego resuelve un problema sencillo e indica cómo encontraste el resultado.', 1, 'PROPIO'),
    (9, 57, 'Cadenas y redes alimentarias', 'La materia y la energía pasan de los organismos fotosintéticos a otros seres vivos a través de la alimentación. Las flechas de una cadena alimentaria indican la dirección de la transferencia de materia y energía.', 'Hierba → saltamontes → rana → culebra muestra una posible relación alimentaria en un ecosistema.', 'Construye una cadena alimentaria de un ecosistema, identifica al productor y explica qué podría ocurrir si uno de sus organismos disminuye.', 1, 'PROPIO'),
    (12, 75, 'El proceso de independencia de Chile', 'La independencia de Chile fue un proceso con avances y retrocesos, protagonizado por distintos grupos y personas. Entre sus hitos están el cabildo abierto de 1810, la Primera Junta de Gobierno, las batallas de Rancagua, Chacabuco y Maipú y la Declaración de Independencia.', 'Una línea de tiempo permite ordenar los hitos y relacionarlos con los cambios en el control político del territorio.', 'Ordena tres hitos del proceso de independencia y explica por qué uno de ellos fue importante para la causa patriota.', 1, 'PROPIO');
INSERT INTO recurso_contenido (id, contenido_id, tipo, url, descripcion, origen) VALUES
    (1, 1, 'IMAGEN', 'recursos/6basico/eje-01-oa.png', 'Recurso visual de apoyo para estrategias de comprensión lectora, relacionado con LE06 OA 02.', 'PROPIO'),
    (4, 4, 'IMAGEN', 'recursos/6basico/eje-04-oa.png', 'Recurso visual de apoyo para comprender y calcular porcentajes, relacionado con MA06 OA 04.', 'PROPIO'),
    (9, 9, 'IMAGEN', 'recursos/6basico/eje-09-oa.png', 'Recurso visual de apoyo para cadenas y redes alimentarias, relacionado con CN06 OA 02.', 'PROPIO'),
    (12, 12, 'IMAGEN', 'recursos/6basico/eje-12-oa.png', 'Recurso visual de apoyo para el proceso de independencia de chile, relacionado con HI06 OA 02.', 'PROPIO');

-- ---------------------------------------------------------------------
-- E) Actividades: una práctica, un desafío y una prueba final por unidad
-- ---------------------------------------------------------------------
INSERT INTO actividad (id, unidad_id, contenido_id, titulo, tipo, orden) VALUES
    (1, 1, 1, 'Actividad: Estrategias de comprensión lectora', 'ACTIVIDAD', 1),
    (2, 1, 1, 'Desafío: aplicar estrategias de comprensión lectora', 'DESAFIO', 2),
    (3, 1, NULL, 'Evaluación: Lectura - Comprensión', 'PRUEBA_FINAL', 3),
    (10, 4, 4, 'Actividad: Comprender y calcular porcentajes', 'ACTIVIDAD', 1),
    (11, 4, 4, 'Desafío: aplicar comprender y calcular porcentajes', 'DESAFIO', 2),
    (12, 4, NULL, 'Evaluación: Números y operaciones', 'PRUEBA_FINAL', 3),
    (25, 9, 9, 'Actividad: Cadenas y redes alimentarias', 'ACTIVIDAD', 1),
    (26, 9, 9, 'Desafío: aplicar cadenas y redes alimentarias', 'DESAFIO', 2),
    (27, 9, NULL, 'Evaluación: Ciencias de la Vida', 'PRUEBA_FINAL', 3),
    (34, 12, 12, 'Actividad: El proceso de independencia de Chile', 'ACTIVIDAD', 1),
    (35, 12, 12, 'Desafío: aplicar el proceso de independencia de chile', 'DESAFIO', 2),
    (36, 12, NULL, 'Evaluación: Historia', 'PRUEBA_FINAL', 3);

-- ---------------------------------------------------------------------
-- F) Preguntas y alternativas de las actividades seleccionadas
-- ---------------------------------------------------------------------
INSERT INTO pregunta (id, actividad_id, unidad_id, oa_id, enunciado, criterio_respuesta, dificultad, orden) VALUES
    (1, 1, 1, 2, '¿Qué conviene hacer cuando una parte del texto no se comprende?', 'Indica una estrategia de comprensión lectora.', 'BAJA', 1),
    (2, 2, 1, 2, '¿Para qué sirve resumir un texto?', 'Relaciona el resumen con las ideas principales.', 'BAJA', 2),
    (3, 3, 1, 2, '¿Qué debe incluir un buen resumen?', 'Reconoce selección de información central.', 'MEDIA', 3),
    (4, 3, 1, 2, 'Si una respuesta no aparece escrita literalmente, ¿qué puede hacer el lector?', 'Explica que puede inferir a partir de pistas y conocimientos previos.', 'MEDIA', 4),
    (5, 3, 1, 2, '¿Cuál estrategia ayuda a comprobar una interpretación?', 'Selecciona una estrategia basada en evidencia textual.', 'ALTA', 5),
    (16, 10, 4, 35, '¿Qué representa 25 %?', 'Interpreta porcentaje como parte de 100.', 'BAJA', 1),
    (17, 11, 4, 35, '¿Cuánto es el 50 % de 20?', 'Calcula la mitad de una cantidad.', 'BAJA', 2),
    (18, 12, 4, 35, 'En una encuesta de 100 personas, 30 prefieren leer. ¿Qué porcentaje corresponde?', 'Relaciona cantidad sobre 100 con porcentaje.', 'MEDIA', 3),
    (19, 12, 4, 35, '¿Qué representación corresponde al 25 % de una cuadrícula de 100 casillas?', 'Identifica 25 de 100 casillas.', 'MEDIA', 4),
    (20, 12, 4, 35, 'Una mochila cuesta $20 000 y tiene un descuento del 10 %. ¿Cuánto se descuenta?', 'Calcula 10 % de 20 000.', 'ALTA', 5),
    (41, 25, 9, 57, '¿Qué organismos incorporan energía lumínica a muchas cadenas alimentarias?', 'Identifica productores fotosintéticos.', 'BAJA', 1),
    (42, 26, 9, 57, '¿Qué indican las flechas en una cadena alimentaria?', 'Interpreta transferencia de materia y energía.', 'BAJA', 2),
    (43, 27, 9, 57, 'En hierba → saltamontes → rana, ¿qué organismo es consumidor primario?', 'Reconoce quién se alimenta del productor.', 'MEDIA', 3),
    (44, 27, 9, 57, '¿Qué podría suceder si disminuye mucho la población de productores?', 'Relaciona productores con disponibilidad de alimento y energía.', 'MEDIA', 4),
    (45, 27, 9, 57, '¿Por qué las redes alimentarias representan mejor algunas relaciones que una sola cadena?', 'Reconoce múltiples relaciones tróficas.', 'ALTA', 5),
    (56, 34, 12, 75, '¿Qué hito ocurrió en Chile en 1810?', 'Reconoce la Primera Junta/cabildo abierto.', 'BAJA', 1),
    (57, 35, 12, 75, '¿Por qué se considera que la independencia de Chile fue un proceso?', 'Reconoce avances, retrocesos y distintos hitos.', 'BAJA', 2),
    (58, 36, 12, 75, '¿Qué permite mostrar una línea de tiempo?', 'Organiza acontecimientos cronológicamente.', 'MEDIA', 3),
    (59, 36, 12, 75, '¿Cuál de estos hitos se relaciona con la causa patriota?', 'Identifica batalla vinculada al proceso.', 'MEDIA', 4),
    (60, 36, 12, 75, '¿Qué enfoque ayuda a explicar el proceso de independencia?', 'Considera actores, bandos y acontecimientos.', 'ALTA', 5);
INSERT INTO alternativa (id, pregunta_id, texto, es_correcta) VALUES
    (1, 1, 'Continuar sin prestar atención', 0),
    (2, 1, 'Releer el pasaje y formular una pregunta', 1),
    (3, 1, 'Mirar solo la última palabra', 0),
    (4, 2, 'Para copiar todas las oraciones', 0),
    (5, 2, 'Para reemplazar el título por una imagen', 0),
    (6, 2, 'Para registrar sus ideas principales de forma breve', 1),
    (7, 3, 'Las ideas principales del texto', 1),
    (8, 3, 'Opiniones que no aparecen en el texto', 0),
    (9, 3, 'Todas las palabras en el mismo orden', 0),
    (10, 4, 'Inventar un dato sin revisar el texto', 0),
    (11, 4, 'Relacionar pistas del texto con sus conocimientos', 1),
    (12, 4, 'Ignorar la pregunta', 0),
    (13, 5, 'Buscar pistas y releer la parte pertinente', 1),
    (14, 5, 'Elegir la respuesta más larga', 0),
    (15, 5, 'Cambiar de tema', 0),
    (46, 16, '25 partes de cada 100', 1),
    (47, 16, '25 partes de cada 10', 0),
    (48, 16, '100 partes de cada 25', 0),
    (49, 17, '5', 0),
    (50, 17, '10', 1),
    (51, 17, '15', 0),
    (52, 18, '3 %', 0),
    (53, 18, '70 %', 0),
    (54, 18, '30 %', 1),
    (55, 19, '25 casillas marcadas', 1),
    (56, 19, '50 casillas marcadas', 0),
    (57, 19, '75 casillas marcadas', 0),
    (58, 20, '$10 000', 0),
    (59, 20, '$2 000', 1),
    (60, 20, '$200', 0),
    (121, 41, 'Los productores fotosintéticos', 1),
    (122, 41, 'Solo los consumidores terciarios', 0),
    (123, 41, 'Los descomponedores exclusivamente', 0),
    (124, 42, 'El tamaño de los organismos', 0),
    (125, 42, 'La dirección de transferencia de materia y energía', 1),
    (126, 42, 'La distancia entre hábitats', 0),
    (127, 43, 'La hierba', 0),
    (128, 43, 'La rana', 0),
    (129, 43, 'El saltamontes', 1),
    (130, 44, 'Podría reducirse el alimento para otros niveles', 1),
    (131, 44, 'Todas las poblaciones aumentarían necesariamente', 0),
    (132, 44, 'La red alimentaria no tendría ningún cambio', 0),
    (133, 45, 'Muestran varias relaciones alimentarias conectadas', 1),
    (134, 45, 'Solo muestran un organismo', 0),
    (135, 45, 'Eliminan los productores', 0),
    (166, 56, 'La batalla de Maipú', 0),
    (167, 56, 'La Declaración de Independencia de 1818', 0),
    (168, 56, 'El cabildo abierto y la formación de la Primera Junta de Gobierno', 1),
    (169, 57, 'Porque ocurrió mediante distintos acontecimientos y etapas', 1),
    (170, 57, 'Porque ocurrió en un único día sin conflictos', 0),
    (171, 57, 'Porque no participaron personas', 0),
    (172, 58, 'La altura de las montañas', 0),
    (173, 58, 'El orden temporal de los acontecimientos', 1),
    (174, 58, 'Solo la población actual', 0),
    (175, 59, 'La formación de la ONU', 0),
    (176, 59, 'La llegada del ferrocarril en el siglo XIX', 0),
    (177, 59, 'La batalla de Chacabuco', 1),
    (178, 60, 'Analizar actores, bandos, avances, retrocesos e hitos', 1),
    (179, 60, 'Mencionar solo una fecha sin contexto', 0),
    (180, 60, 'Excluir los acontecimientos políticos', 0);

-- ---------------------------------------------------------------------
-- G) Asignaciones a 6A
--    Historia queda sin asignación inicial: el docente la crea después de que
--    la UTP publique la unidad.
-- ---------------------------------------------------------------------
INSERT INTO asignacion (id, curso_id, unidad_id, max_intentos, prueba_final_habilitada, fecha) VALUES
    (1, 1, 1, 2, 1, 1775462400000),
    (2, 1, 4, 2, 1, 1775462700000),
    (3, 1, 9, 2, 1, 1775463000000);

INSERT INTO asignacion_oa (asignacion_id, unidad_id, oa_id) VALUES
    (1, 1, 2),
    (2, 4, 35),
    (3, 9, 57);

INSERT INTO asignacion_actividad (asignacion_id, unidad_id, actividad_id) VALUES
    (1, 1, 1),
    (1, 1, 2),
    (2, 4, 10),
    (2, 4, 11),
    (3, 9, 25),
    (3, 9, 26);

-- ---------------------------------------------------------------------
-- H) Intentos y respuestas de ejemplo
-- ---------------------------------------------------------------------
INSERT INTO intento (id, estudiante_id, actividad_id, numero, fecha_inicio, fecha_fin) VALUES
    (1, 3, 3, 1, 1777248000000, 1777248900000),
    (2, 3, 3, 2, 1777420800000, 1777421580000),
    (3, 4, 12, 1, 1777334400000, 1777335240000),
    (4, 5, 27, 1, 1777680000000, NULL);

INSERT INTO respuesta_estudiante (id, intento_id, pregunta_id, alternativa_id, es_correcta) VALUES
    (1, 1, 3, 7, 1),
    (2, 1, 4, 10, 0),
    (3, 1, 5, 14, 0),
    (4, 2, 3, 8, 0),
    (5, 2, 4, 11, 1),
    (6, 2, 5, 13, 1),
    (7, 3, 18, 54, 1),
    (8, 3, 19, 55, 1),
    (9, 3, 20, 58, 0);

-- ---------------------------------------------------------------------
-- I) Informe de ejemplo de Camila en la unidad 1
-- ---------------------------------------------------------------------
INSERT INTO informe_pedagogico (
    id, estudiante_id, unidad_id, docente_id, fecha_generacion, estado,
    fecha_validacion, estudiante_nombre, unidad_titulo, asignatura_nombre,
    docente_nombre
) VALUES
    (
        1, 3, 1, 2, 1777852800000, 'PENDIENTE_VALIDACION', NULL,
        'Camila Rivas', 'Lectura - Comprension',
        'Lengua y Comunicacion', 'Diego Salinas'
    );

INSERT INTO detalle_informe_oa (id, informe_id, oa_id, total_preguntas, correctas, nivel) VALUES
    (1, 1, 2, 3, 2, 'REFUERZO');

-- ---------------------------------------------------------------------
-- J) Estados de contenidos y recursos
--    Las unidades 1, 4 y 9 están publicadas; Historia sigue en borrador.
-- ---------------------------------------------------------------------
UPDATE contenido SET estado = 'PUBLICADO' WHERE id IN (1, 4, 9);
UPDATE contenido SET estado = 'BORRADOR' WHERE id = 12;

UPDATE recurso_contenido SET estado = 'PUBLICADO' WHERE id IN (1, 4, 9);
UPDATE recurso_contenido SET estado = 'BORRADOR' WHERE id = 12;
