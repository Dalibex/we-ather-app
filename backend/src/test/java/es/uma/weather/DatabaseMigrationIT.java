package es.uma.weather;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/** El esquema solo lo construye Flyway, sobre una base de datos PostGIS vacía. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class DatabaseMigrationIT {

    @Autowired private Flyway flyway;

    @Autowired private JdbcTemplate jdbc;

    @Test
    void aplicaTodasLasMigracionesSinPendientesNiFallidas() {
        var info = flyway.info();

        assertThat(info.applied())
                .isNotEmpty()
                .allMatch(migration -> migration.getState().isApplied());
        assertThat(info.pending()).isEmpty();
    }

    @Test
    void activaPostgis() {
        var extensions =
                jdbc.queryForList(
                        "SELECT extname FROM pg_extension WHERE extname = 'postgis'", String.class);

        assertThat(extensions).containsExactly("postgis");
    }
}
