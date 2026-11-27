package com.municipalidad.talleresph.admin_web_service.controller;

import com.municipalidad.talleresph.admin_web_service.dto.DashboardStatsDTO;
import com.municipalidad.talleresph.admin_web_service.service.DashboardAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardAdminController {

    private final DashboardAdminService dashboardAdminService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> obtenerEstadisticas() {
        return ResponseEntity.ok(dashboardAdminService.obtenerEstadisticas());
    }
}
