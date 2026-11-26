# Plan de lanzamiento Open Source (WEather)

Plan común del grupo NeoForge (Actividad 3), elaborado a partir de los cuatro entregables:

| Equipo | Entregable |
| --- | --- |
| A · Estrategia y alcance | `open-source-strategy.md` |
| B · Preparación técnica y seguridad | `technical-readiness.md` |
| C · Licencia, documentación y colaboración | `community-readiness.md` |
| D · Gobernanza y sostenibilidad | `governance-and-sustainability.md` |

Revisión sobre `main` (58231d9) y la rama `docs/p9-preparacion-comunidad`.

> **Qué significa «publicar».** El repositorio ya es público en GitHub. En este plan, publicar es
> sacar la **primera release, `v0.1.0`**, y anunciar el proyecto. Es una versión de desarrollo, no
> la 1.0: el cálculo de rutas todavía no está construido (A, sección 4).

## 1. Decisiones de cada equipo

### A · Estrategia y alcance

- **Por qué abrir:** ampliar el desarrollo, mejorar la calidad y no depender del equipo original.
- **Qué se publica:** el código, los tests, la documentación y el ConOps.
- **Qué queda fuera:** los secretos, los datos personales y las grabaciones Scrum.
- **A quién se busca:** prioridad a los desarrolladores externos.

### B · Preparación técnica y seguridad

- No hay secretos en el código ni en el historial.
- Todas las licencias de las dependencias son compatibles con MIT.
- **Tres riesgos:** arranque local que falla y carga a los proveedores, condiciones de uso de
  teselas y Open-Meteo, y una CI que puede dar verde sin probar.

### C · Licencia, documentación y colaboración

