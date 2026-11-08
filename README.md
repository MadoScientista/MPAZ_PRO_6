# MPAZ_PRO_6 🎓

Aplicación móvil Android desarrollada en **Jetpack Compose** para la gestión pedagógica integral, seguimiento curricular, evaluación de aprendizajes y generación de informes académicos.

---

## 📌 Tabla de Contenidos
1. [Descripción General](#-descripción-general)
2. [Stack Tecnológico](#-stack-tecnológico)
3. [Arquitectura y Estructura del Proyecto](#-arquitectura-y-estructura-del-proyecto)
4. [Módulos del Sistema (26 Entidades)](#-módulos-del-sistema-26-entidades)
5. [Seguridad y Control de Acceso (RBAC)](#-seguridad-y-control-de-acceso-rbac)
6. [Configuración y Variables del Entorno](#-configuración-y-variables-del-entorno)
7. [Credenciales de Prueba](#-credenciales-de-prueba)
8. [Instrucciones de Compilación y Ejecución](#-instrucciones-de-compilación-y-ejecución)
9. [Especificaciones y Documentación Adicional](#-especificaciones-y-documentación-adicional)

---

## 📖 Descripción General

**MPAZ_PRO_6** es una plataforma tecnológica diseñada para optimizar los procesos de enseñanza y aprendizaje en comunidades educativas. Permite a **UTPs (Unidad Técnico Pedagógica)**, **Docentes** y **Estudiantes**:
- Administrar la estructura institucional (Niveles, Secciones, Cursos y Asignaturas).
- Diseñar y organizar contenidos pedagógicos integrados con **Objetivos de Aprendizaje (OA)**.
- Crear y responder evaluaciones (Actividades, Preguntas y Alternativas).
- Monitorear el progreso académico mediante **Informes Pedagógicos** cualitativos y cuantitativos.
- Operar en entornos en línea mediante API REST o en modo 100% offline con persistencia en **Room DB**.

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Kotlin 2.x
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) con Material 3 y Navigation Compose
- **Base de Datos Local:** [Room DB 2.8.5](https://developer.android.com/training/data-storage/room) con KSP (Kotlin Symbol Processing) y soporte de migraciones automáticas/manuales (Base de Datos Versión 5)
- **Red & API REST:** [Retrofit 3.0](https://square.github.io/retrofit/) + [OkHttp 5.5](https://square.github.io/okhttp/) + `kotlinx.serialization`
- **Persistencia de Sesión:** [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) (`SessionManager`)
- **Desugaring de Fechas:** `java.time` habilitado mediante Core Library Desugaring (`minSdk 24`)
- **SDK Target:** `compileSdk 37`, `targetSdk 37`, `minSdk 24`

---

## 🏗️ Arquitectura y Estructura del Proyecto

El proyecto sigue una arquitectura **MVVM / Clean Architecture** pragmática orientada a características (*Feature-based UI & Data Layers*).

```text
com.ccstudio.mpaz_pro_6/
├── data/
│   ├── dto/                # Data Transfer Objects y Resúmenes por Feature
│   │   ├── identidad/      # DTOs de Usuario, Curso, Rol, Nivel, Sección
│   │   ├── curriculo/      # DTOs de Asignatura, Unidad, Contenido, Actividad, Pregunta
│   │   ├── docencia/       # DTOs de vinculación AsignaturaCurso, UsuarioCurso
│   │   ├── evaluacion/     # DTOs de Intento y Respuesta
│   │   └── informe/        # DTOs de InformePedagógico y Detalle
│   ├── local/
│   │   ├── preferences/    # SessionManager con DataStore Preferences
│   │   ├── MpazDatabase.kt  # Base de Datos Room (v5 con exportSchema)
│   │   ├── dao/            # IdentidadDao, CurriculoDao, DocenciaDao, EvaluacionDao, InformeDao
│   │   └── entity/         # Las 26 entidades Room del modelo relacional
│   └── mapper/
│       └── ResumenMapper.kt # Mapeo inmutable entre entidades Room y DTOs Resumen (XxxResumen)
├── ui/
│   ├── home/               # HomeScreen y Scaffold principal
│   ├── login/              # LoginScreen y LoginViewModel
│   ├── navigation/         # NavDestination, NavGraph y control de rutas protegidas
│   └── screens/            # Pantallas específicas por rol y funcionalidad
└── MainActivity.kt         # Punto de entrada de la aplicación
```

---

## 🗂️ Módulos del Sistema (26 Entidades)

El modelo de datos y negocio comprende 26 entidades distribuidas en **5 características clave**:

| Feature | Descripción | Entidades | DAOs y DTOs |
|---|---|---|---|
| 👤 **Identidad** | Gestión de usuarios, roles, niveles, secciones y cursos | `Rol`, `Nivel`, `Seccion`, `Curso`, `Usuario` | `IdentidadDao`, DTOs y `XxxResumen` |
| 📚 **Currículo** | Estructura académica, OA, contenidos, actividades, preguntas y recursos | `Asignatura`, `EstadoAutorizacion`, `TipoActividad`, `TipoRecurso`, `Unidad`, `ObjetivoAprendizaje`, `ObjetivoAprendizajeUnidad`, `Contenido`, `Recurso`, `Actividad`, `Pregunta`, `Alternativa`, `AlternativaPregunta`, `PreguntaActividad`, `ObjetivoAprendizajeContenido` | `CurriculoDao`, DTOs de gestión curricular |
| 👩‍🏫 **Docencia** | Vinculación académica entre profesores, cursos y asignaturas | `AsignaturaCurso`, `UsuarioCurso` | `DocenciaDao`, DTOs puente |
| 📝 **Evaluación** | Registro de ejecución de actividades y respuestas | `Intento`, `Respuesta` | `EvaluacionDao`, DTOs de evaluación |
| 📊 **Informe** | Análisis del rendimiento académico y desglose por OA | `InformePedagogico`, `DetalleInformePedagogico` | `InformeDao`, DTOs de informe |

---

## 🔐 Seguridad y Control de Acceso (RBAC)

1. **Roles del Sistema:**
   - **`UTP`**: Acceso completo a administración institucional, asignaciones docentes, currículo e informes institucionales.
   - **`DOCENTE`**: Creación de contenidos, diseño de actividades, vinculación curricular e inspección de resultados de sus asignaturas.
   - **`ESTUDIANTE`**: Acceso a contenidos asignados, resolución de evaluaciones (intentos/respuestas) y revisión de informes personales.
2. **Protección de Rutas UI:**
   - La navegación está restringida por rol a través de `NavDestination.isRouteAllowedForRole` y la vista envolvente `ProtectedScreen`.
3. **Privacidad de Datos Sensibles:**
   - `UsuarioResponse` y `UsuarioResumen` **nunca exponen la contraseña** ni hashes.
   - `UsuarioRequest` sobrescribe `toString()` para imprimir `password='***'` previniendo filtraciones en registros/logs.

---

## ⚙️ Configuración y Variables del Entorno

En `app/build.gradle.kts` se exponen propiedades de Gradle para configurar la conectividad del cliente:

| Propiedad Gradle | Variable de Entorno | Descripción | Valor por Defecto |
|---|---|---|---|
| `MPAZ_BASE_URL` | `MPAZ_BASE_URL` | Endpoint base para Retrofit API | `http://10.0.2.2:8080/` |
| `MPAZ_LOCAL_ONLY` | `MPAZ_LOCAL_ONLY` | Fuerza el uso exclusivo de Room DB (Offline) | `false` |

Se pueden pasar como argumentos en Gradle:
```bash
./gradlew assembleDebug -PMPAZ_BASE_URL="https://api.tu-servidor.com/" -PMPAZ_LOCAL_ONLY="false"
```

---

## 🔑 Credenciales de Prueba

La base de datos SQLite pre-cargada mediante migraciones de Room provee los siguientes usuarios de prueba:

- **Contraseña Universal:** `mpaz123`
- **Administrador / UTP:**
  - Usuario: `utp`
- **Docentes:**
  - Usuarios: `docente`, `docente2`, `docente3`, `docente4`
- **Estudiantes:**
  - Usuarios: `alumno`, `estudiante2`, `estudiante3`, `estudiante4`

---

## 🚀 Instrucciones de Compilación y Ejecución

### Requisitos Previos
- Android Studio Ladybug (2024.2.1+) o posterior.
- JDK 11 o JDK 17 configurado en el entorno de desarrollo.
- Dispositivo físico o Emulador Android (API Level 24 en adelante).

### Comandos Principales
```bash
# Compilar el proyecto en modo Debug
./gradlew assembleDebug

# Verificar la compilación del código Kotlin
./gradlew app:compileDebugKotlin

# Ejecutar pruebas unitarias
./gradlew test
```

---

## 📄 Especificaciones y Documentación Adicional

Para más detalles sobre el diseño técnico y funcional del proyecto, consulte el directorio [`specs/`](./specs):
- [`specs/1.relaciones_modelo_mpaz.md`](./specs/1.relaciones_modelo_mpaz.md): Relaciones y restricciones de las 26 entidades.
- [`specs/2_dtos.md`](./specs/2_dtos.md): Especificación de DTOs y estructuras JSON.
- [`specs/3_datos_semilla.md`](./specs/3_datos_semilla.md): Datos iniciales cargados en la base de datos.
- [`specs/4_constantes_y_credenciales.md`](./specs/4_constantes_y_credenciales.md): Credenciales y constantes del sistema.
- [`specs/5_seguridad.md`](./specs/5_seguridad.md): Matriz RBAC y seguridad de datos.
- [`specs/6_navegacion.md`](./specs/6_navegacion.md): Rutas, menús y comportamiento de barras de navegación.
- [`specs/7_administracion_docentes.md`](./specs/7_administracion_docentes.md): Flujos de trabajo para docentes y UTP.
- [`AGENTS.md`](./AGENTS.md): Guía del proyecto para asistentes y agentes de inteligencia artificial.

---

