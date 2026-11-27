-- =============================================================================
-- MIGRACIÓN SEGURA E IDEMPOTENTE PARA PORTAL WEB ADMIN (admin-web-service)
-- Base de Datos: talleres_ph_db (PostgreSQL)
-- No modifica ni elimina columnas/tablas existentes (100% compatible con ddl-auto: validate)
-- =============================================================================

-- 1. Columnas adicionales seguras en tablas existentes
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS especialidad VARCHAR(255);
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE taller ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- 2. Tabla satélite de Recintos (sedes municipales / salud)
CREATE TABLE IF NOT EXISTS recinto (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL UNIQUE,
    direccion   VARCHAR(255) NOT NULL,
    lat         DOUBLE PRECISION NOT NULL,
    lng         DOUBLE PRECISION NOT NULL,
    activo      BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. Precarga de los 4 recintos base de Padre Hurtado
INSERT INTO recinto (nombre, direccion, lat, lng, activo) VALUES
    ('Sede Social Los Aromos', 'Calle Los Aromos, Padre Hurtado', -33.5725, -70.8150, TRUE),
    ('Gimnasio Municipal Padre Hurtado', 'Av. San Ignacio, Padre Hurtado', -33.5680, -70.8105, TRUE),
    ('CESFAM Padre Hurtado', 'Av. Camino a Melipilla, Padre Hurtado', -33.5750, -70.8190, TRUE),
    ('Centro Cultural Padre Hurtado', 'Primera Transversal, Padre Hurtado', -33.5710, -70.8130, TRUE)
ON CONFLICT (nombre) DO NOTHING;

-- 4. Asegurar rol ADMIN y usuario Administrador semilla (password BCrypt para '123')
INSERT INTO rol (nombre_rol) VALUES ('ADMIN') ON CONFLICT (nombre_rol) DO NOTHING;

INSERT INTO usuario (id_rol, rut, nombre, apellido, edad, genero, cargo, centro_salud, activo, es_de_la_comuna, correo, telefono, direccion, password, foto_perfil, especialidad)
VALUES (
    (SELECT id_rol FROM rol WHERE nombre_rol = 'ADMIN'),
    '10.111.222-3',
    'Administrador',
    'Salud MPH',
    40,
    'Otro',
    'Coordinador Comunal de Talleres',
    'Dirección de Salud Padre Hurtado',
    TRUE,
    TRUE,
    'admin@saludmph.cl',
    '+56900000001',
    'Padre Hurtado',
    '$2a$10$.BJUAIfO7YtNP9f08H4QUO/TA/PiNbyTllNl.OLyC82.4DRFunPoS',
    NULL,
    'Gestión Comunal de Salud'
)
ON CONFLICT (correo) DO UPDATE SET
    password = EXCLUDED.password,
    activo = TRUE;
