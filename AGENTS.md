# AGENTS.md — WEather

## 1. Propósito

Este archivo guía a los agentes de desarrollo que trabajan en WEather. Recoge dos cosas: las
**reglas** que no cambian de una pull request a otra y un **mapa** de dónde está la información que
sí cambia. No describe el estado del proyecto: eso está en los documentos de la sección 3, y el
código manda sobre todos ellos. Una pull request solo modifica este archivo cuando cambia una regla
o mueve uno de esos documentos.

- Lee estas instrucciones antes de modificar el proyecto.
- Trabaja sobre el estado real del repositorio. No inventes archivos, comandos, funcionalidades ni
  resultados de pruebas.
- Distingue requisitos, propuestas y decisiones acordadas. No presentes una propuesta como una
  decisión tomada.
- Resuelve con criterio las decisiones locales y sencillas. Consulta cuando una decisión que falta
  cambia el dominio, los permisos, el contrato público, el coste o la arquitectura.

## 2. Contexto del producto

WEather es una app móvil para que los peatones de Málaga lleguen a su destino con la menor
exposición al calor posible. El usuario escribe un origen y un destino y recibe tres rutas (la más
corta, la más fresca y una equilibrada), cada una con su distancia, tiempo, exposición estimada e
indicaciones paso a paso en texto.

- El servidor estima la exposición al calor de cada tramo de calle con datos abiertos
  (OpenStreetMap, Open-Meteo, AEMET, Ayuntamiento de Málaga) y con los reportes validados de los
  usuarios. Ese mapa de calor es interno: solo lo usa el cálculo de rutas, no se muestra.
- Los usuarios registrados envían reportes (valoración de 1 a 5 y comentario de texto o voz) sobre
  un punto del mapa. Se guardan anonimizados y solo los validados ajustan el mapa de calor, durante
  un tiempo limitado.
- Los administradores revisan los reportes dudosos, los retiran y bloquean usuarios.

Restricciones que no se negocian:

- **Proveedores externos:** se respetan su atribución y sus límites de peticiones. Sus respuestas
  se cachean y se guarda el último dato válido; nunca se les llama en cada petición del usuario.
- **Datos personales (RGPD y LOPDGDD):** consentimiento, minimización y datos personales cifrados.
  Nadie puede saber quién hizo un reporte ni qué ruta pidió.
- **Alcance:** solo Málaga, sin GPS ni ubicación en tiempo real (el origen y el destino se
  escriben), y sin pintar las rutas ni el mapa de calor sobre el mapa.

## 3. Dónde está la información

| Pregunta | Fuente |
| --- | --- |
| Qué está construido hoy | *Estado actual* en `README.md`; después, el código |
| Alcance (MoSCoW), actores y objetivos (`OBJ-…`, `MIT-…`) | ConOps: `docs/PDF Entregados/DocumentoConOps.pdf` (el MoSCoW está en la sección 5) |
| Priorización, planificación del sprint y proceso Scrum | `docs/PDF Entregados/Práctica 3-6 Gestión.pdf`; lo que afecta al día a día está resumido en `CONTRIBUTING.md` |
| Qué hay que hacer y en qué sprint | Las issues de historia de usuario (`[HU-XX]`) y sus tareas (`[T-XX.n]`), con su milestone |
| Arranque local y comandos | `docs/development.md` (pendiente) |
| Roles, eventos, flujo de trabajo, ramas, commits, PRs y Definition of Done | `CONTRIBUTING.md` |
| Integración continua y sus jobs | `.github/workflows/ci.yml`, `.github/workflows/pr-title.yml` |
| Cambios por versión | `CHANGELOG.md` |

Un documento marcado como *pendiente* todavía no existe: no lo des por hecho.

## 4. Reglas de trabajo

- **Idioma:** todo el proyecto está en español: documentación, issues, pull requests y commits.
- **Git:** GitHub Flow. `main` es estable y no se trabaja directamente en ella; cada cambio va en su
  rama y entra por pull request. Ramas, commits y PRs siguen `CONTRIBUTING.md`.
- **Commits:** los hace siempre una persona del equipo. El agente deja los cambios en el árbol de
  trabajo y explica qué ha cambiado; no ejecuta `git commit` ni `git push` salvo que se le pida
  explícitamente.
- **TDD:** cada comportamiento nuevo empieza por una prueba que falla.
- **Configuración y secretos:** fuera del código. `.env` nunca se versiona; `.env.example`, con
  valores de ejemplo, sí.
- **Alcance según los documentos:** ciñete al MoSCoW del ConOps y a las HU del backlog. No
  propongas funcionalidades, componentes ni herramientas que no recojan. Si algo no está cubierto,
  dilo y déjalo para que el equipo lo decida (el Product Owner en el alcance, el Sprint Planning en
  lo técnico).
- **Diseño simple:** implementa solo lo que piden los criterios de aceptación. No añadas
  componentes ni dependencias sin un problema observable que los justifique.
- **Estructura:** la raíz solo guarda lo compartido; cada parte desplegable va en su carpeta. No
  crees carpetas vacías "para el futuro".
- **Cambios pequeños:** no mezcles una tarea con refactorizaciones ajenas a ella.
- **Documentación al día:** si un cambio altera un comportamiento documentado, actualiza el
  documento en la misma pull request, y el `CHANGELOG.md` cuando el cambio sea visible.
- **Datos personales:** no uses datos reales de personas (nombres, ubicaciones, fotos, audios) en
  pruebas, capturas, issues ni pull requests.
