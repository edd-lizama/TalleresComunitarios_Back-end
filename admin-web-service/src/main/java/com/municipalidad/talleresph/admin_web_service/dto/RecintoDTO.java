package com.municipalidad.talleresph.admin_web_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecintoDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private Double lat;
    private Double lng;
    private Boolean activo;
}
