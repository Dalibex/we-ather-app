package es.uma.weather.mapa;

/**
 * Una tesela del mapa base en el esquema estándar de OpenStreetMap: nivel de zoom {@code z} y
 * posición {@code x}, {@code y} dentro de la cuadrícula de 2^z por 2^z de ese nivel.
 */
record Tesela(int z, int x, int y) {

    /** Último nivel de zoom que sirve tile.openstreetmap.org. */
    static final int ZOOM_MAXIMO = 19;

    // Rectángulo que contiene el término municipal de Málaga, con margen.
    private static final double MALAGA_OESTE = -4.65;
    private static final double MALAGA_ESTE = -4.25;
    private static final double MALAGA_SUR = 36.60;
    private static final double MALAGA_NORTE = 36.85;

    Tesela {
        if (z < 0 || z > ZOOM_MAXIMO) {
            throw new IllegalArgumentException("Zoom fuera de rango: " + z);
        }
        int lado = 1 << z;
        if (x < 0 || x >= lado || y < 0 || y >= lado) {
            throw new IllegalArgumentException(
                    "La tesela %d/%d no existe en el zoom %d".formatted(x, y, z));
        }
    }

    /** Si la tesela cubre alguna parte de Málaga; las demás no se sirven. */
    boolean cortaMalaga() {
        return longitud(x) < MALAGA_ESTE
                && longitud(x + 1) > MALAGA_OESTE
                && latitud(y) > MALAGA_SUR
                && latitud(y + 1) < MALAGA_NORTE;
    }

    /** Longitud del borde oeste de la columna {@code columna}. */
    private double longitud(int columna) {
        return columna / (double) (1 << z) * 360.0 - 180.0;
    }

    /** Latitud del borde norte de la fila {@code fila} (proyección Web Mercator). */
    private double latitud(int fila) {
        double n = Math.PI * (1 - 2.0 * fila / (1 << z));
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }
}
