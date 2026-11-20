# Política de privacidad de WEather

Última actualización: 15 de noviembre de 2026.

WEather te ayuda a moverte a pie por Málaga con el menor calor posible. Queremos que la uses sin
preocuparte por tus datos: pedimos solo lo imprescindible, y nadie puede saber quién ha hecho un
reporte ni qué ruta ha pedido.

Para buscar rutas no necesitas cuenta. La cuenta solo hace falta para enviar reportes sobre el calor.

**WEather está en desarrollo.** Hoy la app solo envía al servidor tu correo y tu contraseña cuando
creas la cuenta o inicias sesión, y todavía no permite enviar reportes. Las medidas de los apartados 3, 7 y 9 (la relación cifrada entre reportes y
cuentas, los registros de acceso, las copias de seguridad y el bloqueo de las cuentas dadas de baja)
se están construyendo y estarán funcionando antes de que la app guarde esos datos.

## 1. Quién trata tus datos

WEather es un proyecto académico del grupo NeoForge, desarrollado en la Universidad de Málaga.

- **Responsable:** grupo NeoForge, equipo de desarrollo de WEather.
- **Contacto para cualquier cuestión de privacidad:** [cueto@uma.es](mailto:cueto@uma.es).

<!-- Contacto provisional: sustituir por un correo del grupo NeoForge cuando exista. -->

## 2. Qué datos tratamos y para qué

| Dato | Para qué lo usamos | Cuándo lo recogemos |
| --- | --- | --- |
| Correo electrónico | Identificar tu cuenta y que puedas iniciar sesión | Al registrarte |
| Contraseña | Que puedas iniciar sesión. Solo guardamos una huella cifrada (hash), nunca la contraseña | Al registrarte |
| Token de sesión | Que no tengas que escribir la contraseña cada vez que abres la app. Lo guarda tu móvil, cifrado; no lleva tu correo, caduca a las 24 horas y se borra al cerrar sesión | Al iniciar sesión |
| Fecha en que confirmaste tener 14 años o más y haber leído esta política | Poder demostrar que se te informó antes de crear la cuenta | Al registrarte |
| Reportes: valoración de 1 a 5, comentario de texto o de voz, punto del mapa y hora | Mejorar la estimación del calor de cada zona | Cuando envías un reporte |
| Origen y destino que escribes | Calcular las rutas. No se asocian a tu cuenta ni se guardan con ella | En cada búsqueda |
| Registros de acceso (fecha, acción realizada y dirección IP). Nunca guardan el origen ni el destino de tus búsquedas, para que nadie pueda saber qué ruta pediste | Proteger el servicio, detectar abusos y poder investigar incidentes | Cuando usas la app |

**Lo que no recogemos:** tu ubicación en tiempo real ni el GPS (el origen y el destino los escribes
tú), tu nombre, tu teléfono, tus contactos ni tus fotos. El micrófono solo se usa si decides enviar
un reporte de voz.

## 3. Cómo protegemos tus reportes

Tus reportes se guardan **seudonimizados**: no llevan tu correo ni ningún dato que te identifique,
sino un código interno distinto del de tu cuenta.

- La relación entre ese código y tu cuenta se guarda aparte, cifrada, y solo se consulta para dos
  cosas: limitar cuántos reportes puede enviar cada cuenta por hora y que los administradores puedan
  bloquear una cuenta que publica contenido falso o inapropiado. Cada consulta queda registrada.
- Los demás usuarios nunca ven quién hizo un reporte ni a qué hora exacta: los reportes se muestran
  agrupados por zona y franja horaria.
- Solo los reportes validados (con un límite por usuario y hora y contrastados con los datos
  meteorológicos) influyen en el cálculo de las rutas, y solo durante un tiempo limitado.
- Los reportes dudosos los revisa un administrador, que puede retirarlos. Más adelante, una
  herramienta de inteligencia artificial ayudará a detectarlos; antes de activarla se indicará aquí
  qué servicio se usa.

Que un dato esté seudonimizado no significa que deje de ser tuyo: sigues teniendo sobre tus reportes
todos los derechos del apartado 8.

## 4. Por qué podemos tratar tus datos

- **Tu cuenta y tus reportes:** porque son necesarios para prestarte el servicio que pides al
  registrarte (artículo 6.1.b del RGPD).
