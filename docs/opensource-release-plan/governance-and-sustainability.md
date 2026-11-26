# Equipo D · Gobernanza y sostenibilidad (WEather)

> Revisado en la Actividad 3: los maintainers y la licencia se han alineado con el Equipo C, y
> el plan común está en `open-source-launch-plan.md`.

## Modelo de gobernanza

**Decisión:** un **grupo pequeño de mantenedores** (comité de mantenimiento de 2–3 personas) que
decide por **consenso perezoso**, con una persona responsable de producto que tiene la última
palabra solo en alcance y prioridades. Lo documentaremos en `GOVERNANCE.md`.

**Justificación (criterios del Tema 4):**

- **Tamaño y madurez:** WEather es un proyecto académico pequeño y sin release publicada; no
  necesita una estructura formal como un *steering council* electo ni una fundación.
- **Por qué no BDFL:** sería lo más rápido, pero el objetivo de la práctica es que el proyecto no
  dependa de sus desarrolladores originales. Una sola autoridad concentra el *bus factor* y crea un
  cuello de botella en las revisiones.
- **Por qué no fundación ni modelo corporativo:** no hay empresas competidoras que exijan
  neutralidad ni activos (marca, dinero) que necesiten una estructura legal.
- **Velocidad frente a inclusión:** el consenso perezoso permite avanzar rápido (si nadie se opone
  en el plazo, se aprueba) y a la vez da voz a la comunidad en issues y PRs.

**Roles:**

| Rol | Quién | Qué puede hacer |
| --- | --- | --- |
| Contributor | Cualquier persona | Abrir issues y PRs, revisar y comentar. |
| Maintainer | Inicialmente @Dalibex, @AlejandroCuetoDiaz y @Lewas88, las tres personas responsables del código de conducta (Equipo C) | Aprobar e integrar PRs, triage, gestionar releases. |
| Responsable de producto | @AlejandroCuetoDiaz, el Product Owner (suplente por nombrar) | Decidir sobre alcance y prioridades del roadmap, con el MoSCoW del ConOps como referencia. |

Un contributor externo puede pasar a maintainer tras **varias contribuciones aceptadas y revisiones
útiles a lo largo de al menos 2–3 meses**, si lo propone una persona maintainer y nadie del grupo se
opone. Así la autoridad se basa en el reconocimiento de la comunidad y no en el puesto que se tenía
en el equipo Scrum.

## Quién revisa y acepta cambios

- Cada PR necesita **CI en verde** y **la aprobación de una persona distinta de la autora**, como ya
  dice `CONTRIBUTING.md`. Después la integra una persona maintainer.
- Un archivo **`CODEOWNERS`** (por crear) asigna revisores obligatorios por módulo (por ejemplo, cálculo de
  rutas, interfaz, datos). Cada módulo tiene **dos responsables**, para que ninguna parte crítica
  dependa de una sola persona.
- Los cambios de alcance o de producto necesitan además el visto bueno de la persona responsable de
  producto.
- Los cambios que afecten a **privacidad, seguridad, licencia o datos personales** no se integran
  sin la aprobación explícita de dos maintainers. El ruleset de `main` hoy exige una sola
  aprobación, así que esta norma se escribe en `GOVERNANCE.md` y se aplica en la revisión.
- Una persona maintainer hace de **first responder** y contesta a las issues y PRs nuevas en un
  máximo de 72 horas. Este rol rota cada semana.

## Resolución de desacuerdos

1. **Discusión en el hilo:** se discute en la issue o PR, exponiendo el impacto y las alternativas.
   La comunicación asíncrona y por escrito es la norma.
2. **Consenso perezoso:** si se propone una decisión y nadie la bloquea con argumentos en
   **72 horas**, queda aprobada.
3. **Si sigue el bloqueo:** las personas maintainers votan y decide la mayoría. Si hay empate:
   - en alcance y prioridades decide la persona responsable de producto;
   - en decisiones técnicas se mantiene lo que ya existe hasta que haya acuerdo.
4. **Registro:** la decisión y su motivo quedan escritos en el hilo, para que cualquier persona que
   llegue después pueda consultarlos.
5. **Conducta:** las quejas por conducta no siguen este proceso. Se envían al contacto privado de
   `CODE_OF_CONDUCT.md` y las gestionan maintainers que no estén implicados en la queja.

Si alguien no está de acuerdo con el rumbo del proyecto, la licencia le permite hacer un fork. Aun
así, el caso OpenOffice/LibreOffice muestra que un fork divide la comunidad, por lo que queremos
que los desacuerdos se resuelvan con este proceso.

## Qué ocurre si una persona desarrolladora principal abandona el proyecto

- **Revisión y permisos:** las demás personas maintainers mantienen la revisión y los permisos.
  Cuando exista, `CODEOWNERS` tendrá un segundo responsable para cada módulo, así que no se
  bloqueará ninguna parte.
