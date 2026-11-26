# Equipo C · Licencia, documentación y colaboración (WEather)

@Dalibex y @Lewas88. Cambios en la rama `docs/p9-preparacion-comunidad`.

El repositorio ya es público y tenía la base: licencia MIT, README, guía de contribución, código de
conducta, política de seguridad y plantillas. Esa base estaba pensada para el equipo, no para alguien
de fuera. Este documento decide qué cambia para abrirla y qué falta.

## Resumen de decisiones

| Tema | Decisión | Dónde queda |
| --- | --- | --- |
| Licencia | MIT para el código; los recursos y datos de terceros mantienen la suya | `LICENSE`, `README.md` |
| README | Responde a qué es, qué funciona, qué no hace, cómo arrancarlo y cómo contribuir | `README.md` |
| Bugs y propuestas | Issues con formulario; el PO convierte en HU las propuestas que el ConOps recoge como *Must*, *Should* o *Could*, y cierra el resto hasta después de la 1.0 | `.github/ISSUE_TEMPLATE/`, `CONTRIBUTING.md` |
| Pull requests | Las mismas normas para todo el mundo; desde fuera, primero se pide la issue | `CONTRIBUTING.md`, `.github/pull_request_template.md` |
| Código de conducta | Contributor Covenant 2.1 en español; las denuncias van a tres responsables, para que siempre haya alternativa | `CODE_OF_CONDUCT.md` |
| Good first issues | Tres tareas pequeñas fuera del camino crítico del sprint | Issues con la label `good first issue` (por crear) |

## 1. Licencia

**MIT**, la que el repositorio tiene desde su primer commit. Queremos que WEather sea código abierto
sin restricciones, y MIT es lo que da:

- **Qué permite:** cualquiera puede usar, copiar, modificar y redistribuir el código, también en
  otros proyectos o con fines comerciales, sin pedirnos permiso.
- **Qué pide a cambio:** solo conservar el aviso de copyright y el texto de la licencia en las
  copias. El software se ofrece tal cual, sin garantía.
- **Por qué encaja:** buscamos que otras ciudades, grupos o administraciones reutilicen el cálculo
  de rutas frescas, y una licencia permisiva es la que más adopción facilita (Tema 4). Además es
  corta y se entiende en un minuto, que es lo que necesita alguien que contribuye por primera vez.
- **Dónde está:** en el archivo `LICENSE` de la raíz, que GitHub reconoce como MIT (comprobado), y
  citada en el apartado *Licencia* del README.

**Lo que la licencia no cubre.** MIT es la licencia del código del equipo, no de todo lo que hay en
el repositorio:

- El **icono de biznaga** (`frontend/apps/mobile/assets/icono-biznaga-malaga.png`) es de Flaticon,
  con su licencia gratuita, que exige atribución. No lo podemos relicenciar como MIT.
- Los **datos** de OpenStreetMap (ODbL) y Open-Meteo no están en el repositorio, porque se
  descargan al ejecutar el servidor, pero quien despliegue WEather tiene que respetar sus
  condiciones de uso y atribución (`docs/fuentes-datos.md`).

Por eso el README dice ya que los recursos y datos de terceros mantienen su licencia, y enlaza
`docs/atribuciones.md` y `docs/fuentes-datos.md`.

## 2. Contenido mínimo del README

El README responde, por este orden, a las preguntas que se hace alguien que llega por primera vez:

| Pregunta | Apartado |
| --- | --- |
| ¿Qué es y para qué sirve? | Introducción, con un aviso de que está en desarrollo |
| ¿Qué funciona hoy? | *Estado actual*, con un enlace a las HU planificadas por sprint y al MoSCoW del ConOps para el resto de la 1.0 |
| ¿Qué no incluye? | *Limitaciones* de la versión 1.0, el *Won't* del ConOps: solo Málaga, sin GPS e indicaciones en texto |
| ¿Cómo lo arranco? | *Puesta en marcha*, con los detalles en `docs/development.md` |
| ¿Dónde está el resto? | *Documentación*, agrupada por lo que buscas |
| ¿Cómo ayudo? | *Contribuir*, con el enlace a las *good first issue* |
| ¿Puedo usarlo? | *Licencia* |

