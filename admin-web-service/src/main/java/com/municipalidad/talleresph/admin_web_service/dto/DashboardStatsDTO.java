package com.municipalidad.talleresph.admin_web_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private KpisDTO kpis;
    private List<AsistenciaMensualDTO> asistenciaMensual;
    private List<DistribucionEdadDTO> distribucionEdad;
    private List<ResumenProgramaDTO> resumenProgramas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpisDTO {
        private Long talleresActivos;
        private Long variacionTalleresMes;
        private Double asistenciaGeneralPorcentaje;
        private Double tasaDesercionPorcentaje;
        private Long usuariosEnrolados;
        private Long nuevosUsuariosMes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AsistenciaMensualDTO {
        private String mes;
        private String anioMes;
        private Double porcentaje;
        private Long presentes;
        private Long totalRegistros;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionEdadDTO {
        private String rango;
        private Long cantidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenProgramaDTO {
        private String programa;
        private Long inscritos;
        private Double porcentajeAsistencia;
        private Long talleresActivos;
    }
}
