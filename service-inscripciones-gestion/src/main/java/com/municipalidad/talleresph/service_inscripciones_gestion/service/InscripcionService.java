package com.municipalidad.talleresph.service_inscripciones_gestion.service;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.InscripcionRequestDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.MensajeResponseDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.model.Inscripcion;
import com.municipalidad.talleresph.service_inscripciones_gestion.model.Taller;
import com.municipalidad.talleresph.service_inscripciones_gestion.model.Usuario;
import com.municipalidad.talleresph.service_inscripciones_gestion.repository.InscripcionRepository;
import com.municipalidad.talleresph.service_inscripciones_gestion.repository.TallerRepository;
import com.municipalidad.talleresph.service_inscripciones_gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscripcionService 
{
    private final InscripcionRepository inscripcionRepository;
    private final TallerRepository tallerRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Long> obtenerTalleresInscritosPorUsuario(Long usuarioId) 
    {
        return inscripcionRepository.findTallerIdsActivosByUsuarioId(usuarioId);
    }

    @Transactional
    public MensajeResponseDTO inscribirUsuario(Long usuarioId, Long tallerId) 
    {
        return inscribirVecino(new InscripcionRequestDTO(usuarioId, tallerId));
    }

    @Transactional
    public MensajeResponseDTO inscribirVecino(InscripcionRequestDTO request) 
    {
        if (request.getUsuarioId() == null || request.getTallerId() == null) 
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId y tallerId son obligatorios");
        }

        // Bloqueo pesimista sobre la fila del taller para evitar sobreventa simultánea
        Taller taller = tallerRepository.findByIdForUpdate(request.getTallerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado"));

        if (Boolean.FALSE.equals(taller.getActivo())) 
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El taller no se encuentra activo");
        }

        Optional<Inscripcion> inscripcionExistente = inscripcionRepository
                .findByUsuarioIdAndTallerId(request.getUsuarioId(), request.getTallerId());

        if (inscripcionExistente.isPresent() && "ACTIVA".equalsIgnoreCase(inscripcionExistente.get().getEstado())) 
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El vecino ya está inscrito en este taller");
        }

        if (taller.getCuposDisponibles() == null || taller.getCuposDisponibles() <= 0)    
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sin cupos disponibles");
        }

        if (inscripcionExistente.isPresent()) 
        {
            // Reactiva una inscripción previamente anulada sin violar la restricción UNIQUE
            Inscripcion inscripcion = inscripcionExistente.get();
            inscripcion.setEstado("ACTIVA");
            inscripcion.setFechaInscripcion(LocalDateTime.now());
            inscripcionRepository.save(inscripcion);
        } 
        else 
        {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            Inscripcion nuevaInscripcion = Inscripcion.builder()
                    .usuario(usuario)
                    .taller(taller)
                    .fechaInscripcion(LocalDateTime.now())
                    .estado("ACTIVA")
                    .build();
            inscripcionRepository.save(nuevaInscripcion);
        }

        taller.setCuposDisponibles(taller.getCuposDisponibles() - 1);
        tallerRepository.save(taller);

        return new MensajeResponseDTO("Inscripción realizada con éxito");
    }

    @Transactional
    public MensajeResponseDTO anularInscripcion(Long usuarioId, Long tallerId) 
    {
        Inscripcion inscripcion = inscripcionRepository
                .findByUsuarioIdAndTallerIdAndEstado(usuarioId, tallerId, "ACTIVA")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción activa no encontrada"));

        inscripcion.setEstado("ANULADA");
        inscripcionRepository.save(inscripcion);

        Taller taller = tallerRepository.findByIdForUpdate(tallerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado"));

        if (taller.getCuposDisponibles() < taller.getCuposTotales()) 
        {
            taller.setCuposDisponibles(taller.getCuposDisponibles() + 1);
            tallerRepository.save(taller);
        }

        return new MensajeResponseDTO("Inscripción anulada correctamente");
    }
}
