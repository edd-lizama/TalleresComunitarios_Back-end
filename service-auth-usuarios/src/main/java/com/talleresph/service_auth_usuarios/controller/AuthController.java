package com.talleresph.service_auth_usuarios.controller;

import com.talleresph.service_auth_usuarios.dto.*;
import com.talleresph.service_auth_usuarios.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "Correo o contraseña incorrectos"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("mensaje", "Error interno al iniciar sesión"));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> register(@Valid @RequestBody RegistroRequestDTO request) {
        try {
            AuthResponseDTO response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Errores de negocio (correo/RUT duplicado): el mensaje es seguro de mostrar
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("mensaje", "Error interno al registrar el usuario"));
        }
    }
}
