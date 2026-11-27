package com.municipalidad.talleresph.admin_web_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TallerAdminResponseDTO {
    private Long id;
    private String nombre;
    private String programaSalud;
    private String descripcion;
    private String imagen;
    private String dias;
    private String horario;
    private String horarioLargo;
    private Integer cupoMaximo;
    private Integer cuposDisponibles;
    private Long inscritosActuales;
    private String recinto;
    private Double lat;
    private Double lng;
    private Long profesorId;
    private String profesorNombre;
    private String estado;
    private Boolean activo;
}
