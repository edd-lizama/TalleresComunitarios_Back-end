package com.municipalidad.talleresph.admin_web_service.repository;

import com.municipalidad.talleresph.admin_web_service.model.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TallerRepository extends JpaRepository<Taller, Long> {

    @Query("SELECT t FROM Taller t LEFT JOIN FETCH t.monitor ORDER BY t.id ASC")
    List<Taller> findAllWithMonitorOrderByIdAsc();

    List<Taller> findByMonitorIdUsuarioOrderByIdAsc(Long monitorId);

    @Query(value = "SELECT COUNT(*) FROM inscripcion WHERE taller_id = :tallerId AND estado = 'ACTIVA'", nativeQuery = true)
    Long countInscritosActivosByTallerId(@Param("tallerId") Long tallerId);

    @Query("SELECT DISTINCT t.categoria FROM Taller t ORDER BY t.categoria ASC")
    List<String> findDistinctCategorias();
}
