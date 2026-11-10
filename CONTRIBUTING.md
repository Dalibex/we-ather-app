# Contribuir a WEather

Todas las personas que participan aceptan el [código de conducta](CODE_OF_CONDUCT.md). Para
vulnerabilidades, sigue la [política de seguridad](SECURITY.md), nunca una issue pública.

Todo el proyecto se escribe en español: documentación, issues, pull requests y commits.

## Documentación de referencia

| Documento | Qué contiene |
| --- | --- |
| [ConOps](docs/PDF%20Entregados/DocumentoConOps.pdf) | Propósito, actores, objetivos (`OBJ-…`), mitigaciones (`MIT-…`) y el alcance MoSCoW (sección 5) |
| [Diagrama Tropos](docs/ESQUEMA_TROPOS.png) | Red de dependencias entre actores y objetivos |
| [Práctica 3-6: Gestión](docs/PDF%20Entregados/Pr%C3%A1ctica%203-6%20Gesti%C3%B3n.pdf) | Priorización del backlog, planificación del Sprint 1, métricas y proceso Scrum |
| [Desarrollo local](docs/development.md) | Requisitos, arranque, comprobaciones y hook `pre-commit` |
| [Decisiones de arquitectura](docs/adr/) | Por qué se eligió cada parte del stack |

Si algo de esta guía choca con el ConOps en el alcance, manda el ConOps y se avisa al Product Owner.

## Equipo y roles

| Rol | Quién | Responsabilidades |
| --- | --- | --- |
| Product Owner | @AlejandroCuetoDiaz | Ordena el backlog, redacta las HU y sus criterios, acepta o rechaza el incremento |
| Scrum Master | @Lewas88 | Modera los eventos, actualiza el tablero, gestiona los bloqueos y prepara las métricas |
| Developers | Todo el equipo | Descomponen y estiman, implementan con TDD, revisan el código de otros y validan criterios |
| Stakeholder | Profesorado | Valida el incremento en la Sprint Review y aporta feedback para el backlog |

- El PO y el SM también desarrollan: reservan 2 de sus 12 horas del sprint a su rol.
- Si el PO no está, el developer puede aclarar dudas sobre su tarea, pero no cambiar prioridades ni
  alcance. Si falta el SM, modera los eventos el siguiente integrante por orden alfabético.
- Las dudas de requisitos van al PO. Lo que no pueda resolver se lleva a la siguiente sesión con el
  profesorado y, mientras, la tarea queda en *Necesita ayuda*.

## Sprints y eventos

Los sprints duran **2 semanas** y cada uno es un milestone de GitHub (`Sprint 1`, `Sprint 2`...).
Cada persona dedica como máximo **12 horas netas** por sprint.

| Evento | Cuándo | Duración | Para qué |
| --- | --- | --- | --- |
| Sprint Planning | Inicio del sprint | Máx. 2 h | Objetivo del sprint, capacidad, HU seleccionadas y tareas con responsable, revisor y estimación |
| Scrum Meeting | Diario | 15 min | Avance hacia el objetivo, bloqueos y quién ayuda |
| Sprint Review | Fin del sprint | 30 min | Demostrar el incremento al profesorado y actualizar el backlog |
| Retrospectiva | Tras la Review | 45 min | Revisar el proceso con las métricas y acordar mejoras con responsable |

**Scrum Meeting:** lunes y miércoles en persona; el resto de días por Discord, a la hora fijada en
el Planning. Se recorre el tablero de derecha a izquierda: de cada tarea en curso, qué falta, qué se
hará antes de la siguiente reunión y si hay bloqueos. Los problemas técnicos no se discuten ahí: se
anotan y los implicados los tratan justo después. Las grabaciones se guardan fuera del repositorio,
como explica [docs/dailys](docs/dailys/README.md).

## Comunicación y acuerdos

- **Canales:** WhatsApp para la coordinación diaria, Discord para los Scrum Meetings en remoto y la
  pull request para las discusiones técnicas. Se responde en menos de 24 h.
- **Bloqueos:** si una tarea no avanza en 2 h, pasa a *Necesita ayuda* y se avisa al SM, que asigna
  a alguien para desbloquearla. Los bloqueos y los riesgos se trabajan en pareja (sesiones de 1 a
  2 h, cambiando de conductor cada 25 min).
- **Riesgos:** se avisan en el Scrum Meeting; el SM reúne a los implicados y al PO, que decide qué se
  recorta. Se recortan funcionalidades, nunca calidad.
- **Cambios de alcance:** se proponen al PO y no entran en el sprint en curso, salvo excepción y a
  cambio de un esfuerzo equivalente.
- **Desacuerdos:** se exponen en la PR. Si en 24 h no hay acuerdo, los implicados se reúnen 15
  minutos. En requisitos decide el PO; en lo técnico, el equipo por mayoría o el SM aplicando diseño
  simple.
- **Ritmo:** nadie supera sus horas y no se acumula trabajo en los últimos días del sprint.

## Historias de usuario y tareas

