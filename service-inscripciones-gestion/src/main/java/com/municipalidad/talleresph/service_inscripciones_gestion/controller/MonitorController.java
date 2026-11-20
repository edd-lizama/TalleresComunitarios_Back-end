package com.municipalidad.talleresph.service_inscripciones_gestion.controller;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.EstadisticasMonitorDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.model.Usuario;
import com.municipalidad.talleresph.service_inscripciones_gestion.repository.UsuarioRepository;
import com.municipalidad.talleresph.service_inscripciones_gestion.security.JwtUtil;
import com.municipalidad.talleresph.service_inscripciones_gestion.service.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8100")
public class MonitorController 
{

    private final AsistenciaService asistenciaService;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    // Endpoint: Estadísticas reales para la pantalla de perfil del monitor
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
