# Práctica 3

## Priorización del Backlog

Para organizar las 27 HUs, hemos utilizado dos criterios de priorización:

1. **Valor para el proyecto:** Basado directamente en la técnica MoSCoW de nuestro ConOps.
2. **Dependencias:** El orden técnico lógico. Tareas fundacionales antes.

**Prioridad URGENTE**

HU-05 / HU-07 / HU-08 / HU-17 / HU-20

**Prioridad ALTA**

HU-01 / HU-02 / HU-09 / HU-10 / HU-16 / HU-19 / HU-21

**Prioridad MEDIA**

HU-04 / HU-11 / HU-12 / HU-13 / HU-14 / HU-15 / HU-18 / HU-22

**Prioridad BAJA**

HU-03 / HU-06 / HU-23 / HU-24 / HU-25 / HU-26 / HU-27

### *Justificación de las decisiones de priorización:*

Para priorizar nuestro Backlog hemos utilizado principalmente dos criterios: el valor para el proyecto y las dependencias técnicas. Por ejemplo, hemos dado prioridad al HU-20 (uso de APIs) frente al HU-12 (envío de reportes) porque la primera tiene una alta dependencia con el mapa de calor de la HU-17, el cual es imprescindible para que el sistema funcione. Los requisitos cuyo alcance es Could Have se les ha dado una prioridad menor.

## Selección del Sprint Backlog

HU Seleccionadas para el Sprint 1 → HU-01 / HU-02 / HU-05 / HU-17 / HU-20

### *Justificación de la selección del Sprint:*

Hemos seleccionado para el Sprint 1 un alcance basado en dependencias técnicas. Incluimos el mapa HU-05, la conexión a APIs HU-20 y la autenticación HU-01/HU-02 por ser las bases para desarrollar rutas y reportes, además hemos seleccionado la HU-17 ya que hay varias HU que dependen de ella como la HU-08.

## Reflexión Final

1. **¿Hubo HU que fueran demasiado grandes y tuvieran que dividirse?** Sí, inicialmente contemplamos la gestión de cuentas como una única historia grande. Tuvimos que dividirla para aislar la HU-01 (Registro de cuenta) de la HU-02 (Inicio de sesión), ya que requerían flujos, validaciones y pantallas diferentes, facilitando así su estimación y reparto en el equipo.

2. **¿Hubo tareas que inicialmente no habíais identificado?** Sí, al desglosar las HUs y revisar los riesgos técnicos nos dimos cuenta de que faltaban tareas de seguridad e infraestructura que no eran tan evidentes, como la implementación de un cooldown de reportes para evitar falsificaciones (spam) y el control y bloqueo de tráfico en el servidor (MIT-02).

3. **¿Qué HU habéis considerado prioritarias y por qué?** Hemos considerado prioritario el mapa base, el mapa de calor, la conexión y almacenamiento en caché de las APIs, y el sistema de autenticación de usuarios. Son prioritarias porque calcular rutas y reportar depende de ellas.

4. **¿Alguna tarea tiene dependencias que condicionen la planificación?** Completamente. La planificación está condicionada por el flujo de datos: no podemos desarrollar el motor de cálculo de rutas ni mostrar un resumen de alternativas al usuario hasta que el mapa base y la extracción de datos climáticos estén terminados e integrados.

5. **¿Qué trabajo debería pasar por Gestionando riesgos?** La Tarea 17.1, correspondiente al diseño de arquitectura e ingesta del motor de calor. Al tener una alta incertidumbre técnica sobre cómo cruzar de forma eficiente los datos del clima con las aportaciones de los usuarios, necesita un prototipo antes de comprometer al equipo a desarrollarlo.

6. **¿Qué ocurriría si desapareciera del equipo la persona responsable de una tarea crítica?** Provocaría un fuerte cuello de botella, sobre todo en tareas de arquitectura base. Para mitigarlo, al ser un equipo de 8 integrantes, nos apoyamos en la exhaustiva documentación del proyecto y en desglosar las historias en subtareas pequeñas para que otro compañero pueda asumir el trabajo.

7. **¿El Sprint seleccionado parece realizable con las personas disponibles?** Sí. Para un equipo de 8 desarrolladores, el Sprint 1 es viable porque hemos limitado el alcance a tareas fundacionales conocidas. Al dejar fuera las funciones complejas dependientes y aislar la investigación del algoritmo en gestión de riesgos, aseguramos que la carga de trabajo sea admisible y realista.

---

# Práctica 4

Este documento recopila la información empleada para completar los registros en ClickUp dentro del marco de la Práctica 4. Resulta clave disponer de esta documentación integral, dado que ciertas limitaciones de la plataforma (derivadas de planes de pago) impiden reflejar la totalidad de los datos directamente en ella. Aunque parte de los contenidos puedan parecer reiterativos, se optó por preservarlos unificados en este espacio para garantizar el contexto necesario. En este apartado se muestran los 10 entregables de la práctica a excepción del 5 que se muestra en el propio ClickUp.

