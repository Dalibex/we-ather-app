package es.uma.weather.heatmap.service;

import java.time.Instant;
import java.util.List;

/**
 * El último mapa de calor interno, tal como lo consulta el motor de rutas.
 *
 * @param zones las zonas del mapa, con su índice de exposición (0-100); al menos una
 * @param generatedAt cuándo se generó el mapa
 * @param observedAt de cuándo es el dato meteorológico más antiguo de sus zonas: dice la edad del
 *     mapa aunque la ingesta lleve horas fallando
 */
public record HeatmapSnapshot(List<ZoneHeat> zones, Instant generatedAt, Instant observedAt) {

    public HeatmapSnapshot {
        if (zones.isEmpty()) {
            throw new IllegalArgumentException("El mapa de calor necesita al menos una zona");
        }
        zones = List.copyOf(zones);
    }

    /** Índice de exposición (0-100) de un punto: el de su zona más cercana. */
    public double heatAt(double latitude, double longitude) {
        return zones.get(NearestZone.indexOf(zones, latitude, longitude)).heatScore();
    }
}
