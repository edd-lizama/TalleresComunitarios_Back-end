package com.municipalidad.talleresph.service_inscripciones_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rut", unique = true, nullable = false, length = 15)
    private String rut;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "correo", unique = true, nullable = false, length = 150)
    private String correo;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "genero", length = 30)
    private String genero;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

}
