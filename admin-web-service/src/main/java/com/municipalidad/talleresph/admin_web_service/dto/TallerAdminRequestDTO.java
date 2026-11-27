package com.municipalidad.talleresph.admin_web_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TallerAdminRequestDTO {
    @NotBlank(message = "El nombre del taller es obligatorio")
    private String nombre;

    @NotBlank(message = "El programa de salud es obligatorio")
    private String programaSalud;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private String imagen;

    @NotBlank(message = "Los días son obligatorios")
    private String dias;

    @NotBlank(message = "El horario es obligatorio")
    private String horario;

    private String horarioLargo;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
    private Integer cupoMaximo;

    private Long recintoId;
    private String lugar;
    private Double lat;
    private Double lng;

    private Long profesorId;
    private String estado;
}
