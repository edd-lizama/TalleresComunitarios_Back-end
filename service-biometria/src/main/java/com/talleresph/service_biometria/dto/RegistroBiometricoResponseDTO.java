package com.talleresph.service_biometria.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistroBiometricoResponseDTO {
    private Long id;
    private Long idPerfil;
    private LocalDate fechaMedicion;
    /** Centímetros */
    private BigDecimal altura;
    /** Kilogramos */
    private BigDecimal peso;
    private BigDecimal indiceMasaCorporal;
}
