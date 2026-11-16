package com.municipalidad.talleresph.service_inscripciones_gestion.repository;

import com.municipalidad.talleresph.service_inscripciones_gestion.model.SesionAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SesionAsistenciaRepository extends JpaRepository<SesionAsistencia, Long> 
{

    Optional<SesionAsistencia> findByTallerIdAndFechaClase(Long tallerId, LocalDate fechaClase);
}