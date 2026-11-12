# Desarrollo local

Cómo arrancar WEather en tu máquina y qué comprobar antes de abrir una pull request. Los mismos
comandos son los que ejecuta la integración continua (`.github/workflows/ci.yml`).

## Requisitos

| Herramienta | Versión | Para qué |
| --- | --- | --- |
| JDK | 25 | Servidor (`backend/`). Maven no hace falta: se usa el wrapper `./mvnw` |
| Docker | Docker Desktop o Docker Engine con Compose | Base de datos local y pruebas de integración (Testcontainers) |
| Node.js | 24 (está en `frontend/.nvmrc`) | Clientes (`frontend/`) |
| pnpm | La que fija `packageManager` en `frontend/package.json` | Clientes. Con `corepack enable` se usa esa versión automáticamente |
| Git | Con `sh` disponible (Git for Windows lo trae) | El hook `pre-commit` |

## Estructura

| Carpeta | Contenido |
| --- | --- |
| `backend/` | Servidor: Spring Boot, PostgreSQL con PostGIS y migraciones Flyway |
| `frontend/apps/mobile/` | App móvil (Expo y React Native) |
| `frontend/apps/web/` | Panel de administración (Vite y React) |
| `.githooks/` | Hook `pre-commit` que formatea los archivos del commit |

## Servidor

```bash
cd backend
cp .env.example .env   # y cambia la contraseña; .env nunca se versiona
./mvnw spring-boot:run
```

Al arrancar, Spring Boot levanta el contenedor de PostgreSQL definido en `docker-compose.yaml` con
los valores de `.env` y aplica las migraciones de `src/main/resources/db/migration`. El estado del
servidor está en <http://localhost:8080/actuator/health>. Son públicos el health check y las teselas
del mapa (`/api/v1/teselas/{z}/{x}/{y}.png`); el resto de rutas pide autenticación.

Las teselas se descargan de OpenStreetMap la primera vez y se guardan en `backend/.cache/teselas`
(no se versiona; se cambia con la variable `WEATHER_TESELAS_CACHE`). Solo se sirven las que cubren
Málaga. El proveedor, el `User-Agent` con el que se identifica el servidor y la caducidad están en
`weather.teselas` de `application.yaml`.

Comprobaciones:

| Comando | Qué hace |
| --- | --- |
| `./mvnw verify` | Todo lo que comprueba la CI: formato (Spotless), pruebas unitarias y de integración, SpotBugs y cobertura mínima del 70 % (JaCoCo) |
| `./mvnw spotless:apply` | Formatea el código Java (google-java-format, estilo AOSP) |
| `./mvnw test` | Solo las pruebas unitarias, sin Docker ni comprobación de cobertura |

Las pruebas unitarias se llaman `*Test` y las de integración `*IT`. Las de integración arrancan su
propia base de datos con Testcontainers, así que Docker tiene que estar en marcha.

## Clientes

```bash
cd frontend
pnpm install
pnpm dev:web      # panel de administración en http://localhost:5173
pnpm dev:mobile   # app móvil con Expo (Expo Go o un emulador)
```

La app móvil pide las teselas del mapa al servidor, así que este tiene que estar arrancado. Su
dirección se configura en `frontend/apps/mobile/.env` (copia `.env.example`), porque en un móvil
`localhost` es el propio móvil:

| Dónde se ejecuta la app | `EXPO_PUBLIC_API_URL` |
| --- | --- |
| Móvil real con Expo Go | La IP del ordenador en la wifi, por ejemplo `http://192.168.1.20:8080` |
| Emulador de Android | `http://10.0.2.2:8080` |
| Simulador de iOS | `http://localhost:8080` (el valor por defecto) |

Tras cambiar `.env`, reinicia Expo.

Comprobaciones, las mismas que el job `frontend` de la CI:

| Comando | Qué hace |
| --- | --- |
| `pnpm format:check` | Comprueba el formato con Prettier (`pnpm format` lo corrige) |
| `pnpm lint` | ESLint |
| `pnpm typecheck` | TypeScript en las dos apps |
| `pnpm test` | Pruebas de las apps que las tengan (hoy, la app móvil con Jest) |
| `pnpm build` | Compila las dos apps |

## Hook pre-commit

`.githooks/pre-commit` formatea los archivos Java (Spotless) y de los clientes (Prettier) que vas a
incluir en el commit y los añade ya formateados. Se instala solo la primera vez que ejecutas
cualquier comando de `./mvnw` (configura `git config core.hooksPath .githooks`). Para instalarlo sin
Maven:

```bash
git config core.hooksPath .githooks
```

Si un archivo tiene a la vez cambios preparados y sin preparar, el hook para el commit para no
mezclarlos. Para saltarlo en un commit concreto: `SKIP_FORMAT=1 git commit ...`. La CI sigue
comprobando el formato igualmente.
