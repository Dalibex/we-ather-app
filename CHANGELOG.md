# Changelog

Los cambios relevantes del proyecto se documentan en este archivo.

El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y el proyecto usa
[Versionado Semántico](https://semver.org/lang/es/).

## [Unreleased]

### Añadido

- Base del repositorio: README, guía de contribución, código de conducta, política de seguridad,
  instrucciones para agentes (`AGENTS.md`), plantillas de issue y pull request, y configuración
  común (`.gitignore`, `.gitattributes`, `.editorconfig`).
- Integración continua: comprobación del título de las pull requests y jobs `lint`, `test`,
  `frontend` y `build`, que se saltan sus pasos mientras no exista su parte del proyecto.
- Dependabot: una PR semanal con las actualizaciones de las GitHub Actions.
- Documentación de la asignatura (ConOps, diagrama Tropos y Práctica 3-6) en `docs/`, enlazada desde
  el README, la guía de contribución y `AGENTS.md`.
- Guía de contribución con los roles, los eventos Scrum, los acuerdos del equipo, la relación entre
  ClickUp y GitHub y la estrategia de pruebas.
- Esqueleto del proyecto: servidor Spring Boot con PostgreSQL, PostGIS y Flyway en `backend/`, app
  móvil (Expo) y panel de administración (Vite y React) en `frontend/`, y hook `pre-commit` que
  formatea el código. El arranque está en `docs/development.md`.
- Dependabot vigila también las dependencias de Maven y de pnpm.
- App móvil: la pantalla principal muestra el mapa base de Málaga con teselas de OpenStreetMap y su
  atribución (T-05.2). La app móvil tiene ya pruebas con Jest.
- Servidor: `GET /api/v1/teselas/{z}/{x}/{y}.png`, público, sirve las teselas de OpenStreetMap que
  cubren Málaga. Las descarga identificándose, las guarda en disco y, si OpenStreetMap falla, sirve
  la última copia válida. La app móvil pide las teselas al servidor (`EXPO_PUBLIC_API_URL`), porque
  OpenStreetMap bloquea las peticiones directas desde Android (T-05.2).
- App móvil: la atribución de OpenStreetMap y el logo del mapa se colocan por encima de los botones
  de navegación del sistema, para que se lean y se puedan pulsar (T-05.2).
- App móvil: guarda localmente las teselas; al volver a solicitar una que tenga más de siete días,
  la muestra mientras intenta actualizarla. Errores HTTP no pisan teselas válidas y Android limita
  los reintentos automáticos de fallos de red (T-05.4).
- App móvil: el mapa se limita a Málaga. No se puede alejar más allá de lo que ocupa la ciudad y,
  si se arrastra fuera, vuelve al borde de Málaga al soltarlo (T-05.3).
- App móvil: el mapa muestra la atribución de todos los proveedores de datos (OpenStreetMap,
  Open-Meteo, AEMET y Ayuntamiento de Málaga), cada una enlazada a su licencia o a su web
  (T-20.2).
- Servidor: límite de peticiones por proveedor de datos externo, configurado en
  `weather.proveedores.limites` de `application.yaml` (T-20.2).
- Servidor: ingesta manual de los datos del mapa de calor. `POST /api/ingestion/weather` guarda el
  tiempo de cinco puntos de Málaga (Open-Meteo) y `POST /api/ingestion/urban` guarda las calles
  (OpenStreetMap, Overpass), las dos con autenticación. `GET /api/heatmap` devuelve la última
  observación de cada punto. Fuentes y frecuencias en `docs/fuentes-datos.md` (T-17.1).
- Servidor: la ingesta meteorológica guarda también la nubosidad (`cloud_cover`) de cada punto.
  Las peticiones a Open-Meteo y Overpass se identifican con el `User-Agent` de WEather y tienen un
  tiempo máximo de espera (`spring.http.clients` en `application.yaml`), para que un proveedor que
  no responde no deje la ingesta colgada (T-17.2).
- Servidor: mapa de calor interno por zona en la tabla `zone_heat`. Se genera cada hora con la
  última observación de Open-Meteo de cada punto, ajustada con los reportes validados (todavía no
  hay ninguno). Las calles de Overpass se recargan cada semana, y al arrancar si faltan o tienen más
  de siete días. Las frecuencias se configuran en `weather.heat.schedule` (T-17.3, T-17.4).
- App móvil: la app abre en una pantalla de bienvenida con «Iniciar sesión», «Crear cuenta» y
  «Continuar sin cuenta», que lleva al mapa. Desde el mapa, un botón pequeño abre el inicio de sesión
  (T-01.1).
- App móvil: pantalla de registro con correo, contraseña y la confirmación de tener 14 años o más y
  haber leído la política de privacidad, con un enlace para leerla. Todavía no valida ni envía los
  datos (HU-01, T-01.1).
- App móvil: el registro valida el formulario al pulsar «Crear cuenta»: un correo con formato
  correcto, una contraseña de al menos 8 caracteres y la casilla de edad y política marcada. Los
  errores aparecen bajo cada campo, el lector de pantalla los anuncia y desaparecen al corregir el
  campo. Si el correo ya está registrado lo dirá el servidor (HU-01, T-01.3).
- App móvil: pantalla de inicio de sesión con correo y contraseña. Todavía no autentica ni envía las
  credenciales al servidor (HU-02, T-02.1).
- App móvil: el registro y el inicio de sesión se enlazan entre sí y conservan el correo escrito al
  pasar de una a otra; el botón o gesto «atrás» de Android vuelve a la pantalla anterior; el
  formulario se desplaza si no cabe y se aparta del teclado.
- Política de privacidad en `docs/privacidad.md`: datos que se tratan, seudonimización de los
  reportes, servicios externos (OpenStreetMap no recibe la IP del usuario, porque el servidor sirve
  las teselas), plazos de conservación y bloqueo, derechos y edad mínima.
- Atribuciones de los recursos de terceros en `docs/atribuciones.md`.
