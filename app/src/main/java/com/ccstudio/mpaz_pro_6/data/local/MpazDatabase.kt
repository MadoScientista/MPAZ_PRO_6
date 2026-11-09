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
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.ActividadCursoEntity
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

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE asignatura_curso ADD COLUMN docente_id INTEGER REFERENCES usuario(usuario_id) ON DELETE SET NULL")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_asignatura_curso_docente_id ON asignatura_curso (docente_id)")
        db.execSQL("UPDATE asignatura_curso SET docente_id = 2 WHERE id IN (1, 5)")
        db.execSQL("UPDATE asignatura_curso SET docente_id = 3 WHERE id IN (2, 6)")
        db.execSQL("UPDATE asignatura_curso SET docente_id = 4 WHERE id IN (3, 7)")
        db.execSQL("UPDATE asignatura_curso SET docente_id = 5 WHERE id IN (4, 8)")
    }
}

val MIGRATION_1_5 = object : Migration(1, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_2_5 = object : Migration(2, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_3_5 = object : Migration(3, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE informe_pedagogico ADD COLUMN fecha_corte TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE informe_pedagogico ADD COLUMN origen TEXT NOT NULL DEFAULT 'MANUAL'")
    }
}

val MIGRATION_1_6 = object : Migration(1, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_2_6 = object : Migration(2, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_3_6 = object : Migration(3, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_4_6 = object : Migration(4, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedMathData(db)
    }
}

val MIGRATION_1_7 = object : Migration(1, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_2_7 = object : Migration(2, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_3_7 = object : Migration(3, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_4_7 = object : Migration(4, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_5_7 = object : Migration(5, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedHistoryData(db)
    }
}

val MIGRATION_1_8 = object : Migration(1, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_2_8 = object : Migration(2, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_3_8 = object : Migration(3, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_4_8 = object : Migration(4, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_5_8 = object : Migration(5, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

val MIGRATION_6_8 = object : Migration(6, 8) {
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

    // 9. Asignaturas (con nivel_id)
    db.execSQL("INSERT OR IGNORE INTO asignatura (asignatura_id, nombre, nivel_id) VALUES (1, 'Lenguaje y Comunicación', 1)")
    db.execSQL("INSERT OR IGNORE INTO asignatura (asignatura_id, nombre, nivel_id) VALUES (2, 'Matemática', 1)")
    db.execSQL("INSERT OR IGNORE INTO asignatura (asignatura_id, nombre, nivel_id) VALUES (3, 'Ciencias Naturales', 1)")
    db.execSQL("INSERT OR IGNORE INTO asignatura (asignatura_id, nombre, nivel_id) VALUES (4, 'Historia, Geografía y Ciencias Sociales', 1)")

    // 10. Unidades
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (1, 'Lectura - Comprensión', 1, 2)")
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (2, 'Números y operaciones', 2, 3)")
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (3, 'Ciencias de la Vida', 3, 2)")
    db.execSQL("INSERT OR IGNORE INTO unidad (unidad_id, nombre, asignatura_id, estado_id) VALUES (4, 'Historia', 4, 4)")

    // 11. AsignaturaCurso
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id, docente_id) VALUES (1, 1, 1, 2)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id, docente_id) VALUES (2, 1, 2, 3)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id, docente_id) VALUES (3, 1, 3, 4)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id, docente_id) VALUES (4, 1, 4, 5)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id, docente_id) VALUES (5, 2, 1, 2)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id, docente_id) VALUES (6, 2, 2, 3)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id, docente_id) VALUES (7, 2, 3, 4)")
    db.execSQL("INSERT OR IGNORE INTO asignatura_curso (id, curso_id, asignatura_id, docente_id) VALUES (8, 2, 4, 5)")

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

    // 17. Actividades (1 por contenido) - Plantillas reutilizables
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (1, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 1, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (2, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 2, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (3, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 3, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (4, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 4, 1, 4)")

    // 17b. ActividadCurso (Configuración de actividades por curso)
    db.execSQL("CREATE TABLE IF NOT EXISTS actividad_curso (actividad_curso_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, actividad_id INTEGER NOT NULL, curso_id INTEGER NOT NULL, fecha_inicio TEXT, fecha_fin TEXT, habilitada INTEGER NOT NULL DEFAULT 0, visible INTEGER NOT NULL DEFAULT 0, max_intentos INTEGER, FOREIGN KEY(actividad_id) REFERENCES actividad(actividad_id) ON UPDATE NO ACTION ON DELETE NO ACTION, FOREIGN KEY(curso_id) REFERENCES curso(curso_id) ON UPDATE NO ACTION ON DELETE NO ACTION)")
    db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_actividad_curso_actividad_id_curso_id ON actividad_curso (actividad_id, curso_id)")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_actividad_curso_curso_id ON actividad_curso (curso_id)")

    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (1, 1, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (2, 1, 2, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (3, 2, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (4, 2, 2, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (5, 3, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (6, 3, 2, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (7, 4, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (8, 4, 2, 1, 1, 3)")

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

    seedMathData(db)
    seedHistoryData(db)
}

fun seedMathData(db: SupportSQLiteDatabase) {
    // 1. Actualizar estado de la Unidad de Matemática a PUBLICADO (4)
    db.execSQL("UPDATE unidad SET estado_id = 4 WHERE unidad_id = 2")

    // 2. Contenido de Matemática (ID = 5)
    val c5Markdown = """
# Factores, múltiplos y números primos

## ¿Cómo se relacionan los números entre sí?

En matemática, los números tienen propiedades que nos permiten establecer relaciones entre ellos. Por ejemplo, podemos descubrir cuándo un número puede dividirse exactamente por otro, cuáles son sus múltiplos y cómo se pueden descomponer en factores.

Estas relaciones son muy útiles para resolver problemas cotidianos, como organizar objetos en grupos iguales, distribuir materiales o calcular cuándo coinciden determinados acontecimientos.

En este contenido aprenderemos a reconocer múltiplos, identificar factores y distinguir entre números primos y compuestos.

## 1. ¿Qué son los múltiplos?

Los múltiplos de un número se obtienen al multiplicarlo por los números naturales.

Por ejemplo, para encontrar los múltiplos de 4, multiplicamos:

- 4 × 1 = 4
- 4 × 2 = 8
- 4 × 3 = 12
- 4 × 4 = 16
- 4 × 5 = 20

Por lo tanto, algunos múltiplos de 4 son:

**4, 8, 12, 16, 20, 24, 28, 32...**

Los múltiplos continúan indefinidamente, por lo que cada número natural tiene infinitos múltiplos.

### ¿Cómo podemos reconocer un múltiplo?

Un número es múltiplo de otro cuando se obtiene mediante una multiplicación por un número natural.

Por ejemplo:

- 30 es múltiplo de 5 porque 5 × 6 = 30.
- 42 es múltiplo de 7 porque 7 × 6 = 42.
- 25 es múltiplo de 4 porque no existe un número natural que multiplicado por 4 dé exactamente 25: en realidad, 25 no es múltiplo de 4.

## 2. ¿Qué son los factores o divisores?

Los factores de un número son aquellos números naturales que permiten dividirlo exactamente, es decir, sin dejar resto.

Por ejemplo, analicemos el número 12.

Podemos dividirlo exactamente por:

- 1, porque 12 ÷ 1 = 12.
- 2, porque 12 ÷ 2 = 6.
- 3, porque 12 ÷ 3 = 4.
- 4, porque 12 ÷ 4 = 3.
- 6, porque 12 ÷ 6 = 2.
- 12, porque 12 ÷ 12 = 1.

Por lo tanto, los factores de 12 son:

**1, 2, 3, 4, 6 y 12.**

### Relación entre factores y múltiplos

Los factores y los múltiplos están directamente relacionados.

Si 3 es factor de 18, significa que 18 es múltiplo de 3.

Podemos comprobarlo mediante la multiplicación:

3 × 6 = 18

Esta relación nos permite reconocer fácilmente cuándo un número es divisor o múltiplo de otro.

## 3. Números primos y compuestos

Los números naturales mayores que 1 pueden clasificarse, según su cantidad de divisores, en números primos y compuestos.

### Números primos

Un número primo es aquel que tiene exactamente dos divisores: el número 1 y él mismo.

Por ejemplo:

- 2 tiene como divisores al 1 y al 2.
- 3 tiene como divisores al 1 y al 3.
- 5 tiene como divisores al 1 y al 5.
- 7 tiene como divisores al 1 y al 7.

Por esta razón, 2, 3, 5 y 7 son números primos.

El número 2 tiene una característica especial: es el único número primo par.

### Números compuestos

Un número compuesto tiene más de dos divisores.

Por ejemplo, el número 12 tiene seis divisores: 1, 2, 3, 4, 6 y 12.

Otros ejemplos de números compuestos son 4, 6, 8, 9, 10 y 15.

**Importante:** el número 1 no es primo ni compuesto, porque tiene un solo divisor.

## 4. ¿Cómo podemos descomponer un número en factores primos?

Todo número compuesto puede expresarse como una multiplicación de números primos.

Este procedimiento se conoce como descomposición en factores primos.

Por ejemplo, descompongamos el número 24:

Primero, podemos dividirlo por 2:

24 ÷ 2 = 12

Luego continuamos dividiendo:

12 ÷ 2 = 6

6 ÷ 2 = 3

Finalmente, 3 es un número primo.

Por lo tanto:

24 = 2 × 2 × 2 × 3

Esta representación permite identificar los factores primos que componen un número.

## 5. ¿Para qué sirven los múltiplos y factores en la vida cotidiana?

Estas propiedades matemáticas nos permiten resolver situaciones relacionadas con la organización y distribución.

**Ejemplo 1: Organización de materiales**

Una profesora tiene 24 lápices y quiere distribuirlos en grupos con la misma cantidad de lápices, sin que sobre ninguno.

Puede formar grupos de:

- 2 grupos de 12 lápices.
- 3 grupos de 8 lápices.
- 4 grupos de 6 lápices.
- 6 grupos de 4 lápices.
- 8 grupos de 3 lápices.
- 12 grupos de 2 lápices.

En este caso, utilizamos los factores de 24 para encontrar distintas formas de distribuir los lápices.

**Ejemplo 2: Coincidencia de acontecimientos**

Una luz se enciende cada 4 segundos y otra cada 6 segundos. Si ambas se encienden al mismo tiempo, podemos utilizar los múltiplos de ambos números para descubrir cuándo volverán a coincidir.

Múltiplos de 4: 4, 8, 12, 16, 20, 24...

Múltiplos de 6: 6, 12, 18, 24, 30...

El primer múltiplo común es 12.

Por lo tanto, ambas luces volverán a encenderse simultáneamente después de 12 segundos.

## Para recordar

- Los múltiplos se obtienen mediante la multiplicación de un número por números naturales.
- Los factores o divisores permiten dividir un número exactamente.
- Los números primos tienen exactamente dos divisores.
- Los números compuestos tienen más de dos divisores.
- El número 1 no es primo ni compuesto.

**Comprender estas relaciones nos permite reconocer propiedades de los números y utilizarlas para resolver diferentes situaciones matemáticas.**
""".trimIndent().replace("'", "''")

    db.execSQL("INSERT OR IGNORE INTO contenido (contenido_id, descripcion, unidad_id, estado_id) VALUES (5, '$c5Markdown', 2, 4)")

    // 3. ObjetivoAprendizajeContenido
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (9, 5, 29, 2)")

    // 4. Actividades (5: Actividad 1, 6: Actividad 2, 7: Prueba Final)
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (5, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 5, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (6, 'Lee atentamente cada pregunta y selecciona la alternativa correcta. Solo una alternativa es válida.', 5, 1, 4)")
    val instPF = "Lee cuidadosamente cada pregunta y selecciona la alternativa correcta. Cada pregunta tiene cuatro alternativas y solo una respuesta válida. Cada respuesta correcta equivale a un punto."
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (7, '$instPF', 5, 3, 4)")

    // 5. ActividadCurso
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (9, 5, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (10, 5, 2, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (11, 6, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (12, 6, 2, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (13, 7, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (14, 7, 2, 1, 1, 3)")

    // 6. Preguntas (13..28)
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (13, '¿Cuál de los siguientes números es múltiplo de 7?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (14, '¿Cuál de las siguientes alternativas contiene todos los divisores del número 18?', NULL)")
    val p15 = "Una profesora tiene 36 cuadernos y quiere distribuirlos en grupos con la misma cantidad de cuadernos, sin que sobre ninguno. ¿Cuál de las siguientes cantidades de grupos es posible?".replace("'", "''")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (15, '$p15', NULL)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (16, '¿Cuál de los siguientes números es primo?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (17, '¿Por qué el número 15 se clasifica como compuesto?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (18, '¿Cuál es la descomposición correcta del número 30 en factores primos?', NULL)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (19, '¿Cuál de los siguientes números es múltiplo de 9?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (20, '¿Cuál de las siguientes alternativas presenta los primeros cinco múltiplos positivos de 6?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (21, '¿Cuál de los siguientes números es divisor de 48?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (22, '¿Cuál es el conjunto completo de divisores del número 20?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (23, '¿Cuál de los siguientes números es primo?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (24, '¿Por qué el número 1 no se considera primo ni compuesto?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (25, '¿Cuál es la descomposición correcta del número 36 en factores primos?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (26, '¿Cuál es la descomposición en factores primos del número 50?', NULL)")
    val p27 = "En una escuela hay 24 estudiantes que deben organizarse en equipos con igual cantidad de integrantes, sin que sobre ninguno. ¿Cuál de las siguientes cantidades de integrantes por equipo permite realizar esta distribución?".replace("'", "''")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (27, '$p27', NULL)")
    val p28 = "Dos autobuses salen de un terminal al mismo tiempo. Uno sale cada 8 minutos y el otro cada 12 minutos. ¿Después de cuántos minutos volverán a salir simultáneamente?".replace("'", "''")
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (28, '$p28', NULL)")

    // 7. PreguntaActividad (13..28)
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (13, 5, 13)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (14, 5, 14)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (15, 5, 15)")

    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (16, 6, 16)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (17, 6, 17)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (18, 6, 18)")

    for (pId in 19..28) {
        db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES ($pId, 7, $pId)")
    }

    // 8. Alternativas (49..112)
    // Pregunta 13
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (49, '32', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (50, '42', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (51, '45', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (52, '50', 0, NULL)")

    // Pregunta 14
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (53, '1, 2, 3, 6, 9 y 18.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (54, '1, 2, 4, 6, 9 y 18.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (55, '1, 3, 5, 6, 9 y 18.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (56, '2, 3, 6, 9 y 18.', 0, NULL)")

    // Pregunta 15
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (57, '5 grupos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (58, '7 grupos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (59, '8 grupos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (60, '9 grupos.', 1, NULL)")

    // Pregunta 16
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (61, '21', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (62, '27', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (63, '29', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (64, '33', 0, NULL)")

    // Pregunta 17
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (65, 'Porque es divisible solamente por 1 y por 15.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (66, 'Porque tiene más de dos divisores: 1, 3, 5 y 15.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (67, 'Porque es un número impar.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (68, 'Porque no puede dividirse exactamente por ningún número.', 0, NULL)")

    // Pregunta 18
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (69, '2 × 3 × 5.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (70, '2 × 2 × 5.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (71, '3 × 5 × 5.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (72, '2 × 3 × 3.', 0, NULL)")

    // Pregunta 19
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (73, '45', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (74, '52', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (75, '64', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (76, '70', 0, NULL)")

    // Pregunta 20
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (77, '6, 12, 18, 24, 30.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (78, '6, 10, 14, 18, 22.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (79, '1, 6, 12, 18, 24.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (80, '6, 12, 16, 24, 36.', 0, NULL)")

    // Pregunta 21
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (81, '5', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (82, '7', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (83, '8', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (84, '10', 0, NULL)")

    // Pregunta 22
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (85, '1, 2, 4, 5, 10 y 20.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (86, '1, 2, 3, 4, 5 y 20.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (87, '2, 4, 5, 10 y 20.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (88, '1, 2, 4, 6, 10 y 20.', 0, NULL)")

    // Pregunta 23
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (89, '35', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (90, '39', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (91, '41', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (92, '49', 0, NULL)")

    // Pregunta 24
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (93, 'Porque es un número impar.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (94, 'Porque solamente tiene un divisor positivo.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (95, 'Porque no puede dividirse por sí mismo.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (96, 'Porque es múltiplo de todos los números.', 0, NULL)")

    // Pregunta 25
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (97, '2 × 2 × 3 × 3.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (98, '2 × 3 × 6.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (99, '3 × 3 × 4.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (100, '2 × 2 × 2 × 3.', 0, NULL)")

    // Pregunta 26
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (101, '2 × 5 × 5.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (102, '2 × 2 × 5.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (103, '5 × 5 × 5.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (104, '2 × 25 × 5.', 0, NULL)")

    // Pregunta 27
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (105, '5 estudiantes.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (106, '7 estudiantes.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (107, '8 estudiantes.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (108, '10 estudiantes.', 0, NULL)")

    // Pregunta 28
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (109, '16 minutos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (110, '20 minutos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (111, '24 minutos.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (112, '32 minutos.', 0, NULL)")

    // 9. AlternativaPregunta (49..112)
    for (i in 49..112) {
        val preguntaId = ((i - 49) / 4) + 13
        db.execSQL("INSERT OR IGNORE INTO alternativa_pregunta (id, pregunta_id, alternativa_id) VALUES ($i, $preguntaId, $i)")
    }
}

fun seedHistoryData(db: SupportSQLiteDatabase) {
    // 1. Actualizar estado de la Unidad de Historia a PUBLICADO (4)
    db.execSQL("UPDATE unidad SET estado_id = 4 WHERE unidad_id = 4")

    // 2. Contenido de Historia (ID = 6)
    val c6Markdown = """
# Antecedentes de la Independencia de América y Chile

## ¿Por qué las colonias americanas comenzaron a buscar su independencia?

Durante varios siglos, gran parte del continente americano estuvo bajo el dominio de las monarquías europeas. En el caso de Chile y de muchos otros territorios americanos, el poder político dependía de la Corona española. Las autoridades eran designadas por el rey y las decisiones más importantes se tomaban siguiendo sus intereses.

Sin embargo, hacia fines del siglo XVIII y comienzos del siglo XIX, comenzaron a producirse cambios políticos, económicos y sociales que debilitaron el dominio español. Distintos acontecimientos ocurridos en Europa y América influyeron en las ideas de quienes vivían en las colonias.

La independencia no fue el resultado de una sola causa ni ocurrió al mismo tiempo en todos los territorios. Fue un proceso continental en el que participaron distintos grupos y que tuvo características particulares en cada lugar.

## 1. Antecedentes externos

Los antecedentes externos fueron acontecimientos e ideas que surgieron fuera de las colonias españolas y que influyeron en los movimientos independentistas americanos.

### La Ilustración

Durante el siglo XVIII se difundió en Europa un movimiento intelectual conocido como la Ilustración. Sus pensadores defendían el uso de la razón, la libertad y la igualdad ante la ley. También cuestionaban que una sola persona, como un rey, concentrara todo el poder político.

Estas ideas promovieron nuevas formas de entender el gobierno y la organización de la sociedad. Algunos criollos americanos conocieron estos planteamientos mediante libros, publicaciones y viajes.

### La independencia de Estados Unidos (1776)

Las trece colonias inglesas de América del Norte declararon su independencia de Gran Bretaña en 1776, después de un conflicto con la monarquía británica.

Este acontecimiento mostró que un territorio americano podía separarse de una potencia europea y organizar un gobierno propio. También difundió ideas relacionadas con la libertad y la representación política.

### La Revolución francesa (1789)

En 1789 comenzó la Revolución francesa, un proceso que cuestionó el poder absoluto del rey y los privilegios de ciertos grupos sociales.

Entre sus principales ideas estuvieron la libertad, la igualdad ante la ley y la soberanía nacional. Esta última plantea que el poder político tiene su origen en la nación y no únicamente en la voluntad de un monarca.

La Revolución francesa tuvo una gran influencia en la difusión de ideas políticas que también llegaron a América.

### La independencia de Haití (1804)

Haití fue una colonia francesa del Caribe donde se produjo una revolución protagonizada principalmente por personas esclavizadas y libres de ascendencia africana.

Después de años de conflicto, Haití declaró su independencia en 1804. Fue un acontecimiento importante porque se convirtió en el primer Estado independiente de América Latina y el Caribe y demostró que el sistema colonial podía ser cuestionado por distintos sectores de la población.

### La invasión napoleónica a España (1808)

En 1808, las tropas de Napoleón Bonaparte invadieron España. En medio de la crisis política, el rey Fernando VII fue apartado del trono y quedó prisionero. Napoleón puso a su hermano, José Bonaparte, como nuevo rey de España.

La situación generó una pregunta fundamental: ¿quién debía gobernar los territorios españoles si el rey legítimo estaba prisionero?

En España se organizaron juntas de gobierno para resistir la ocupación francesa. La crisis también tuvo consecuencias en América, donde comenzaron a surgir propuestas para formar gobiernos locales.

## 2. Antecedentes internos

Los antecedentes internos fueron situaciones que se desarrollaron dentro de las colonias americanas y que aumentaron el descontento con el dominio español.

### El descontento de los criollos

En la sociedad colonial existían diferencias entre los grupos sociales. Los españoles nacidos en España, llamados peninsulares, solían ocupar los cargos políticos y administrativos más importantes.

Los criollos, descendientes de españoles nacidos en América, podían tener importantes riquezas y propiedades, pero encontraban limitaciones para acceder a los cargos de mayor autoridad.

Esta situación generó descontento entre algunos sectores criollos, que comenzaron a cuestionar la desigualdad en el acceso al poder.

### Las reformas de la Corona española

Durante el siglo XVIII, los reyes de la dinastía Borbón impulsaron reformas para fortalecer el control sobre sus colonias y aumentar los ingresos de la Corona.

Estas medidas modificaron la administración y el comercio colonial. Aunque buscaban mejorar los recursos y el control de España, también provocaron conflictos de intereses entre comerciantes y grupos locales.

### El desarrollo de una identidad americana

Con el paso del tiempo, algunos criollos comenzaron a desarrollar un mayor sentido de pertenencia hacia los territorios donde habían nacido y vivido.

Aunque compartían elementos culturales con España, sus intereses y experiencias estaban relacionados con América. Esto contribuyó a que algunos cuestionaran la autoridad de la Corona y defendieran una mayor participación en las decisiones políticas.

## 3. La Independencia de Chile como parte de un proceso continental

Los acontecimientos ocurridos en Chile no fueron aislados. A comienzos del siglo XIX, distintas colonias españolas de América comenzaron a organizar gobiernos propios y posteriormente a luchar por su independencia.

La crisis de la monarquía española en 1808 fue un factor decisivo para el desarrollo de estos movimientos. En territorios como Nueva Granada, Venezuela, el Río de la Plata, México y Chile surgieron iniciativas políticas que cuestionaron la autoridad de las instituciones españolas.

Sin embargo, cada territorio vivió un proceso diferente. Algunos comenzaron antes, otros enfrentaron conflictos más prolongados y las condiciones sociales y políticas no fueron iguales en todo el continente.

En Chile, la crisis de la monarquía española favoreció la organización de un gobierno local en 1810. Este hecho marcó el inicio de un proceso que, después de varios años de conflictos y cambios políticos, conduciría a la independencia del país.

## 4. ¿Qué debemos recordar?

La independencia de Chile y de otras colonias americanas fue consecuencia de múltiples antecedentes:

- **Ideas ilustradas:** difundieron principios como la libertad, la igualdad ante la ley y la soberanía.
- **Independencia de Estados Unidos:** mostró que una colonia americana podía separarse de una potencia europea.
- **Revolución francesa:** cuestionó la monarquía absoluta y difundió nuevas ideas políticas.
- **Independencia de Haití:** mostró la fuerza de los movimientos contra el sistema colonial y la esclavitud.
- **Invasión napoleónica a España:** provocó una crisis de la monarquía que abrió un debate sobre quién debía gobernar.
- **Descontento criollo:** surgió por las limitaciones políticas y los conflictos económicos existentes en las colonias.

Comprender estos antecedentes permite reconocer que la Independencia de Chile formó parte de un proceso mucho más amplio: la transformación política de América durante las primeras décadas del siglo XIX.
""".trimIndent().replace("'", "''")

    db.execSQL("INSERT OR IGNORE INTO contenido (contenido_id, descripcion, unidad_id, estado_id) VALUES (6, '$c6Markdown', 4, 4)")

    // 3. Vincular con Objetivo de Aprendizaje (HI06 OA 01 = ID 20)
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (10, 6, 20, 4)")

    // 4. Actividades
    // Actividades formativas (tipo_actividad_id = 1)
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (8, 'Lee atentamente cada pregunta sobre los antecedentes externos de la Independencia y selecciona la alternativa correcta. Solo una alternativa es válida.', 6, 1, 4)")
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (9, 'Lee atentamente cada pregunta sobre los antecedentes internos y el proceso continental y selecciona la alternativa correcta. Solo una alternativa es válida.', 6, 1, 4)")

    // Prueba Final de la Unidad 4 (tipo_actividad_id = 3)
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (10, 'Evaluación sumativa final de la Unidad de Historia de Chile. Lee con atención cada enunciado y selecciona la opción correcta.', 6, 3, 4)")

    // 5. Configurar Visibilidad por Curso (Cursos 1 y 2 = 6°A y 6°B)
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (15, 8, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (16, 8, 2, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (17, 9, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (18, 9, 2, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (19, 10, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (20, 10, 2, 1, 1, 3)")

    // 6. Preguntas (IDs 29..48) y PreguntaActividad (IDs 29..48)
    // --- Actividad 8 (Preguntas 29..33) ---
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (29, '¿Qué idea promovida por la Ilustración cuestionaba el poder absoluto de los monarcas?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (29, 8, 29)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (30, '¿Por qué fue importante la independencia de Estados Unidos en 1776 para las colonias americanas?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (30, 8, 30)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (31, '¿Qué consecuencia política importante tuvo la Revolución francesa de 1789?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (31, 8, 31)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (32, '¿Por qué la independencia de Haití, declarada en 1804, fue un acontecimiento significativo?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (32, 8, 32)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (33, '¿Qué ocurrió en España en 1808 y cómo influyó en las colonias americanas?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (33, 8, 33)")

    // --- Actividad 9 (Preguntas 34..38) ---
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (34, '¿Quiénes eran los criollos durante el período colonial?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (34, 9, 34)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (35, '¿Por qué algunos criollos estaban descontentos con el sistema colonial español?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (35, 9, 35)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (36, '¿Cuál fue uno de los principales objetivos de las reformas borbónicas del siglo XVIII?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (36, 9, 36)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (37, '¿Qué relación existió entre la crisis de la monarquía española y los movimientos de independencia en América?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (37, 9, 37)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (38, '¿Por qué se afirma que la Independencia de Chile formó parte de un proceso continental?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (38, 9, 38)")

    // --- Actividad 10 - Prueba Final (Preguntas 39..48) ---
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (39, '¿Cuál de las siguientes alternativas reúne antecedentes que influyeron en los procesos de independencia de las colonias americanas?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (39, 10, 39)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (40, '¿Qué importancia tuvo la Primera Junta Nacional de Gobierno, formada el 18 de septiembre de 1810?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (40, 10, 40)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (41, '¿Cuál fue una característica importante de la Constitución chilena de 1833?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (41, 10, 41)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (42, '¿Qué aporte realizó Andrés Bello al desarrollo cultural y educativo de Chile durante el siglo XIX?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (42, 10, 42)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (43, '¿Qué consecuencia territorial tuvo la Guerra del Pacífico para Chile?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (43, 10, 43)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (44, '¿Qué consecuencia social estuvo relacionada con el auge de la explotación del salitre?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (44, 10, 44)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (45, '¿Cuál de los siguientes acontecimientos representa un avance en la democratización de la sociedad chilena durante el siglo XX?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (45, 10, 45)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (46, '¿Qué ocurrió el 11 de septiembre de 1973 en Chile?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (46, 10, 46)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (47, '¿Qué importancia tuvo el plebiscito de 1988 en el proceso de recuperación de la democracia?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (47, 10, 47)")

    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (48, '¿Cuál de los siguientes ejemplos representa una continuidad y un cambio en la sociedad chilena a lo largo de su historia?', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (48, 10, 48)")

    // 7. Alternativas (IDs 113..192)
    // Actividad 1 - Pregunta 29 (113..116) - Correcta: 114
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (113, 'Que el poder político debía concentrarse en una familia real.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (114, 'Que la autoridad debía tener su origen en la nación.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (115, 'Que los privilegios sociales debían mantenerse por nacimiento.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (116, 'Que las colonias debían depender políticamente de Europa.', 0, NULL)")

    // Actividad 1 - Pregunta 30 (117..120) - Correcta: 118
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (117, 'Porque permitió que España recuperara sus colonias.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (118, 'Porque demostró que una colonia podía independizarse de una potencia europea.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (119, 'Porque provocó la desaparición de todas las monarquías europeas.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (120, 'Porque inició la independencia de Chile.', 0, NULL)")

    // Actividad 1 - Pregunta 31 (121..124) - Correcta: 123
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (121, 'Reforzó el poder absoluto de los reyes europeos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (122, 'Estableció que las colonias debían gobernarse desde España.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (123, 'Cuestionó la monarquía absoluta y difundió ideas de libertad e igualdad ante la ley.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (124, 'Impidió la difusión de nuevas ideas políticas en América.', 0, NULL)")

    // Actividad 1 - Pregunta 32 (125..128) - Correcta: 128
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (125, 'Porque fue el primer territorio americano en independizarse de España.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (126, 'Porque estableció nuevamente el dominio francés sobre América.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (127, 'Porque permitió que España recuperara el control del Caribe.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (128, 'Porque demostró que era posible terminar con el dominio colonial mediante una revolución.', 1, NULL)")

    // Actividad 1 - Pregunta 33 (129..132) - Correcta: 129
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (129, 'Napoleón invadió España y el rey Fernando VII quedó apartado del trono, provocando una crisis de autoridad.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (130, 'España reconoció la independencia de todas sus colonias americanas.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (131, 'El rey Fernando VII decidió entregar el gobierno de América a los criollos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (132, 'España estableció nuevas repúblicas en sus territorios americanos.', 0, NULL)")

    // Actividad 2 - Pregunta 34 (133..136) - Correcta: 135
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (133, 'Españoles nacidos en la península ibérica que ocupaban cargos administrativos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (134, 'Personas extranjeras que llegaron a América después de la Independencia.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (135, 'Descendientes de españoles nacidos en América.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (136, 'Representantes de los pueblos originarios que gobernaban las colonias.', 0, NULL)")

    // Actividad 2 - Pregunta 35 (137..140) - Correcta: 138
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (137, 'Porque tenían prohibido participar en cualquier actividad económica.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (138, 'Porque, pese a tener riquezas e influencia, enfrentaban limitaciones para acceder a los principales cargos políticos.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (139, 'Porque España había eliminado todas las autoridades locales.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (140, 'Porque no podían vivir ni establecer propiedades en América.', 0, NULL)")

    // Actividad 2 - Pregunta 36 (141..144) - Correcta: 144
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (141, 'Entregar la independencia a las colonias americanas.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (142, 'Eliminar completamente los impuestos coloniales.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (143, 'Permitir que los criollos controlaran el gobierno de América.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (144, 'Fortalecer el control de la Corona española sobre sus territorios y aumentar sus ingresos.', 1, NULL)")

    // Actividad 2 - Pregunta 37 (145..148) - Correcta: 145
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (145, 'La ausencia de un rey legítimo generó debates sobre quién debía gobernar los territorios americanos.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (146, 'La crisis permitió que España aumentara su control político sobre todas las colonias.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (147, 'Los movimientos americanos comenzaron únicamente por conflictos comerciales.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (148, 'La crisis provocó que todas las colonias fueran gobernadas por Francia.', 0, NULL)")

    // Actividad 2 - Pregunta 38 (149..152) - Correcta: 151
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (149, 'Porque Chile fue el único territorio americano que buscó independizarse.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (150, 'Porque todos los territorios americanos lograron su independencia en el mismo año.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (151, 'Porque distintas colonias americanas enfrentaron procesos de independencia relacionados con la crisis del dominio europeo.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (152, 'Porque los movimientos independentistas fueron organizados exclusivamente desde Chile.', 0, NULL)")

    // Prueba Final - Pregunta 39 (153..156) - Correcta: 153
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (153, 'La Revolución francesa, la Ilustración y la crisis de la monarquía española.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (154, 'La Revolución Industrial, la Guerra del Pacífico y la Constitución de 1833.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (155, 'La fundación de la Universidad de Chile y la explotación del salitre.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (156, 'La colonización europea del sur de Chile y la cuestión social.', 0, NULL)")

    // Prueba Final - Pregunta 40 (157..160) - Correcta: 158
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (157, 'Declaró inmediatamente la independencia definitiva de Chile.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (158, 'Estableció un gobierno local en Chile durante la ausencia del rey Fernando VII.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (159, 'Inició la Guerra del Pacífico contra Perú y Bolivia.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (160, 'Estableció una monarquía independiente dirigida por los criollos.', 0, NULL)")

    // Prueba Final - Pregunta 41 (161..164) - Correcta: 163
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (161, 'Estableció un sistema parlamentario con escasas atribuciones presidenciales.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (162, 'Eliminó la autoridad del presidente de la República.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (163, 'Fortaleció el poder del presidente y estableció un gobierno republicano.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (164, 'Entregó el gobierno del país a autoridades elegidas por la Corona española.', 0, NULL)")

    // Prueba Final - Pregunta 42 (165..168) - Correcta: 168
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (165, 'Dirigió la ocupación militar de la Araucanía.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (166, 'Fue uno de los principales responsables de la explotación del salitre.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (167, 'Participó en la organización de la Primera Junta Nacional de Gobierno.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (168, 'Fue el primer rector de la Universidad de Chile y contribuyó al desarrollo de la educación y la cultura.', 1, NULL)")

    // Prueba Final - Pregunta 43 (169..172) - Correcta: 169
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (169, 'La incorporación de territorios del norte, anteriormente pertenecientes a Perú y Bolivia.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (170, 'La pérdida definitiva de todos los territorios del norte del país.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (171, 'La incorporación de la Patagonia argentina al territorio chileno.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (172, 'La separación de Isla de Pascua del territorio nacional.', 0, NULL)")

    // Prueba Final - Pregunta 44 (173..176) - Correcta: 175
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (173, 'La desaparición de las diferencias entre los grupos sociales.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (174, 'La eliminación de los problemas laborales en las ciudades.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (175, 'El crecimiento de la población trabajadora y el desarrollo de problemas asociados a las condiciones laborales y de vida.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (176, 'La disminución de las actividades económicas en las zonas mineras.', 0, NULL)")

    // Prueba Final - Pregunta 45 (177..180) - Correcta: 178
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (177, 'La eliminación de las elecciones presidenciales.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (178, 'El reconocimiento del derecho de las mujeres a votar en las elecciones presidenciales y parlamentarias en 1949.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (179, 'La prohibición de la participación política de los ciudadanos.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (180, 'La reducción del acceso de la población a la educación.', 0, NULL)")

    // Prueba Final - Pregunta 46 (181..184) - Correcta: 184
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (181, 'Se realizó el primer cabildo abierto de Chile.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (182, 'Se promulgó la Constitución de 1833.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (183, 'Se realizó el plebiscito que permitió iniciar el retorno a la democracia.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (184, 'Las Fuerzas Armadas y Carabineros derrocaron al gobierno del presidente Salvador Allende, iniciándose un régimen militar.', 1, NULL)")

    // Prueba Final - Pregunta 47 (185..188) - Correcta: 187
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (185, 'Permitió que continuara el régimen militar por un nuevo período presidencial.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (186, 'Estableció una nueva monarquía en Chile.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (187, 'La opción NO obtuvo la mayoría, lo que abrió el camino a elecciones presidenciales y parlamentarias y al retorno a un gobierno democrático.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (188, 'Eliminó la participación ciudadana en las decisiones políticas.', 0, NULL)")

    // Prueba Final - Pregunta 48 (189..192) - Correcta: 189
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (189, 'La continuidad del uso del idioma español y el aumento del acceso de la población a la educación.', 1, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (190, 'La desaparición del idioma español y la eliminación de las escuelas.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (191, 'La permanencia de todas las formas de gobierno sin modificaciones.', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (192, 'La ausencia de cambios en la participación política y en la vida cotidiana.', 0, NULL)")

    // 8. AlternativaPregunta (vinculación 113..192 con preguntas 29..48)
    for (i in 113..192) {
        val preguntaId = ((i - 113) / 4) + 29
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

        // Docencia (3)
        AsignaturaCursoEntity::class,
        UsuarioCursoEntity::class,
        ActividadCursoEntity::class,

        // Evaluación (2)
        IntentoEntity::class,
        RespuestaEntity::class,

        // Informe (2)
        InformePedagogicoEntity::class,
        DetalleInformePedagogicoEntity::class
    ],
    version = 8,
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
                .addMigrations(
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3, MIGRATION_3_4, MIGRATION_1_4, MIGRATION_2_4,
                    MIGRATION_4_5, MIGRATION_1_5, MIGRATION_2_5, MIGRATION_3_5,
                    MIGRATION_5_6, MIGRATION_1_6, MIGRATION_2_6, MIGRATION_3_6, MIGRATION_4_6,
                    MIGRATION_6_7, MIGRATION_1_7, MIGRATION_2_7, MIGRATION_3_7, MIGRATION_4_7, MIGRATION_5_7,
                    MIGRATION_7_8, MIGRATION_1_8, MIGRATION_2_8, MIGRATION_3_8, MIGRATION_4_8, MIGRATION_5_8, MIGRATION_6_8
                )
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

