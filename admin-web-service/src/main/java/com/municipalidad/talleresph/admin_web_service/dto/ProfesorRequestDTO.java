package com.municipalidad.talleresph.admin_web_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfesorRequestDTO {
    private String rut;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    private String telefono;
    private String direccion;
    private String cargo;
    private String especialidad;
    private String centro;
    private String password;
    private Boolean activo;
}