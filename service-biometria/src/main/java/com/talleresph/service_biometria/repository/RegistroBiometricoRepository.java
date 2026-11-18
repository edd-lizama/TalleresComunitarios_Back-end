package com.talleresph.service_biometria.repository;

import com.talleresph.service_biometria.model.RegistroBiometrico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RegistroBiometricoRepository extends JpaRepository<RegistroBiometrico, Long> {

    List<RegistroBiometrico> findByIdPerfilInOrderByFechaMedicionDescIdBiometricoDesc(Collection<Long> idsPerfil);
}