- **Los registros de acceso y las copias de seguridad:** por nuestro interés legítimo en mantener el
  servicio seguro y disponible (artículo 6.1.f del RGPD).
- **El historial de rutas, si la app lo ofrece:** solo si tú lo activas (apartado 6).

## 5. Con quién se comparte información

No vendemos tus datos ni los usamos para publicidad. Para funcionar, WEather usa estos servicios
externos de datos abiertos:

| Servicio | Qué recibe | Por qué |
| --- | --- | --- |
| OpenStreetMap (imágenes del mapa) | Nada sobre ti: nuestro servidor descarga las imágenes del mapa de Málaga, las guarda y se las sirve a la app, así que OpenStreetMap no ve tu dirección IP | Mostrarte el mapa de Málaga |
| Nominatim (OpenStreetMap) | El texto del origen y el destino, enviado por nuestro servidor, sin ningún dato de tu cuenta | Convertir lo que escribes en una posición del mapa |
| Open-Meteo, AEMET y Ayuntamiento de Málaga | Nada sobre ti: solo les pedimos datos meteorológicos y urbanos | Estimar el calor de cada calle |

Las rutas las calcula nuestro propio motor de rutas (OSRM), alojado por WEather, así que no se
envían a ningún tercero. La Fundación OpenStreetMap tiene su sede en Reino Unido. Si en el futuro se
añade un servicio para transcribir los reportes de voz, se indicará aquí antes de usarlo.

## 6. Historial de rutas

Si la app ofrece guardar las rutas que consultas, será **opcional** y el historial se guardará
**solo en tu dispositivo**: nunca se envía a nuestro servidor y solo tú puedes verlo. Puedes borrarlo
cuando quieras.

## 7. Cuánto tiempo guardamos tus datos

| Dato | Cuánto tiempo |
| --- | --- |
| Cuenta (correo, hash de la contraseña y fecha de esa confirmación) | Mientras la cuenta esté activa |
| Reportes | Mientras la cuenta esté activa, seudonimizados. Solo influyen en las rutas durante un tiempo limitado |
| Registros de acceso | 1 año |
| Copias de seguridad | 30 días; después se sustituyen por otras nuevas |

**Si das de baja tu cuenta** (o la bloquea un administrador), no se borra en el momento: queda
**deshabilitada y bloqueada** durante **3 años**, el plazo en que prescriben las posibles
responsabilidades (artículos 32 y 72 de la LOPDGDD). Durante
ese tiempo nadie puede usarla ni consultarla, salvo que la pidan jueces, tribunales o autoridades
para atender posibles responsabilidades. Pasados los 3 años, se borra.

Al dar de baja la cuenta, tus reportes se desvinculan de ella para siempre: se rompe la relación con
el código interno y pasan a ser anónimos, así que nadie, ni siquiera nosotros, puede volver a saber
que eran tuyos.

## 8. Tus derechos

Puedes pedirnos en cualquier momento:

- **Acceso:** saber qué datos tuyos tenemos.
- **Rectificación:** corregir los que estén mal.
- **Supresión:** que eliminemos tus datos (con el bloqueo del apartado 7).
- **Oposición y limitación:** que dejemos de usarlos, o que los usemos solo para ciertas cosas.
- **Portabilidad:** recibirlos en un formato que puedas llevarte a otro servicio.

Por ahora se ejercen escribiendo al correo de contacto del apartado 1. Te contestaremos en un mes
como máximo. Si crees que no hemos tratado bien tus datos, puedes reclamar ante la
[Agencia Española de Protección de Datos](https://www.aepd.es).

## 9. Seguridad

- Todas las comunicaciones entre la app y el servidor van cifradas (HTTPS).
- Los datos personales se guardan cifrados y las contraseñas, solo como hash.
- Se hacen copias de seguridad periódicas y quedan registrados los accesos y los cambios de datos,
  tanto de los usuarios como de los administradores.

## 10. Edad mínima

Para crear una cuenta tienes que tener **14 años o más**. Es la edad a partir de la cual la ley
española permite que un menor decida por sí mismo sobre sus datos (artículo 7 de la LOPDGDD), y la
usamos como edad mínima para tener cuenta.

## 11. Cambios en esta política

Si cambiamos esta política, actualizaremos la fecha de arriba. Si el cambio afecta a cómo usamos tus
datos, te lo avisaremos en la app y te pediremos que la vuelvas a leer antes de seguir usando tu
cuenta.
