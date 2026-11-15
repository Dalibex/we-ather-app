package es.uma.weather.shared.proveedores;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Límites de peticiones de cada proveedor externo ({@code weather.proveedores.limites} en
 * application.yaml).
 *
 * @param limites límite de cada proveedor, por su nombre
 */
@ConfigurationProperties("weather.proveedores")
public record LimitesProveedoresProperties(Map<String, Limite> limites) {

    public LimitesProveedoresProperties {
        limites = limites == null ? Map.of() : Map.copyOf(limites);
    }
}
