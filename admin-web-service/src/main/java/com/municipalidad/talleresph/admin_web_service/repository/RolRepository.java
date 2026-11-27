package com.municipalidad.talleresph.admin_web_service.repository;

import com.municipalidad.talleresph.admin_web_service.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreRol(String nombreRol);
}
