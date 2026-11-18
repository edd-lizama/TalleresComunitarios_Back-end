package com.talleresph.service_auth_usuarios.repository;

import com.talleresph.service_auth_usuarios.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
}