Las issues siguen la misma estructura que el tablero de ClickUp:

- **Historia de usuario** (`[HU-XX]: título`): la HU del Product Backlog, con los objetivos que
  cubre, su alcance MoSCoW y la checklist de criterios de aceptación. Lleva el milestone del sprint
  y su prioridad.
- **Tarea** (`[T-XX.n]: título`): un bloque de trabajo de la HU, de **6 horas como máximo** (si
  pasa de 6 h, se divide). Indica la HU, la estimación, la incertidumbre, el responsable, el
  revisor y de qué depende. Se añade como sub-issue de su HU y no repite sus criterios. Una tarea
  de configuración o mantenimiento (el esqueleto, la CI, una guía) puede no tener HU.
- **Bug** (`[Bug]: título`): un error, con los pasos para reproducirlo.

### ClickUp y GitHub

Cada tarea existe en los dos sitios con el mismo identificador (`T-17.1`):

- **ClickUp** manda en el estado, las horas registradas y las métricas del sprint.
- **GitHub** guarda el trabajo de código: la issue de la tarea se cierra sola al fusionar la PR que
  la enlaza (`Closes #N`).
- La issue de la **HU** no se cierra sola al cerrar sus tareas: la cierra el PO cuando se han
  comprobado sus criterios de aceptación.

Estados en ClickUp: Pendiente → Sprint Backlog → En curso → En revisión → Completado, más
*Necesita ayuda* (bloqueo) y *Gestionando riesgos* (riesgo que obliga a replanificar la tarea).
GitHub no tiene estos estados: una issue está abierta hasta que se fusiona su PR.

## Flujo

1. Coge una tarea del Sprint Backlog asignada a ti.
2. Crea una **rama** desde el último `main`.
3. Desarrolla con **TDD**: prueba que falla, código mínimo para que pase, refactorización.
4. Haz **commits pequeños** con Conventional Commits. El hook `pre-commit` formatea el código antes
   de cada commit (ver [Desarrollo local](docs/development.md#hook-pre-commit)).
5. Abre una **pull request** a `main` que cierre la tarea (`Closes #N`). Su revisor la revisa en
   menos de 24 h.
6. Cuando todas las tareas de una HU están integradas, su responsable ejecuta las pruebas de
   aceptación y marca los criterios. El PO lo revisa, cierra la issue de la HU y la mueve a
   *Completado* en ClickUp.

Las ramas son cortas: se integran como mínimo cada dos días.

## Ramas

`tipo/<tarea>-descripcion-corta`, en minúsculas y con guiones:

| Tipo | Uso |
| --- | --- |
| `feature/` | Funcionalidad nueva |
| `fix/` | Corrección de un error |
| `docs/` | Solo documentación |
| `chore/` | Configuración, dependencias, mantenimiento |

Ejemplos: `feature/t-17.1-ingesta-calor`, `fix/42-coordenadas-fuera-de-rango`.

## Commits

[Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/): `tipo(ámbito opcional):
descripción`, en imperativo y corta, con la tarea al final.

```text
feat(heatmap): ingerir los datos de Open-Meteo (T-17.1)
fix(apis): devolver el último dato válido si la API no responde (T-20.1)
docs: añadir guía de desarrollo
```

Tipos habituales: `feat`, `fix`, `docs`, `chore`, `refactor`, `test`, `build`, `ci`.

## Pruebas

- **Unitarias:** cada developer las ejecuta en local antes de subir cambios, y la CI las repite.
- **Integración:** comprueban la base de datos y las APIs externas usando mocks, para no gastar sus
  límites de peticiones.
- **Aceptación:** se ejecutan cuando todas las tareas de la HU están integradas; cada criterio de
  aceptación tiene su prueba.
- Cada tarea incluye sus pruebas: ninguna HU llega a revisión sin ellas.
- Objetivo del equipo: el 100 % de las PRs con pruebas y más del 70 % de cobertura en el backend.

## Pull requests

- Una PR por tarea, pequeña y solo con cambios de esa tarea.
- El título sigue el formato de los commits, con la tarea al final.
- Solo se fusiona con la CI en verde, la aprobación de una persona distinta del autor, pruebas para
  la lógica nueva o modificada y sin conflictos con `main`.
- El revisor rechaza la PR si no tiene pruebas o si el historial de commits muestra que se
  escribieron al final, después del código.

## Definition of Done

Una tarea está terminada cuando:

- Se ha desarrollado con TDD y sus pruebas unitarias están en verde.
- La CI está en verde: compilación, pruebas unitarias, pruebas de integración y linter.
- Una persona distinta del autor la ha revisado y aprobado.
- Los criterios de aceptación que cubre tienen una prueba asociada y se han comprobado.
- Está integrada en `main` sin romper funcionalidades anteriores.
- La documentación afectada está actualizada.

## Labels

- Tipo: `historia`, `tarea`, `bug`.
- Prioridad: `prioridad: urgente`, `prioridad: alta`, `prioridad: media`, `prioridad: baja`.
