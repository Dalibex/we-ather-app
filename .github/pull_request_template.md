## Qué cambia y por qué

Escríbelo como texto normal, no dentro de un bloque de código, para que GitHub cierre la issue al
fusionar.

Closes #

## Tipo de cambio

- [ ] Funcionalidad nueva
- [ ] Corrección de un error
- [ ] Refactorización
- [ ] Documentación
- [ ] Pruebas
- [ ] Mantenimiento (dependencias, CI, configuración)

## Cómo se ha probado

Las comprobaciones de las partes que has cambiado (ver `docs/development.md`):

- Servidor, desde `backend/`: `./mvnw verify` (incluye las pruebas de integración con
  PostgreSQL/PostGIS mediante Testcontainers, que necesitan Docker).
- Clientes, desde `frontend/`: `pnpm format:check`, `pnpm lint`, `pnpm typecheck`, `pnpm test` y
  `pnpm build`.
- `git diff --check`.

## Definition of Done

- [ ] Desarrollada con TDD: las pruebas nuevas están en verde en local.
- [ ] Los criterios de aceptación que cubre tienen una prueba asociada.
- [ ] La documentación afectada y el `CHANGELOG.md` están actualizados, o no hace falta.
- [ ] Solo incluye cambios de esta issue, sin secretos ni datos personales reales.
