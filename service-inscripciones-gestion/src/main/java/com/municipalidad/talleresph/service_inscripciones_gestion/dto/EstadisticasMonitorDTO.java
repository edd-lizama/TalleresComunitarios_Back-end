package com.municipalidad.talleresph.service_inscripciones_gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasMonitorDTO 
{
    private int talleres;
    private int registros;
    private int esteMes;
}
