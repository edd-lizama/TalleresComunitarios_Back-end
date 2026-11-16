package com.municipalidad.talleresph.service_inscripciones_gestion.repository;

import com.municipalidad.talleresph.service_inscripciones_gestion.model.Inscripcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> 
{

    // Devuelve solo los IDs de talleres activos para pintar botones "Inscrito / Anular" en la app
    @Query("SELECT i.taller.id FROM Inscripcion i WHERE i.usuario.id = :usuarioId AND i.estado = 'ACTIVA'")
    List<Long> findTallerIdsActivosByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Busca el registro (activo o anulado) para respetar la restricción UNIQUE (usuario_id, taller_id)
    Optional<Inscripcion> findByUsuarioIdAndTallerId(Long usuarioId, Long tallerId);

    // Busca específicamente una inscripción activa para poder anularla
    Optional<Inscripcion> findByUsuarioIdAndTallerIdAndEstado(Long usuarioId, Long tallerId, String estado);

    // Obtiene todos los alumnos activos de un taller cargando sus datos de usuario en una sola consulta
    @EntityGraph(attributePaths = {"usuario"})
    List<Inscripcion> findByTallerIdAndEstadoOrderByIdAsc(Long tallerId, String estado);
}
