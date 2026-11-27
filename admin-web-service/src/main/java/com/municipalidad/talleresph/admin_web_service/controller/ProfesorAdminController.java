package com.municipalidad.talleresph.admin_web_service.controller;

import com.municipalidad.talleresph.admin_web_service.dto.ProfesorRequestDTO;
import com.municipalidad.talleresph.admin_web_service.dto.ProfesorResponseDTO;
import com.municipalidad.talleresph.admin_web_service.service.ProfesorAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/profesores")
@RequiredArgsConstructor
public class ProfesorAdminController {

    private final ProfesorAdminService profesorAdminService;

    @GetMapping
    public ResponseEntity<List<ProfesorResponseDTO>> listarProfesores() {
        return ResponseEntity.ok(profesorAdminService.listarProfesores());
    }

    @PostMapping
    public ResponseEntity<ProfesorResponseDTO> crearProfesor(@Valid @RequestBody ProfesorRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profesorAdminService.crearProfesor(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesorResponseDTO> actualizarProfesor(
            @PathVariable Long id,
            @Valid @RequestBody ProfesorRequestDTO request
    ) {
        return ResponseEntity.ok(profesorAdminService.actualizarProfesor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProfesor(@PathVariable Long id) {
        profesorAdminService.eliminarProfesor(id);
        return ResponseEntity.noContent().build();
    }
}
