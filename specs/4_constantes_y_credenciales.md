# Constantes y Credenciales del Sistema MPAZ_PRO_6

Este documento detalla las credenciales de prueba de los usuarios preconfigurados en la base de datos local y el catálogo de constantes maestras utilizadas en la aplicación.

---

## 1. Credenciales de Usuarios de Prueba

Todos los usuarios predeterminados de la aplicación comparten la misma contraseña para entornos de prueba y desarrollo:

- **Contraseña universal de prueba:** `mpaz123`

### 1.1. Usuario UTP (Administrador Académico)
- **Rol ID:** 1 (`UTP`)
- **Usuario:** `utp`
- **Nombre:** Carlos UTP
- **Descripción:** Acceso total a la supervisión pedagógica y estados de autorización.

| ID | Usuario | Nombre Completo | Rol | Activo |
|---|---|---|---|---|
| 1 | `utp` | Carlos UTP | UTP | Sí |

### 1.2. Usuarios Docentes
- **Rol ID:** 2 (`DOCENTE`)
- **Asociaciones:** Todos los docentes están asociados a los cursos **6A** (ID 1) y **6B** (ID 2).

| ID | Usuario | Nombre Completo | Rol | Cursos Asignados |
|---|---|---|---|---|
| 2 | `docente` | Ana Silva | DOCENTE | 6A, 6B (2026) |
| 3 | `docente2` | Bruno Morales | DOCENTE | 6A, 6B (2026) |
| 4 | `docente3` | Carla Rojas | DOCENTE | 6A, 6B (2026) |
| 5 | `docente4` | Daniel Vargas | DOCENTE | 6A, 6B (2026) |

### 1.3. Usuarios Estudiantes
- **Rol ID:** 3 (`ESTUDIANTE`)
- **Asociaciones:** Distribuidos entre los cursos de 6° Básico.

| ID | Usuario | Nombre Completo | Rol | Curso Asignado |
|---|---|---|---|---|
| 6 | `alumno` | Elena Castro | ESTUDIANTE | 6A (2026) |
| 7 | `estudiante2` | Felipe Gómez | ESTUDIANTE | 6A (2026) |
| 8 | `estudiante3` | Gabriela Herrera | ESTUDIANTE | 6B (2026) |
| 9 | `estudiante4` | Hugo Ibarra | ESTUDIANTE | 6B (2026) |

---

## 2. Catálogos y Constantes Maestras

### 2.1. Roles (`rol`)
| ID | Nombre | Descripción |
|---|---|---|
| 1 | `UTP` | Unidad Técnico Pedagógica |
| 2 | `DOCENTE` | Profesor / Docente de asignatura |
| 3 | `ESTUDIANTE` | Alumno registrado en la plataforma |

### 2.2. Estados de Autorización (`estado_autorizacion`)
| ID | Nombre | Descripción |
|---|---|---|
| 1 | `BORRADOR` | En elaboración inicial por el docente |
| 2 | `EN_REVISION` | Enviado a UTP para revisión |
| 3 | `AUTORIZADO` | Aprobado por UTP |
| 4 | `PUBLICADO` | Disponible para los estudiantes |

### 2.3. Tipos de Actividad (`tipo_actividad`)
| ID | Nombre |
|---|---|
| 1 | `ACTIVIDAD` |
| 2 | `DESAFIO` |
| 3 | `PRUEBA_FINAL` |

### 2.4. Tipos de Recurso (`tipo_recurso`)
| ID | Nombre |
|---|---|
| 1 | `IMAGEN` |
| 2 | `AUDIO` |
| 3 | `DOCUMENTO` |
| 4 | `VIDEO` |

### 2.5. Niveles y Secciones (`nivel`, `seccion`)
- **Nivel:** ID 1 = `Sexto Básico`
- **Secciones:** ID 1 = `A`, ID 2 = `B`

### 2.6. Cursos (`curso`)
| ID | Año | Nivel | Sección | Identificador Visual |
|---|---|---|---|---|
| 1 | 2026 | Sexto Básico (1) | A (1) | 6A |
| 2 | 2026 | Sexto Básico (1) | B (2) | 6B |

### 2.7. Asignaturas y Unidades Semilla
| Asignatura ID | Nombre Asignatura | Unidad ID | Nombre Unidad | Estado Unidad |
|---|---|---|---|---|
| 1 | Lenguaje y Comunicación | 1 | Lectura - Comprensión | `EN_REVISION` (2) |
| 2 | Matemática | 2 | Números y operaciones | `AUTORIZADO` (3) |
| 3 | Ciencias Naturales | 3 | Ciencias de la Vida | `BORRADOR` (1) |
| 4 | Historia, Geografía y Ciencias Sociales | 4 | Historia | `PUBLICADO` (4) |
