package com.municipalidad.talleresph.service_catalogo_sucursales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TallerResponseDTO 
{

    private Long id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private String imagen;
    private Double lat;
    private Double lng;
    private String lugar;
    private String dias;
    private String horario;
    private String horarioLargo;
    private Integer cuposDisponibles;
    private String profesor;
    private Long monitorId;
    
}