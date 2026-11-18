-- Ejecutar UNA vez sobre la base talleres_ph_db que ya tienes creada.
-- La entidad Usuario ahora mapea foto_perfil (imagen en Base64, por eso TEXT y no VARCHAR(255)).
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS foto_perfil TEXT;
