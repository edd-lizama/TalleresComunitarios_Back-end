package com.talleresph.service_biometria.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroBiometricoRequestDTO {

    private LocalDate fechaMedicion;

    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "50.0", message = "La altura debe estar entre 50 y 250 cm")
    @DecimalMax(value = "250.0", message = "La altura debe estar entre 50 y 250 cm")
    private BigDecimal altura;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "2.0", message = "El peso debe estar entre 2 y 500 kg")
    @DecimalMax(value = "500.0", message = "El peso debe estar entre 2 y 500 kg")
    private BigDecimal peso;

    public LocalDate getFechaMedicion() { return fechaMedicion; }
    public void setFechaMedicion(LocalDate fechaMedicion) { this.fechaMedicion = fechaMedicion; }
    public BigDecimal getAltura() { return altura; }
    public void setAltura(BigDecimal altura) { this.altura = altura; }
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }
}
