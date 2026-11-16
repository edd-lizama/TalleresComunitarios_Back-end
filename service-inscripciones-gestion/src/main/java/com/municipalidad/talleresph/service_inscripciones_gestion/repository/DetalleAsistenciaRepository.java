package com.municipalidad.talleresph.service_inscripciones_gestion.repository;

import com.municipalidad.talleresph.service_inscripciones_gestion.model.DetalleAsistencia;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleAsistenciaRepository extends JpaRepository<DetalleAsistencia, Long> {

    @EntityGraph(attributePaths = {"usuario"})
    List<DetalleAsistencia> findBySesionId(Long sesionId);

    Optional<DetalleAsistencia> findBySesionIdAndUsuarioId(Long sesionId, Long usuarioId);
}