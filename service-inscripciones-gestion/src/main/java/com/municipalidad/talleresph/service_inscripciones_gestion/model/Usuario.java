package com.municipalidad.talleresph.service_inscripciones_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "rut", unique = true, length = 255)
    private String rut;

    @Column(name = "nombre", length = 255)
    private String nombre;

    @Column(name = "apellido", length = 255)
    private String apellido;

    @Column(name = "correo", unique = true, nullable = false, length = 255)
    private String correo;

    @Column(name = "telefono", length = 255)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "genero", length = 50)
    private String genero;

    @Column(name = "cargo", length = 255)
    private String cargo;

    @Column(name = "centro_salud", length = 255)
    private String centroSalud;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Builder.Default
    @Column(name = "activo")
    private Boolean activo = true;

}
