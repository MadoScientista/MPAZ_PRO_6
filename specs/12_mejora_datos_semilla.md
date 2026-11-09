# Guía para la Adición y Mejora de Datos Semilla

Este documento detalla el procedimiento estándar y las convenciones del proyecto **MPAZ_PRO_6** para agregar o actualizar datos semilla en la base de datos local Room de manera rápida y sin necesidad de inspeccionar todo el código fuente.

---

## 1. Ubicación Clave de los Datos Semilla

Todo el código de inicialización, población de datos semilla y migraciones se centraliza en:
- **`app/src/main/java/com/ccstudio/mpaz_pro_6/data/local/MpazDatabase.kt`**

---

## 2. Convenciones de Modelos y Dominio

Al agregar nuevos contenidos y actividades para una asignatura, se deben respetar las siguientes relaciones y reglas de negocio:

### 2.1. Unidades y Asignaturas Preexistentes
- **Asignaturas (`asignatura`):**
  - `1`: Lenguaje y Comunicación
  - `2`: Matemática
  - `3`: Ciencias Naturales
  - `4`: Historia, Geografía y Ciencias Sociales
- **Unidades (`unidad`):**
  - `1`: Lectura - Comprensión (Asignatura 1)
  - `2`: Números y operaciones (Asignatura 2)
  - `3`: Ciencias de la Vida (Asignatura 3)
  - `4`: Historia (Asignatura 4)

### 2.2. Estados de Autorización (`estado_autorizacion`)
- `1`: `BORRADOR`
- `2`: `EN_REVISION`
- `3`: `AUTORIZADO`
- `4`: `PUBLICADO` *(Los contenidos y actividades visibles para los alumnos deben estar en estado `4`)*.

### 2.3. Tipos de Actividad (`tipo_actividad`)
- `1`: `ACTIVIDAD`
- `2`: `DESAFIO`
- `3`: `PRUEBA_FINAL`

> [!IMPORTANT]
> **Regla de Visualización de Actividades:**
> - Las actividades normales (`tipo_actividad_id` 1 y 2) pertenecen a un **Contenido** y se muestran en la pantalla de detalle del contenido (`ContenidoDetalleScreen`).
> - Las pruebas finales (`tipo_actividad_id = 3`) pertenecen a la **Unidad** completa y **no** deben mostrarse en el detalle del contenido, sino únicamente en el detalle de la unidad (`UnidadDetalleScreen`).

---

## 3. Rangos de IDs Usados (Evitar Colisiones)

Al insertar nuevos registros mediante SQL en `MpazDatabase.kt`, utiliza la cláusula `INSERT OR IGNORE INTO` y asigna IDs superiores a los rangos ya ocupados:

| Tabla | Rangos Actuales Ocupados | Siguiente ID Disponible |
|---|---|---|
| `unidad` | 1 - 4 | 5 |
| `contenido` | 1 - 5 | 6 |
| `objetivo_aprendizaje_contenido` | 1 - 9 | 10 |
| `actividad` | 1 - 7 | 8 |
| `actividad_curso` | 1 - 14 | 15 |
| `pregunta` | 1 - 28 | 29 |
| `pregunta_actividad` | 1 - 28 | 29 |
| `alternativa` | 1 - 112 | 113 |
| `alternativa_pregunta` | 1 - 112 | 113 |

---

## 4. Paso a Paso para Agregar Nuevos Datos Semilla

### Paso 1: Crear la Función Auxiliar de Población
En `MpazDatabase.kt`, crea una función descriptiva para los datos de la asignatura/unidad, por ejemplo `seedNombreMateriaData(db: SupportSQLiteDatabase)`:

