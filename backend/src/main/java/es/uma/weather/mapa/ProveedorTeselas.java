package es.uma.weather.mapa;

import java.io.IOException;

/** Servicio externo del que se descargan las teselas del mapa base. */
@FunctionalInterface
interface ProveedorTeselas {

    /**
     * Descarga la imagen PNG de la tesela.
     *
     * @throws IOException si el proveedor no responde o no devuelve la imagen
     */
    byte[] descargar(Tesela tesela) throws IOException;
}
