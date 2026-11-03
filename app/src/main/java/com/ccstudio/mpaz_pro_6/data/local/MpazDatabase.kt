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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_1_4 = object : Migration(1, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_2_4 = object : Migration(2, 4) {
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
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (3, 'Ciencias de la Vida', 3, 2)")
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

    // 15. Contenidos (Markdown)
    // Contenido 1: Lenguaje - Estrategias para comprender lo que leemos (Unidad 1)
    val c1Markdown = """# Estrategias para comprender lo que leemos

## ¿Cómo podemos comprender mejor un texto?

Leer es mucho más que reconocer palabras y pronunciarlas correctamente. Cuando leemos, nuestra mente trabaja para comprender las ideas, relacionar información y construir significados.

Cada texto nos entrega información que podemos conectar con lo que ya sabemos, con nuestras experiencias y con otros conocimientos que hemos adquirido.

Para comprender mejor lo que leemos, podemos utilizar distintas estrategias antes, durante y después de la lectura.

## 1. Antes de leer: activar nuestros conocimientos

Antes de comenzar, podemos observar el título, las imágenes y otros elementos que acompañan al texto.

Esto nos permite anticipar de qué podría tratarse la lectura y recordar lo que sabemos sobre ese tema.

**Por ejemplo:**

Si vamos a leer un texto titulado *La importancia de los bosques nativos*, podemos preguntarnos:

- ¿Qué sabemos sobre los bosques nativos?
- ¿Qué animales y plantas viven en ellos?
- ¿Por qué son importantes para nuestro entorno?

Estas preguntas nos ayudan a preparar nuestra mente para recibir nueva información.

## 2. Durante la lectura: identificar y relacionar información

Mientras leemos, podemos utilizar diferentes estrategias para comprender las ideas que presenta el texto.

**Identificar información explícita**

Es reconocer información que aparece directamente en el texto. Para encontrarla, podemos buscar nombres, fechas, lugares, acontecimientos y otros datos.

**Inferir información**

Consiste en descubrir información que el autor no expresa directamente, utilizando pistas del texto y nuestros conocimientos.

Por ejemplo, si leemos:

> Camila salió de su casa con paraguas y botas. Mientras caminaba, observó que las calles estaban cubiertas de agua.

Podemos inferir que probablemente estaba lloviendo, aunque el texto no lo diga expresamente.

**Releer cuando sea necesario**

Si encontramos una palabra, oración o párrafo que nos resulta difícil de comprender, podemos volver a leerlo con atención. A veces, una segunda lectura nos permite descubrir información que pasó inadvertida.

**Formular preguntas**

Hacer preguntas durante la lectura nos ayuda a mantener la atención y profundizar en las ideas.

Algunas preguntas útiles son:

- ¿De quién o de qué habla el texto?
- ¿Qué ocurrió?
- ¿Por qué sucedió?
- ¿Qué información importante nos entrega?

## 3. Después de leer: organizar y sintetizar la información

Cuando terminamos una lectura, podemos realizar algunas acciones para comprobar qué comprendimos y recordar las ideas más importantes.

**Identificar las ideas principales**

La idea principal corresponde a la información más importante de un párrafo o texto. Las ideas secundarias entregan detalles, explicaciones o ejemplos que complementan esa información.

**Organizar la información**

Podemos utilizar esquemas, cuadros comparativos o mapas conceptuales para relacionar las ideas y observar cómo se conectan entre sí.

**Elaborar un resumen**

Resumir consiste en expresar las ideas principales de un texto utilizando nuestras propias palabras y manteniendo su significado.

Un buen resumen permite recordar lo esencial de una lectura de manera clara y ordenada.

## 4. Pongamos en práctica lo aprendido

Imaginemos que leemos el siguiente texto:

### Las abejas y su importancia para el planeta

Las abejas cumplen una función fundamental en los ecosistemas. Al visitar las flores para obtener néctar y polen, transportan pequeñas partículas de polen de una flor a otra. Este proceso se conoce como polinización y permite que muchas plantas produzcan frutos y semillas.

Además, la polinización contribuye a la producción de numerosos alimentos que consumimos diariamente, como manzanas, frutillas y almendras.

Sin embargo, las abejas enfrentan diversas amenazas, entre ellas la pérdida de sus hábitats, el uso de ciertos pesticidas y el cambio climático.

### ¿Cómo aplicamos las estrategias?

- **Conocimientos previos:** pensamos en lo que sabemos sobre las abejas y su relación con las flores.
- **Información explícita:** identificamos que las abejas transportan polen entre las flores.
- **Inferencias:** podemos concluir que la disminución de las abejas podría afectar la producción de algunos alimentos.
- **Idea principal:** las abejas son fundamentales para los ecosistemas y la producción de alimentos.
- **Resumen:** las abejas contribuyen a la polinización, un proceso necesario para la reproducción de muchas plantas y la producción de alimentos. Actualmente enfrentan amenazas que ponen en riesgo su población.

## Para recordar

Comprender un texto es un proceso que podemos fortalecer con la práctica. Activar nuestros conocimientos, identificar información, realizar inferencias, formular preguntas y organizar las ideas nos permite aprovechar mejor cada lectura.

**Cada vez que leemos con atención, tenemos una nueva oportunidad para aprender.**""".replace("'", "''")

    // Contenido 2: Lenguaje - Comprendemos y analizamos textos narrativos (Unidad 1)
    val c2Markdown = """# Comprendemos y analizamos textos narrativos

## ¿Qué hace interesante una historia?

Cuando leemos un cuento, una leyenda o una fábula, nos encontramos con personajes que viven experiencias, enfrentan dificultades y toman decisiones. Cada uno de estos elementos contribuye al desarrollo de la historia.

Comprender una narración significa descubrir qué sucede, quiénes participan, dónde ocurren los acontecimientos y por qué los personajes actúan de determinada manera.

En este contenido aprenderemos a reconocer los elementos de una narración y analizar cómo se relacionan entre sí.

## 1. ¿Qué es un texto narrativo?

Un texto narrativo es un relato que presenta una serie de acontecimientos protagonizados por uno o varios personajes. Estos acontecimientos pueden ser reales o imaginarios y se desarrollan en un tiempo y lugar determinados.

Existen distintos tipos de narraciones, entre ellas:

- **Cuentos:** relatos breves que presentan personajes y acontecimientos organizados en una historia.
- **Fábulas:** narraciones que generalmente utilizan animales como personajes y transmiten una enseñanza o moraleja.
- **Leyendas:** relatos tradicionales que mezclan elementos reales y fantásticos, y que forman parte de la cultura de una comunidad.
- **Mitos:** narraciones tradicionales que explican el origen del mundo, fenómenos naturales o creencias de una cultura.
- **Novelas:** narraciones extensas que desarrollan historias, personajes y conflictos con mayor profundidad.

## 2. Elementos de una narración

Para comprender una historia, debemos prestar atención a sus principales elementos.

### Los personajes

Son quienes participan en los acontecimientos de la historia. Cada personaje puede tener características, intereses, motivaciones y formas particulares de actuar.

Podemos distinguir:

- **Personaje principal:** participa directamente en los acontecimientos centrales de la historia.
- **Personajes secundarios:** acompañan al personaje principal y contribuyen al desarrollo de los acontecimientos.

También es importante observar cómo reaccionan los personajes frente a las situaciones que enfrentan. Sus decisiones nos permiten comprender sus motivaciones y características.

### El ambiente

Corresponde al entorno en el que ocurren los acontecimientos. Podemos reconocer dos dimensiones:

- **Ambiente físico:** lugar donde sucede la historia, como un bosque, una ciudad, una escuela o una montaña.
- **Ambiente social:** características de la comunidad, las costumbres, las relaciones y las condiciones de vida de los personajes.

El ambiente influye en las acciones de los personajes y en las situaciones que deben enfrentar.

### El tiempo

Corresponde al momento o período en que ocurren los acontecimientos. Una historia puede desarrollarse durante unas horas, varios días, años o incluso siglos.

Reconocer el tiempo nos ayuda a comprender el contexto de la narración y las circunstancias que rodean a sus personajes.

### El conflicto

Es la situación o dificultad que impulsa el desarrollo de la historia. Los personajes deben enfrentarla y tomar decisiones para avanzar.

Por ejemplo, un personaje puede tener que resolver un problema, superar un desafío o alcanzar un objetivo.

## 3. ¿Cómo se organiza una narración?

Generalmente, las narraciones presentan tres momentos que permiten organizar los acontecimientos.

**Inicio**

Se presentan los personajes, el ambiente y las primeras situaciones de la historia. El lector conoce el contexto en el que se desarrollarán los acontecimientos.

**Desarrollo**

Aparece el conflicto y se presentan las acciones que realizan los personajes para enfrentarlo. En esta parte suelen ocurrir los acontecimientos más importantes.

**Desenlace**

Se resuelve el conflicto y se conoce el resultado de las acciones de los personajes. La historia llega a su conclusión.

## 4. Analizamos las acciones y motivaciones de los personajes

Una buena comprensión lectora nos permite ir más allá de reconocer lo que sucede. También podemos analizar por qué suceden los acontecimientos y qué motivaciones tienen los personajes.

Por ejemplo, si un personaje decide compartir su alimento con alguien que tiene hambre, podemos preguntarnos:

- ¿Qué situación motivó su decisión?
- ¿Qué característica de su personalidad demuestra esta acción?
- ¿Qué consecuencias puede tener su decisión en la historia?

Al responder estas preguntas, utilizamos información del relato para fundamentar nuestras interpretaciones.

## 5. El lenguaje figurado en las narraciones

En algunas historias encontramos expresiones que utilizan las palabras de manera creativa para transmitir ideas, emociones o imágenes.

Por ejemplo:

**"El viento susurraba entre los árboles".**

El viento no puede hablar ni susurrar como una persona. Esta expresión utiliza una personificación para transmitir la sensación de un viento suave que se mueve entre los árboles.

Otro ejemplo:

**"Tenía un corazón de piedra".**

Esta expresión no significa que el corazón esté hecho de piedra. Se utiliza para representar a una persona que parece no demostrar sentimientos o compasión.

Reconocer estas expresiones nos permite comprender mejor lo que el autor quiere transmitir.

## 6. Pongamos en práctica lo aprendido

### La decisión de Martina

Una mañana de otoño, Martina caminaba por un sendero cercano a su casa. Le gustaba recorrer aquel lugar porque podía observar los árboles, escuchar el canto de los pájaros y disfrutar de la tranquilidad del bosque.

Mientras avanzaba, encontró una pequeña caja de madera junto a un árbol. Al abrirla, descubrió que contenía varias fotografías antiguas y una carta.

La carta estaba dirigida a una persona llamada Elena y explicaba que aquellas fotografías pertenecían a una familia que había vivido en el sector muchos años atrás.

Martina sintió curiosidad por conocer la historia de aquella familia. En lugar de llevarse la caja a su casa, decidió entregarla en la biblioteca del pueblo, donde podrían investigar su origen.

Días después, la bibliotecaria descubrió que las fotografías correspondían a los primeros habitantes de la localidad. Gracias al hallazgo, pudieron organizar una exposición para compartir aquella historia con toda la comunidad.

### Analicemos la narración

- **Personaje principal:** Martina, quien encuentra la caja y decide entregarla en la biblioteca.
- **Personajes secundarios:** la bibliotecaria y las personas relacionadas con la historia de las fotografías.
- **Ambiente físico:** un sendero del bosque y la biblioteca del pueblo.
- **Tiempo:** una mañana de otoño y los días posteriores al hallazgo.
- **Conflicto o situación central:** el descubrimiento de una caja con fotografías y una carta cuyo origen se desconoce.
- **Desenlace:** las fotografías permiten conocer parte de la historia de los primeros habitantes de la localidad.

### Reflexionemos

La decisión de Martina demuestra curiosidad y responsabilidad. Al entregar la caja en la biblioteca, permitió que otras personas pudieran investigar y conocer la historia de su comunidad.

Esta situación nos muestra cómo las acciones de un personaje pueden influir en el desarrollo de una historia y beneficiar a otras personas.

## Para recordar

Cuando leemos una narración, podemos comprenderla mejor si identificamos sus personajes, el ambiente, el tiempo y el conflicto.

También es importante analizar las acciones y motivaciones de los personajes, reconocer cómo se relacionan los acontecimientos e interpretar las expresiones que utiliza el autor.

**Leer una historia es descubrir mucho más que los acontecimientos: es comprender las decisiones, emociones y experiencias que dan vida a sus personajes.**""".replace("'", "''")

    // Contenido 3: Ciencias Naturales - La fotosíntesis (Unidad 3)
    val c3Markdown = """# La fotosíntesis: cómo las plantas producen su alimento

## ¿Cómo obtienen alimento las plantas?

Las plantas son organismos fundamentales para la vida en nuestro planeta. A diferencia de los animales, no necesitan alimentarse de otros seres vivos para obtener la energía química que utilizan para desarrollarse. Producen su propio alimento mediante un proceso llamado **fotosíntesis**.

La fotosíntesis es un proceso mediante el cual las plantas utilizan la energía de la luz solar para transformar agua y dióxido de carbono en azúcares. Durante este proceso también liberan oxígeno al ambiente.

Este fenómeno ocurre principalmente en las hojas, donde se encuentran estructuras celulares llamadas cloroplastos, que contienen clorofila, un pigmento responsable de captar la energía luminosa.

## 1. ¿Qué necesitan las plantas para realizar la fotosíntesis?

Para que la fotosíntesis ocurra, las plantas necesitan tres elementos fundamentales:

### Agua

El agua es absorbida desde el suelo a través de las raíces y transportada hacia las hojas mediante estructuras conductoras de la planta.

Este elemento aporta la materia necesaria para que ocurran las transformaciones químicas propias de la fotosíntesis.

### Dióxido de carbono

Es un gas presente en el aire que ingresa principalmente a través de pequeñas aberturas de las hojas llamadas estomas.

El dióxido de carbono aporta el carbono que la planta utiliza para producir azúcares.

### Energía lumínica

Proviene principalmente del Sol y es captada por la clorofila presente en los cloroplastos.

Esta energía permite que ocurran las transformaciones necesarias para producir azúcares a partir del agua y el dióxido de carbono.

## 2. ¿Qué producen las plantas durante la fotosíntesis?

Como resultado de la fotosíntesis se producen dos elementos importantes:

**Azúcares:** son sustancias que almacenan energía química y que la planta utiliza para obtener energía, crecer y desarrollar sus estructuras.

**Oxígeno:** es liberado al ambiente y puede ser utilizado por numerosos organismos durante la respiración celular.

Por esta razón, la fotosíntesis es un proceso esencial para mantener la vida en los ecosistemas.

## 3. ¿Dónde ocurre la fotosíntesis?

La fotosíntesis ocurre principalmente en las hojas de las plantas.

Dentro de sus células encontramos los cloroplastos, que contienen clorofila. Este pigmento absorbe parte de la luz que llega desde el Sol y permite aprovechar su energía.

Podemos reconocer una relación entre las distintas estructuras de la planta:

- Las raíces absorben agua del suelo.
- Las hojas incorporan dióxido de carbono del aire.
- Los cloroplastos captan la energía lumínica.
- Las hojas liberan oxígeno al ambiente.

## 4. ¿Qué factores pueden influir en la fotosíntesis?

La disponibilidad de agua, dióxido de carbono y energía lumínica influye directamente en la fotosíntesis.

Por ejemplo, una planta que recibe suficiente agua y dióxido de carbono, pero permanece en un lugar con muy poca luz, tendrá dificultades para realizar este proceso a un ritmo adecuado.

Del mismo modo, la falta de agua puede limitar la fotosíntesis, incluso cuando la planta recibe abundante luz solar.

Por eso, estos tres elementos deben estar disponibles para que la planta pueda desarrollar adecuadamente este proceso.

## 5. La fotosíntesis y la historia de la ciencia

El conocimiento científico sobre la fotosíntesis se ha construido gracias al trabajo de diferentes investigadores.

A lo largo del tiempo, científicos como Jan Ingenhousz, Joseph Priestley y Melvin Calvin realizaron aportes importantes para comprender cómo las plantas interactúan con la luz, el aire y otras sustancias.

Sus investigaciones permitieron conocer mejor los procesos que ocurren en las plantas y explicar cómo producen azúcares y liberan oxígeno.

Este conocimiento también ha permitido comprender la importancia de la vegetación para los ecosistemas y para la vida en nuestro planeta.

## 6. Pongamos en práctica lo aprendido

Imaginemos que una estudiante realiza una investigación con dos plantas de características similares.

Ambas reciben la misma cantidad de agua y se encuentran en lugares con condiciones ambientales semejantes. Sin embargo, una de ellas permanece cerca de una ventana con abundante luz solar, mientras que la otra se mantiene durante varios días en un lugar oscuro.

Al observarlas, la estudiante registra diferencias en su desarrollo.

**¿Qué podemos analizar?**

- La luz solar proporciona la energía necesaria para la fotosíntesis.
- La disponibilidad de agua y dióxido de carbono también influye en este proceso.
- Una menor disponibilidad de luz puede disminuir la actividad fotosintética.
- Las observaciones permiten formular preguntas y desarrollar investigaciones experimentales.

## Para recordar

La fotosíntesis es un proceso mediante el cual las plantas utilizan agua, dióxido de carbono y energía lumínica para producir azúcares y liberar oxígeno.

Gracias a este proceso, los organismos fotosintéticos producen materia orgánica que constituye la base de muchas relaciones alimentarias en los ecosistemas.

**Las plantas transforman la energía de la luz solar en energía química almacenada en sustancias que permiten sostener la vida.**""".replace("'", "''")

    // Contenido 4: Ciencias Naturales - Cadenas y redes alimentarias (Unidad 3)
    val c4Markdown = """# Cadenas y redes alimentarias: cómo se relacionan los seres vivos

## ¿De dónde obtienen energía los seres vivos?

En los ecosistemas, los organismos necesitan obtener energía y materia para crecer, desarrollarse y realizar sus funciones vitales.

Las plantas, las algas y algunos microorganismos pueden producir su propio alimento mediante la fotosíntesis. Otros seres vivos obtienen energía alimentándose de plantas o de otros animales.

De esta manera, los organismos se relacionan mediante la alimentación, formando cadenas y redes alimentarias.

## 1. ¿Qué es una cadena alimentaria?

Una cadena alimentaria representa cómo se transfiere la materia y la energía entre los organismos de un ecosistema a través de la alimentación.

Cada organismo ocupa una posición dentro de esta cadena, dependiendo de cómo obtiene su alimento.

Por ejemplo:

**Pasto → Saltamontes → Rana → Serpiente → Águila**

En esta cadena:

- El pasto produce su propio alimento mediante la fotosíntesis.
- El saltamontes se alimenta del pasto.
- La rana se alimenta del saltamontes.
- La serpiente se alimenta de la rana.
- El águila se alimenta de la serpiente.

Las flechas indican la dirección en que se transfiere la materia y la energía: desde el organismo que sirve de alimento hacia el organismo que lo consume.

## 2. ¿Qué función cumplen los organismos en una cadena alimentaria?

Podemos clasificar a los organismos según la función que cumplen en la transferencia de materia y energía.

### Productores

Son organismos capaces de producir su propio alimento, principalmente mediante la fotosíntesis.

Por ejemplo:
- Plantas.
- Algas.
- Algunas bacterias.

Los productores constituyen la base de muchas cadenas alimentarias.

### Consumidores

Son organismos que obtienen materia y energía alimentándose de otros seres vivos.

Podemos distinguir:

- **Consumidores primarios:** se alimentan directamente de los productores. Por ejemplo, un conejo que come pasto.
- **Consumidores secundarios:** se alimentan de consumidores primarios. Por ejemplo, una serpiente que se alimenta de un ratón herbívoro.
- **Consumidores terciarios:** se alimentan de consumidores secundarios. Por ejemplo, un águila que captura una serpiente.

### Descomponedores

Son organismos que obtienen materia y energía al descomponer restos de seres vivos y desechos orgánicos.

Entre ellos encontramos hongos y bacterias.

Su función es fundamental porque permiten que los nutrientes regresen al ambiente y puedan ser aprovechados nuevamente por los organismos productores.

## 3. ¿Qué diferencia existe entre materia y energía?

En una cadena alimentaria se transfieren tanto materia como energía, pero ambas siguen dinámicas diferentes.

**La materia** circula entre los organismos y el ambiente. Por ejemplo, los nutrientes presentes en el suelo pueden ser absorbidos por las plantas y luego pasar a los animales que se alimentan de ellas. Mediante la descomposición, parte de esa materia regresa al ambiente.

**La energía** ingresa principalmente al ecosistema desde el Sol. Los organismos productores captan parte de esa energía mediante la fotosíntesis y la transfieren a otros organismos a través de la alimentación.

En cada transferencia, una parte de la energía se utiliza en las funciones vitales y se disipa al ambiente en forma de calor.

Por esta razón, la energía disponible disminuye a medida que avanzamos hacia los siguientes niveles de una cadena alimentaria.

## 4. ¿Qué es una red alimentaria?

En la naturaleza, los organismos suelen alimentarse de distintas especies y, al mismo tiempo, pueden servir de alimento para varios organismos.

Por esta razón, muchas cadenas alimentarias se conectan entre sí y forman una **red alimentaria**.

Una red alimentaria representa las múltiples relaciones de alimentación que existen dentro de un ecosistema.

Por ejemplo, en un ecosistema terrestre:

- El pasto puede ser consumido por conejos, saltamontes y ratones.
- Los saltamontes pueden ser consumidos por ranas y aves.
- Los ratones pueden ser consumidos por serpientes y águilas.
- Las serpientes pueden ser consumidas por águilas.

Estas relaciones muestran que los organismos dependen unos de otros para obtener materia y energía.

## 5. ¿Cómo afecta la actividad humana a las redes alimentarias?

Las actividades humanas pueden modificar las condiciones de los ecosistemas y afectar las relaciones alimentarias entre sus organismos.

Algunos ejemplos son:

**Deforestación:** la eliminación de grandes extensiones de vegetación reduce los hábitats y los recursos alimentarios de muchas especies.

**Contaminación:** la presencia de sustancias contaminantes en el agua, el suelo o el aire puede afectar a los productores, consumidores y descomponedores.

**Sobreexplotación de especies:** la captura o extracción excesiva de organismos puede disminuir sus poblaciones y alterar las relaciones alimentarias.

**Introducción de especies exóticas invasoras:** algunas especies introducidas pueden competir con las especies nativas o convertirse en nuevos depredadores, modificando el equilibrio del ecosistema.

Cuando una población disminuye considerablemente, otros organismos que dependen de ella pueden verse afectados. Por eso, las modificaciones en una parte de la red alimentaria pueden producir consecuencias en todo el ecosistema.

## 6. ¿Cómo podemos proteger las redes alimentarias?

El cuidado de los ecosistemas permite mantener las relaciones entre los seres vivos y conservar la biodiversidad.

Algunas acciones que contribuyen a su protección son:

- Evitar la contaminación de ríos, lagos y suelos.
- Proteger los bosques y la vegetación nativa.
- Respetar las áreas naturales protegidas.
- Evitar la extracción indiscriminada de especies.
- Participar en actividades de conservación y restauración ambiental.

Cada una de estas acciones contribuye a mantener las condiciones necesarias para que los organismos puedan desarrollarse y relacionarse.

## 7. Pongamos en práctica lo aprendido

Imaginemos un ecosistema donde encontramos pasto, conejos, zorros y hongos descomponedores.

Podemos representar algunas de sus relaciones de la siguiente manera:

**Pasto → Conejo → Zorro**

Además, cuando los organismos mueren, sus restos pueden ser descompuestos por los hongos, permitiendo que los nutrientes regresen al suelo.

Ahora imaginemos que una actividad humana provoca la desaparición de gran parte del pasto.

¿Qué podría ocurrir?

- Los conejos tendrían menos alimento disponible.
- La población de conejos podría disminuir.
- Los zorros podrían encontrar menos alimento.
- Las relaciones alimentarias del ecosistema se verían modificadas.

Este ejemplo demuestra que los organismos están conectados y que los cambios en una población pueden afectar a otras especies.

## Para recordar

Las cadenas y redes alimentarias representan las relaciones mediante las cuales los seres vivos transfieren materia y energía.

Los productores, consumidores y descomponedores cumplen funciones fundamentales para el funcionamiento de los ecosistemas.

Las actividades humanas pueden modificar estas relaciones, por lo que proteger los ambientes naturales es esencial para conservar la biodiversidad.

**En un ecosistema, cada organismo cumple una función y forma parte de una red de relaciones que sostiene la vida.**""".replace("'", "''")

    db.execSQL("INSERT OR IGNORE INTO contenido (contenido_id, descripcion, unidad_id, estado_id) VALUES (1, '$c1Markdown', 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO contenido (contenido_id, descripcion, unidad_id, estado_id) VALUES (2, '$c2Markdown', 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO contenido (contenido_id, descripcion, unidad_id, estado_id) VALUES (3, '$c3Markdown', 3, 4)")
    db.execSQL("INSERT OR IGNORE INTO contenido (contenido_id, descripcion, unidad_id, estado_id) VALUES (4, '$c4Markdown', 3, 4)")

    // 16. ObjetivoAprendizajeContenido
    // Contenido 1 (Lenguaje C1): LE06 OA 02 (id 2), LE06 OA 06 (id 6), LE06 OA 08 (id 8)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (1, 1, 2, 1)")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (2, 1, 6, 1)")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (3, 1, 8, 1)")

    // Contenido 2 (Lenguaje C2): LE06 OA 03 (id 3), LE06 OA 04 (id 4)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (4, 2, 3, 1)")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (5, 2, 4, 1)")

    // Contenido 3 (Ciencias C1): CN06 OA 01 (id 13)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (6, 3, 13, 3)")

    // Contenido 4 (Ciencias C2): CN06 OA 02 (id 14), CN06 OA 03 (id 15)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (7, 4, 14, 3)")
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (8, 4, 15, 3)")

    // 17. Actividades (1 por contenido)
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, habilitada, contenido_id, tipo_actividad_id, estado_id) VALUES (1, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 1, 1, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, habilitada, contenido_id, tipo_actividad_id, estado_id) VALUES (2, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 1, 2, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, habilitada, contenido_id, tipo_actividad_id, estado_id) VALUES (3, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 1, 3, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, habilitada, contenido_id, tipo_actividad_id, estado_id) VALUES (4, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 1, 4, 1, 4)")

    // 18. Preguntas (3 por actividad, 12 en total)
    // Actividad 1 (Lenguaje C1)
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (1, '¿Cuál es el principal propósito de activar nuestros conocimientos previos antes de leer un texto?', NULL)")
    val p2Enunciado = """Lee el siguiente fragmento:

"Martín observó por la ventana. El cielo estaba oscuro, los árboles se movían con fuerza y las primeras gotas golpeaban el techo. Rápidamente, fue a buscar su chaqueta impermeable".

¿Qué podemos inferir a partir de la información del texto?""".replace("'", "''")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (2, '$p2Enunciado', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (3, '¿Cuál de las siguientes opciones describe mejor lo que significa elaborar un resumen?', NULL)")

    // Actividad 2 (Lenguaje C2)
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (4, '¿Cuál es la función principal del conflicto dentro de una narración?', NULL)")
    val p5Enunciado = """Lee el siguiente fragmento:

"Pedro encontró una billetera en el patio de su escuela. Aunque necesitaba dinero para comprar materiales, decidió entregarla a su profesora para que pudiera encontrar a su dueño".

¿Qué podemos concluir sobre la actitud de Pedro?""".replace("'", "''")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (5, '$p5Enunciado', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (6, '¿Qué significa la expresión ''El sol sonreía sobre los campos'' dentro de una narración?', NULL)")

    // Actividad 3 (Ciencias C1)
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (7, '¿Cuáles son los tres elementos fundamentales que necesitan las plantas para realizar la fotosíntesis?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (8, '¿Qué función cumple la clorofila durante la fotosíntesis?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (9, 'Una planta recibe suficiente agua y dióxido de carbono, pero permanece varios días en un lugar con muy poca luz. ¿Qué consecuencia es más probable?', NULL)")

    // Actividad 4 (Ciencias C2)
    val p10Enunciado = """En la siguiente cadena alimentaria:

Pasto → Saltamontes → Rana → Serpiente

¿Qué función cumple el saltamontes?""".replace("'", "''")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (10, '$p10Enunciado', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (11, '¿Por qué se considera que las redes alimentarias representan mejor las relaciones de alimentación de un ecosistema que las cadenas alimentarias?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (12, 'En un bosque se produce una deforestación que reduce considerablemente la cantidad de plantas disponibles. ¿Cuál podría ser una consecuencia de esta situación?', NULL)")

    // 19. PreguntaActividad
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (1, 1, 1)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (2, 1, 2)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (3, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (4, 2, 4)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (5, 2, 5)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (6, 2, 6)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (7, 3, 7)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (8, 3, 8)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (9, 3, 9)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (10, 4, 10)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (11, 4, 11)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (12, 4, 12)")

    // 20. Alternativas (4 por pregunta, 48 en total)
    // Pregunta 1 (Respuesta B)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (1, 'Memorizar todas las palabras que aparecen en la lectura.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (2, 'Anticipar el tema y relacionar la información con lo que ya sabemos.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (3, 'Identificar inmediatamente todas las ideas secundarias.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (4, 'Elaborar un resumen antes de comenzar la lectura.', 0, NULL)")

    // Pregunta 2 (Respuesta C)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (5, 'Martín tenía que ir a comprar una chaqueta nueva.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (6, 'Martín estaba esperando a un amigo.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (7, 'Se aproximaba una tormenta o estaba comenzando a llover intensamente.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (8, 'Martín quería salir a jugar con sus amigos.', 0, NULL)")

    // Pregunta 3 (Respuesta B)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (9, 'Copiar todas las oraciones del texto en el mismo orden.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (10, 'Expresar las ideas principales utilizando nuestras propias palabras.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (11, 'Escribir solamente nuestra opinión sobre el tema.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (12, 'Seleccionar las palabras más difíciles y buscar su significado.', 0, NULL)")

    // Pregunta 4 (Respuesta C)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (13, 'Describir físicamente a todos los personajes.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (14, 'Presentar la época en que fue escrito el relato.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (15, 'Introducir una situación o dificultad que impulsa el desarrollo de la historia.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (16, 'Explicar la moraleja al comienzo del relato.', 0, NULL)")

    // Pregunta 5 (Respuesta B)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (17, 'Es una persona distraída que suele perder sus pertenencias.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (18, 'Demuestra honestidad al decidir devolver algo que no le pertenece.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (19, 'Está preocupado porque no tiene materiales escolares.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (20, 'Quiere recibir una recompensa por parte de su profesora.', 0, NULL)")

    // Pregunta 6 (Respuesta B)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (21, 'Que el sol tiene un rostro y puede expresar emociones.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (22, 'Que los campos estaban iluminados por la luz del sol, utilizando una personificación.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (23, 'Que una persona estaba sonriendo mientras observaba el sol.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (24, 'Que el sol estaba desapareciendo detrás de las montañas.', 0, NULL)")

    // Pregunta 7 (Respuesta B)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (25, 'Oxígeno, azúcar y energía térmica.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (26, 'Agua, dióxido de carbono y energía lumínica.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (27, 'Agua, oxígeno y sales minerales.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (28, 'Dióxido de carbono, oxígeno y energía térmica.', 0, NULL)")

    // Pregunta 8 (Respuesta A)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (29, 'Absorber la energía luminosa para que pueda utilizarse en la fotosíntesis.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (30, 'Transportar agua desde las raíces hasta las hojas.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (31, 'Liberar dióxido de carbono hacia el ambiente.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (32, 'Transformar directamente el oxígeno en azúcar.', 0, NULL)")

    // Pregunta 9 (Respuesta C)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (33, 'La planta producirá más oxígeno que una planta iluminada.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (34, 'La planta dejará de necesitar agua para desarrollarse.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (35, 'La actividad fotosintética disminuirá debido a la menor disponibilidad de energía lumínica.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (36, 'La planta comenzará a producir dióxido de carbono mediante la fotosíntesis.', 0, NULL)")

    // Pregunta 10 (Respuesta B)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (37, 'Productor.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (38, 'Consumidor primario.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (39, 'Consumidor secundario.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (40, 'Descomponedor.', 0, NULL)")

    // Pregunta 11 (Respuesta C)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (41, 'Porque muestran que todos los organismos se alimentan exclusivamente de plantas.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (42, 'Porque representan una única secuencia de alimentación entre organismos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (43, 'Porque muestran las múltiples conexiones entre diferentes cadenas alimentarias.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (44, 'Porque explican solamente cómo los descomponedores obtienen energía.', 0, NULL)")

    // Pregunta 12 (Respuesta D)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (45, 'Los consumidores primarios tendrían más alimento disponible.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (46, 'Las redes alimentarias permanecerían sin cambios.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (47, 'Los productores aumentarían automáticamente su población.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (48, 'Los herbívoros podrían disminuir por la falta de alimento, afectando también a sus depredadores.', 1, NULL)")

    // 21. AlternativaPregunta (vinculación)
    for (i in 1..48) {
        val preguntaId = ((i - 1) / 4) + 1
        db.execSQL("INSERT OR IGNORE INTO alternativa_pregunta (id, pregunta_id, alternativa_id) VALUES ($i, $preguntaId, $i)")
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
    version = 4,
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3, MIGRATION_3_4, MIGRATION_1_4, MIGRATION_2_4)
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