- **Licencia:** MIT.
- **README:** estado, limitaciones (el *Won't* del ConOps) y arranque.
- **Issues:** con formulario, y el PO hace el triage contra el MoSCoW.
- **Pull requests:** las mismas normas para todo el mundo.
- **Código de conducta:** Contributor Covenant 2.1 con tres responsables.
- **Comunidad:** tres *good first issue*.

### D · Gobernanza y sostenibilidad

- **Gobernanza:** comité de 2–3 maintainers con consenso perezoso (72 h); el PO decide en alcance.
- **Revisión:** dos responsables por módulo en `CODEOWNERS`.
- **Sostenibilidad:** patrocinio institucional (TFG, prácticas) y donaciones, sin monetizar datos.

## 2. Incoherencias detectadas y cómo se han corregido

| # | Equipos | Incoherencia | Corrección |
| --- | --- | --- | --- |
| 1 | A ↔ ConOps, README | A describía WEather como una app para «consultar información sobre el calor», pero el producto son tres rutas y el mapa de calor no se muestra | A reescribe el objetivo y el perfil de usuarios según el ConOps |
| 2 | A ↔ C | A excluía «los documentos de gestión académica», pero el triage de C se basa en el MoSCoW del ConOps, y el README, `CONTRIBUTING.md` y `AGENTS.md` lo enlazan | Se publican el ConOps y la Práctica 3-6; solo quedan fuera las grabaciones Scrum, que ya no están en el repositorio |
| 3 | A ↔ README | A no decía que la función principal (las rutas) aún no existe | A lo añade en *Madurez*; por eso la release es `v0.1.0` |
| 4 | D ↔ C | D dejaba la licencia «pendiente del Equipo C» y valoraba el *dual licensing* según esa elección | La licencia es MIT desde el primer commit; D lo refleja |
| 5 | D ↔ C | D proponía «el equipo NeoForge» como maintainers sin nombres; C ya nombraba a tres responsables del código de conducta | Maintainers iniciales: @Dalibex, @AlejandroCuetoDiaz y @Lewas88. Responsable de producto: el PO (@AlejandroCuetoDiaz) |
| 6 | D ↔ C | D tenía como pendiente «confirmar quién aplica el código de conducta», ya resuelto por C | Marcada como hecha en D |
| 7 | D ↔ B | D hablaba de `CODEOWNERS` como si existiera y pedía dos aprobaciones en cambios sensibles; el ruleset exige una y no hay `CODEOWNERS` | D lo marca como «por crear» y la norma de dos aprobaciones va a `GOVERNANCE.md` |
| 8 | D ↔ B | D no tenía en cuenta que la API gratuita de Open-Meteo es solo no comercial ni que `tile.openstreetmap.org` no admite apps publicadas | D lo añade al modelo de sostenibilidad y a por qué se descarta el SaaS |
| 9 | B ↔ C | B daba el arranque rápido del README como roto; C ya lo arregla en su rama | B indica qué parte resuelve C y qué queda pendiente |
| 10 | B ↔ C | La *good first issue* 1 de C (pantalla de créditos) es a la vez un requisito para publicar (B, riesgo 2) | Sigue como *good first issue*, pero si nadie de fuera la coge en dos semanas, la hace el equipo antes de la release |
| 11 | C ↔ repositorio | La rama de C enlaza el ConOps y la Práctica 3-6 en `docs/PDF Entregados/`, que la rama `docs/modificacion-pdf` sustituye por Markdown | Se fusiona primero `docs/modificacion-pdf` y C actualiza los enlaces antes de fusionar |
| 12 | C ↔ repositorio | `docs/atribuciones.md` dice que WEather «no se publica» | Se corrige junto al riesgo 1 de C (licencia del icono) |

## 3. Roadmap de apertura

El roadmap tiene tres fases consecutivas. Cada una empieza cuando termina la anterior.

| Fase | Termina cuando… |
| --- | --- |
| **Fase 1 · Antes de publicar** | Están hechas todas sus tareas y el proyecto puede publicarse sin riesgos legales ni de seguridad |
| **Fase 2 · Primera publicación** | La release `v0.1.0` está publicada y anunciada, con las issues iniciales abiertas |
| **Fase 3 · Después de publicar** | No termina: es el mantenimiento continuo del proyecto |

### Fase 1 · Antes de publicar

| Tarea | Qué se hace | Responsable | Origen |
| --- | --- | --- | --- |
| Resolver secretos | B no encuentra secretos reales en el código ni en el historial. Se mantiene el bloqueo de `.env` en CI y la clave JWT de pruebas sigue marcada como tal | Equipo B | B §2 |
| Elegir licencia | MIT (hecho). Resolver la licencia del icono de Flaticon y corregir `docs/atribuciones.md` | Equipo C | C §1, §7 |
| Preparar documentación | Fusionar `docs/modificacion-pdf` y después `docs/p9-preparacion-comunidad`. `.env` único y arranque sin cargar Overpass. Pantalla de créditos | Equipos B y C | B riesgo 1, C §2 |
| Revisar dependencias | Decidir el proveedor de teselas y el plan de Open-Meteo. Que cada job de CI falle si falta su parte | Equipo B | B §3, riesgos 2 y 3 |
| Gobernanza inicial | `GOVERNANCE.md`, `CODEOWNERS` con dos responsables por módulo, suplente del PO y al menos dos administradores del repositorio | Equipo D | D, acciones 1–3 |

### Fase 2 · Primera publicación

| Tarea | Qué se hace | Responsable | Origen |
| --- | --- | --- | --- |
| Crear una release | `v0.1.0` con su entrada en `CHANGELOG.md` y las limitaciones conocidas | PO y Scrum Master | A §4 |
| Publicar documentación | README con nivel de soporte y estado de mantenimiento | Equipos C y D | C §2, D acción 5 |
| Anunciar el proyecto | En la universidad y entre grupos de datos abiertos y movilidad de Málaga | Equipo A | A §3 |
| Canales de comunicación | Issues para bugs y propuestas, aviso privado de GitHub para vulnerabilidades y contacto privado del código de conducta. Sin chat ni correo propio hasta que la comunidad crezca | Equipo C | C §3, §5 |
| Issues iniciales | Las tres *good first issue*, y labels y descripción del repositorio en español | Equipo C | C §6, §8 |

### Fase 3 · Después de publicar

| Tarea | Qué se hace | Responsable | Origen |
| --- | --- | --- | --- |
| Revisar contribuciones y responder issues | *First responder* rotatorio semanal, con respuesta en 72 h | Maintainers | D |
| Integrar PRs | CI en verde y una aprobación de otra persona (dos en privacidad, seguridad o licencia) | Maintainers | C §4, D |
| Gestionar releases | SemVer y `CHANGELOG.md` en cada versión, hasta llegar a la 1.0 del MoSCoW | PO | C §3 |
| Monitorizar la salud | Revisión mensual de los indicadores de la sección 4 | Scrum Master | D acción 6 |

## 4. Indicadores de salud

Se revisan cada mes con los datos de GitHub (*Insights* e issues).

### Cómo se mide cada indicador

| Indicador | Cómo se mide | Umbral |
| --- | --- | --- |
| **Tiempo de respuesta a issues** | Mediana desde que se abre una issue o PR hasta la primera respuesta de un maintainer | Objetivo: menos de 72 horas |
| **Concentración de contribuciones** | Porcentaje de commits fusionados del último trimestre que hace la persona que más aporta | Alerta: más del 50 % |
| **Número de nuevos colaboradores** | Personas con su primera PR fusionada en el mes | Objetivo: al menos una cada mes tras el anuncio |
| **Actividad de mantenimiento** | Releases, PRs fusionadas e issues cerradas en el mes | Alerta: un mes sin PRs fusionadas |

### Qué revela y qué se hace si empeora

| Indicador | Qué permite detectar | Posible actuación |
| --- | --- | --- |
| **Tiempo de respuesta a issues** | Comunidad desatendida: quien no recibe respuesta no vuelve | Reforzar el *first responder* rotatorio y revisar el triage del PO |
| **Concentración de contribuciones** | Dependencia de pocas personas (*bus factor*) | Repartir la revisión con `CODEOWNERS`, documentar las partes que solo conoce una persona y abrir *good first issues* en ellas |
| **Número de nuevos colaboradores** | Dificultad para atraer comunidad: arranque largo, guía poco clara o falta de tareas iniciales | Mejorar el onboarding (medir de nuevo el arranque, *good first issue* 2) y preparar más tareas iniciales |
| **Actividad de mantenimiento** | Riesgo de abandono, sobre todo al acabar la asignatura | Revisar la carga y las prioridades, buscar continuidad con TFG o prácticas y, si no hay nadie, marcar el README como *sin mantenimiento* (D) |

## 5. Acciones pendientes y responsables

| Acción | Quién | Cuándo |
| --- | --- | --- |
| Validar este plan y los cuatro documentos en la próxima reunión del equipo | Todo el equipo | Antes de publicar |
| Fusionar `docs/modificacion-pdf` y, después, `docs/p9-preparacion-comunidad` | Equipo C con revisión | Antes de publicar |
| Crear las issues de las tareas de *Antes de publicar* para seguirlas en el tablero | PO | Antes de publicar |
