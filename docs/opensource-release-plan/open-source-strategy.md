# Equipo A · Estrategia y alcance de la apertura Open Source (WEather)

Revisión sobre `main` (58231d9, 24/11/2026) y el historial de todas las ramas.

## 1. Objetivo de abrir el proyecto

El objetivo de publicar WEather como proyecto Open Source es permitir que otras personas puedan utilizarlo, estudiar su funcionamiento y contribuir a su desarrollo.

WEather es una aplicación móvil para que los peatones de Málaga lleguen a su destino con la menor exposición al calor posible. El usuario escribe un origen y un destino y recibe tres rutas (la más corta, la más fresca y una equilibrada) con indicaciones en texto. El servidor estima el calor de cada calle con datos abiertos de mapas y meteorología y con los reportes validados de los usuarios; ese mapa de calor es interno y no se muestra (ConOps, secciones 1 y 5).

La apertura del proyecto busca alcanzar tres beneficios principales:

- **Ampliar el desarrollo:** incorporar mejoras y correcciones de desarrolladores externos.
- **Mejorar la calidad:** aprovechar las contribuciones de la comunidad para detectar errores y mejorar la aplicación.
- **Facilitar la continuidad:** reducir la dependencia del equipo original y permitir que otras personas puedan mantener y desarrollar el software.

La publicación se plantea como una oportunidad para convertir un proyecto académico en un software que pueda evolucionar mediante la colaboración de una comunidad externa.

## 2. Componentes que se publicarán

Se propone publicar el código fuente y la documentación necesarios para comprender, instalar y utilizar WEather.

| Componentes | Decisión |
|---|---|
| Backend desarrollado con Spring Boot. | Publicar. |
| Aplicación móvil desarrollada con Expo y React Native. | Publicar. |
| Panel de administración web desarrollado con React y Vite (hoy, un esqueleto). | Publicar. |
| Tests, migraciones de base de datos y configuración de arranque (Docker Compose, `.env.example`). | Publicar. |
| Documentación técnica, fuentes de datos y atribuciones. | Publicar. |
| ConOps y Práctica 3-6 (`docs/ConOps.md`, `docs/Practica_3-6_Gestion.md`). | Publicar: el MoSCoW del ConOps fija el alcance de la 1.0 y el triage de propuestas (Equipo C) depende de él. |
| Credenciales, tokens, contraseñas y claves privadas. | Excluir. |
| Datos personales de usuarios y configuraciones con información sensible. | Excluir. |
| Grabaciones de las reuniones Scrum. | Excluir. No están en el repositorio: `docs/dailys/README.md` solo explica cómo pedir acceso. |

Se publicarán los componentes necesarios para que una persona externa pueda ejecutar el proyecto y comprender su arquitectura.

Los servicios externos de los que depende WEather se documentarán, indicando sus condiciones de uso y las limitaciones que puedan afectar a la aplicación. No se publicarán credenciales de servicios ni datos privados.

Las grabaciones de las reuniones Scrum no se consideran necesarias para utilizar o desarrollar el software y siguen fuera del repositorio. Los documentos de la asignatura que definen el producto (ConOps y Práctica 3-6) sí se publican, porque explican el alcance y el proceso que debe seguir quien contribuya.

**Justificación:** publicar el código necesario para utilizar y modificar WEather facilita la colaboración y la reutilización del software. Excluir información sensible protege al equipo y evita publicar contenido que no aporta valor a los usuarios externos.

## 3. Usuarios y colaboradores potenciales

Se identifican tres perfiles principales:

- **Ciudadanos y peatones de Málaga:** personas que quieren elegir rutas a pie con menos calor.
- **Desarrolladores externos:** personas interesadas en mejorar el backend, la aplicación móvil, el panel web o el procesamiento de datos meteorológicos y urbanos.
- **Estudiantes e investigadores:** personas interesadas en proyectos relacionados con movilidad sostenible, meteorología, sistemas de información geográfica o desarrollo de aplicaciones.

Se dará prioridad a atraer desarrolladores externos que puedan contribuir con correcciones de errores, mejoras en la documentación y pequeñas funcionalidades.

También se considera interesante atraer a personas con conocimientos de meteorología y movilidad urbana, ya que pueden aportar mejoras en el tratamiento de los datos y en la interpretación de la información sobre el calor.

La comunidad inicial será pequeña y estará orientada a mejorar el software existente antes de ampliar su alcance geográfico o funcional.

## 4. Madurez actual del proyecto

WEather se encuentra en una **fase inicial de desarrollo, con una base técnica consolidada pero sin una versión pública estable**.

