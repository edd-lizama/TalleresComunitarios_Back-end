package com.municipalidad.talleresph.service_inscripciones_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "taller")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Taller 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "imagen", columnDefinition = "TEXT")
    private String imagen;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "lugar", nullable = false, length = 150)
    private String lugar;

    @Column(name = "dias", nullable = false, length = 100)
    private String dias;

    @Column(name = "horario", nullable = false, length = 60)
    private String horario;

    @Column(name = "horario_largo", nullable = false, length = 60)
    private String horarioLargo;

    @Column(name = "cupos_totales", nullable = false)
    private Integer cuposTotales;

    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id")
    private Usuario monitor;

    @Column(name = "activo", nullable = false)
    private Boolean activo;
}
