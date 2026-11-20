package es.uma.weather.heatmap.service;

import java.util.List;

/** Busca la zona más cercana a un punto: la regla que usan los reportes y el motor de rutas. */
final class NearestZone {

    private NearestZone() {}

    /** Índice de la zona más cercana (en un empate, la primera), o -1 si no hay ninguna. */
    static int indexOf(List<ZoneHeat> zones, double latitude, double longitude) {
        // Aproximación equirrectangular: suficiente para ordenar puntos a escala de ciudad.
        double cosLat = Math.cos(Math.toRadians(latitude));
        int nearest = -1;
        double best = Double.MAX_VALUE;
        for (int i = 0; i < zones.size(); i++) {
            double dLat = zones.get(i).latitude() - latitude;
            double dLon = (zones.get(i).longitude() - longitude) * cosLat;
            double distance = dLat * dLat + dLon * dLon;
            if (distance < best) {
                best = distance;
                nearest = i;
            }
        }
        return nearest;
    }
}
