package com.municipalidad.talleresph.service_catalogo_sucursales.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity 
@Table(name = "sucursal")
@Data 
@NoArgsConstructor 
public class Sucursal 
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Column(name = "nombreSucursal")
    private String nombreSucursal;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "latitud", precision= 10, scale = 7)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 10, scale = 7)
    private BigDecimal longitud;
}
