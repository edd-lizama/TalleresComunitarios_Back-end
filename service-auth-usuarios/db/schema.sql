-- Esquema mínimo que necesita el microservicio de autenticación (PostgreSQL).
-- Basado en el MER: ROL, PERMISO, ROL_PERMISO, USUARIO y PERFIL.
-- Cambios respecto al MER:
--   * IDs en BIGINT con identity (en vez de NUMERIC) para que Hibernate (ddl-auto: validate) los acepte como Long.
--   * USUARIO incluye apellido, edad, genero, cargo, centro_salud y activo (los usa el frontend y la entidad Usuario).
--     Conviene agregarlos también al MER.

CREATE TABLE IF NOT EXISTS rol (
    id_rol      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_rol  VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS permiso (
    id_permiso      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_permiso  VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS rol_permiso (
    id_rol      INTEGER NOT NULL REFERENCES rol(id_rol),
    id_permiso  INTEGER NOT NULL REFERENCES permiso(id_permiso),
    PRIMARY KEY (id_rol, id_permiso)
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_rol          INTEGER NOT NULL REFERENCES rol(id_rol),
    rut             VARCHAR(255) NOT NULL UNIQUE,
    nombre          VARCHAR(255) NOT NULL,
    apellido        VARCHAR(255),
    edad            INTEGER,
    genero          VARCHAR(50),
    cargo           VARCHAR(255),
    centro_salud    VARCHAR(255),
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    es_de_la_comuna BOOLEAN,
    correo          VARCHAR(255) NOT NULL UNIQUE,
    telefono        VARCHAR(255),
    direccion       VARCHAR(255),
    password        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS perfil (
    id_perfil            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario           INTEGER NOT NULL REFERENCES usuario(id_usuario),
    tipo_perfil          VARCHAR(255),
    consentimiento_foto  BOOLEAN NOT NULL DEFAULT FALSE
);

-- Roles iniciales (el registro público usa app.auth.default-role = VECINO)
INSERT INTO rol (nombre_rol) VALUES ('VECINO'), ('MONITOR'), ('ADMIN')
ON CONFLICT (nombre_rol) DO NOTHING;
