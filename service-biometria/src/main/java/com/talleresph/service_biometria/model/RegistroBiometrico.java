package com.talleresph.service_biometria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "registro_biometrico")
@Getter
@Setter
@NoArgsConstructor
public class RegistroBiometrico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_biometrico")
    private Integer idBiometrico;

    @Column(name = "id_perfil", nullable = false)
    private Integer idPerfil;

    @Column(name = "fecha_medicion", nullable = false)
    private LocalDate fechaMedicion;

    /** Centímetros */
    @Column(name = "altura", nullable = false, precision = 5, scale = 2)
    private BigDecimal altura;

    /** Kilogramos */
    @Column(name = "peso", nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(name = "indice_masa_corporal", nullable = false, precision = 4, scale = 1)
    private BigDecimal indiceMasaCorporal;
}
