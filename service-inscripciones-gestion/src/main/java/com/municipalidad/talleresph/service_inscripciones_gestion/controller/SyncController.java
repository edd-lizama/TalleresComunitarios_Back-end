package com.municipalidad.talleresph.service_inscripciones_gestion.controller;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.MensajeResponseDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.SyncLoteRequestDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController 
{

    private final SyncService syncService;

    // Endpoint #11: Sincronización por lotes de operaciones offline
    @PostMapping("/lote")
    public ResponseEntity<MensajeResponseDTO> sincronizarLote(@RequestBody SyncLoteRequestDTO request) 
    {
        return ResponseEntity.ok(syncService.procesarLote(request));
    }

    
}
