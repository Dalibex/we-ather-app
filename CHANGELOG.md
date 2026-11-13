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
- App móvil: el mapa muestra la atribución de todos los proveedores de datos (OpenStreetMap,
  Open-Meteo, AEMET y Ayuntamiento de Málaga), cada una enlazada a su licencia o a su web
  (T-20.2).
- Servidor: límite de peticiones por proveedor de datos externo, configurado en
  `weather.proveedores.limites` de `application.yaml` (T-20.2).