Las limitaciones se han añadido porque el alcance de la versión 1.0 es muy cerrado: sin ellas, la
propuesta más probable de alguien de fuera es «añadir GPS». Lo que el ConOps marca como *Won't*
(salir de Málaga, el GPS y pintar las rutas o el mapa de calor) queda fuera de la 1.0, no para
siempre: después se podrá plantear.

## 3. Reporte de bugs y propuestas de funcionalidades

Todo entra como issue de GitHub, con su formulario correspondiente; las issues en blanco siguen
desactivadas.

| Plantilla | Para quién | Qué cambia |
| --- | --- | --- |
| Bug | Todo el mundo | Pasa a formulario, con campos obligatorios (pasos, resultado esperado y obtenido, parte afectada, versión) para poder reproducirlo sin preguntar |
| Propuesta de funcionalidad | Todo el mundo | Nueva. Pide el problema, no la solución, y que se confirme haber leído qué queda fuera de la 1.0 |
| Historia de usuario y Tarea | El equipo | Pasan a formulario con los mismos campos, casi todos obligatorios (objetivos, MoSCoW y criterios en la HU; estimación, responsable, revisor y pruebas en la tarea), e indican que son del equipo |

- **Triage:** el Product Owner revisa cada propuesta contra el MoSCoW del ConOps, que fija el
  alcance de la versión 1.0:
  - Si el ConOps la recoge como *Must*, *Should* o *Could*, la convierte en una HU del backlog o la
    añade a la HU que ya la cubre.
  - Si la marca como *Won't* o no la recoge, cierra la issue indicando que queda fuera de la 1.0 y
    que se podrá retomar después.

  Así una propuesta de fuera no se salta la priorización del equipo ni amplía el alcance de la 1.0.
- **Vulnerabilidades:** nunca en público. Van por el aviso privado de vulnerabilidades de GitHub,
  que está activado (comprobado), como explica `SECURITY.md`.

## 4. Normas para Pull Requests

**Una persona de fuera sigue las mismas normas que el equipo.** Si hubiera dos niveles de
exigencia, la calidad de `main` dependería de quién envía el cambio.

- **Normas para todo el mundo** (apartado *Convenciones de todo cambio* de `CONTRIBUTING.md`): una
  PR pequeña por issue, título en Conventional Commits, `Closes #N`, desarrollo con TDD, y CI en
  verde con la aprobación de una persona distinta del autor para fusionar. Lo último lo impone el
  ruleset de `main` (comprobado: 1 aprobación, solo *squash* e historial lineal).
- **Además, para quien viene de fuera:**
  - Pide la issue antes de trabajar en ella: comenta y espera a que alguien del equipo te la
    asigne, para que no se haga dos veces. Si en una semana no hay avances, vuelve a quedar libre.
  - Trabaja desde un fork. En una primera contribución, la CI espera a que alguien del equipo
    apruebe la ejecución.
- **Plantilla de PR:** incluye ahora el tipo de cambio y las comprobaciones de los clientes. Antes
  solo pedía las del servidor, y alguien que tocara la app no sabía qué ejecutar.

## 5. Código de conducta

**Contributor Covenant 2.1, traducido al español**, con la escala de aplicación de Mozilla
(corrección, advertencia, expulsión temporal y expulsión permanente). Ya estaba en el repositorio.

- **Por qué este:** usar un código conocido da más confianza que redactar normas propias, y la
  escala de aplicación evita improvisar ante el primer conflicto.
