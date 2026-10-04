# MallaFlex
Aplicación web que calcula la **ruta óptima de inscripción semestre por semestre** para estudiantes de la Facultad de Ingeniería de la Universidad Nacional de Colombia. La ruta se adapta a la historia académica de cada estudiante (materias aprobadas, perdidas o en curso) y evita cuellos de botella por prerrequisitos en los últimos semestres.

> Proyecto del curso **Ingeniería de Software I** – Universidad Nacional de Colombia, sede Bogotá (2026-II).
> Instructora: Magda Lucia Mejia Torres

## Problema
Hoy los estudiantes planean su malla a mano (hojas impresas o archivos con colores), solo miran el semestre inmediato y, al perder una materia, pueden terminar con cuellos de botella que alargan su permanencia y aumentan el riesgo de deserción.

## Qué hace el sistema
- Registro e inicio de sesión con roles (estudiante / administrador)
- Gestión de mallas curriculares (materias, créditos, prerrequisitos y correquisitos)
- Visualización de la malla con el estado de cada materia
- Registro de historia académica (notas y número de matrícula)
- Cálculo de P.A.P.A., créditos aprobados y semestre estimado de graduación
- Generación automática de la ruta de inscripción hasta la graduación
- Recálculo de la ruta al perder o aprobar una materia
- Reportes y estadísticas para el administrador

## Fuera de alcance (esta versión)
Integración con el SIA, inscripción real de cursos, generación de horarios, comparación de rutas alternativas, ajuste manual de la ruta, otras facultades/universidades y app móvil nativa.

## Arquitectura
- **Estilo:** Layered (4 capas), monolito Spring Boot
- **Stack:** Java 23 · Spring Boot · Thymeleaf · SQLite (JDBC)
- **Despliegue:** navegador web → HTTP → `mallaflex.jar` (JVM) → `mallaflex.db`
- **Módulos:** Autenticación y roles, Catálogo, Registro académico, Avance académico, Materias habilitadas, Motor de rutas, Reportes y estadísticas, Acceso a datos

## Documentación
- 📄 [Entregable 1 – Especificación de requisitos y diseño arquitectónico](docs/Entregable_1.pdf)
> Requisitos: Java 23.

## Equipo
| Integrante | Correo | Rol |
|---|---|---|
| Laura Sofia Calderón Torres | lacalderont@unal.edu.co | _por definir_ |
| Maria Paula Pérez Meléndez | maperezme@unal.edu.co | _por definir_ |
| Martin Alejandro Vargas Buitrago | marvargasbu@unal.edu.co | _por definir_ |
| Juan Diego Sanchez Peña | juasanchezpe@unal.edu.co | _por definir_ |
| Erik Santiago Martinez Perez | erimartinezpe@unal.edu.co | _por definir_ |

## Estado

🚧 Entregable 1 (requisitos y arquitectura) – en desarrollo.
