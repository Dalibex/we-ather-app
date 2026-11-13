# Fuentes abiertas y estrategia de actualización

## 1. Objetivo

Este documento define las fuentes de datos abiertas utilizadas por WEather, el procedimiento de obtención de los datos y la frecuencia de actualización propuesta para el mapa de exposición al calor de Málaga.

## 2. Fuentes de datos

### 2.1. Open-Meteo

- **Servicio:** Open-Meteo Forecast API.
- **URL:** https://api.open-meteo.com/v1/forecast
- **Tipo de petición:** HTTP GET.
- **Datos obtenidos:**
  - Temperatura a 2 metros.
  - Humedad relativa.
  - Velocidad del viento a 10 metros.
  - Radiación solar de onda corta.
  - Índice ultravioleta (UV).
- **Cobertura inicial:** cinco puntos geográficos de Málaga:
  - Centro.
  - Teatinos.
  - El Palo.
  - Carretera de Cádiz.
  - Ciudad Jardín.
- **Almacenamiento:** los datos se guardan en PostgreSQL/PostGIS, junto con la fecha de observación y la puntuación y nivel de exposición al calor calculados por el backend.
- **Frecuencia propuesta:** una actualización cada hora.

La consulta utiliza la zona horaria `Europe/Madrid`. Cada punto se consulta mediante una petición independiente.

### 2.2. OpenStreetMap mediante Overpass API

- **Fuente de datos:** OpenStreetMap.
- **Servicio de consulta:** Overpass API.
- **URL:** https://overpass-api.de/api/interpreter
- **Tipo de petición:** HTTP POST.
- **Datos obtenidos:** segmentos de calles, identificadores de vías, geometrías y etiquetas disponibles, como el tipo de vía y su nombre.
- **Cobertura inicial:** área de Málaga delimitada por el recuadro geográfico configurado en la aplicación.
- **Almacenamiento:** los segmentos se guardan en PostgreSQL/PostGIS como geometrías espaciales.
- **Frecuencia propuesta:** una actualización cada siete días.

Se propone una frecuencia menor que la meteorológica porque los datos de calles cambian menos habitualmente y Overpass es un servicio compartido.

## 3. Procedimiento de obtención

El backend implementa dos servicios de ingesta:

- `WeatherIngestionService`: consulta Open-Meteo para los puntos configurados y almacena las observaciones meteorológicas.
- `UrbanDataIngestionService`: consulta Overpass para obtener las geometrías de las calles y almacenarlas en PostGIS.

La ejecución se realiza actualmente mediante estos endpoints:

| Endpoint | Fuente |
|---|---|
| `POST /api/ingestion/weather` | Open-Meteo |
| `POST /api/ingestion/urban` | OpenStreetMap / Overpass |

Ambos endpoints requieren autenticación.

## 4. Frecuencia y planificación

Se establecen las siguientes frecuencias objetivo:

| Fuente | Frecuencia propuesta | Motivo |
|---|---|---|
| Open-Meteo | Cada hora | Los datos meteorológicos pueden variar durante el día. |
| OpenStreetMap / Overpass | Cada siete días | La información de las vías cambia con menor frecuencia. |

**Estado actual:** las ingestas están implementadas y pueden ejecutarse manualmente mediante los endpoints indicados. La ejecución periódica automática todavía no está implementada.

La incorporación de un planificador automático queda pendiente de aprobación y planificación en una iteración posterior. Las frecuencias de actualización han sido consensuadas con el Product Owner: una hora para los datos meteorológicos y siete días para los datos urbanos.

## 5. Gestión de errores y uso responsable

- Las peticiones a fuentes externas deben contemplar errores de conexión, respuestas HTTP no satisfactorias y respuestas con formato no válido.
- Se deben evitar consultas innecesarias a Overpass, al tratarse de un servicio compartido.
- Las consultas deben limitarse al área geográfica y a las variables necesarias para la aplicación.
- Los errores de ingesta deben quedar registrados para facilitar su diagnóstico.
- Se debe evitar que una respuesta externa incompleta o inválida se almacene como si fuera información correcta.

## 6. Licencias y atribución

- **Open-Meteo:** se deben respetar sus condiciones de uso y los requisitos de atribución aplicables.
- **OpenStreetMap:** los datos están sujetos a la licencia Open Database License (ODbL), por lo que se deben respetar sus condiciones de uso y atribución.

La aplicación debe mostrar la atribución correspondiente cuando utilice o presente datos procedentes de OpenStreetMap.

## 7. Limitaciones conocidas

- La cobertura meteorológica inicial se basa en cinco puntos de Málaga, por lo que no representa mediciones continuas en toda la ciudad.
- Los datos meteorológicos proceden de Open-Meteo y no de estaciones de medición propias de WEather.
- Los datos urbanos dependen de los elementos disponibles en OpenStreetMap.
- La información de calles obtenida mediante Overpass no garantiza por sí sola la disponibilidad de datos sobre árboles, sombra o zonas de refugio.
- Las frecuencias propuestas no se ejecutan automáticamente en la implementación actual.