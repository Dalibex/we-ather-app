-- Nubosidad (%) de cada observación meteorológica de Open-Meteo, que pide la T-17.2 (HU-04).
-- Las filas anteriores no la tienen: queda vacía en ellas.
ALTER TABLE weather_observations
    ADD COLUMN cloud_cover_pct DOUBLE PRECISION CHECK (cloud_cover_pct BETWEEN 0 AND 100);