## 1. Estimación de Tareas

| ID | HU | Esfuerzo (h) | Incertidumbre (Baja/Media/Alta) |
|---|---|---|---|
| T-1.1 | HU-01 | 5 | Baja |
| T-1.2 | HU-01 | 6 | Media |
| T-1.3 | HU-01 | 3 | Baja |
| T-2.1 | HU-02 | 3 | Baja |
| T-2.2 | HU-02 | 4 | Media |
| T-2.3 | HU-02 | 6 | Media |
| T-5.1 | HU-05 | 4 | Baja |
| T-5.2 | HU-05 | 5 | Media |
| T-5.3 | HU-05 | 3 | Baja |
| T-5.4 | HU-05 | 4 | Media |
| T-17.1 | HU-17 | 5 | **Alta** |
| T-17.2 | HU-17 | 6 | Media |
| T-17.3 | HU-17 | 6 | **Alta** |
| T-17.4 | HU-17 | 4 | Media |
| T-17.5 | HU-17 | 4 | Media |
| T-20.1 | HU-20 | 4 | Media |
| T-20.2 | HU-20 | 2 | Baja |

**Esfuerzo total del Sprint = 74 h**

## 2. Capacidad del equipo

| Miembro | Disponibilidad (h) | Capacidad neta (h) |
|---|---|---|
| Alejandro Cueto Díaz | 16 | 12 |
| Daniel Linares Bernal | 16 | 12 |
| Francisco Javier Jordá G. | 16 | 12 |
| Mario Fernández Calderón | 16 | 12 |
| Julián David Lemus Rubiano | 16 | 12 |
| Andrés Santaella González | 16 | 12 |
| Nicolai Alejandro Pirela O. | 16 | 12 |
| Pablo Ortega Serapio | 16 | 12 |
| **Total** | **128** | **96** |

La capacidad neta se dimensiona a partir del esfuerzo estimado (74 h): 96 h de capacidad dejan **22 h de margen (≈ 23 %)** para revisiones, coordinación y desviaciones de estimación.

## 3|4. Asignación y revisión

| Tarea | Responsable | Revisor | Dev / Pruebas / Revisión |
|---|---|---|---|
| T-1.1 | Alejandro | Daniel | 3h / 1h / 1h |
| T-1.2 | Mario | Alejandro | 4h / 1h / 1h |
| T-1.3 | Alejandro | Mario | 1.5h / 0.5h / 1h |
| T-2.1 | Mario | Andrés | 1.5h / 0.5h / 1h |
| T-2.2 | Daniel | Nicolai | 2.5h / 0.5h / 1h |
| T-2.3 | Daniel | Julián | 4h / 1h / 1h |
| T-5.1 | Andrés | Pablo | 2.5h / 0.5h / 1h |
| T-5.2 | Julián | Andrés | 3h / 1h / 1h |
| T-5.3 | Pablo | Alejandro | 1.5h / 0.5h / 1h |
| T-5.4 | Andrés | Francisco | 2.5h / 0.5h / 1h |
| T-17.1 | Nicolai | Francisco | 3h / 1h / 1h |
| T-17.2 | Nicolai | Julián | 4h / 1h / 1h |
| T-17.3 | Francisco | Nicolai | 4h / 1h / 1h |
| T-17.4 | Francisco | Daniel | 2.5h / 0.5h / 1h |
| T-17.5 | Pablo | Mario | 2.5h / 0.5h / 1h |
| T-20.1 | Julián | Pablo | 2.5h / 0.5h / 1h |
| T-20.2 | Pablo | Daniel | 1h / 0.5h / 0.5h |

## 7. Carga de trabajo por miembro

| Miembro | Capacidad (h) | Asignado (h) | Margen (h) | Situación |
|---|---|---|---|---|
| Alejandro | 12 | 8 (T-1.1, T-1.3) | 4 | ✓ |
| Daniel | 12 | 10 (T-2.2, T-2.3) | 2 | ✓ |
| Francisco | 12 | 10 (T-17.3, T-17.4) | 2 | ✓ |
| Mario | 12 | 9 (T-1.2, T-2.1) | 3 | ✓ |
| Julián | 12 | 9 (T-5.2, T-20.1) | 3 | ✓ |
| Andrés | 12 | 8 (T-5.1, T-5.4) | 4 | ✓ |
| Nicolai | 12 | 11 (T-17.1, T-17.2) | 1 | ✓ (al límite) |
| Pablo | 12 | 9 (T-5.3, T-17.5, T-20.2) | 3 | ✓ |
| **Total** | **96** | **74** | **22** | **Margen global ≈ 23%** |

