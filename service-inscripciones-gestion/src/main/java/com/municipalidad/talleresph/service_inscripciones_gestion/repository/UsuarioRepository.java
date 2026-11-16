package com.municipalidad.talleresph.service_inscripciones_gestion.repository;

import com.municipalidad.talleresph.service_inscripciones_gestion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}