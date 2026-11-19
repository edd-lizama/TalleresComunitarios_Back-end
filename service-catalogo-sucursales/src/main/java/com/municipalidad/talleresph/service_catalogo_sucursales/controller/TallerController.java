package com.municipalidad.talleresph.service_catalogo_sucursales.controller;

import com.municipalidad.talleresph.service_catalogo_sucursales.dto.TallerResponseDTO;
import com.municipalidad.talleresph.service_catalogo_sucursales.model.Usuario;
import com.municipalidad.talleresph.service_catalogo_sucursales.repository.UsuarioRepository;
import com.municipalidad.talleresph.service_catalogo_sucursales.security.JwtUtil;
import com.municipalidad.talleresph.service_catalogo_sucursales.service.TallerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TallerController 
{
    private final TallerService tallerService;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    // Endpoint #4: Listar todos los talleres disponibles (Vista Vecino)
    @GetMapping("/talleres")
    public ResponseEntity<List<TallerResponseDTO>> listarTalleres() 
    {
        return ResponseEntity.ok(tallerService.obtenerTodosLosTalleres());
    }

    // Endpoint: Mis Talleres del monitor autenticado (Token JWT)
    @GetMapping("/monitor/mis-talleres")
    public ResponseEntity<List<TallerResponseDTO>> listarMisTalleres(
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
        return ResponseEntity.ok(tallerService.obtenerTalleresPorMonitor(monitorId));
    }

    // Endpoint #8: Listar talleres asignados a un monitor por ID de path
    @GetMapping("/monitor/{monitorId}/talleres")
    public ResponseEntity<List<TallerResponseDTO>> listarTalleresPorMonitor(@PathVariable Long monitorId) 
    {
        return ResponseEntity.ok(tallerService.obtenerTalleresPorMonitor(monitorId));
    }
}