## 8. Posibles sobrecargas

En el reparto inicial, las HU asignadas en ClickUp caían casi enteras en sus responsables generando así claras sobrecargas. Se decidió equilibrar las tareas como objetos separados. Una posible sobrecarga actual podría estar en Nicolai ya que se encuentra cerca del límite de su capacidad.

## 6. Dependencias

| Tarea | Depende de | Tipo | Notas de coordinación |
|---|---|---|---|
| T-1.2 | T-2.2 | Técnica | El endpoint de registro necesita primero las estructuras de BD. |
| T-1.3 | T-1.1 | Técnica | Las validaciones viven en el formulario de registro. |
| T-2.3 | T-1.2, T-2.2 | Técnica | El login reutiliza el modelo de usuarios y las tablas creadas. |
| T-5.3 | T-5.2 | Técnica | La restricción geográfica se configura sobre el mapa ya integrado. |
| T-5.4 | T-5.2 | Técnica | La caché de teselas entra después del proveedor de teselas. |
| T-17.2 | T-17.1 | Técnica | La recolección sigue la arquitectura diseñada en el prototipo. |
| T-17.3 | T-17.1 | Técnica | El algoritmo se diseña sobre la arquitectura; **incertidumbre Alta**. |
| T-17.4 | T-17.2, T-17.3 | Técnica | El refresco periódico necesita datos y algoritmo listos. |
| T-17.5 | T-17.4 | Técnica | Solo se expone al motor de rutas cuando el heatmap ya se genera. |
| T-20.1 | T-17.2 | Técnica | El refresco horario reutiliza el recolector de datos ambientales. |

## 9. Riesgos e incertidumbres

| ID | Riesgo / incertidumbre | Impacto | Tarea de gestión |
|---|---|---|---|
| R1 | Alta incertidumbre sobre cómo cruzar eficientemente datos climáticos y aportaciones de usuarios. | Alto | **T-17.1 — Diseño de arquitectura e ingesta del motor de calor**. |
| R2 | Las APIs pueden limitar peticiones o cambiar de formato. | Alto | T-20.1 (caché y último dato válido) + incertidumbre Media T-17.2. |
| R3 | Si T-17.2 se retrasa, bloquea T-17.4, T-17.5 y T-20.1. | Alto | T-17.1/T-17.2 el día 1; revisión temprana por Francisco Javier. |
| R4 | T-17.3 puede requerir varias iteraciones. | Medio | Incertidumbre Alta declarada + margen global del 23 %. |
| R5 | RGPD: registro y login tratan datos personales. | Alto | Criterios de aceptación de HU-01/HU-02. |

## 10. Reflexión final y Justificación

| Pregunta | Respuesta del equipo |
|---|---|
| ¿Está equilibrada la carga de trabajo? | Sí: 8–11 h por miembro; ninguna sobrecarga tras la corrección del punto 8. |
| ¿Hay personas con conocimientos críticos? | No: la mayoría de personas tenemos conocimientos similares. |
| ¿Hay tareas que dependen de una única persona? | Cada HU está dividida para que dependan al menos de 2 personas. |
| ¿Alguna tarea está insuficientemente estimada? | T-17.3 tiene una alta incertidumbre, por lo que podría requerir más tiempo. |
| ¿Se ha reservado capacidad para revisión e imprevistos? | Sí: 22 h de margen y además una de las horas asignadas se centra en revisión. |
| ¿Hay algún miembro que revise demasiado trabajo? | No: máximo 3 revisiones (Daniel), el resto 2 cada uno. |
| ¿Es realmente viable completar el Sprint? | Sí, siempre que T-17.1 arranque el primer día: es la condición de la ruta crítica. |
| ¿Qué ocurriría si faltase un miembro? | Su responsable de Tarea reasigna con apoyo del revisor y la documentación ConOps. |

Las decisiones tomadas en la planificación se basan en la priorización de valor para el proyecto y dependencias técnicas. Por ende, han sido elegidas y planificadas las HU con mayor prioridad y con mayor cantidad de HU dependientes. Además hemos escogido la cantidad de tareas suficiente para dejar margen a la capacidad de carga de cada compañero y hemos reunido 3 subgrupos de dominio → **Autenticación** (Alejandro|Mario|Daniel), **Mapa/APIs** (Julián|Andrés|Pablo) y **Heatmap** (Nicolai/Francisco).

---

# Práctica 5

