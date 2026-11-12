# WEather

WEather es una app móvil para moverte por Málaga a pie con el menor calor posible. Escribes un
origen y un destino y recibes tres rutas (la más corta, la más fresca y una equilibrada) con su
distancia, tiempo y exposición al calor estimada, e indicaciones paso a paso.

Las estimaciones combinan datos abiertos de mapas y meteorología con los reportes de los propios
usuarios, que cuentan cómo perciben el calor en cada punto para ayudar a los demás.

Proyecto del grupo NeoForge.

## Estado actual

El proyecto está en su fase inicial: el repositorio tiene su base (documentación, plantillas e
integración continua) y el esqueleto de cada parte, que arranca y pasa la CI pero todavía no
implementa ninguna historia de usuario. No hay versión publicada.

| Parte | Carpeta | Estado |
| --- | --- | --- |
| Servidor | `backend/` | Esqueleto: Spring Boot con PostgreSQL y PostGIS, solo el health check |
| App móvil | `frontend/apps/mobile/` | Expo: pantalla inicial y pantalla de registro (sin validaciones ni conexión con el servidor) |
| Panel de administración | `frontend/apps/web/` | Esqueleto: Vite y React con una pantalla inicial |

## Puesta en marcha

Necesitas JDK 25, Docker, Node.js 24 y pnpm. Los pasos completos están en
[docs/development.md](docs/development.md); en resumen:

```bash
# Servidor
cd backend && cp .env.example .env && ./mvnw spring-boot:run

# Clientes, en otra terminal desde la raíz
cd frontend && pnpm install && pnpm dev:web
```

## Documentación

- [ConOps](docs/PDF%20Entregados/DocumentoConOps.pdf): propósito, actores, objetivos y alcance
  (MoSCoW).
- [Diagrama Tropos](docs/ESQUEMA_TROPOS.png): dependencias entre actores y objetivos.
- [Práctica 3-6: Gestión](docs/PDF%20Entregados/Pr%C3%A1ctica%203-6%20Gesti%C3%B3n.pdf):
  priorización, planificación del Sprint 1, métricas y proceso Scrum.
- [Política de privacidad](docs/privacidad.md): qué datos trata la app, durante cuánto tiempo y
  cómo ejercer tus derechos.
- [Desarrollo local](docs/development.md): requisitos, arranque y comprobaciones.
- [Grabaciones de los Scrum Meetings](docs/dailys/README.md): dónde están y cómo pedir acceso.

## Contribuir

- [Guía de contribución](CONTRIBUTING.md): roles, eventos, ramas, commits, issues y pull requests.
- [Código de conducta](CODE_OF_CONDUCT.md).
- [Política de seguridad](SECURITY.md): cómo informar de una vulnerabilidad.
- [Changelog](CHANGELOG.md): cambios de cada versión.

## Licencia

[MIT](LICENSE).
