-- Cuentas de usuario: registro (HU-01) e inicio de sesión (HU-02).
-- El correo se guarda normalizado (sin espacios y en minúsculas, ver Correo.java), así que la
-- restricción única impide registrar dos veces el mismo correo escrito de otra forma.
-- El cifrado del correo y el algoritmo del hash de la contraseña los decide la HU-21.
CREATE TABLE usuarios (
    id                     uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    correo                 varchar(254) NOT NULL,
    hash_contrasena        text         NOT NULL,
    privacidad_aceptada_en timestamptz  NOT NULL,
    creado_en              timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT usuarios_correo_unico UNIQUE (correo)
);

COMMENT ON TABLE usuarios IS 'Cuentas de usuario de la app (HU-01, HU-02)';
COMMENT ON COLUMN usuarios.correo IS 'Correo normalizado: sin espacios y en minúsculas. Máximo 254 caracteres (RFC 5321)';
COMMENT ON COLUMN usuarios.hash_contrasena IS 'Hash de la contraseña, nunca la contraseña en claro';
COMMENT ON COLUMN usuarios.privacidad_aceptada_en IS 'Cuándo aceptó la política de privacidad: prueba del consentimiento (RGPD)';
