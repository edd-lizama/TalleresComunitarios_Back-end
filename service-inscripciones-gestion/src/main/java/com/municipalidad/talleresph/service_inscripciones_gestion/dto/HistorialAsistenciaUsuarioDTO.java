package com.municipalidad.talleresph.service_inscripciones_gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAsistenciaUsuarioDTO 
{
    private Long detalleId;
    private Long sesionId;
    private Long tallerId;
    private String tallerNombre;
    private LocalDate fecha;
    private String estado;
    private String justificacion;
}
