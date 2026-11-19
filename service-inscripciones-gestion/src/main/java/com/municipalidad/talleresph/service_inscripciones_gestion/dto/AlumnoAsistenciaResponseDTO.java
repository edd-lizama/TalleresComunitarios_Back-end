package com.municipalidad.talleresph.service_inscripciones_gestion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoAsistenciaResponseDTO 
{
    private Long id;
    private String nombre;
    private Integer edad;
    private String genero;
    private String estado;

    @JsonProperty("justificacion")
    @JsonAlias({"justificacion", "observacion"})
    private String justificacion;
}
