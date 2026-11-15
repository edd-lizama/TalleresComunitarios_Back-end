package com.municipalidad.talleresph.service_catalogo_sucursales.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "clase_horario")
@Data 
@NoArgsConstructor 
public class ClaseHorario 
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clase")
    private Long idClase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_taller")
    private Taller taller;

    @Column(name = "dia")
    private String dia;

    @Column(name = "horario")
    private String horario;

}
