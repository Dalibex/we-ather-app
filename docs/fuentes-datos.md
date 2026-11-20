# Fuentes abiertas y estrategia de actualización

## 1. Objetivo

Este documento define las fuentes de datos abiertas utilizadas por WEather, el procedimiento de obtención de los datos y la frecuencia de actualización acordada para el mapa de exposición al calor de Málaga.

## 2. Fuentes de datos

### 2.1. Open-Meteo

- **Servicio:** Open-Meteo Forecast API.
- **URL:** https://api.open-meteo.com/v1/forecast
- **Tipo de petición:** HTTP GET.
- **Datos obtenidos:**
  - Temperatura a 2 metros.
  - Humedad relativa.
  - Velocidad del viento a 10 metros.
  - Nubosidad total (%).
  - Radiación solar de onda corta.
  - Índice ultravioleta (UV).
- **Cobertura inicial:** cinco puntos geográficos de Málaga:
  - Centro.
  - Teatinos.
  - El Palo.
  - Carretera de Cádiz.
  - Ciudad Jardín.
- **Almacenamiento:** los datos se guardan en PostgreSQL/PostGIS, junto con la fecha de observación y la puntuación y nivel de exposición al calor calculados por el backend.
- **Frecuencia:** una actualización cada hora.

La consulta utiliza la zona horaria `Europe/Madrid`. Cada punto se consulta mediante una petición independiente.

### 2.2. OpenStreetMap mediante Overpass API

- **Fuente de datos:** OpenStreetMap.
- **Servicio de consulta:** Overpass API.
- **URL:** https://overpass-api.de/api/interpreter
- **Tipo de petición:** HTTP POST.
- **Datos obtenidos:** segmentos de calles, identificadores de vías, geometrías y etiquetas disponibles, como el tipo de vía y su nombre.
- **Cobertura inicial:** área de Málaga delimitada por el recuadro geográfico configurado en la aplicación.
- **Almacenamiento:** los segmentos se guardan en PostgreSQL/PostGIS como geometrías espaciales.
- **Frecuencia:** una actualización cada siete días.

La nubosidad (`cloud_cover`) se guarda con cada observación, pero no entra en el cálculo de la puntuación de exposición: ese cálculo usa la radiación solar de onda corta (`shortwave_radiation`), que ya refleja el efecto de las nubes sobre la radiación que llega a la superficie.

### 2.3. Reportes de los usuarios

- **Fuente:** los reportes que envían los usuarios registrados, con una valoración de sensación térmica de 1 (fresco) a 5 (mucho calor) sobre un punto de Málaga.
- **Qué reportes cuentan:** solo los validados y vigentes. El algoritmo de combinación los recibe sin autor, identificador ni hora.
- **Combinación** (`ReportCombiner`, T-17.3):
  - Cada reporte cuenta en la zona más cercana de las que recibe el algoritmo (con T-17.4, los puntos de `weather.heat.points`). En caso de empate, cuenta en la primera.
  - Su valoración se pasa a la escala 0-100 del índice de exposición: `s = (valoración − 1) · 25`.
  - El calor de la zona se ajusta con una media ponderada, redondeada a un decimal: `(k · estimación + Σ s) / (k + n)`. Aquí `n` es el número de reportes de la zona y `k` (`weather.reportes.peso-estimacion`, fijado a 5 en `application.yaml`) indica cuántos reportes equivalentes vale la estimación meteorológica. Un reporte aislado no arrastra la zona; muchos reportes coherentes sí la mueven. Con `k` igual o menor que 0 (o si falta la propiedad), la aplicación no arranca.
