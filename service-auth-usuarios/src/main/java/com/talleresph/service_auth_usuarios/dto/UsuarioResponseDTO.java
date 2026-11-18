package com.talleresph.service_auth_usuarios.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;     
    private String telefono;
    private String direccion;
    private String fotoPerfil;
    // Campos exclusivos Monitor (nulos si es vecino)
    private String cargo;
    private String centroSalud;
    private EstadisticasDTO estadisticas;
    
    @Getter
    @Setter
    @Builder
    public static class EstadisticasDTO {
        private Integer talleres;
        private Integer registros;
        private Integer esteMes;
    }
}
