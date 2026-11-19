-- Mapa de calor interno por zona (T-17.4, HU-17): la última generación, que sustituye entera a la
-- anterior. generated_at es cuándo se calculó; observed_at, de cuándo es el dato meteorológico usado.
CREATE TABLE zone_heat (
    zone_name VARCHAR(100) PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL CHECK (latitude BETWEEN -90 AND 90),
    longitude DOUBLE PRECISION NOT NULL CHECK (longitude BETWEEN -180 AND 180),
    heat_score DOUBLE PRECISION NOT NULL CHECK (heat_score BETWEEN 0 AND 100),
    observed_at TIMESTAMPTZ NOT NULL,
    generated_at TIMESTAMPTZ NOT NULL
);
