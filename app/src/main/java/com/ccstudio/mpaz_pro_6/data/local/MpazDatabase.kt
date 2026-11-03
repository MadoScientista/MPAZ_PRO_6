package com.ccstudio.mpaz_pro_6.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculoDao
import com.ccstudio.mpaz_pro_6.data.local.dao.DocenciaDao
import com.ccstudio.mpaz_pro_6.data.local.dao.EvaluacionDao
import com.ccstudio.mpaz_pro_6.data.local.dao.IdentidadDao
import com.ccstudio.mpaz_pro_6.data.local.dao.InformeDao
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaPreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.EstadoAutorizacionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeUnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.RecursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoRecursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.AsignaturaCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.UsuarioCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.IntentoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.RespuestaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.NivelEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.RolEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.SeccionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.DetalleInformePedagogicoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.InformePedagogicoEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_1_3 = object : Migration(1, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

fun seedInitialData(db: SupportSQLiteDatabase) {
    // 1. Roles
    db.execSQL("INSERT OR IGNORE INTO rol (rol_id, nombre) VALUES (1, 'UTP')")
    db.execSQL("INSERT OR IGNORE INTO rol (rol_id, nombre) VALUES (2, 'DOCENTE')")
    db.execSQL("INSERT OR IGNORE INTO rol (rol_id, nombre) VALUES (3, 'ESTUDIANTE')")

    // 2. Usuarios (1 UTP, 4 Docentes, 4 Estudiantes)
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (1, 'utp', 'mpaz123', 'Carlos', NULL, 'UTP', NULL, 1, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (2, 'docente', 'mpaz123', 'Ana', NULL, 'Silva', NULL, 2, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (3, 'docente2', 'mpaz123', 'Bruno', NULL, 'Morales', NULL, 2, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (4, 'docente3', 'mpaz123', 'Carla', NULL, 'Rojas', NULL, 2, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (5, 'docente4', 'mpaz123', 'Daniel', NULL, 'Vargas', NULL, 2, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (6, 'alumno', 'mpaz123', 'Elena', NULL, 'Castro', NULL, 3, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (7, 'estudiante2', 'mpaz123', 'Felipe', NULL, 'Gómez', NULL, 3, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (8, 'estudiante3', 'mpaz123', 'Gabriela', NULL, 'Herrera', NULL, 3, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario (usuario_id, nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo) VALUES (9, 'estudiante4', 'mpaz123', 'Hugo', NULL, 'Ibarra', NULL, 3, 1)")

    // 3. Nivel
    db.execSQL("INSERT OR IGNORE INTO nivel (nivel_id, nombre) VALUES (1, 'Sexto Básico')")

    // 4. Secciones
    db.execSQL("INSERT OR IGNORE INTO seccion (seccion_id, letra) VALUES (1, 'A')")
    db.execSQL("INSERT OR IGNORE INTO seccion (seccion_id, letra) VALUES (2, 'B')")

    // 5. Cursos
    db.execSQL("INSERT OR IGNORE INTO curso (curso_id, annio, nivel_id, seccion_id) VALUES (1, 2026, 1, 1)")
    db.execSQL("INSERT OR IGNORE INTO curso (curso_id, annio, nivel_id, seccion_id) VALUES (2, 2026, 1, 2)")

    // 6. Estados de Autorización
    db.execSQL("INSERT OR IGNORE INTO estado_autorizacion (estado_id, nombre) VALUES (1, 'BORRADOR')")
    db.execSQL("INSERT OR IGNORE INTO estado_autorizacion (estado_id, nombre) VALUES (2, 'EN_REVISION')")
    db.execSQL("INSERT OR IGNORE INTO estado_autorizacion (estado_id, nombre) VALUES (3, 'AUTORIZADO')")
    db.execSQL("INSERT OR IGNORE INTO estado_autorizacion (estado_id, nombre) VALUES (4, 'PUBLICADO')")

    // 7. Tipos de Actividad
    db.execSQL("INSERT OR IGNORE INTO tipo_actividad (tipo_actividad_id, nombre) VALUES (1, 'ACTIVIDAD')")
    db.execSQL("INSERT OR IGNORE INTO tipo_actividad (tipo_actividad_id, nombre) VALUES (2, 'DESAFIO')")
    db.execSQL("INSERT OR IGNORE INTO tipo_actividad (tipo_actividad_id, nombre) VALUES (3, 'PRUEBA_FINAL')")

    // 8. Tipos de Recurso
    db.execSQL("INSERT OR IGNORE INTO tipo_recurso (tipo_recurso_id, nombre) VALUES (1, 'IMAGEN')")
    db.execSQL("INSERT OR IGNORE INTO tipo_recurso (tipo_recurso_id, nombre) VALUES (2, 'AUDIO')")
    db.execSQL("INSERT OR IGNORE INTO tipo_recurso (tipo_recurso_id, nombre) VALUES (3, 'DOCUMENTO')")
    db.execSQL("INSERT OR IGNORE INTO tipo_recurso (tipo_recurso_id, nombre) VALUES (4, 'VIDEO')")

    // 9. Asignaturas
    db.execSQL("INSERT OR IGNORE INTO asignatura (asignatura_id, nombre) VALUES (1, 'Lenguaje y Comunicación')")
    db.execSQL("INSERT OR IGNORE INTO asignatura (asignatura_id, nombre) VALUES (2, 'Matemática')")
    db.execSQL("INSERT OR IGNORE INTO asignatura (asignatura_id, nombre) VALUES (3, 'Ciencias Naturales')")
    db.execSQL("INSERT OR IGNORE INTO asignatura (asignatura_id, nombre) VALUES (4, 'Historia, Geografía y Ciencias Sociales')")

    // 10. Unidades
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (1, 'Lectura - Comprensión', 1, 2)")
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (2, 'Números y operaciones', 2, 3)")
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (3, 'Ciencias de la Vida', 3, 1)")
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (4, 'Historia', 4, 4)")

    // 11. AsignaturaCurso
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id) VALUES (1, 1, 1)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id) VALUES (2, 1, 2)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id) VALUES (3, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id) VALUES (4, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id) VALUES (5, 2, 1)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id) VALUES (6, 2, 2)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id) VALUES (7, 2, 3)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id) VALUES (8, 2, 4)")

    // 12. UsuarioCurso
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (1, 2, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (2, 2, 2)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (3, 3, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (4, 3, 2)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (5, 4, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (6, 4, 2)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (7, 5, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (8, 5, 2)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (9, 6, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (10, 7, 1)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (11, 8, 2)")
    db.execSQL("INSERT OR IGNORE INTO usuario_curso (id, usuario_id, curso_id) VALUES (12, 9, 2)")

    // 13. Objetivos de Aprendizaje
    // Lenguaje y Comunicación (OAs 1..12 -> Unidad 1)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (1, 'LE06 OA 01', 'Leer de manera fluida textos variados apropiados a su edad: pronunciando las palabras con precisión, respetando la prosodia indicada por todos los signos de puntuación, decodificando de manera automática la mayoría de las palabras del texto.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (2, 'LE06 OA 02', 'Comprender textos aplicando estrategias de comprensión lectora; por ejemplo: relacionar la información del texto con sus experiencias y conocimientos, releer lo que no fue comprendido, formular preguntas sobre lo leído y responderlas, organizar la información en esquemas o mapas conceptuales, resumir.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (3, 'LE06 OA 03', 'Leer y familiarizarse con un amplio repertorio de literatura para aumentar su conocimiento del mundo, desarrollar su imaginación y reconocer su valor social y cultural; por ejemplo: poemas, cuentos folclóricos y de autor, fábulas, leyendas, mitos, novelas, historietas, otros.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (4, 'LE06 OA 04', 'Analizar aspectos relevantes de las narraciones leídas para profundizar su comprensión: identificando las acciones principales del relato y explicando cómo influyen en el desarrollo de la historia, explicando las actitudes y reacciones de los personajes de acuerdo con sus motivaciones y las situaciones que viven, describiendo el ambiente y las costumbres representadas en el texto y explicando su influencia en las acciones del relato, relacionando el relato, si es pertinente, con la época y el lugar en que se ambienta, interpretando el lenguaje figurado presente en el texto, expresando opiniones sobre las actitudes y acciones de los personajes y fundamentándolas con ejemplos del texto, llegando a conclusiones sustentadas en la información del texto, comparando textos de autores diferentes y justificando su preferencia por alguno.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (5, 'LE06 OA 05', 'Analizar aspectos relevantes de diversos poemas para profundizar su comprensión: explicando cómo el lenguaje poético que emplea el autor apela a los sentidos, sugiere estados de ánimo y crea imágenes en el lector, identificando personificaciones, comparaciones e hipérboles y explicando su significado dentro del poema, analizando cómo los efectos sonoros (aliteración y onomatopeya) utilizados por el poeta refuerzan lo dicho.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (6, 'LE06 OA 06', 'Leer independientemente y comprender textos no literarios (cartas, biografías, relatos históricos, libros y artículos informativos, noticias, etc.) para ampliar su conocimiento del mundo y formarse una opinión: extrayendo información explícita e implícita, haciendo inferencias a partir de la información del texto y de sus experiencias y conocimientos, relacionando la información de imágenes, gráficos, tablas, mapas o diagramas, con el texto en el cual están insertos, interpretando expresiones en lenguaje figurado, comparando información entre dos textos del mismo tema, formulando una opinión sobre algún aspecto de la lectura, fundamentando su opinión con información del texto o sus conocimientos previos.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (7, 'LE06 OA 07', 'Evaluar críticamente la información presente en textos de diversa procedencia: determinando quién es el emisor, cuál es su propósito y a quién dirige el mensaje, evaluando si un texto entrega suficiente información para responder una determinada pregunta o cumplir un propósito, comparando la información que se entrega sobre una misma noticia en distintas fuentes.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (8, 'LE06 OA 08', 'Sintetizar, registrar y ordenar las ideas principales de textos leídos para satisfacer propósitos como estudiar, hacer una investigación, recordar detalles, etc.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (9, 'LE06 OA 09', 'Desarrollar el gusto por la lectura, leyendo habitualmente diversos textos.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (10, 'LE06 OA 10', 'Asistir habitualmente a la biblioteca para satisfacer diversos propósitos (seleccionar textos, investigar sobre un tema, informarse sobre actualidad, etc.), adecuando su comportamiento y cuidando el material para permitir el trabajo y la lectura de los demás.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (11, 'LE06 OA 11', 'Buscar y comparar información sobre un tema, utilizando fuentes como internet, enciclopedias, libros, prensa, etc., para llevar a cabo una investigación.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (12, 'LE06 OA 12', 'Aplicar estrategias para determinar el significado de palabras nuevas: claves contextuales, raíces y afijos, preguntar a otro, diccionarios, enciclopedias e internet.')")

    // Ciencias Naturales (OAs 13..19 -> Unidad 3)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (13, 'CN06 OA 01', 'Explicar, a partir de una investigación experimental, los requerimientos de agua, dióxido de carbono y energía lumínica para la producción de azúcar y liberación de oxígeno en la fotosíntesis, comunicando sus resultados y los aportes de científicos en este campo a través del tiempo.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (14, 'CN06 OA 02', 'Representar, por medio de modelos, la transferencia de energía y materia desde los organismos fotosintéticos a otros seres vivos por medio de cadenas y redes alimentarias en diferentes ecosistemas.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (15, 'CN06 OA 03', 'Analizar los efectos de la actividad humana sobre las redes alimentarias.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (16, 'CN06 OA 04', 'Identificar y describir las funciones de las principales estructuras del sistema reproductor humano femenino y masculino.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (17, 'CN06 OA 05', 'Describir y comparar los cambios que se producen en la pubertad en mujeres y hombres, reconociéndola, como una etapa del desarrollo humano.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (18, 'CN06 OA 06', 'Reconocer los beneficios de realizar actividad física en forma regular y de cuidar la higiene corporal en el período de la pubertad.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (19, 'CN06 OA 07', 'Investigar y comunicar los efectos nocivos de algunas drogas para la salud, proponiendo conductas de protección.')")

    // Historia, Geografía y Ciencias Sociales (OAs 20..28 -> Unidad 4)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (20, 'HI06 OA 01', 'Explicar los múltiples antecedentes de la independencia de las colonias americanas y reconocer que la independencia de Chile se enmarca en un proceso continental.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (21, 'HI06 OA 02', 'Explicar el desarrollo del proceso de independencia de Chile, considerando actores y bandos que se enfrentaron, hombres y mujeres destacados, avances y retrocesos de la causa patriota y algunos acontecimientos significativos, como la celebración del cabildo abierto de 1810 y la formación de la Primera Junta Nacional de Gobierno, la elección del primer Congreso Nacional, las batallas de Rancagua, Chacabuco y Maipú, y la Declaración de la Independencia, entre otros.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (22, 'HI06 OA 03', 'Describir algunos hitos y procesos de la organización de la república, incluyendo las dificultades y los desafíos que implicó organizar en Chile una nueva forma de gobierno, el surgimiento de grupos con diferentes ideas políticas (conservadores y liberales), las características de la Constitución de 1833 y el impacto de las reformas realizadas por los liberales en la segunda mitad del siglo XIX.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (23, 'HI06 OA 04', 'Investigar sobre algunos aspectos culturales del siglo XIX, como los avances en educación y la fundación de instituciones, el aporte de intelectuales y científicos nacionales (por ejemplo, Diego Barros Arana, Benjamín Vicuña Mackenna, José Victorino Lastarria) y extranjeros (por ejemplo, Andrés Bello, Claudio Gay, Charles Darwin y María Graham), las primeras mujeres en obtener títulos universitarios y el impacto en la sociedad de la llegada del ferrocarril y de otros avances tecnológicos, entre otros.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (24, 'HI06 OA 05', 'Describir cómo se conformó el territorio de Chile durante el siglo XIX, considerando colonizaciones europeas, la incorporación de Isla de Pascua, la ocupación de la Araucanía, la Guerra del Pacífico y diversos conflictos bélicos, entre otros factores.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (25, 'HI06 OA 06', 'Caracterizar los principales aspectos que definieron el período de riqueza aportada por la explotación del salitre, considerando la expansión económica y el inicio de la ''cuestión social''.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (26, 'HI06 OA 07', 'Explicar y dar ejemplos de la progresiva democratización de la sociedad durante el siglo XX, considerando el acceso creciente al voto, la participación de la mujer en la vida pública y el acceso a la educación ya la cultura, entre otros.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (27, 'HI06 OA 08', 'Comparar diferentes visiones sobre el quiebre de la democracia en Chile, el régimen o dictadura militar y el proceso de recuperación de la democracia a fines del siglo XX, considerando los distintos actores, experiencias y puntos de vista, y el consenso actual con respecto al valor de la democracia.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (28, 'HI06 OA 09', 'Explicar y dar ejemplos de aspectos que se mantienen y aspectos que han cambiado o se han desarrollado en la sociedad chilena a lo largo de su historia.')")

    // Matemática (OAs 29..36 -> Unidad 2)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (29, 'MA06 OA 01', 'Demostrar que comprenden los factores y múltiplos: determinando los múltiplos y factores de números naturales menores de 100, identificando números primos y compuestos, resolviendo problemas que involucran múltiplos.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (30, 'MA06 OA 02', 'Realizar cálculos que involucren las cuatro operaciones en el contexto de la resolución de problemas, utilizando la calculadora en ámbitos superiores a 10000.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (31, 'MA06 OA 03', 'Demostrar que comprenden el concepto de razón de manera concreta, pictórica y simbólica, en forma manual y/o usando software educativo.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (32, 'MA06 OA 04', 'Demostrar que comprenden el concepto de porcentaje de manera concreta, pictórica y simbólica, de forma manual y/o usando software educativo.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (33, 'MA06 OA 05', 'Demostrar que comprenden las fracciones y números mixtos.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (34, 'MA06 OA 06', 'Resolver adiciones y sustracciones de fracciones propias e impropias y números mixtos con numeradores y denominadores de hasta dos dígitos.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (35, 'MA06 OA 07', 'Demostrar que comprenden la multiplicación y la división de decimales por números naturales de un dígito, múltiplos de 10 y decimales hasta la milésima de manera concreta, pictórica y simbólica.')")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje (objetivo_aprendizaje_id, codigo, descripcion) VALUES (36, 'MA06 OA 08', 'Resolver problemas rutinarios y no rutinarios que involucren adiciones y sustracciones de fracciones propias, impropias, números mixtos o decimales hasta la milésima.')")

    // 14. ObjetivoAprendizajeUnidad
    for (oaId in 1L..12L) {
        db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_unidad (id, objetivo_aprendizaje_id, unidad_id) VALUES ($oaId, $oaId, 1)")
    }
    for (i in 0..6) {
        val id = 13L + i
        val oaId = 13L + i
        db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_unidad (id, objetivo_aprendizaje_id, unidad_id) VALUES ($id, $oaId, 3)")
    }
    for (i in 0..8) {
        val id = 20L + i
        val oaId = 20L + i
        db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_unidad (id, objetivo_aprendizaje_id, unidad_id) VALUES ($id, $oaId, 4)")
    }
    for (i in 0..7) {
        val id = 29L + i
        val oaId = 29L + i
        db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_unidad (id, objetivo_aprendizaje_id, unidad_id) VALUES ($id, $oaId, 2)")
    }
}

@Database(
    entities = [
        // Identidad (5)
        RolEntity::class,
        NivelEntity::class,
        SeccionEntity::class,
        CursoEntity::class,
        UsuarioEntity::class,

        // Currículo (15)
        AsignaturaEntity::class,
        EstadoAutorizacionEntity::class,
        TipoActividadEntity::class,
        TipoRecursoEntity::class,
        UnidadEntity::class,
        ObjetivoAprendizajeEntity::class,
        ObjetivoAprendizajeUnidadEntity::class,
        ContenidoEntity::class,
        RecursoEntity::class,
        ActividadEntity::class,
        PreguntaEntity::class,
        AlternativaEntity::class,
        AlternativaPreguntaEntity::class,
        PreguntaActividadEntity::class,
        ObjetivoAprendizajeContenidoEntity::class,

        // Docencia (2)
        AsignaturaCursoEntity::class,
        UsuarioCursoEntity::class,

        // Evaluación (2)
        IntentoEntity::class,
        RespuestaEntity::class,

        // Informe (2)
        InformePedagogicoEntity::class,
        DetalleInformePedagogicoEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class MpazDatabase : RoomDatabase() {

    abstract fun identidadDao(): IdentidadDao
    abstract fun curriculoDao(): CurriculoDao
    abstract fun docenciaDao(): DocenciaDao
    abstract fun evaluacionDao(): EvaluacionDao
    abstract fun informeDao(): InformeDao

    fun resetDatabase() {
        clearAllTables()
        seedInitialData(openHelper.writableDatabase)
    }

    companion object {
        @Volatile
        private var INSTANCE: MpazDatabase? = null

        fun getDatabase(context: Context): MpazDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MpazDatabase::class.java,
                    "mpaz_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        seedInitialData(db)
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

