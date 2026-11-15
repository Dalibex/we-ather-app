package es.uma.weather.mapa;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Descarga las teselas de OpenStreetMap. Su política de uso exige que cada aplicación se
 * identifique con un {@code User-Agent} propio; las peticiones sin él se bloquean con un 403.
 */
@Component
class OsmProveedorTeselas implements ProveedorTeselas {

    private static final Duration ESPERA_MAXIMA = Duration.ofSeconds(10);

    private final HttpClient cliente =
            HttpClient.newBuilder().connectTimeout(ESPERA_MAXIMA).build();
    private final String plantillaUrl;
    private final String userAgent;

    OsmProveedorTeselas(
            @Value("${weather.teselas.url}") String plantillaUrl,
            @Value("${weather.teselas.user-agent}") String userAgent) {
        this.plantillaUrl = plantillaUrl;
        this.userAgent = userAgent;
    }

    @Override
    public byte[] descargar(Tesela tesela) throws IOException {
        String url =
                plantillaUrl
                        .replace("{z}", Integer.toString(tesela.z()))
                        .replace("{x}", Integer.toString(tesela.x()))
                        .replace("{y}", Integer.toString(tesela.y()));
        HttpRequest peticion =
                HttpRequest.newBuilder(URI.create(url))
                        .header("User-Agent", userAgent)
                        .timeout(ESPERA_MAXIMA)
                        .build();
        HttpResponse<byte[]> respuesta;
        try {
            respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofByteArray());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Descarga de la tesela interrumpida", e);
        }
        if (respuesta.statusCode() != 200) {
            throw new IOException(
                    "El proveedor de teselas respondió " + respuesta.statusCode() + " a " + url);
        }
        return respuesta.body();
    }
}
