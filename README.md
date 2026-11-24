# 🌡️ WEather

[![CI](https://github.com/Dalibex/we-ather-app/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Dalibex/we-ather-app/actions/workflows/ci.yml)
[![Licencia](https://img.shields.io/github/license/Dalibex/we-ather-app)](LICENSE)

WEather es una app móvil para moverte por Málaga a pie con el menor calor posible. Escribes un
origen y un destino y recibes tres rutas (la más corta, la más fresca y una equilibrada) con su
distancia, tiempo y exposición al calor estimada, e indicaciones paso a paso.

Las estimaciones combinan datos abiertos de mapas y meteorología con los reportes de los propios
usuarios, que cuentan cómo perciben el calor en cada punto para ayudar a los demás.

Proyecto del grupo NeoForge, desarrollado en la Universidad de Málaga.

> [!NOTE]
> **En desarrollo.** Todavía no hay versión publicada y el cálculo de rutas aún no está construido.
> Lo que ya funciona está en [Estado actual](#-estado-actual).

## 📑 Contenido

- **[Estado actual](#-estado-actual)**: qué hace hoy cada parte.
- **[Limitaciones](#-limitaciones)**: lo que no incluye la versión 1.0.
- **[Puesta en marcha](#-puesta-en-marcha)**: el servidor y los clientes en tu equipo.
- **[Documentación](#-documentación)**: todos los documentos del proyecto, según lo que buscas.
- **[Contribuir](#-contribuir)**: cómo informar de un error, proponer algo o enviar un cambio.
- **[Licencia](#-licencia)**: MIT.

## 📦 Estado actual

El repositorio tiene su base (documentación, plantillas e integración continua) y el esqueleto de
cada parte. Ya incorpora algunas funcionalidades, pero todavía no implementa todas las historias de
usuario.

| Parte | Carpeta | Estado |
| --- | --- | --- |
| Servidor | `backend/` | Spring Boot con PostgreSQL y PostGIS: health check, teselas del mapa base de Málaga con caché e ingesta de los datos del mapa de calor (Open-Meteo cada hora, OpenStreetMap semanal), generación horaria del mapa de calor interno por zona, lectura de ese mapa para el motor de rutas y registro e inicio de sesión de usuarios con autenticación por token JWT |
| App móvil | `frontend/apps/mobile/` | Expo: bienvenida, registro con validaciones, inicio y cierre de sesión con el servidor y mapa base de Málaga, al que se llega sin cuenta; las teselas de OpenStreetMap las pide al servidor |
| Panel de administración | `frontend/apps/web/` | Esqueleto: Vite y React con una pantalla inicial |

Las historias de usuario ya planificadas están en las
[issues](https://github.com/Dalibex/we-ather-app/issues?q=is%3Aissue+label%3Ahistoria), cada una
con el milestone de su sprint. Una HU sigue abierta hasta que el Product Owner comprueba sus
criterios de aceptación, aunque ya esté construida. El resto del alcance de la versión 1.0 está en
el MoSCoW del ConOps y se planifica en los siguientes sprints.

## 🚧 Limitaciones

El alcance de la versión 1.0 es lo que el MoSCoW del
[ConOps](docs/PDF%20Entregados/DocumentoConOps.pdf) marca como *Must*, *Should* o *Could*. Lo que
marca como *Won't* queda fuera de ella y se podrá plantear para versiones posteriores:

- **Solo Málaga.** El mapa, los datos y las rutas se limitan al municipio.
- **Sin GPS.** El origen y el destino se escriben; la app no usa tu ubicación en tiempo real.
- **Indicaciones en texto.** Las rutas y el mapa de calor no se pintan sobre el mapa: el mapa de
  calor es interno y solo lo usa el cálculo de rutas.

Además, la exposición al calor es una estimación hecha con datos abiertos y reportes de usuarios, no
una medida en el lugar.

## ⚡ Puesta en marcha

Necesitas JDK 25, Docker, Node.js 24 y pnpm. Los pasos completos están en
[docs/development.md](docs/development.md); en resumen:

```bash
# Servidor. JWT_SECRET es una clave Base64 de al menos 32 bytes (openssl rand -base64 32)
cd backend && cp .env.example .env && JWT_SECRET=<clave> ./mvnw spring-boot:run

# Clientes, en otra terminal desde la raíz
cd frontend && pnpm install && pnpm dev:web
```

En PowerShell, define antes la clave con `$env:JWT_SECRET = "<clave>"` o ponla en la configuración
de ejecución del IDE. No va en `.env`, que solo lee Docker Compose para PostgreSQL.

## 📚 Documentación

### 🧠 Entender el proyecto

- [ConOps](docs/PDF%20Entregados/DocumentoConOps.pdf): propósito, actores, objetivos y alcance
  (MoSCoW).
- [Diagrama Tropos](docs/ESQUEMA_TROPOS.png): dependencias entre actores y objetivos.
- [Práctica 3-6: Gestión](docs/PDF%20Entregados/Pr%C3%A1ctica%203-6%20Gesti%C3%B3n.pdf):
  priorización, planificación del Sprint 1, métricas y proceso Scrum.
- [Fuentes de datos](docs/fuentes-datos.md): de dónde salen los datos del mapa de calor.

### 🛠 Desarrollar

- [Desarrollo local](docs/development.md): requisitos, arranque y comprobaciones.
- [Changelog](CHANGELOG.md): cambios de cada versión.

### 🔒 Privacidad y terceros

- [Política de privacidad](docs/privacidad.md): qué datos trata la app, durante cuánto tiempo y
  cómo ejercer tus derechos.
- [Atribuciones](docs/atribuciones.md): recursos de terceros que usa la app y su autoría.

### 👥 Equipo

- [Grabaciones de los Scrum Meetings](docs/dailys/README.md): dónde están y cómo pedir acceso.

## 🤝 Contribuir

Cualquier persona puede informar de un error, proponer una funcionalidad o enviar un cambio. La
[guía de contribución](CONTRIBUTING.md) explica cómo; si buscas por dónde empezar, mira las issues
marcadas como
[good first issue](https://github.com/Dalibex/we-ather-app/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22).

- [Código de conducta](CODE_OF_CONDUCT.md): lo aceptan todas las personas que participan.
- [Política de seguridad](SECURITY.md): cómo informar de una vulnerabilidad sin hacerla pública.

## 📄 Licencia

El código se publica con la licencia [MIT](LICENSE). Los recursos y datos de terceros, como el
icono de la app o los datos de OpenStreetMap y Open-Meteo, mantienen su propia licencia, como
explican [Atribuciones](docs/atribuciones.md) y
[Fuentes de datos](docs/fuentes-datos.md#6-licencias-y-atribución).