El proyecto cuenta con una arquitectura organizada en backend, aplicación móvil y aplicación web. También dispone de documentación técnica, licencia MIT, tests automatizados e integración continua.

La preparación técnica incluye 196 casos de prueba de backend y 117 de la aplicación móvil. Además, existen comprobaciones automáticas para revisar el formato, los tests y la construcción de los componentes del proyecto.

Sin embargo, todavía existen funcionalidades y aspectos técnicos que deben mejorarse antes de una publicación estable:

- El cálculo de rutas, la función principal, aún no está construido: hoy funcionan el registro, el inicio de sesión, el mapa base y la generación interna del mapa de calor (*Estado actual* del `README.md`). Los reportes y la moderación tampoco existen todavía.
- El proceso de instalación local necesita simplificarse y documentarse mejor.
- Deben revisarse las condiciones de uso de los servicios externos de mapas y meteorología.
- La aplicación web no dispone todavía de tests automatizados.
- Es necesario reforzar las comprobaciones de integración entre la aplicación y el servidor.

**Decisión:** se considera viable preparar una primera publicación Open Source, pero no presentar WEather como una aplicación terminada. Se publicará como una versión inicial de desarrollo, indicando sus funcionalidades disponibles y sus limitaciones conocidas.

Antes de anunciar una versión estable, será necesario resolver los riesgos técnicos identificados y comprobar que una persona externa puede ejecutar el proyecto siguiendo la documentación.

## 5. Dificultades para usuarios y colaboradores externos

Se identifican las siguientes dificultades:

| Dificultad | Consecuencia | Medida propuesta |
|---|---|---|
| Instalación local compleja y configuración repartida entre distintos archivos. | Los nuevos colaboradores pueden no conseguir ejecutar el proyecto. | Unificar la configuración de ejemplo y actualizar las instrucciones de instalación. |
| Dependencia de servicios externos de mapas y meteorología. | La aplicación puede dejar de funcionar si se incumplen sus condiciones de uso o se alcanzan sus límites. | Documentar las condiciones de uso y seleccionar proveedores adecuados para una publicación pública. |
| Arquitectura dividida en backend, aplicación móvil y aplicación web. | Los colaboradores pueden necesitar tiempo para comprender cómo se comunican los componentes. | Documentar la arquitectura y explicar cómo ejecutar cada componente. |
| Cobertura de pruebas incompleta en algunos componentes. | Un cambio puede introducir errores sin que las comprobaciones automáticas los detecten. | Ampliar los tests de la aplicación web y comprobar los flujos principales de la aplicación. |

Estas dificultades se abordarán antes de intentar atraer una comunidad amplia.

## 6. Tres prioridades para que la apertura tenga éxito

### Prioridad 1. Garantizar una publicación segura y legalmente viable

Revisar el código y el historial del repositorio para detectar secretos, comprobar las licencias de las dependencias y verificar que los componentes publicados pueden distribuirse legalmente.

También será necesario revisar las condiciones de uso de los servicios externos de mapas y meteorología, ya que pueden afectar directamente al funcionamiento de WEather.

Esta prioridad es fundamental porque un problema de seguridad o de licencias puede comprometer la publicación y afectar a los usuarios.

### Prioridad 2. Facilitar que una persona externa pueda utilizar WEather

Simplificar el proceso de instalación y configuración, actualizar la documentación y comprobar que una persona externa puede ejecutar el proyecto sin depender de recursos privados del equipo.

Se priorizará que el backend, la aplicación móvil y la aplicación web puedan ejecutarse siguiendo las instrucciones publicadas.

Esta prioridad permitirá que WEather sea útil para los usuarios y no se limite a ser un repositorio de código.

### Prioridad 3. Facilitar las contribuciones y la continuidad

Aprovechar la infraestructura existente de GitHub para facilitar la recepción de errores y propuestas de mejora.

Se prepararán tareas iniciales relacionadas con la documentación, los tests y pequeñas mejoras de los componentes del proyecto. También se explicará cómo ejecutar las pruebas y cómo proponer cambios.

Esta prioridad permitirá reducir la dependencia del equipo original y facilitar la incorporación de nuevos colaboradores.

## Conclusión

La apertura de WEather se plantea como un proceso gradual. El proyecto dispone de una base técnica suficiente para preparar su publicación, pero todavía debe resolver algunos problemas de instalación, dependencias externas y comprobaciones automatizadas.

El éxito de la apertura no se medirá únicamente por la disponibilidad pública del código, sino por la capacidad de WEather para atraer usuarios, aceptar contribuciones externas y mantenerse en el tiempo.