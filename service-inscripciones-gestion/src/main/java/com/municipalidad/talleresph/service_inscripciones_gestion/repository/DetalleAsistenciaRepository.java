package com.municipalidad.talleresph.service_inscripciones_gestion.repository;

import com.municipalidad.talleresph.service_inscripciones_gestion.model.DetalleAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleAsistenciaRepository extends JpaRepository<DetalleAsistencia, Long> {

    @Query("SELECT d FROM DetalleAsistencia d JOIN FETCH d.usuario WHERE d.sesion.id = :sesionId")
    List<DetalleAsistencia> findBySesionId(@Param("sesionId") Long sesionId);

    @Query("SELECT d FROM DetalleAsistencia d WHERE d.sesion.id = :sesionId AND d.usuario.id = :usuarioId")
    Optional<DetalleAsistencia> findBySesionIdAndUsuarioId(@Param("sesionId") Long sesionId, @Param("usuarioId") Long usuarioId);

    @Query("SELECT d FROM DetalleAsistencia d JOIN FETCH d.sesion s JOIN FETCH s.taller t WHERE d.usuario.id = :usuarioId ORDER BY s.fechaClase DESC")
    List<DetalleAsistencia> findHistorialByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT d FROM DetalleAsistencia d WHERE d.usuario.id = :usuarioId AND d.sesion.taller.id = :tallerId AND d.sesion.fechaClase = :fecha")
    Optional<DetalleAsistencia> findByUsuarioIdAndTallerIdAndFecha(
            @Param("usuarioId") Long usuarioId,
            @Param("tallerId") Long tallerId,
            @Param("fecha") LocalDate fecha
    );

    @Query("SELECT COUNT(d) FROM DetalleAsistencia d WHERE d.sesion.monitor.id = :monitorId AND LOWER(d.estado) IN ('asiste', 'no_asiste')")
    long countRegistrosByMonitorId(@Param("monitorId") Long monitorId);

    @Query("SELECT COUNT(d) FROM DetalleAsistencia d WHERE d.sesion.monitor.id = :monitorId AND LOWER(d.estado) IN ('asiste', 'no_asiste') AND d.sesion.fechaClase >= :inicioMes AND d.sesion.fechaClase <= :finMes")
    long countRegistrosMesByMonitorId(@Param("monitorId") Long monitorId, @Param("inicioMes") LocalDate inicioMes, @Param("finMes") LocalDate finMes);
}