```kotlin
fun seedNombreMateriaData(db: SupportSQLiteDatabase) {
    // 1. Actualizar estado de la unidad a PUBLICADO (4)
    db.execSQL("UPDATE unidad SET estado_id = 4 WHERE unidad_id = X")

    // 2. Insertar Contenido (Markdown)
    val markdown = """...""".trimIndent().replace("'", "''")
    db.execSQL("INSERT OR IGNORE INTO contenido (contenido_id, descripcion, unidad_id, estado_id) VALUES (N, '$markdown', X, 4)")

    // 3. Vincular con Objetivo de Aprendizaje
    db.execSQL("INSERT OR IGNORE INTO objetivo_aprendizaje_contenido (id, contenido_id, objetivo_aprendizaje_id, unidad_id) VALUES (N, contenido_id, oa_id, unidad_id)")

    // 4. Insertar Actividades
    // Actividades formativas (tipo_actividad_id = 1 o 2)
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (N, 'Instrucciones...', contenido_id, 1, 4)")
    
    // Prueba Final de la Unidad (tipo_actividad_id = 3)
    db.execSQL("INSERT OR IGNORE INTO actividad (actividad_id, instrucciones, contenido_id, tipo_actividad_id, estado_id) VALUES (N, 'Instrucciones prueba final...', contenido_id, 3, 4)")

    // 5. Configurar Visibilidad por Curso (Cursos 1 y 2 = 6°A y 6°B)
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (N, actividad_id, 1, 1, 1, 3)")
    db.execSQL("INSERT OR IGNORE INTO actividad_curso (actividad_curso_id, actividad_id, curso_id, habilitada, visible, max_intentos) VALUES (N+1, actividad_id, 2, 1, 1, 3)")

    // 6. Insertar Preguntas y vincular con actividad
    db.execSQL("INSERT OR IGNORE INTO pregunta (pregunta_id, enunciado, recurso_id) VALUES (P_ID, 'Enunciado...', NULL)")
    db.execSQL("INSERT OR IGNORE INTO pregunta_actividad (id, actividad_id, pregunta_id) VALUES (PA_ID, actividad_id, P_ID)")

    // 7. Insertar Alternativas (correcta = 1 para la opción válida)
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (ALT_ID, 'Opción A', 0, NULL)")
    db.execSQL("INSERT OR IGNORE INTO alternativa (alternativa_id, enunciado, correcta, recurso_id) VALUES (ALT_ID+1, 'Opción B', 1, NULL)") // Correcta
    
    // 8. Vincular Alternativas con Preguntas
    db.execSQL("INSERT OR IGNORE INTO alternativa_pregunta (id, pregunta_id, alternativa_id) VALUES (ALT_ID, P_ID, ALT_ID)")
}
```

> [!CAUTION]
> **Escape de Comillas Simples:**
> Al escribir consultas SQL en cadenas de Kotlin, asegúrate de reemplazar comillas simples por comillas dobles repetidas (`' -> ''`) o usar `.replace("'", "''")` para evitar fallos de sintaxis SQLite.

---

### Paso 2: Incrementar la Versión de la Base de Datos
En `MpazDatabase.kt`, incrementa el campo `version` en la anotación `@Database`:
```kotlin
@Database(
    entities = [...],
    version = N + 1, // Incrementar versión
    exportSchema = true
)
```

---

### Paso 3: Definir los Objetos de Migración
En `MpazDatabase.kt`, crea la migración entre la versión previa y la nueva:
```kotlin
val MIGRATION_N_N1 = object : Migration(N, N + 1) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedNombreMateriaData(db)
    }
}

// Agregar también las migraciones directas desde versiones anteriores
val MIGRATION_1_N1 = object : Migration(1, N + 1) { override fun migrate(db: SupportSQLiteDatabase) { seedInitialData(db) } }
val MIGRATION_2_N1 = object : Migration(2, N + 1) { override fun migrate(db: SupportSQLiteDatabase) { seedInitialData(db) } }
// ...
```

---

### Paso 4: Invocar la Función en `seedInitialData`
Agrega la llamada a `seedNombreMateriaData(db)` al final de la función `seedInitialData(db)`:
```kotlin
fun seedInitialData(db: SupportSQLiteDatabase) {
    // ... datos iniciales anteriores ...
    seedMathData(db)
    seedNombreMateriaData(db) // Nueva llamada
}
```

---

### Paso 5: Registrar la Migración en `Room.databaseBuilder`
En la función `getDatabase` de `MpazDatabase.kt`, añade la nueva migración al método `.addMigrations(...)`:
```kotlin
.addMigrations(
    // ... migraciones anteriores ...
    MIGRATION_N_N1, MIGRATION_1_N1, MIGRATION_2_N1, ...
)
```

---

### Paso 6: Compilar y Generar el Esquema
Ejecuta la tarea de Gradle para compilar la aplicación. Room/KSP generará automáticamente el archivo de esquema JSON (por ejemplo, `(N+1).json`):
```bash
gradle_build("app:assembleDebug")
```

---

### Paso 7: Actualizar la Documentación
Actualiza el archivo `AGENTS.md` reflejando la nueva versión de la base de datos y la lista de migraciones registradas.
