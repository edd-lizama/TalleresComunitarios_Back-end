package com.municipalidad.talleresph.service_catalogo_sucursales.repository;

import com.municipalidad.talleresph.service_catalogo_sucursales.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> 
{
    Optional<Usuario> findByCorreo(String correo);
}
