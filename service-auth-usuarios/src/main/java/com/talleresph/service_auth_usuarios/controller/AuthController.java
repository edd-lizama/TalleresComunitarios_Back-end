package com.talleresph.service_auth_usuarios.controller;

import com.talleresph.service_auth_usuarios.dto.AuthResponseDTO;
import com.talleresph.service_auth_usuarios.dto.LoginRequestDTO;
import com.talleresph.service_auth_usuarios.dto.RegistroRequestDTO;
import com.talleresph.service_auth_usuarios.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Los errores (401, 409, 400, 500) los traduce GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegistroRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
}
