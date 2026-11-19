package com.municipalidad.talleresph.service_inscripciones_gestion.controller;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.AsistenciaDiaResponseDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.EstadisticasMonitorDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.GuardarAsistenciaRequestDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.MensajeResponseDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.model.Usuario;
import com.municipalidad.talleresph.service_inscripciones_gestion.repository.UsuarioRepository;
import com.municipalidad.talleresph.service_inscripciones_gestion.security.JwtUtil;
import com.municipalidad.talleresph.service_inscripciones_gestion.service.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/monitor/talleres")
@RequiredArgsConstructor
public class AsistenciaMonitorController 
{

    private final AsistenciaService asistenciaService;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    // Endpoint #9: Obtener nómina de inscritos y asistencia de una fecha
    @GetMapping("/{tallerId}/asistencia")
    public ResponseEntity<AsistenciaDiaResponseDTO> obtenerAsistencia(
            @PathVariable Long tallerId,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(asistenciaService.obtenerAsistenciaDia(tallerId, fecha));
    }

    // Endpoint #10a: Guardar asistencia con tallerId en el path
    @PostMapping("/{tallerId}/asistencia")
    public ResponseEntity<MensajeResponseDTO> guardarAsistencia(
            @PathVariable Long tallerId, 
            @RequestBody GuardarAsistenciaRequestDTO request
    ) {
        if (request.getTallerId() == null) {
            request.setTallerId(tallerId);
        }
        return ResponseEntity.ok(asistenciaService.guardarAsistencia(tallerId, request));
    }

    // Endpoint #10b: Guardar asistencia con tallerId en el body (POST /api/monitor/talleres/asistencia)
    @PostMapping("/asistencia")
    public ResponseEntity<MensajeResponseDTO> guardarAsistenciaDirecta(
            @RequestBody GuardarAsistenciaRequestDTO request
    ) {
        if (request.getTallerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tallerId es obligatorio en el cuerpo de la petición");
        }
        return ResponseEntity.ok(asistenciaService.guardarAsistencia(request.getTallerId(), request));
    }

    // Endpoint alternativo de estadísticas bajo /api/monitor/talleres/estadisticas
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasMonitorDTO> obtenerEstadisticas(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication authentication
    ) {
        Long monitorId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            monitorId = jwtUtil.extraerIdUsuario(token);
        }
        if (monitorId == null && authentication != null) {
            monitorId = usuarioRepository.findByCorreo(authentication.getName())
                    .map(Usuario::getId)
                    .orElse(null);
        }
        if (monitorId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se pudo identificar al monitor desde el token");
        }
        return ResponseEntity.ok(asistenciaService.obtenerEstadisticasMonitor(monitorId));
    }
}
