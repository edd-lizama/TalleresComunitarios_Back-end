-- Tabla de este servicio (PostgreSQL). Requiere que ya exista PERFIL (la crea service-auth-usuarios).
-- Cambios respecto al MER:
--   * IDs BIGINT con identity (mismo criterio que el servicio de autenticación; en Java son Long).
--   * altura en CENTÍMETROS y peso en KILOGRAMOS (el MER no definía la unidad).
--   * indice_masa_corporal lo calcula el servicio (peso / altura^2); se guarda porque el MER lo incluye.

CREATE TABLE IF NOT EXISTS registro_biometrico (
    id_biometrico         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_perfil             BIGINT NOT NULL REFERENCES perfil(id_perfil),
    fecha_medicion        DATE NOT NULL,
    altura                NUMERIC(5,2) NOT NULL CHECK (altura > 0),
    peso                  NUMERIC(5,2) NOT NULL CHECK (peso > 0),
    indice_masa_corporal  NUMERIC(4,1) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_registro_biometrico_perfil_fecha
    ON registro_biometrico (id_perfil, fecha_medicion DESC);
