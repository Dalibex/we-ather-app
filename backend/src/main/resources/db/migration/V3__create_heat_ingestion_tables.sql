-- Observaciones meteorológicas obtenidas de fuentes abiertas y su índice de exposición.
CREATE TABLE weather_observations (
    id BIGSERIAL PRIMARY KEY,
    source VARCHAR(40) NOT NULL,
    grid_name VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL CHECK (latitude BETWEEN -90 AND 90),
    longitude DOUBLE PRECISION NOT NULL CHECK (longitude BETWEEN -180 AND 180),
    observed_at TIMESTAMPTZ NOT NULL,
    temperature_c DOUBLE PRECISION NOT NULL,
    relative_humidity_pct DOUBLE PRECISION NOT NULL CHECK (relative_humidity_pct BETWEEN 0 AND 100),
    wind_speed_kmh DOUBLE PRECISION NOT NULL CHECK (wind_speed_kmh >= 0),
    shortwave_radiation_wm2 DOUBLE PRECISION NOT NULL CHECK (shortwave_radiation_wm2 >= 0),
    uv_index DOUBLE PRECISION NOT NULL DEFAULT 0 CHECK (uv_index >= 0),
    heat_score DOUBLE PRECISION NOT NULL CHECK (heat_score BETWEEN 0 AND 100),
    heat_level VARCHAR(20) NOT NULL,
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    ingested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_weather_observation UNIQUE (source, latitude, longitude, observed_at)
);
CREATE INDEX idx_weather_observations_location ON weather_observations USING GIST (location);
CREATE INDEX idx_weather_observations_observed_at ON weather_observations (observed_at DESC);

-- Tramos de calle descargados de OpenStreetMap (ODbL; conservar atribución en el producto).
CREATE TABLE street_segments (
    osm_way_id BIGINT PRIMARY KEY,
    highway VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    geom GEOMETRY(LINESTRING, 4326) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_street_segments_geom ON street_segments USING GIST (geom);
CREATE INDEX idx_street_segments_highway ON street_segments (highway);