La selección de métricas busca asegurar el cumplimiento de los plazos, la calidad del entregable y la correcta gestión del equipo de desarrollo. Se han definido dos KPIs: **Porcentaje de HUs completadas** y **Desviación del esfuerzo estimado**. Complementariamente, para garantizar la calidad del software y un entorno sostenible, se han incorporado cuatro métricas operativas internas: **Porcentaje de tareas bloqueadas**, **Defectos detectados en revisión**, **Evolución diaria del esfuerzo** y **Tiempo en gestión de riesgos**.

## 1. Catálogo de métricas y KPIs

| Métrica | Propósito | Definición / Método de Cálculo | Frecuencia | Responsable | Criterio de Interpretación |
|---|---|---|---|---|---|
| Porcentaje de HU Completadas (KPI) | Comprobar que el equipo está terminando las funcionalidades previstas | Unidad: Porcentaje<br>Datos: Estado de las HU<br>$\frac{HU\ Terminadas}{HU\ Planificadas} \ast 100$ | Semanal | Revisor | Objetivo → Más del 80%<br>Si no → Analizar Causas |
| Porcentaje de Tareas Bloqueadas | Identificar impedimentos que retrasan el flujo de trabajo del equipo | Unidad: Porcentaje<br>Datos: Estado de las Tareas<br>$\frac{Tareas\ Bloqueadas}{Tareas\ Activas} \ast 100$ | Semanal | Desarrollador - Responsable Seguimiento | Objetivo → 0% - 10%<br>Si no → Asignación Inmediata para Resolver |
| Desviación del Esfuerzo Estimado (KPI) | Comprobar si las estimaciones de tiempo se ajustan a la realidad | Unidad: Porcentaje<br>Datos: Horas estimada y reales<br>$\left\lvert \frac{Esfuerzo\ Real - Esfuerzo\ Estimado}{Esfuerzo\ Estimado} \ast 100 \right\rvert$ | Semanal | Desarrollador - Responsable Seguimiento | Objetivo → 0% - 15%<br>Si no → Requiere Supervisión |
| Evolución Diaria Esfuerzo | Visualizar ritmo de trabajo a lo largo de la semana | Unidad: Horas<br>Datos: Asignaciones y Horas Estimadas<br>$\sum Horas\ reales\ invertidas\ al\ día$ | Diaria | Responsable Seguimiento | Objetivos → Umbral Numérico<br>Si no → Investigar Dependencias y Redistribuir Esfuerzo |
| Defectos Detectados en Revisión | Evalúa la calidad del código | Unidad: Número Entero<br>Datos: Historial de Comentarios y Cambios de Estado<br>$\sum Rechazos\ Tareas$ | Continua (Durante Revisión) | Revisor | Objetivo → 0 - 1 por Tarea<br>Si no → Paralizar Tarea y Reevaluar Diseño |
| Desviación de Tiempo en Gestión de Riesgos | Evaluar si la Gestión de Riesgos avanza o se atasca | Unidad: Porcentaje<br>Datos: Tiempo en Gestionando Riesgos<br>$\frac{Tiempo\ Real - Tiempo\ Estimado}{Tiempo\ Estimado} \ast 100$ | Diaria | Responsable de Seguimiento | Objetivo → 0% - 15%<br>Si no → Reevaluar Diseño o Asignar Apoyo |

## 2. Clasificación de Métricas Internas y Externas

| Métrica | Clasificación | Necesidades y Decisiones |
|---|---|---|
| Porcentaje de HU Completadas (KPI) | Interna / Externa | A nivel externo, atiende la necesidad de los Stakeholders de conocer el progreso. A nivel interno, informa al equipo si su nivel de valor es bajo, en cuyo caso se decide investigar las causas. |
| Porcentaje de Tareas Bloqueadas | Interna | Cubre la necesidad de identificar cuellos de botella y atascos en el flujo de trabajo. Si se detecta se decide reasignar los recursos para apoyar las tareas bloqueadas. |
| Desviación del Esfuerzo Estimado (KPI) | Interna | Satisface la necesidad de vigilar el cumplimiento de la planificación temporal. Si se detectan desviaciones altas se decide reajustar las estimaciones o utilizar el margen reservado para imprevistos |
| Evolución Diaria del Esfuerzo | Interna | Atiende la necesidad de supervisar el ritmo de avance continuo del equipo. Si el diagrama detecta un estancamiento prolongado se decide revisar dependencias, resolver impedimentos técnicos o redistribuir esfuerzo antes de que se produzcan picos a final de semana. |
| Defectos Detectados en Revisión | Interna | Cubre la necesidad de informar sobre la calidad de trabajo. Si se acumulan demasiados rechazos, se decide paralizar la tarea, reevaluar el diseño y organizar una corrección. |
| Desviación de Tiempo en Gestión de Riesgos | Interna | Atiende la necesidad de identificar bloqueos tempranos en el desarrollo de prototipos. Si excede el tiempo, se decide reasignar recursos en pareja o reevaluar dependencias. |

