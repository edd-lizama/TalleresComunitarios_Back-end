package com.municipalidad.talleresph.service_inscripciones_gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacionSyncDTO 
{
    private Long id;
    private String tipo;
    private String fecha;
    private Map<String, Object> payload;
}
