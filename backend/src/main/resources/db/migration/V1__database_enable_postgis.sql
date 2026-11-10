-- PostGIS aporta el tipo geography y los índices espaciales para los tramos de calle y los reportes.
-- La imagen postgis/postgis ya trae la extensión; IF NOT EXISTS mantiene esta migración válida allí
-- y en un PostgreSQL normal, donde todavía hay que crearla.
CREATE EXTENSION IF NOT EXISTS postgis;