- **Aplicación:** la generación periódica del mapa de calor (T-17.4, sección 4) pide los reportes a `ValidatedReportSource`. Mientras no existan, su implementación (`EmptyValidatedReportSource`) no devuelve ninguno y el mapa refleja solo la estimación meteorológica.
- **Pendiente:** guardar los reportes (T-12.2) y filtrar los validados y vigentes (T-19.2), que sustituye a `EmptyValidatedReportSource` por la fuente real. Como el mapa se regenera cada hora, un reporte puede seguir influyendo hasta una hora después de caducar, y uno nuevo tarda hasta una hora en notarse. El sentido de la escala, el reparto por zona más cercana y el valor de `k` están pendientes de confirmación por el Product Owner.

## 3. Procedimiento de obtención

El backend implementa dos servicios de ingesta:

- `WeatherIngestionService`: consulta Open-Meteo para los puntos configurados y almacena las observaciones meteorológicas.
- `UrbanDataIngestionService`: consulta Overpass para obtener las geometrías de las calles y almacenarlas en PostGIS.

Además de su ejecución periódica (sección 4), se pueden lanzar a mano con estos endpoints:

| Endpoint | Fuente |
|---|---|
| `POST /api/ingestion/weather` | Open-Meteo |
| `POST /api/ingestion/urban` | OpenStreetMap / Overpass |

Ambos endpoints requieren autenticación.

## 4. Frecuencia y planificación

Frecuencias acordadas con el Product Owner:

| Fuente | Frecuencia | Motivo |
|---|---|---|
| Open-Meteo | Cada hora | Los datos meteorológicos pueden variar durante el día. |
| OpenStreetMap / Overpass | Cada siete días | La información de las vías cambia con menor frecuencia. |

Las tareas periódicas se ejecutan con expresiones cron de `application.yaml`, en hora de `Europe/Madrid`:

| Tarea | Cron | Qué hace |
|---|---|---|
| Ingesta meteorológica | `0 0 * * * *` (`weather.heat.refresco-cron`) | `RefrescoMeteorologico` lanza la ingesta de Open-Meteo de todos los puntos cada hora en punto (T-20.1, HU-20). Las peticiones de los usuarios nunca llaman a Open-Meteo: `GET /api/heatmap` lee las observaciones guardadas |
| Generación del mapa de calor | `0 15 * * * *` (`weather.heat.schedule.generation`) | `HeatmapScheduler` toma la última observación de Open-Meteo de cada punto de `weather.heat.points`, la ajusta con los reportes (sección 2.3) y sustituye entera la tabla `zone_heat` en una transacción. Si ningún punto tiene observación, conserva el mapa anterior |
| Ingesta urbana | `0 0 4 * * MON` (`weather.heat.schedule.urban`) | `HeatmapScheduler` vuelve a cargar las calles de Overpass cada lunes. Spring no admite «cada siete días» en un cron |

- El mapa se genera en el minuto 15 para usar la observación de la hora en punto.
- Un cron no recupera las ejecuciones que se pierden con el servidor apagado. Por eso, al arrancar, si no hay calles guardadas o la última se actualizó hace más de siete días, se cargan una vez.
- `zone_heat` es el mapa de calor interno, una fila por zona. Guarda dos fechas: `generated_at` (cuándo se calculó) y `observed_at` (de cuándo es el dato meteorológico usado). Si la ingesta falla durante horas, el mapa se sigue generando con el último dato válido, y `observed_at` dice su antigüedad. El motor de rutas también usa el último mapa, sea cual sea su antigüedad (sección 8).
- Con el valor `-`, una tarea queda desactivada, y la carga de calles al arrancar también. Las pruebas usan ese valor (`src/test/resources/config/application.yaml`).
- Se supone una sola instancia del servidor: con varias, cada una ejecutaría las tareas por su cuenta.

## 5. Gestión de errores y uso responsable

