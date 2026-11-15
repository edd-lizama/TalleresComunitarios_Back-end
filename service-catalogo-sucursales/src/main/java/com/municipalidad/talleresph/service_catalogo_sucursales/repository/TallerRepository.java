package com.municipalidad.talleresph.service_catalogo_sucursales.repository;

import com.municipalidad.talleresph.service_catalogo_sucursales.model.Taller;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TallerRepository extends JpaRepository<Taller, Long> 
{

    // Para GET /api/talleres (todos los talleres activos ordenados por ID)
    @EntityGraph(attributePaths = {"monitor"})
    List<Taller> findByActivoTrueOrderByIdAsc();

    // Para GET /api/monitor/{monitorId}/talleres (talleres activos de un monitor)
    @EntityGraph(attributePaths = {"monitor"})
    List<Taller> findByMonitorIdAndActivoTrueOrderByIdAsc(Long monitorId);
}
