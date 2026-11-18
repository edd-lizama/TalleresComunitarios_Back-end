package com.talleresph.service_auth_usuarios.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @Column(name = "rut", unique = true, length = 255)
    private String rut;

    @Column(name = "nombre", length = 255)
    private String nombre;
    
    // --- CAMPOS NUEVOS FRONTEND ---
    @Column(name = "apellido", length = 255)
    private String apellido;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "genero", length = 50)
    private String genero;

    @Column(name = "cargo", length = 255)
    private String cargo;

    @Column(name = "centro_salud", length = 255)
    private String centroSalud;

    @Column(name = "activo")
    private Boolean activo = true;
    // ----------------------------------

    @Column(name = "es_de_la_comuna")
    private Boolean esDeLaComuna;

    @Column(name = "correo", unique = true, length = 255)
    private String correo;

    @Column(name = "telefono", length = 255)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "password", length = 255)
    private String password;

    // Imagen en formato DataURL Base64 (data:image/jpeg;base64,...) -> columna TEXT
    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;
}
