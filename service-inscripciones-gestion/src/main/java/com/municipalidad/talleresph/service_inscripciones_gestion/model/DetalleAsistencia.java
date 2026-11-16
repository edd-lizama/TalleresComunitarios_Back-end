package com.municipalidad.talleresph.service_inscripciones_gestion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "detalle_asistencia",
    uniqueConstraints = @UniqueConstraint(name = "uq_sesion_usuario", columnNames = {"sesion_id", "usuario_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleAsistencia 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionAsistencia sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "justificacion", columnDefinition = "TEXT")
    private String justificacion;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = "pendiente";
        }
    }
}
