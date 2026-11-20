package com.municipalidad.talleresph.service_inscripciones_gestion.controller;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.HistorialAsistenciaUsuarioDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.InscripcionRequestDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.JustificacionRequestDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.MensajeResponseDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.service.AsistenciaService;
import com.municipalidad.talleresph.service_inscripciones_gestion.service.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8100")
public class InscripcionController 
{

    private final InscripcionService inscripcionService;
    private final AsistenciaService asistenciaService;

    // Endpoint #5: Obtener IDs de talleres inscritos por un vecino
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Long>> listarTalleresInscritos(@PathVariable Long usuarioId) 
    {
        return ResponseEntity.ok(inscripcionService.obtenerTalleresInscritosPorUsuario(usuarioId));
    }

    // Endpoint: Historial de asistencias del Vecino con sus justificaciones
    @GetMapping("/usuario/{usuarioId}/asistencias")
    public ResponseEntity<List<HistorialAsistenciaUsuarioDTO>> listarHistorialAsistencias(@PathVariable Long usuarioId) 
    {
        return ResponseEntity.ok(asistenciaService.obtenerHistorialAsistenciasUsuario(usuarioId));
    }

    // Endpoint #6: Inscribir vecino a un taller
    @PostMapping
    public ResponseEntity<MensajeResponseDTO> inscribir(@RequestBody InscripcionRequestDTO request) 
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionService.inscribirVecino(request));
    }

    // Endpoint #7: Anular inscripción y liberar cupo
    @DeleteMapping("/{usuarioId}/{tallerId}")
    public ResponseEntity<MensajeResponseDTO> anular(@PathVariable Long usuarioId, @PathVariable Long tallerId) 
    {
        return ResponseEntity.ok(inscripcionService.anularInscripcion(usuarioId, tallerId));
    }

    // Endpoint: Enviar o actualizar justificación de inasistencia del Vecino
    @PutMapping("/asistencia/justificar")
    public ResponseEntity<HistorialAsistenciaUsuarioDTO> justificarAsistencia(@RequestBody JustificacionRequestDTO request) 
    {
        return ResponseEntity.ok(asistenciaService.guardarJustificacion(request));
    }
}