## 3. Planificación actualizada en ClickUp

En ClickUp hemos creado y añadido las tareas de control al Sprint 1. Las 22h de margen se han reducido a 15,5h.

## 4. Procedimiento de control y seguimiento

- **Cuándo se recopilarán y actualizarán los datos.**

  Los datos se registrarán diariamente en ClickUp por cada miembro del equipo, mientras que los revisores documentarán los defectos de forma continua durante su fase de pruebas.

- **Quién será responsable de calcular y revisar cada indicador.**

  Andrés evaluará semanalmente las métricas internas (% de bloqueos, desviación de esfuerzo y tiempo en gestión de riesgos). Alejandro consolidará y revisará los KPIs externos al finalizar el sprint.

- **Cómo se compararán los resultados con los objetivos y la planificación.**

  Los resultados reales se compararán con el catálogo de métricas, emitiendo una alerta inmediata si se superan los límites establecidos: 15% de desviación en resolución de riesgos, 15% de desviación de esfuerzo, 10% de tareas bloqueadas o 2 rechazos por tarea.

- **Qué desviaciones requieren atención y cómo se comunicarán.**

  Las desviaciones requerirán atención inmediata si superan los umbrales establecidos: desviación de esfuerzo mayor al 15%, bloqueos superiores al 10%, acumulación de 2 o más rechazos, o un progreso inferior al 80%. Estas anomalías se comunicarán mediante un aviso urgente en el canal del equipo y etiquetando al implicado en los comentarios de ClickUp.

- **Qué acciones se podrán adoptar cuando se detecten problemas.**

  El Responsable de Seguimiento aplicará las siguientes acciones correctivas inmediatas según el tipo de problema detectado:

  - **Sobrecarga o desviación de esfuerzo:** Redistribuir el trabajo hacia miembros con capacidad libre utilizando las 15.5 horas de margen global.
  - **Tareas críticas bloqueadas:** Paralizar historias de menor prioridad y reasignar desarrolladores para resolver el atasco trabajando en pareja.
  - **Exceso de defectos técnicos:** Detener el avance en solitario del desarrollador y organizar una sesión de reevaluación conjunta con el revisor.
  - **Retrasos irrecuperables en el progreso:** Mover las tareas inacabadas al Backlog y ajustar a la baja el alcance planificado para el Sprint 2.
  - **Atasco en tareas de riesgo:** Reevaluar el diseño técnico conjuntamente con el revisor asignado o realizar pair programming para desbloquear la investigación sin comprometer la ruta crítica.

---

# Práctica 6

## 1. Proceso Scrum

| Rol | Miembro | Responsabilidades | Motivo |
|---|---|---|---|
| Product Owner | Alejandro | Ordena el Backlog, redacta las HU y sus criterios de aceptación, acepta o rechaza el incremento en la Sprint Review | Ha redactado y priorizado la mayoría de HU, por lo que conoce el alcance y las prioridades |
| Scrum Master | Julián | Modera los eventos y controla su duración, actualiza el tablero, gestiona los bloqueos y prepara las métricas de proceso | Organizó el arranque del Sprint 1 |
| Developer | Todos | Descomponen y estiman las HU, implementan con TDD, revisan el código de otros y validan criterios de aceptación | Todos participamos en el desarrollo del proyecto |
| Stakeholder | Profesor | Actúa como cliente, valida el incremento en la Sprint Review y aporta feedback que se convierte en entradas del backlog | No tenemos un cliente real |

**Limitaciones de acumular roles.** El PO y el SM también desarrollan, así que tienen menos horas para tareas técnicas. En cada Planning reservan 2 de sus 12 horas a las tareas propias del rol y se comprometen con menos trabajo de desarrollo. Si el PO no está disponible, el developer asignado puede aclarar dudas sobre su tarea pero no cambiar prioridades ni alcance. Si falta el SM, el integrante que le sigue alfabéticamente en la lista modera los eventos de esa semana.

**Participación del Stakeholder.** Las dudas sobre requisitos se plantean al PO. Lo que el PO no pueda resolver se lleva a la siguiente sesión con los profesores, y mientras tanto la tarea afectada queda en “Necesita ayuda”.

| Evento | Frecuencia | Duración | Participantes | Finalidad |
|---|---|---|---|---|
| Sprint | Quincenal | 2 semanas | Todos | Producir un incremento demostrable que cumpla el objetivo del sprint |
| Sprint Planning | Inicio Sprint | Máximo 2h | Todos | Analizar HU seleccionadas, capacidad comprobada, tareas con responsable y revisor |
| Scrum Meeting | Diaria (Persona/Virtual) | 15m | Todos | Revisar el alcance hacia el objetivo, detectar bloqueos y asignar ayuda |
| Sprint Review | Fin Sprint | 30m | Equipo Profesores | Demostración del incremento, comprobación de criterios y actualización del Product Backlog |
| Retrospectiva | Tras la Review | 45m | Equipo | Revisión del proceso con los indicadores y acciones de mejora con responsable |