- En Open-Meteo se exige que temperatura, humedad, viento, nubosidad, radiación, UV, hora de observación y desfase UTC estén presentes y tengan el tipo esperado. Los campos numéricos ausentes o nulos provocan el rechazo de ese punto; no se convierten implícitamente en `0.0`.
- La ingesta meteorológica procesa cada punto de forma independiente: si una respuesta falla o está incompleta, no se guarda esa observación y se conserva la última observación válida ya almacenada, que es la que sigue devolviendo `GET /api/heatmap`.
- La ingesta urbana sustituye las calles enteras o no cambia nada. Overpass responde `200` aunque la consulta se quede sin tiempo o sin memoria, y lo indica con el campo `remark`; esa respuesta incompleta se descarta, igual que una sin ninguna vía válida, y se conservan las calles anteriores. Una respuesta completa se guarda en una sola transacción, que también retira las vías que ya no están en OpenStreetMap; si algo falla, se deshace y quedan las calles anteriores.
- Antes de cada petición, ambos servicios consultan `LimitadorPeticiones`, configurado en `weather.proveedores.limites`. Si se alcanza el límite de Open-Meteo, se omite ese punto; si se alcanza el de Overpass, no se solicita la actualización urbana.
- Overpass tiene un límite de dos peticiones por día, acordado con el Product Owner: con la carga semanal deja margen para un reintento si Overpass corta la consulta.
- Las peticiones a ambos proveedores se identifican con el `User-Agent` de WEather (`weather.heat.user-agent`) y tienen un tiempo máximo de conexión (10 s) y de lectura (90 s, porque la consulta de Overpass puede tardar hasta 60 s), configurados en `spring.http.clients`. Una petición que los supera cuenta como un error de ese punto o de esa actualización.
- Los errores por punto quedan registrados en el log. Las respuestas HTTP no satisfactorias o no procesables no deben generar observaciones válidas.
- Los servicios devuelven el número de registros guardados. Los endpoints manuales responden `200` con estado `completed` si guardan al menos un registro y `503` con estado `failed` si no se guarda ninguno.
- Las consultas se limitan al área geográfica y a las variables necesarias para la aplicación.

## 6. Licencias y atribución

- **Open-Meteo:** se deben respetar sus condiciones de uso y los requisitos de atribución aplicables.
- **OpenStreetMap:** los datos están sujetos a la licencia Open Database License (ODbL), por lo que se deben respetar sus condiciones de uso y atribución.

La aplicación debe mostrar la atribución correspondiente cuando utilice o presente datos procedentes de OpenStreetMap.

## 7. Limitaciones conocidas

- La cobertura meteorológica inicial se basa en cinco puntos de Málaga, por lo que no representa mediciones continuas en toda la ciudad.
- Los datos meteorológicos proceden de Open-Meteo y no de estaciones de medición propias de WEather.
- Los datos urbanos dependen de los elementos disponibles en OpenStreetMap.
- La información de calles obtenida mediante Overpass no garantiza por sí sola la disponibilidad de datos sobre árboles, sombra o zonas de refugio.
- Cada zona es un punto de `weather.heat.points`, y cada reporte cuenta en la más cercana, sin distancia máxima. Si una zona no tiene observación, sus reportes pasan a la zona vecina.

## 8. Uso del mapa de calor en el motor de rutas

OSRM calcula las rutas sin tener en cuenta el calor; el servidor calcula después la exposición de cada alternativa con el mapa de calor interno (HU-08). Para ello lo lee con `HeatmapReader` (T-17.5):

- `latest()` devuelve el último mapa guardado en `zone_heat` (`HeatmapSnapshot`): sus zonas con su índice de exposición (0-100), `generatedAt` (cuándo se generó) y `observedAt` (la observación meteorológica más antigua de sus zonas, que dice su edad). Si todavía no se ha generado ningún mapa, no devuelve nada, y el cálculo de rutas tiene que tratar ese caso.
- `HeatmapSnapshot.heatAt(lat, lon)` da el calor de un punto: el de su zona más cercana, con la misma regla que los reportes (sección 2.3).
- Nunca llama a los proveedores en la petición del usuario: solo lee la tabla.
- No hay antigüedad máxima: con la ingesta caída se usa el último mapa válido, igual que en la generación (sección 4). Si hiciera falta avisar al usuario de un dato viejo, la información está en `observedAt`.
