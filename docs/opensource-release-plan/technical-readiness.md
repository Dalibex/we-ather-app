# Equipo B · Preparación técnica y seguridad (WEather)

Revisión sobre `main` (58231d9, 24/11/2026) y el historial de todas las ramas.

## 1. Estructura del repositorio

```
we-ather-app/
├── backend/    Spring Boot 4.1 · Java 25 · Maven · PostgreSQL/PostGIS (Flyway V1–V5)
│   └── es/uma/weather/{heatmap, ingestion, mapa, usuarios, shared}
├── frontend/   monorepo pnpm: apps/mobile (Expo/React Native) · apps/web (Vite/React)
├── docs/       ConOps, Práctica 3-6, desarrollo, fuentes de datos, privacidad, atribuciones, dailys
├── .github/    CI (ci.yml, pr-title.yml), Dependabot, plantillas de issue/PR
└── .githooks/  pre-commit (formato)
```

Monorepo con una carpeta por parte desplegable: backend/ (Spring Boot 4, Java 25, PostgreSQL con
PostGIS y Flyway) y frontend/ (workspace pnpm con apps/mobile en Expo y apps/web en Vite). La raíz
solo guarda lo común: README, LICENSE, CONTRIBUTING, CODE_OF_CONDUCT, SECURITY, CHANGELOG, .github/
y .githooks/. La documentación técnica está en docs/ (development.md, privacidad.md,
fuentes-datos.md, atribuciones.md), junto al ConOps y la Práctica 3-6 en Markdown.

## 2. Secretos y datos sensibles

**Sin secretos reales** en el código ni en el historial.

- Credenciales por variables de entorno (`POSTGRES_PASSWORD`, `JWT_SECRET` sin valor por defecto),
  `.env` ignorado y bloqueado en CI, token móvil en `expo-secure-store`, CI con permisos mínimos y
  sin `secrets`.
- Clave JWT **de pruebas** en `backend/src/test/resources/config/application.yaml`, marcada como tal
  y fuera del `.jar`. Riesgo bajo.

## 3. Dependencias y licencias

**Licencia del proyecto MIT, compatible con todo el árbol.** Inventario completo: 402 paquetes npm
de producción (689 contando también los de desarrollo) y 103 artefactos Maven de ejecución.

- Mayoría MIT / Apache-2.0 / BSD / ISC. Ninguna copyleft fuerte (GPL/AGPL) obligatoria:
  - Logback (EPL-2.0 o LGPL-2.1) y Jakarta EE (EPL-2.0 o GPL-2 con excepción): se usan bajo EPL.
  - `node-forge` (BSD-3 o GPL-2): se elige BSD. `lightningcss` (MPL-2.0): copyleft por fichero.
  - JUnit (EPL) y JNA (LGPL) solo en tests.
- Mantenimiento: Dependabot semanal agrupado.

## 4. Configuración y parámetros

Toda la configuración está en `application.yaml`, con propiedades tipadas
(`@ConfigurationProperties` como records que validan en el constructor) y un `application.yaml` de
test que desactiva las tareas periódicas.

  - Secretos solo por entorno: el servidor no arranca sin `JWT_SECRET` y rechaza claves de menos de
    256 bits.
  - Valores seguros por defecto: `ddl-auto=validate` (Flyway manda), `open-in-view=false`, sesión
    `STATELESS`, Actuator limitado a `health`, Postgres solo en `127.0.0.1`.
  - Variables principales documentadas en `.env.example` y `docs/development.md`.

## 5. Tests, CI/CD y automatización

**CI únicamente; no hay CD por el momento.**

- **Tests:**
  - Backend: 196 casos en 41 ficheros (29 unitarios y 12 de integración con Testcontainers sobre
    PostGIS real).
  - App móvil: 145 casos en 12 ficheros (Jest + Testing Library).
- **CI (GitHub Actions)**, en cada PR y en `main`:
  - `lint`: formato con Spotless, ningún `.env` versionado y Compose válido.
  - `test`: `mvn verify` con SpotBugs y JaCoCo, que exige **un 70 % de líneas cubiertas**.
  - `frontend`: formato, lint, tipos, tests y build.
  - `pr-title`: título en Conventional Commits.
  - CodeQL y Dependabot activos. Las últimas 10 ejecuciones en `main` están en verde y tardan unos 2
    minutos.
- **Ruleset de `main`:** los 5 checks son obligatorios con la rama al día, hace falta 1 aprobación,
  solo se fusiona con squash, con historial lineal y sin force-push.
- **Hook pre-commit:** formatea Java y TypeScript antes de cada commit.

## 6. Tres riesgos técnicos antes de publicar

| # | Riesgo | Impacto si se publica así | Cómo resolverlo |
|---|---|---|---|
| 1 | **Un arranque local que falla y que carga a los proveedores compartidos.** El arranque rápido del `README` de `main` no funciona (falta `JWT_SECRET`), la configuración está repartida entre `.env` y el IDE, y cada instalación nueva descarga Málaga entera de Overpass con el `User-Agent` del proyecto. | La primera impresión de un contribuidor es un error. Si llegan muchos, el proyecto puede acabar bloqueado por Overpass u OSM, y eso afecta también al despliegue del equipo. | El Equipo C ya añade `JWT_SECRET` al arranque rápido en la rama `docs/p9-preparacion-comunidad`. Falta un `.env` único y completo, la carga de calles desactivada por defecto en local y un `User-Agent` propio de cada despliegue. |
| 2 | **Condiciones de uso de servicios y licencias.** Las teselas de `tile.openstreetmap.org` no admiten apps publicadas, la API gratuita de Open-Meteo es solo no comercial, y faltan en la app los avisos de licencias de terceros y la atribución de Flaticon. | Bloqueo del servicio de mapas o del tiempo (la app queda sin función principal) e incumplimiento de licencias. | Proveedor de teselas con plan adecuado, decidir el plan de Open-Meteo (el modelo de sostenibilidad del Equipo D es no comercial, así que la API gratuita vale mientras no se cobre por el servicio) y pantalla de créditos con atribuciones y licencias de terceros (*good first issue* 1 del Equipo C). |
| 3 | **La CI puede dar verde sin probar.** Cada job se salta si no encuentra su parte (`pom.xml`, `package.json`, `Dockerfile`), y el job obligatorio `build` ya pasa sin hacer nada. Además la app web no tiene tests, el frontend no tiene umbral de cobertura y no hay tests end-to-end ni de carga. | Un cambio que rompe la integración app-servidor o el rendimiento llega a `main` con todos los checks en verde. | Que cada job falle si falta su parte, umbral de cobertura en Jest, tests de la web y una prueba end-to-end del flujo principal (registro, sesión y mapa). |
