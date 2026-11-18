package es.uma.weather.lugares;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Busca lugares en Nominatim, el buscador de OpenStreetMap. Su política de uso
 * (https://operations.osmfoundation.org/policies/nominatim/) exige que cada aplicación se
 * identifique con un {@code User-Agent} propio.
 */
@Component
class NominatimProveedorLugares implements ProveedorLugares {

    private static final Duration ESPERA_MAXIMA = Duration.ofSeconds(10);

    private final HttpClient cliente =
            HttpClient.newBuilder().connectTimeout(ESPERA_MAXIMA).build();
    private final String url;
    private final String userAgent;
    private final int maxResultados;
    private final JsonMapper json;

    NominatimProveedorLugares(
            @Value("${weather.lugares.url}") String url,
            @Value("${weather.lugares.user-agent}") String userAgent,
            @Value("${weather.lugares.max-resultados}") int maxResultados,
            JsonMapper json) {
        this.url = url;
        this.userAgent = userAgent;
        this.maxResultados = maxResultados;
        this.json = json;
    }

    @Override
    public List<Lugar> buscar(String texto) throws IOException {
        // Los mensajes de error no llevan la URL: incluye el texto buscado (docs/privacidad.md).
        URI uri =
                URI.create(
                        url
                                + "?q="
                                + URLEncoder.encode(texto, StandardCharsets.UTF_8)
                                + "&format=jsonv2&limit="
                                + maxResultados);
        HttpRequest peticion =
                HttpRequest.newBuilder(uri)
                        .header("User-Agent", userAgent)
                        .header("Accept-Language", "es")
                        .timeout(ESPERA_MAXIMA)
                        .build();
        HttpResponse<String> respuesta;
        try {
            respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Búsqueda en Nominatim interrumpida", e);
        }
        if (respuesta.statusCode() != 200) {
            throw new IOException("Nominatim respondió " + respuesta.statusCode());
        }
        return leerLugares(respuesta.body());
    }

    private List<Lugar> leerLugares(String cuerpo) throws IOException {
        JsonNode raiz;
        try {
            raiz = json.readTree(cuerpo);
        } catch (JacksonException e) {
            throw new IOException("Nominatim no ha devuelto un JSON válido", e);
        }
        if (!raiz.isArray()) {
            throw new IOException("Nominatim no ha devuelto una lista de lugares");
        }
        List<Lugar> lugares = new ArrayList<>();
        for (JsonNode lugar : raiz) {
            lugares.add(leerLugar(lugar));
        }
        return List.copyOf(lugares);
    }

    private static Lugar leerLugar(JsonNode lugar) throws IOException {
        JsonNode nombre = lugar.path("display_name");
        if (!nombre.isString()) {
            throw new IOException("Nominatim ha devuelto un lugar sin nombre");
        }
        // Nominatim devuelve las coordenadas como texto.
        try {
            return new Lugar(
                    nombre.asString(),
                    Double.parseDouble(lugar.path("lat").asString()),
                    Double.parseDouble(lugar.path("lon").asString()));
        } catch (NumberFormatException e) {
            throw new IOException("Nominatim ha devuelto un lugar sin coordenadas válidas", e);
        }
    }
}