- **A quién se denuncia:** antes solo al mantenedor (Dalibex), y si la denuncia era contra él no
  había alternativa. Ahora se puede comunicar de forma privada a cualquiera de tres responsables:
  @Dalibex, mantenedor y responsable del repositorio (aunque no sea un rol de la asignatura),
  @AlejandroCuetoDiaz (Product Owner) y @Lewas88 (Scrum Master). Si la queja afecta a uno de
  ellos, la tratan los otros dos sin él.
- **Sin correo del proyecto, de momento:** con el tamaño actual del proyecto basta con contactar
  con los responsables. Si la comunidad crece, se valorará un canal propio.

## 6. Tres tareas para good first issue

**Criterios:** cada una se hace en menos de 3 horas, no está en el camino crítico del sprint (no
toca las HU urgentes), no pide decisiones de diseño y dice qué archivos tocar y cómo comprobarla.

| # | Tarea | Por qué es buena para empezar | Archivos y comprobación |
| --- | --- | --- | --- |
| 1 | **Pantalla de créditos en la app móvil** con las atribuciones de terceros | Hace falta antes de la primera release (lo pide la licencia del icono, según `docs/atribuciones.md`, y la marca el Equipo B como riesgo 2) y es aislada: una pantalla, un enlace y su prueba. Si nadie de fuera la coge en dos semanas, la hace el equipo | Nueva pantalla en `frontend/apps/mobile/src/`, enlazada desde la bienvenida. Una prueba con Testing Library como `PantallaBienvenida.test.tsx`; `pnpm test` en verde |
| 2 | **Probar la guía de arranque desde cero** y corregir lo que falle | Quien llega de nuevo es quien mejor ve lo que falta en la guía, y no hace falta conocer el código | `docs/development.md`. Se anota cuánto tarda el primer arranque y qué paso falló |
| 3 | **Documentar los endpoints que ya existen** (registro, inicio de sesión, teselas, mapa de calor) con un ejemplo de petición y respuesta | Hoy no hay documentación de la API, y se aprende el servidor leyéndolo, sin modificarlo | Nuevo `docs/api.md` a partir de los controladores de `backend/src/main/java/es/uma/weather/`. Cada ejemplo se comprueba contra el servidor arrancado |

Están por crear en GitHub con las labels `good first issue` y `documentation` o `tarea`, según
corresponda.

## 7. Riesgos

| # | Riesgo | Qué puede pasar | Mitigación |
| --- | --- | --- | --- |
| 1 | **El icono de Flaticon parece MIT** | Alguien lo reutiliza como si fuera MIT e incumple su licencia | Aclarar en `LICENSE` y en `docs/atribuciones.md` que queda fuera, y quitar de `docs/atribuciones.md` la frase de que WEather «no se publica»; comprobar si su licencia gratuita permite tenerlo en un repositorio público y, si no, sustituirlo |
| 2 | **El arranque es largo** | JDK 25, Docker, Node.js 24, pnpm y una clave `JWT_SECRET`: si tarda más de 5 minutos, mucha gente se va antes de empezar (Tema 4). Además, el arranque rápido del README fallaba sin `JWT_SECRET` | El README ya incluye la clave en el arranque rápido. La *good first issue* 2 mide el tiempo y corrige la guía |

## 8. Acciones pendientes

| Acción | Momento | Quién |
| --- | --- | --- |
| Revisar y fusionar la rama `docs/p9-preparacion-comunidad`, después de la que pasa el ConOps y la Práctica 3-6 a Markdown: hay que actualizar los enlaces del README, que todavía apuntan a `docs/PDF Entregados/` | Antes de publicar | Equipo C, con revisión del equipo |
| Resolver la licencia del icono de Flaticon | Antes de publicar | Equipo C |
| Crear las tres *good first issue* | Primera publicación | Equipo C |
| Traducir al español las descripciones de las labels de GitHub que siguen en inglés (`enhancement`, `good first issue`, `help wanted`...) y corregir la descripción del repositorio | Primera publicación | Equipo C |
