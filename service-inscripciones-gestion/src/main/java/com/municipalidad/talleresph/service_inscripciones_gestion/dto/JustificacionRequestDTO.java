package com.municipalidad.talleresph.service_inscripciones_gestion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JustificacionRequestDTO 
{
    private Long detalleId;
    private Long usuarioId;
    private Long sesionId;
    private Long tallerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @JsonAlias({"justificacion", "motivo", "observacion"})
    private String justificacion;
}
