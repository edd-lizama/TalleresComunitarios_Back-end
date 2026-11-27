package com.municipalidad.talleresph.admin_web_service.controller;

import com.municipalidad.talleresph.admin_web_service.dto.*;
import com.municipalidad.talleresph.admin_web_service.service.TallerAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class TallerAdminController {

    private final TallerAdminService tallerAdminService;

    @GetMapping("/talleres")
    public ResponseEntity<List<TallerAdminResponseDTO>> listarTalleres() {
        return ResponseEntity.ok(tallerAdminService.listarTalleres());
    }

    @PostMapping("/talleres")
    public ResponseEntity<TallerAdminResponseDTO> crearTaller(@Valid @RequestBody TallerAdminRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tallerAdminService.crearTaller(request));
    }

    @PutMapping("/talleres/{id}")
    public ResponseEntity<TallerAdminResponseDTO> actualizarTaller(
            @PathVariable Long id,
            @Valid @RequestBody TallerAdminRequestDTO request
    ) {
        return ResponseEntity.ok(tallerAdminService.actualizarTaller(id, request));
    }

    @PatchMapping("/talleres/{id}/estado")
    public ResponseEntity<TallerAdminResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestBody(required = false) CambiarEstadoTallerDTO request
    ) {
        return ResponseEntity.ok(tallerAdminService.cambiarEstado(id, request));
    }

    @GetMapping("/recintos")
    public ResponseEntity<List<RecintoDTO>> listarRecintos() {
        return ResponseEntity.ok(tallerAdminService.listarRecintos());
    }

    @GetMapping("/programas")
    public ResponseEntity<List<ProgramaSaludDTO>> listarProgramas() {
        return ResponseEntity.ok(tallerAdminService.listarProgramas());
    }
}
