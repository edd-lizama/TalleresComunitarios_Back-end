package com.municipalidad.talleresph.admin_web_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfesorResponseDTO {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String direccion;
    private String cargo;
    private String especialidad;
    private String centro;
    private Boolean activo;
    private String rol;
    private List<TallerResumenDTO> talleresAsignados;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TallerResumenDTO {
        private Long id;
        private String nombre;
        private String programaSalud;
        private String dias;
        private String horario;
        private Boolean activo;
    }
}