**Duración del sprint.** En la asignatura se ha establecido 2 semanas para el Sprint.

**Objetivo del sprint.** Se redacta en una frase que describe algo observable por el usuario y se elige a partir de las HU de mayor prioridad que caben en la capacidad del equipo. El objetivo del Sprint 1 es: «un usuario puede registrarse, iniciar sesión y ver el mapa de Málaga con una primera capa de exposición al calor, respetando los límites de las APIs externas».

**Organización del Sprint Planning.** La reunión sigue cuatro pasos: el PO propone un objetivo; cada persona indica sus horas disponibles en el sprint, con un máximo de 12 netas; el equipo selecciona HU en orden de prioridad hasta llenar la capacidad; y cada HU se descompone en tareas con responsable, revisor y estimación en horas.

**Scrum Meeting**

- Lunes/miércoles 15m en persona, el resto de días por videollamada a una hora fija acordada en el Planning.
- Participantes: los 8 developers. El SM modera y controla el tiempo.
- Se proyecta el ClickUp y se recorren las columnas de derecha a izquierda. De cada tarea en curso se dice qué falta para cerrarla, qué se hará antes del siguiente Scrum Meeting y si hay algún bloqueo.
- Resultado → tablero actualizado y cada bloqueo con un asignado para ayudar antes de la siguiente reunión.

**Cómo evitamos que sea una reunión de informes** → La conversación gira en torno al objetivo del sprint y a las tareas del tablero. Los problemas técnicos no se discuten en la reunión; se anotan y quienes estén implicados los tratan justo después. Si una reunión se convierte en una ronda de informes, se anota como problema para la retrospectiva.

### Artefactos y compromisos

**Product Backlog**: Cada HU indica los objetivos que cubre y su alcance, además lleva un checklist con sus criterios de aceptación. Solo el PO cambia el orden; cualquier integrante puede proponer HU nuevas.

**Sprint Backlog**: El PO presenta las HU ordenadas por prioridad y propone el objetivo del Sprint. Se seleccionan y asignan las HU necesarias respetando la capacidad del equipo.

**Incremento**: El código integrado en la rama principal que pasa el CI y cumple la Definition of Done.

**Definition of Done**:

- Cada tarea de código se ha desarrollado con TDD y sus pruebas unitarias están en verde.
- La CI está en verde → compilación, pruebas unitarias, pruebas de integración y linter.
- El código ha sido revisado y aprobado por una persona distinta del autor.
- Todos los criterios de aceptación del checklist tienen una prueba asociada y se han comprobado.
- El código está integrado en la rama principal sin romper funcionalidades anteriores.
- La documentación afectada está actualizada.

El tablero y el repositorio son visibles para todos y el estado de cada tarea refleja su situación real. **Inspeccionamos** el avance en cada Scrum Meeting, el producto en la Review y el proceso con los indicadores en la retrospectiva. **Adaptamos** el plan en el Planning y el proceso con las acciones que salen de la retrospectiva.

## 2. Matriz de prácticas XP

