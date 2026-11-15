package com.municipalidad.talleresph.service_catalogo_sucursales.controller;

import com.municipalidad.talleresph.service_catalogo_sucursales.dto.TallerResponseDTO;
import com.municipalidad.talleresph.service_catalogo_sucursales.service.TallerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TallerController 
{
    private final TallerService tallerService;

    // Endpoint #4: Listar todos los talleres disponibles (Vista Vecino)
    @GetMapping("/talleres")
    public ResponseEntity<List<TallerResponseDTO>> listarTalleres() 
    {
        return ResponseEntity.ok(tallerService.obtenerTodosLosTalleres());
    }

    // Endpoint #8: Listar talleres asignados a un monitor (Módulo Monitor)
    @GetMapping("/monitor/{monitorId}/talleres")
    public ResponseEntity<List<TallerResponseDTO>> listarTalleresPorMonitor(@PathVariable Long monitorId) 
    {
        return ResponseEntity.ok(tallerService.obtenerTalleresPorMonitor(monitorId));
    }
}
