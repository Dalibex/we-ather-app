# Contribuir a WEather

Todas las personas que participan aceptan el [código de conducta](CODE_OF_CONDUCT.md). Para
vulnerabilidades, sigue la [política de seguridad](SECURITY.md), nunca una issue pública.

Todo el proyecto se escribe en español: documentación, issues, pull requests y commits.

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

Estados de una tarea: Pendiente → Sprint Backlog → En curso → En revisión → Completado. Si una tarea
no avanza en 2 h, pasa a *Necesita ayuda* y se avisa al Scrum Master. Si la frena un riesgo
técnico, pasa a *Gestionando riesgos*.

## Flujo

1. Coge una tarea del Sprint Backlog asignada a ti.
2. Crea una **rama** desde el último `main`.
3. Desarrolla con **TDD**: prueba que falla, código mínimo para que pase, refactorización.
4. Haz **commits pequeños** con Conventional Commits.
5. Abre una **pull request** a `main` que cierre la tarea (`Closes #N`). Su revisor la revisa en
   menos de 24 h.
6. Cuando todas las tareas de una HU están integradas, su responsable comprueba los criterios de
   aceptación y el Product Owner la mueve a *Completado*.

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

## Pull requests

- Una PR por tarea, pequeña y solo con cambios de esa tarea.
- El título sigue el formato de los commits.
- Solo se fusiona con la CI en verde, la aprobación de una persona distinta del autor, pruebas para
  la lógica nueva o modificada y sin conflictos con `main`.
- Los desacuerdos se hablan en la PR. Si en 24 h no hay acuerdo, los implicados se reúnen 15
  minutos.

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
