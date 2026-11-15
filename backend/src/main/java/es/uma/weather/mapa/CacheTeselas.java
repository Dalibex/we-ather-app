package es.uma.weather.mapa;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Copia en disco de las teselas ya descargadas, en {@code <directorio>/z/x/y.png}. La fecha de
 * modificación del archivo es el momento en que se guardó. Sobrevive a los reinicios del servidor.
 */
@Component
class CacheTeselas {

    private final Path directorio;
    private final Clock reloj;

    @Autowired
    CacheTeselas(@Value("${weather.teselas.directorio-cache}") Path directorio) {
        this(directorio, Clock.systemUTC());
    }

    CacheTeselas(Path directorio, Clock reloj) {
        this.directorio = directorio;
        this.reloj = reloj;
    }

    Optional<byte[]> leer(Tesela tesela) {
        Path archivo = archivo(tesela);
        if (!Files.isRegularFile(archivo)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllBytes(archivo));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Optional<Instant> guardadaEn(Tesela tesela) {
        Path archivo = archivo(tesela);
        if (!Files.isRegularFile(archivo)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.getLastModifiedTime(archivo).toInstant());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    void guardar(Tesela tesela, byte[] imagen) {
        Path carpeta = carpeta(tesela);
        Path archivo = archivo(tesela);
        try {
            Files.createDirectories(carpeta);
            // Se escribe aparte y se mueve, para que nadie lea una tesela a medio escribir.
            Path temporal = Files.createTempFile(carpeta, "tesela", ".tmp");
            Files.write(temporal, imagen);
            Files.setLastModifiedTime(temporal, FileTime.from(reloj.instant()));
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path carpeta(Tesela tesela) {
        return directorio
                .resolve(Integer.toString(tesela.z()))
                .resolve(Integer.toString(tesela.x()));
    }

    private Path archivo(Tesela tesela) {
        return carpeta(tesela).resolve(tesela.y() + ".png");
    }
}
