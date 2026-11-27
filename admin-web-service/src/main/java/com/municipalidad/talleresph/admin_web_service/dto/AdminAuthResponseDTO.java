package com.municipalidad.talleresph.admin_web_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuthResponseDTO {
    private String token;
    private UsuarioAdminDTO usuario;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioAdminDTO {
        private Long idUsuario;
        private String rut;
        private String nombre;
        private String apellido;
        private String correo;
        private String rol;
        private String cargo;
        private String centroSalud;
    }
}
