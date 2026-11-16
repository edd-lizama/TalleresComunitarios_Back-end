package com.municipalidad.talleresph.service_inscripciones_gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaDiaResponseDTO 
{

    private Long tallerId;
    private LocalDate fecha;
    private String fotoEvidencia;
    private List<AlumnoAsistenciaResponseDTO> alumnos;
}