-- =============================================================================
-- ESQUEMA MAESTRO UNIFICADO - PLATAFORMA TALLERES PH (PADRE HURTADO)
-- Base de Datos: talleres_ph_db (PostgreSQL)
-- Compatible al 100% con los 4 microservicios (ddl-auto: validate)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. TABLAS BASE DE AUTENTICACIÓN Y SEGURIDAD (service-auth-usuarios)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS rol (
    id_rol          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_rol      VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS permiso (
    id_permiso      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_permiso  VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS rol_permiso (
    id_rol          BIGINT NOT NULL REFERENCES rol(id_rol) ON DELETE CASCADE,
    id_permiso      BIGINT NOT NULL REFERENCES permiso(id_permiso) ON DELETE CASCADE,
    PRIMARY KEY (id_rol, id_permiso)
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_rol          BIGINT NOT NULL REFERENCES rol(id_rol),
    rut             VARCHAR(255) NOT NULL UNIQUE,
    nombre          VARCHAR(255) NOT NULL,
    apellido        VARCHAR(255),
    edad            INTEGER,
    genero          VARCHAR(50),
    cargo           VARCHAR(255),
    centro_salud    VARCHAR(255),
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    es_de_la_comuna BOOLEAN DEFAULT TRUE,
    correo          VARCHAR(255) NOT NULL UNIQUE,
    telefono        VARCHAR(255),
    direccion       VARCHAR(255),
    password        VARCHAR(255) NOT NULL,
    foto_perfil     TEXT,
    especialidad    VARCHAR(255),
    fecha_creacion  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS perfil (
    id_perfil            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario           BIGINT NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    tipo_perfil          VARCHAR(255),
    consentimiento_foto  BOOLEAN NOT NULL DEFAULT FALSE
);

-- -----------------------------------------------------------------------------
-- 2. TABLA BIOMETRÍA (service-biometria)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS registro_biometrico (
    id_biometrico         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_perfil             BIGINT NOT NULL REFERENCES perfil(id_perfil) ON DELETE CASCADE,
    fecha_medicion        DATE NOT NULL,
    altura                NUMERIC(5,2) NOT NULL CHECK (altura > 0),
    peso                  NUMERIC(5,2) NOT NULL CHECK (peso > 0),
    indice_masa_corporal  NUMERIC(4,1) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_registro_biometrico_perfil_fecha
    ON registro_biometrico (id_perfil, fecha_medicion DESC);

-- -----------------------------------------------------------------------------
-- 3. TABLA CATÁLOGO DE TALLERES (service-catalogo-sucursales / service-inscripciones-gestion)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS taller (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre              VARCHAR(150) NOT NULL,
    categoria           VARCHAR(100) NOT NULL,
    descripcion         TEXT NOT NULL,
    imagen              TEXT,
    lat                 DOUBLE PRECISION NOT NULL,
    lng                 DOUBLE PRECISION NOT NULL,
    lugar               VARCHAR(150) NOT NULL,
    dias                VARCHAR(100) NOT NULL,
    horario             VARCHAR(60) NOT NULL,
    horario_largo       VARCHAR(60) NOT NULL,
    cupos_totales       INTEGER NOT NULL,
    cupos_disponibles   INTEGER NOT NULL,
    monitor_id          BIGINT REFERENCES usuario(id_usuario),
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recinto (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre              VARCHAR(150) NOT NULL UNIQUE,
    direccion           VARCHAR(255) NOT NULL,
    lat                 DOUBLE PRECISION NOT NULL,
    lng                 DOUBLE PRECISION NOT NULL,
    activo              BOOLEAN NOT NULL DEFAULT TRUE
);

-- -----------------------------------------------------------------------------
-- 4. TABLAS DE INSCRIPCIONES Y ASISTENCIA (service-inscripciones-gestion)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS inscripcion (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id          BIGINT NOT NULL REFERENCES usuario(id_usuario),
    taller_id           BIGINT NOT NULL REFERENCES taller(id),
    fecha_inscripcion   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT uq_usuario_taller UNIQUE (usuario_id, taller_id)
);

CREATE TABLE IF NOT EXISTS sesion_asistencia (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    taller_id           BIGINT NOT NULL REFERENCES taller(id),
    monitor_id          BIGINT NOT NULL REFERENCES usuario(id_usuario),
    fecha_clase         DATE NOT NULL,
    foto_evidencia      TEXT,
    fecha_sincronizacion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_taller_fecha UNIQUE (taller_id, fecha_clase)
);

CREATE TABLE IF NOT EXISTS detalle_asistencia (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sesion_id           BIGINT NOT NULL REFERENCES sesion_asistencia(id) ON DELETE CASCADE,
    usuario_id          BIGINT NOT NULL REFERENCES usuario(id_usuario),
    estado              VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    observacion         TEXT,
    CONSTRAINT uq_sesion_usuario UNIQUE (sesion_id, usuario_id)
);

-- -----------------------------------------------------------------------------
-- 5. VISTAS DE COMPATIBILIDAD PLURAL/SINGULAR
-- -----------------------------------------------------------------------------

CREATE OR REPLACE VIEW talleres AS SELECT * FROM taller;
CREATE OR REPLACE VIEW inscripciones AS SELECT * FROM inscripcion;
CREATE OR REPLACE VIEW sesiones_asistencia AS SELECT * FROM sesion_asistencia;
CREATE OR REPLACE VIEW usuarios AS SELECT * FROM usuario;

-- -----------------------------------------------------------------------------
-- 6. DATOS SEMILLA (INSERT ... ON CONFLICT DO NOTHING)
-- -----------------------------------------------------------------------------

-- 6.1 ROLES
INSERT INTO rol (nombre_rol) VALUES 
    ('VECINO'), 
    ('MONITOR'), 
    ('ADMIN')
ON CONFLICT (nombre_rol) DO NOTHING;

-- 6.2 USUARIOS DEMO (Password en BCrypt para '123': $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG)

-- Admin Demo: Administrador Salud MPH
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
ON CONFLICT (correo) DO NOTHING;

-- Monitor Demo: Claudia Peñaloza Vera
INSERT INTO usuario (id_rol, rut, nombre, apellido, edad, genero, cargo, centro_salud, activo, es_de_la_comuna, correo, telefono, direccion, password, foto_perfil)
VALUES (
    (SELECT id_rol FROM rol WHERE nombre_rol = 'MONITOR'),
    '15.432.198-7',
    'Claudia',
    'Peñaloza Vera',
    38,
    'Femenino',
    'Enfermera Comunitaria',
    'CESFAM Padre Hurtado',
    TRUE,
    TRUE,
    'claudia.penaloza@saludmph.cl',
    '+56912345678',
    'Padre Hurtado',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    NULL
)
ON CONFLICT (correo) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    apellido = EXCLUDED.apellido,
    cargo = EXCLUDED.cargo,
    centro_salud = EXCLUDED.centro_salud;

-- Vecino Demo 1: Cristian López
INSERT INTO usuario (id_rol, rut, nombre, apellido, edad, genero, cargo, centro_salud, activo, es_de_la_comuna, correo, telefono, direccion, password, foto_perfil)
VALUES (
    (SELECT id_rol FROM rol WHERE nombre_rol = 'VECINO'),
    '12345678-9',
    'Cristian',
    'López',
    45,
    'Masculino',
    NULL,
    NULL,
    TRUE,
    TRUE,
    'vecino@padrehurtado.cl',
    '+56987654321',
    'Los Silos 123, Padre Hurtado',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    NULL
)
ON CONFLICT (correo) DO NOTHING;

-- Vecino Demo 2: María González Rojas
INSERT INTO usuario (id_rol, rut, nombre, apellido, edad, genero, cargo, centro_salud, activo, es_de_la_comuna, correo, telefono, direccion, password, foto_perfil)
VALUES (
    (SELECT id_rol FROM rol WHERE nombre_rol = 'VECINO'),
    '9.876.543-2',
    'María',
    'González Rojas',
    67,
    'Femenino',
    NULL,
    NULL,
    TRUE,
    TRUE,
    'maria.gonzalez@correo.cl',
    '+56976543210',
    'Av. San Ignacio 456, Padre Hurtado',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    NULL
)
ON CONFLICT (correo) DO NOTHING;

-- Vecino Demo 3: Juan Pérez Silva
INSERT INTO usuario (id_rol, rut, nombre, apellido, edad, genero, cargo, centro_salud, activo, es_de_la_comuna, correo, telefono, direccion, password, foto_perfil)
VALUES (
    (SELECT id_rol FROM rol WHERE nombre_rol = 'VECINO'),
    '11.223.344-5',
    'Juan',
    'Pérez Silva',
    70,
    'Masculino',
    NULL,
    NULL,
    TRUE,
    TRUE,
    'juan.perez@correo.cl',
    '+56965432109',
    'Calle Los Aromos 789, Padre Hurtado',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    NULL
)
ON CONFLICT (correo) DO NOTHING;

-- Vecino Demo 4: Rosa Morales Pino
INSERT INTO usuario (id_rol, rut, nombre, apellido, edad, genero, cargo, centro_salud, activo, es_de_la_comuna, correo, telefono, direccion, password, foto_perfil)
VALUES (
    (SELECT id_rol FROM rol WHERE nombre_rol = 'VECINO'),
    '8.765.432-1',
    'Rosa',
    'Morales Pino',
    65,
    'Femenino',
    NULL,
    NULL,
    TRUE,
    TRUE,
    'rosa.morales@correo.cl',
    '+56954321098',
    'Pasaje Las Flores 102, Padre Hurtado',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    NULL
)
ON CONFLICT (correo) DO NOTHING;

-- 6.3 PERFILES DE USUARIOS
INSERT INTO perfil (id_usuario, tipo_perfil, consentimiento_foto)
SELECT u.id_usuario, 'FUNCIONARIO', TRUE FROM usuario u WHERE u.correo = 'claudia.penaloza@saludmph.cl'
ON CONFLICT DO NOTHING;

INSERT INTO perfil (id_usuario, tipo_perfil, consentimiento_foto)
SELECT u.id_usuario, 'ADULTO', TRUE FROM usuario u WHERE u.correo = 'vecino@padrehurtado.cl'
ON CONFLICT DO NOTHING;

INSERT INTO perfil (id_usuario, tipo_perfil, consentimiento_foto)
SELECT u.id_usuario, 'ADULTO_MAYOR', TRUE FROM usuario u WHERE u.correo = 'maria.gonzalez@correo.cl'
ON CONFLICT DO NOTHING;

INSERT INTO perfil (id_usuario, tipo_perfil, consentimiento_foto)
SELECT u.id_usuario, 'ADULTO_MAYOR', TRUE FROM usuario u WHERE u.correo = 'juan.perez@correo.cl'
ON CONFLICT DO NOTHING;

INSERT INTO perfil (id_usuario, tipo_perfil, consentimiento_foto)
SELECT u.id_usuario, 'ADULTO_MAYOR', TRUE FROM usuario u WHERE u.correo = 'rosa.morales@correo.cl'
ON CONFLICT DO NOTHING;

-- 6.4 REGISTRO BIOMÉTRICO DEMO (service-biometria)
INSERT INTO registro_biometrico (id_perfil, fecha_medicion, altura, peso, indice_masa_corporal)
SELECT p.id_perfil, '2026-09-01', 162.00, 68.50, 26.1
FROM perfil p
JOIN usuario u ON p.id_usuario = u.id_usuario
WHERE u.correo = 'maria.gonzalez@correo.cl'
ON CONFLICT DO NOTHING;

-- 6.5.0 RECINTOS BASE DE PADRE HURTADO
INSERT INTO recinto (nombre, direccion, lat, lng, activo) VALUES
    ('Sede Social Los Aromos', 'Calle Los Aromos, Padre Hurtado', -33.5725, -70.8150, TRUE),
    ('Gimnasio Municipal Padre Hurtado', 'Av. San Ignacio, Padre Hurtado', -33.5680, -70.8105, TRUE),
    ('CESFAM Padre Hurtado', 'Av. Camino a Melipilla, Padre Hurtado', -33.5750, -70.8190, TRUE),
    ('Centro Cultural Padre Hurtado', 'Primera Transversal, Padre Hurtado', -33.5710, -70.8130, TRUE)
ON CONFLICT (nombre) DO NOTHING;

-- 6.5 LOS 4 TALLERES BASE ASIGNADOS A CLAUDIA PEÑALOZA
INSERT INTO taller (nombre, categoria, descripcion, imagen, lat, lng, lugar, dias, horario, horario_largo, cupos_totales, cupos_disponibles, monitor_id, activo)
VALUES 
(
    'Yoga Terapéutico',
    'Salud Mental y Bienestar',
    'Clases orientadas a mejorar la flexibilidad, respiración y bienestar general en un ambiente tranquilo y comunitario.',
    'https://images.unsplash.com/photo-1545205597-3d9d02c29597?auto=format&fit=crop&w=600&q=80',
    -33.5725,
    -70.8150,
    'Sede Social Los Aromos',
    'Lunes, Miércoles y Viernes',
    'Lun/Mié/Vie 09:00',
    '09:00 – 10:30',
    20,
    16,
    (SELECT id_usuario FROM usuario WHERE correo = 'claudia.penaloza@saludmph.cl'),
    TRUE
),
(
    'Zumba para Adultos Mayores',
    'Actividad Física y Deporte',
    'Sesiones dinámicas de baile adaptado para personas mayores, promoviendo la movilidad articular y la salud cardiovascular.',
    'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=600&q=80',
    -33.5680,
    -70.8105,
    'Gimnasio Municipal Padre Hurtado',
    'Martes y Jueves',
    'Mar/Jue 10:00',
    '10:00 – 11:30',
    25,
    25,
    (SELECT id_usuario FROM usuario WHERE correo = 'claudia.penaloza@saludmph.cl'),
    TRUE
),
(
    'Control de Hipertensión',
    'Salud Preventiva',
    'Monitoreo preventivo, educación nutricional y pautas de autocuidado para vecinos con hipertensión arterial.',
    'https://images.unsplash.com/photo-1576091160550-2173dba999ef?auto=format&fit=crop&w=600&q=80',
    -33.5750,
    -70.8190,
    'CESFAM Padre Hurtado',
    'Miércoles',
    'Mié 15:00',
    '15:00 – 16:30',
    15,
    15,
    (SELECT id_usuario FROM usuario WHERE correo = 'claudia.penaloza@saludmph.cl'),
    TRUE
),
(
    'Psicomotricidad Infantil',
    'Desarrollo Infantil',
    'Talleres lúdicos para el desarrollo de habilidades motrices finas y gruesas en niños de 4 a 8 años.',
    'https://images.unsplash.com/photo-1596464716127-f2a82984de30?auto=format&fit=crop&w=600&q=80',
    -33.5710,
    -70.8130,
    'Centro Cultural Padre Hurtado',
    'Sábados',
    'Sáb 11:00',
    '11:00 – 12:30',
    18,
    18,
    (SELECT id_usuario FROM usuario WHERE correo = 'claudia.penaloza@saludmph.cl'),
    TRUE
)
ON CONFLICT DO NOTHING;

-- 6.6 ALUMNOS INSCRITOS DE PRUEBA EN TALLER 1 ('Yoga Terapéutico')
INSERT INTO inscripcion (usuario_id, taller_id, fecha_inscripcion, estado)
SELECT u.id_usuario, t.id, CURRENT_TIMESTAMP - INTERVAL '5 days', 'ACTIVA'
FROM usuario u, taller t
WHERE u.correo = 'maria.gonzalez@correo.cl' AND t.nombre = 'Yoga Terapéutico'
ON CONFLICT (usuario_id, taller_id) DO NOTHING;

INSERT INTO inscripcion (usuario_id, taller_id, fecha_inscripcion, estado)
SELECT u.id_usuario, t.id, CURRENT_TIMESTAMP - INTERVAL '4 days', 'ACTIVA'
FROM usuario u, taller t
WHERE u.correo = 'juan.perez@correo.cl' AND t.nombre = 'Yoga Terapéutico'
ON CONFLICT (usuario_id, taller_id) DO NOTHING;

INSERT INTO inscripcion (usuario_id, taller_id, fecha_inscripcion, estado)
SELECT u.id_usuario, t.id, CURRENT_TIMESTAMP - INTERVAL '3 days', 'ACTIVA'
FROM usuario u, taller t
WHERE u.correo = 'rosa.morales@correo.cl' AND t.nombre = 'Yoga Terapéutico'
ON CONFLICT (usuario_id, taller_id) DO NOTHING;

INSERT INTO inscripcion (usuario_id, taller_id, fecha_inscripcion, estado)
SELECT u.id_usuario, t.id, CURRENT_TIMESTAMP - INTERVAL '2 days', 'ACTIVA'
FROM usuario u, taller t
WHERE u.correo = 'vecino@padrehurtado.cl' AND t.nombre = 'Yoga Terapéutico'
ON CONFLICT (usuario_id, taller_id) DO NOTHING;

-- 6.7 SESIÓN Y DETALLE DE ASISTENCIA DEMO
INSERT INTO sesion_asistencia (taller_id, monitor_id, fecha_clase, foto_evidencia)
SELECT t.id, u.id_usuario, CURRENT_DATE, NULL
FROM taller t, usuario u
WHERE t.nombre = 'Yoga Terapéutico' AND u.correo = 'claudia.penaloza@saludmph.cl'
ON CONFLICT (taller_id, fecha_clase) DO NOTHING;

INSERT INTO detalle_asistencia (sesion_id, usuario_id, estado, observacion)
SELECT s.id, u.id_usuario, 'asiste', ''
FROM sesion_asistencia s
JOIN taller t ON s.taller_id = t.id
JOIN usuario u ON u.correo = 'maria.gonzalez@correo.cl'
WHERE t.nombre = 'Yoga Terapéutico' AND s.fecha_clase = CURRENT_DATE
ON CONFLICT (sesion_id, usuario_id) DO NOTHING;

INSERT INTO detalle_asistencia (sesion_id, usuario_id, estado, observacion)
SELECT s.id, u.id_usuario, 'asiste', ''
FROM sesion_asistencia s
JOIN taller t ON s.taller_id = t.id
JOIN usuario u ON u.correo = 'juan.perez@correo.cl'
WHERE t.nombre = 'Yoga Terapéutico' AND s.fecha_clase = CURRENT_DATE
ON CONFLICT (sesion_id, usuario_id) DO NOTHING;

INSERT INTO detalle_asistencia (sesion_id, usuario_id, estado, observacion)
SELECT s.id, u.id_usuario, 'no_asiste', 'Compromiso familiar'
FROM sesion_asistencia s
JOIN taller t ON s.taller_id = t.id
JOIN usuario u ON u.correo = 'rosa.morales@correo.cl'
WHERE t.nombre = 'Yoga Terapéutico' AND s.fecha_clase = CURRENT_DATE
ON CONFLICT (sesion_id, usuario_id) DO NOTHING;

INSERT INTO detalle_asistencia (sesion_id, usuario_id, estado, observacion)
SELECT s.id, u.id_usuario, 'asiste', ''
FROM sesion_asistencia s
JOIN taller t ON s.taller_id = t.id
JOIN usuario u ON u.correo = 'vecino@padrehurtado.cl'
WHERE t.nombre = 'Yoga Terapéutico' AND s.fecha_clase = CURRENT_DATE
ON CONFLICT (sesion_id, usuario_id) DO NOTHING;
