package com.talleresph.service_biometria.controller;

import com.talleresph.service_biometria.dto.RegistroBiometricoRequestDTO;
import com.talleresph.service_biometria.dto.RegistroBiometricoResponseDTO;
import com.talleresph.service_biometria.security.UsuarioAutenticado;
import com.talleresph.service_biometria.service.BiometriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biometria")
public class BiometriaController {

    private final BiometriaService biometriaService;

    public BiometriaController(BiometriaService biometriaService) {
        this.biometriaService = biometriaService;
    }

    @PostMapping
    public ResponseEntity<RegistroBiometricoResponseDTO> registrar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @Valid @RequestBody RegistroBiometricoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(biometriaService.registrar(usuario.idUsuario(), request));
    }

    @GetMapping("/mis-registros")
    public ResponseEntity<List<RegistroBiometricoResponseDTO>> misRegistros(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(biometriaService.listarPropios(usuario.idUsuario()));
    }

    /** GET /api/biometria/perfil/{idPerfil}: historial de un perfil (solo MONITOR y ADMIN). */
    @GetMapping("/perfil/{idPerfil}")
    public ResponseEntity<List<RegistroBiometricoResponseDTO>> porPerfil(@PathVariable Integer idPerfil) {
        return ResponseEntity.ok(biometriaService.listarPorPerfil(idPerfil));
    }

    /** DELETE /api/biometria/{id}: elimina una medición propia (o cualquiera si es ADMIN). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                         Authentication authentication,
                                         @PathVariable Integer id) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        biometriaService.eliminar(usuario.idUsuario(), esAdmin, id);
        return ResponseEntity.noContent().build();
    }
}
