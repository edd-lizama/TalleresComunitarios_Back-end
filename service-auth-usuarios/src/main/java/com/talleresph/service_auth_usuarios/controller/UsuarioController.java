package com.talleresph.service_auth_usuarios.controller;

import com.talleresph.service_auth_usuarios.dto.ActualizarUsuarioRequestDTO;
import com.talleresph.service_auth_usuarios.dto.UsuarioResponseDTO;
import com.talleresph.service_auth_usuarios.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** PUT /api/usuarios/{id}: actualiza datos y foto de perfil (requiere token). */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Integer id,
                                                         @RequestBody ActualizarUsuarioRequestDTO request,
                                                         Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return ResponseEntity.ok(
                usuarioService.actualizar(id, request, authentication.getName(), esAdmin));
    }
}
