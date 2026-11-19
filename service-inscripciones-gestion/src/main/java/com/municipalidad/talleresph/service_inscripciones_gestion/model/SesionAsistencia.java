package com.municipalidad.talleresph.service_inscripciones_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "sesion_asistencia",
    uniqueConstraints = @UniqueConstraint(name = "uq_taller_fecha", columnNames = {"taller_id", "fecha_clase"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionAsistencia 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taller_id", nullable = false)
    private Taller taller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Usuario monitor;

    @Column(name = "fecha_clase", nullable = false)
    private LocalDate fechaClase;

    @Column(name = "foto_evidencia", columnDefinition = "TEXT")
    private String fotoEvidencia;

    @Column(name = "fecha_sincronizacion")
    private LocalDateTime fechaSincronizacion;

    @PrePersist
    @PreUpdate
    protected void onSync() 
    {
        this.fechaSincronizacion = LocalDateTime.now();
    }
}