| Práctica | Decisión | Justificación | Aplicación | Beneficio Esperado | Verificación |
|---|---|---|---|---|---|
| Planificación Incremental | Adoptar | Tenemos 27 HU con prioridades distintas | En el Sprint Planning el PO ordena por valor | Entran primero las HU de mayor valor | HU prioritarias terminadas al cierre |
| Pequeñas Versiones | Adoptar | Cada 2 semanas el profesor revisa el proyecto | En el Sprint Review un incremento integrado | Feedback temprano y problemas de integración detectados antes | Versión etiquetada y demostrada |
| Diseño Simple | Adoptar | El mapa de calor y la moderación con IA tienden a crecer | Implementar solo lo que exigen los criterios de aceptación | Menos código que mantener | Comentarios del revisor por complejidad innecesaria |
| TDD | Adoptar | Lógica compleja difícil de probar a mano. | Ciclo rojo, verde y refactorización en toda la lógica. | Menos defectos y criterios convertidos en pruebas desde el inicio | Pull request con test y cobertura del backend |
| Refactorización | Adoptar | Con 8 personas escribiendo en el mismo repositorio aparecerá código duplicado. | Último paso de cada ciclo TDD y antes de pedir revisión. Las refactorizaciones grandes se planifican como tarea propia. | Código legible y entendible | Rechazos en revisión por duplicación o legibilidad |
| Pair Programming | Adoptar Adaptado | No es viable siempre por horarios, solo en bloqueos y en riesgos | Sesiones de 1 a 2h con cambio de conductor cada 25 min | Menos bloqueos y más conocimiento de riesgos | Bloqueos y riesgos con dos personas implicadas |
| Integración Continua (CI) | Adoptar | 8 personas integran a la vez en el mismo repositorio | Pipeline en cada pull request con compilación, pruebas y linter. Ramas cortas, integradas como mínimo cada dos días | Conflictos y regresiones detectados el mismo día | Ejecuciones fallidas en la rama principal y tiempo hasta corregirlas. |
| Cliente Miembro | No Adoptar | No disponemos de un cliente real con disponibilidad diaria para integrarse en el equipo | El PO asume la representación del cliente resolviendo dudas y los profesores validan en Review | Evita dependencias bloqueantes durante el desarrollo diario | Tiempo de resolución de dudas por el PO y tasa de aceptación en Sprint Review |
| Test Cliente | Adoptar | Las HU tienen checklist de criterios de aceptación | Cada criterio se convierte en una prueba que se ejecuta antes de cerrar la HU | Terminado significa cumplir lo que pide el usuario | Porcentaje de criterios con prueba asociada y superada |
| Estándares Codificación | Adoptar | Todos trabajan a la vez | Guía de estilo acordada en el Sprint 1, linter y formateador en el CI y commits referenciando la tarea asociada | Revisiones centradas en la lógica y no en el formato | Linter en verde en todas las pull request |
| Propiedad Colectiva | Adoptar Adaptado | La asignación individual de tareas puede generar dependencias de autoría | Cualquier integrante puede modificar vía Pull Request con revisión cruzada | Una ausencia imprevista no bloquea la evolución | Diversidad de autores y revisores en los commits |
| Paso Sostenible | Adoptar | Compaginamos la asignatura con otras materias | Límite de 12h por Sprint y reajuste de alcance si el objetivo peligra | Ritmo constante de trabajo sin sobrecargas | Horas reales invertidas frente a la estimación inicial |

## 3. Flujo de desarrollo y calidad

| Etapa | Qué ocurre | Condición para avanzar |
|---|---|---|
| Pendiente | HU en Product Backlog | Criterios claros, estimada y con tamaño para un sprint |
| Sprint Backlog | Seleccionada y descompuesta en tareas | Tarea asignada con responsable y revisor con horas disponibles |
| En Curso | Desarrollo con TDD en una rama propia | Pruebas de la tarea en verde en local y refactorización hecha |
| Necesita Ayuda | Hay un bloqueo que impide avanzar | Bloqueo resuelto |
| Gestionando Riesgos | Hay un riesgo que impide avanzar | Tarea re-planificada |
| En Revisión | Pull Request abierta, la CI ejecuta las comprobaciones automatizadas y el revisor analiza el código | CI en verde, aprobación del revisor y comprobación de que el código cubre su parte de los criterios |
| Completado | HU parte del incremento del Sprint | Se demuestra en el Sprint Review |

**Descomposición de HU en tareas**

Las HU se descomponen en el Planning en tareas de 6 horas como máximo. Si una tarea supera ese tamaño, se divide. Cada tarea indica en su descripción la HU a la que pertenece, la estimación y su revisor.

**Relación de tareas con requisitos y criterios de aceptación**

La HU actúa como contenedor del requisito funcional y alberga la checklist con los criterios de aceptación. Las subtareas no duplican los criterios de la HU, se encargan de representar los bloques cuya suma permite cumplir los criterios.

**Aplicación de TDD**

Para cada comportamiento nuevo se sigue este ciclo:

- Prueba inicial: Se escribe una prueba a partir del criterio de aceptación o del comportamiento esperado.
- Comprobación del fallo: Se ejecuta y se confirma que falla por el motivo esperado.
- Implementación mínima: Se escribe el código justo para que la prueba pase.
- Refactorización: Se mejora el diseño con todas las pruebas en verde.

La verificación se realiza durante la revisión de código: el revisor deniega la Pull Request si no existen pruebas asociadas, comprobando en el historial de commits que las pruebas no fueron desarrolladas al final de la implementación.

**Pruebas unitarias e integración**

- Pruebas unitarias: Cada developer las ejecuta en local antes de subir cambios y se vuelven a ejecutar en el CI.
- Pruebas de integración: Comprueban BD y APIs usando mocks en el CI para no superar sus límites de uso.
- Pruebas de aceptación: Se ejecutan tras integrar todas las tareas de la HU, revisando los criterios del checklist.
- Sin acumulación: Cada tarea incluye sus pruebas, así que ninguna HU puede llegar a revisión sin ellas.

**Revisión de código**

La revisión empieza al abrir la pull request y la hace el revisor asignado en el Planning, en un plazo de 24 horas.

