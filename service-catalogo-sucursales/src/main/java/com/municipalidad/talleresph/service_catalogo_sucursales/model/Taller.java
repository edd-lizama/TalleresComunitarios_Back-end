package com.municipalidad.talleresph.service_catalogo_sucursales.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "taller")
@Data 
@NoArgsConstructor 
public class Taller 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_taller")
    private Long idTaller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal")
    private Sucursal sucursal;

    @Column(name = "nombre_taller")
    private String nombreTaller;

    @Column(name = "programa_salud")
    private String programaSalud;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "cupo_maximo")
    private Integer cupoMaximo;

    @Column(name = "es_online")
    private Boolean esOnline;

    @Column(name = "enlace_reunion")
    private String enlaceReunion;
    
}