- **Conocimiento:** el contexto está escrito en issues, PRs y documentación (arranque, despliegue,
  decisiones de arquitectura y cómo recuperar los servicios).
- **Responsable de producto:** si se va esta persona, su suplente asume el rol. Mientras no se
  confirme a alguien, se pausan los cambios que amplíen el alcance.
- **Si no queda nadie activo:** se marca el README como *"sin mantenimiento"* y se pide a la
  comunidad que proponga nuevas personas maintainers. Si en 3 meses nadie se hace cargo, el
  repositorio se archiva. Los permisos se transfieren solo a personas que ya hayan contribuido.

## Riesgos de continuidad y mitigaciones

| Riesgo | Mitigación |
| --- | --- |
| **Bus factor:** solo una persona conoce o puede modificar una parte crítica. | Dos responsables por módulo en `CODEOWNERS`, revisión por pares obligatoria y documentación de arranque, despliegue y recuperación. |
| **Permisos y cuentas concentrados:** la administración del repositorio, los secretos o los servicios dependen de una sola cuenta. | Al menos dos personas con permisos de administración y la cuenta de los servicios a nombre del proyecto, no de una persona. Los permisos se revisan cada vez que cambia el equipo. |
| **Fin de la asignatura:** el equipo deja de tener tiempo y el mantenimiento no tiene recursos. | Hacer visible el estado de mantenimiento, reducir el soporte prometido, captar maintainers antes de aceptar nuevas funcionalidades y buscar continuidad mediante TFG o prácticas (ver Sostenibilidad). |

## Modelo de sostenibilidad

**Decisión:** un modelo **basado en patrocinio institucional y donaciones**, sin monetizar el
software. Sostener el proyecto significa cubrir **personas** (tiempo de mantenimiento) y
**recursos** (alojamiento y fuentes de datos). El código abierto por sí solo no lo garantiza.

1. **Patrocinio institucional (fuente principal):** acuerdos con la universidad o con entidades
   locales interesadas en el calor urbano en Málaga, para cubrir el alojamiento y, sobre todo, dar
   continuidad al trabajo. Por ejemplo, ofrecer WEather como base de TFG o prácticas, para que cada
   curso entren nuevas personas maintainers.
2. **Donaciones voluntarias (complemento):** GitHub Sponsors u Open Collective, con un uso del
   dinero transparente y limitado a infraestructura.
3. **Coste cero mientras sea posible:** GitHub Actions es gratuito para repositorios públicos, y
   antes de ofrecer disponibilidad continua se revisan los límites gratuitos de las fuentes de
   datos externas. La API gratuita de Open-Meteo es solo para uso no comercial, y las teselas de
   `tile.openstreetmap.org` no admiten apps publicadas (Equipo B, riesgo 2): ambas cosas hay que
   resolverlas antes de ofrecer un servicio abierto.

**Modelos descartados (tabla de modelos de negocio del Tema 4):**

| Modelo | Por qué no, por ahora |
| --- | --- |
| Open Core | Dividiría funciones entre una versión libre y otra de pago en un proyecto sin usuarios. Las funciones de privacidad o de rutas nunca serían de pago. |
| Dual licensing | Con MIT (Equipo C) no tiene sentido vender otra licencia, y exigiría poseer los derechos de todas las contribuciones (CLA), lo que frena las aportaciones externas. |
| SaaS / alojamiento de pago | Es una opción futura si aparecen usuarios reales (por ejemplo, un ayuntamiento), pero crea obligaciones de servicio que el equipo no puede asumir hoy y obligaría a contratar planes comerciales de Open-Meteo y de teselas. |
| Soporte y consultoría | No hay demanda ni personas con dedicación para ofrecerlo. |

**Límites:** nunca se financiará el proyecto vendiendo datos de ubicación ni debilitando la
privacidad. Además, no queremos depender de un único patrocinador: el caso OpenSolaris muestra que
un proyecto queda expuesto si un solo actor controla sus recursos.

## Acciones pendientes antes de publicar

1. Validar este modelo con el equipo y escribir `GOVERNANCE.md` con los roles, el proceso de
   decisión y cómo llegar a ser maintainer.
2. Confirmar a las tres personas maintainers, nombrar la suplente de la persona responsable de
   producto y un responsable de seguridad.
3. Crear `CODEOWNERS` con dos responsables por módulo y revisar los permisos de administración del
   repositorio y de los servicios.
4. ~~Confirmar quién aplica el código de conducta~~: hecho por el Equipo C (tres responsables en
   `CODE_OF_CONDUCT.md`, rama `docs/p9-preparacion-comunidad`).
5. Indicar en el README el nivel de soporte (sin prometer alojamiento ni tiempos de respuesta que no
   podamos cumplir) y el estado de mantenimiento.
6. Revisar cada mes los indicadores de salud del plan común (`open-source-launch-plan.md`). La
   licencia MIT ya es coherente con este modelo, que no depende de vender licencias.
