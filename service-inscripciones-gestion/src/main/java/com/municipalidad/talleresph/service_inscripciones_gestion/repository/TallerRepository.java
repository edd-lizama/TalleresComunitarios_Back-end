package com.municipalidad.talleresph.service_inscripciones_gestion.repository;

import com.municipalidad.talleresph.service_inscripciones_gestion.model.Taller;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TallerRepository extends JpaRepository<Taller, Long>
{
    // Bloquea la fila en PostgreSQL mientras se descuenta el cupo disponible
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Taller t WHERE t.id = :id")
    Optional<Taller> findByIdForUpdate(@Param("id") Long id);
}
