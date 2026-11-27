package com.municipalidad.talleresph.admin_web_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaSaludDTO {
    private String nombre;
    private Long cantidadTalleres;
}
