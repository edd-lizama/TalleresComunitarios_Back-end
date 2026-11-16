package com.municipalidad.talleresph.service_inscripciones_gestion.controller;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.AsistenciaDiaResponseDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.GuardarAsistenciaRequestDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.dto.MensajeResponseDTO;
import com.municipalidad.talleresph.service_inscripciones_gestion.service.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/monitor/talleres")
@RequiredArgsConstructor
public class AsistenciaMonitorController 
{

    private final AsistenciaService asistenciaService;

    // Endpoint #9: Obtener nómina de inscritos y asistencia de una fecha
    @GetMapping("/{tallerId}/asistencia")
    public ResponseEntity<AsistenciaDiaResponseDTO> obtenerAsistencia( @PathVariable Long tallerId,
         @RequestParam(required = false) 
         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaService.obtenerAsistenciaDia(tallerId, fecha));
    }

    // Endpoint #10: Guardar o actualizar asistencia y fotografía de evidencia
    @PostMapping("/{tallerId}/asistencia")
    public ResponseEntity<MensajeResponseDTO> guardarAsistencia(@PathVariable Long tallerId, @RequestBody GuardarAsistenciaRequestDTO request) 
    {
        return ResponseEntity.ok(asistenciaService.guardarAsistencia(tallerId, request));
    }
}
