package com.municipalidad.talleresph.admin_web_service.controller;

import com.municipalidad.talleresph.admin_web_service.dto.AdminAuthResponseDTO;
import com.municipalidad.talleresph.admin_web_service.dto.AdminLoginRequestDTO;
import com.municipalidad.talleresph.admin_web_service.service.AdminAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<AdminAuthResponseDTO> login(@Valid @RequestBody AdminLoginRequestDTO request) {
        return ResponseEntity.ok(adminAuthService.login(request));
    }
}
