package com.municipalidad.talleresph.service_inscripciones_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "inscripcion",
    uniqueConstraints = @UniqueConstraint(name = "uq_usuario_taller", columnNames = {"usuario_id", "taller_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taller_id", nullable = false)
    private Taller taller;

    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @PrePersist
    protected void onCreate() 
    {
        if (this.fechaInscripcion == null) {
            this.fechaInscripcion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "ACTIVA";
        }
    }
}
