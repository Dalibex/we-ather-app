# Atribuciones

Recursos de terceros que usa WEather y la atribución que exige su licencia.

| Recurso | Dónde se usa | Autoría y licencia |
| --- | --- | --- |
| Icono de biznaga (`frontend/apps/mobile/assets/icono-biznaga-malaga.png`) | Fondo de las pantallas de bienvenida, registro e inicio de sesión | Diseñado por Magnific, de [Flaticon](https://www.flaticon.es/icono-gratis/biznaga_9595546), con su licencia gratuita, que exige atribución |

La atribución del icono solo está en este documento porque WEather es un proyecto académico que no
se publica. Si la app llegara a publicarse, habría que mostrarla también dentro de la app, por
ejemplo en una pantalla de créditos, como pide la licencia gratuita de Flaticon.

La atribución de los proveedores de datos (OpenStreetMap, Open-Meteo, AEMET y Ayuntamiento de
Málaga) se muestra en la propia app, sobre el mapa.

La búsqueda de lugares (`GET /api/lugares`) usa [Nominatim](https://nominatim.org/), el buscador de
OpenStreetMap. Sus resultados son datos de OpenStreetMap, © colaboradores de OpenStreetMap, con
licencia [ODbL](https://www.openstreetmap.org/copyright), así que los cubre la misma atribución de
OpenStreetMap que ya muestra el mapa. El servidor respeta su
[política de uso](https://operations.osmfoundation.org/policies/nominatim/): se identifica con su
propio `User-Agent`, hace como máximo una petición por segundo, guarda los resultados para no
repetir la misma búsqueda y solo busca cuando el usuario confirma el texto, no mientras escribe.