**Integración de cambios**

Una pull request solo se integra en la rama principal si cumple estas condiciones:

- CI en verde.
- Aprobación de al menos un revisor distinto del autor.
- Pruebas nuevas para la lógica añadida o modificada.
- Sin conflictos con la rama principal.
- Commit o descripción que referencia la tarea de ClickUp.

**Verificación del incremento**

Cuando todas las tareas de una HU están integradas, el developer asignado ejecuta las pruebas de aceptación y marca el checklist de criterios en ClickUp. El PO revisa el resultado y, si se cumple la Definition of Done, mueve la HU a Completado. Al final del sprint se etiqueta la versión en el repositorio y se demuestra a los profesores en la Sprint Review.

## 4. Acuerdo de trabajo del equipo

| Tema | Acuerdo |
|---|---|
| Canales y Plazos | WhatsApp para la rápida coordinación diaria, Discord para el Scrum Meeting no presencial y GitHub para las discusiones técnicas dentro de la PR. Las comunicaciones deben realizarse en menos de 24h. |
| Ayuda y Bloqueos | Si una tarea no avanza tras 2h, se mueve a Necesita Ayuda y se avisa al SM. El SM asigna a alguien que ayude al desbloqueo. |
| Asignación y Revisión | Asignación de tareas en el Sprint Planning y asignación de revisores al crear cada tarea. |
| Desacuerdos y Decisiones | Exposición en el PR, si en 24h no hay acuerdo, los implicados se reúnen 15m. Si es un desacuerdo de requisitos decide el PO. Si es una disputa técnica decide el equipo por mayoría o el SM aplicando Diseño Simple. |
| Cambios | Pueden proponerse al PO. No entran al Sprint en curso, salvo excepciones y a cambio de esfuerzo equivalente. |
| Disponibilidad y Ritmo | Cada miembro indica sus horas disponibles. Prohibidas las sobrecargas los últimos días del Sprint. |
| Riesgos | Aviso en Scrum Meeting. El SM reúne a los implicados y al PO, quien decide qué recortar. Se recortan funcionalidades, no calidad. |

## 5. Análisis del escenario → Una historia resulta mucho más compleja de lo previsto

Elegimos este escenario porque ya se está dando en el Sprint 1. La HU-17 tiene prioridad urgente y se ha dividido en cinco tareas. La tarea 17.1 estaba estimada en 5 horas, está en el estado «Gestionando riesgos» y las otras cuatro dependen de ella.

Supongamos que, a mitad de la primera semana, el responsable de la 17.1 comprueba que las fuentes de datos abiertos tienen formatos y frecuencias de actualización distintas, y que diseñar su ingesta llevará más del doble de lo estimado. Si nadie actúa, las tareas 17.2 a 17.5 no podrán empezar y el objetivo del sprint no se cumplirá.

**Resolución del problema inmediato**

- Detección: El responsable lo comunica en el siguiente Scrum Meeting y la tarea pasa a «Necesita ayuda».
- Personas implicadas: El SM reúne al PO, al responsable y revisor de la 17.1 y sus tareas dependientes.
- Decisiones: La 17.1 se trabaja en pareja con su revisor para repartir el conocimiento. La HU-17 se divide en una primera versión que usa una sola fuente de datos y una capa de calor básica, que entra en este sprint, y una segunda versión con el resto de fuentes y los reportes de usuarios, que vuelve al Product Backlog. El PO confirma que el objetivo del sprint se mantiene con la primera versión.
- Artefactos actualizados: Sprint Backlog con las tareas y estimaciones nuevas, Product Backlog con la nueva HU para la segunda versión, y registro de horas y bloqueos en T-Control.1.
- Prácticas aplicadas: programación en parejas, diseño simple, Planning Game para reordenar y pequeñas versiones para entregar una parte útil del mapa de calor.

**Adaptación del proceso**

- Ante una HU que usa tecnología nueva, se investiga durante un tiempo limitado para realizar la estimación.
- Las tareas de las que dependen otras se marcan como críticas en el Planning.
- En la retrospectiva se compara la estimación con las horas reales para ajustar el siguiente sprint.

## 6. Plan de evaluación

| Indicador | Cómo se mide | Valor Objetivo | Acción si no se cumple |
|---|---|---|---|
| HU / DoD | Porcentaje de HU del Sprint en Completado al cierre | Más del 80% | Seleccionar menos HU o dividir las seleccionadas |
| TDD | Porcentaje de PR con pruebas y cobertura del backend | 100% PR con pruebas<br>Más del 70% del backend con cobertura | Repasar el ciclo TDD en la retrospectiva |
| Carga | Horas reales registradas frente al límite | Nadie por encima | Reducir el compromiso y revisar estimaciones |
