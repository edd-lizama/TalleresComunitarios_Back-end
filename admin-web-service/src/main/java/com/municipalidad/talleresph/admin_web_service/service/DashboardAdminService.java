package com.municipalidad.talleresph.admin_web_service.service;

import com.municipalidad.talleresph.admin_web_service.dto.DashboardStatsDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardAdminService {

    private static final String[] MESES_CORTOS = {
            "", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public DashboardStatsDTO obtenerEstadisticas() {
        return DashboardStatsDTO.builder()
                .kpis(obtenerKpis())
                .asistenciaMensual(obtenerAsistenciaMensual())
                .distribucionEdad(obtenerDistribucionEdad())
                .resumenProgramas(obtenerResumenProgramas())
                .build();
    }

    private DashboardStatsDTO.KpisDTO obtenerKpis() {
        long talleresActivos = queryLong("SELECT COUNT(*) FROM taller WHERE activo = true");
        long variacionTalleresMes = queryLong(
                "SELECT COUNT(*) FROM taller WHERE activo = true AND fecha_creacion >= date_trunc('month', CURRENT_DATE)"
        );
        double asistenciaGeneral = queryDouble(
                "SELECT COALESCE(ROUND(100.0 * SUM(CASE WHEN LOWER(estado) = 'asiste' THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 1), 0.0) FROM detalle_asistencia"
        );
        double tasaDesercion = queryDouble(
                "SELECT COALESCE(ROUND(100.0 * SUM(CASE WHEN UPPER(estado) IN ('ANULADA', 'CANCELADA', 'DESERTOR') THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 1), 0.0) FROM inscripcion"
        );
        long usuariosEnrolados = queryLong(
                "SELECT COUNT(*) FROM usuario u JOIN rol r ON u.id_rol = r.id_rol WHERE r.nombre_rol = 'VECINO' AND u.activo = true"
        );
        long nuevosUsuariosMes = queryLong(
                "SELECT COUNT(*) FROM usuario u JOIN rol r ON u.id_rol = r.id_rol WHERE r.nombre_rol = 'VECINO' AND u.activo = true AND u.fecha_creacion >= date_trunc('month', CURRENT_DATE)"
        );

        return DashboardStatsDTO.KpisDTO.builder()
                .talleresActivos(talleresActivos)
                .variacionTalleresMes(variacionTalleresMes)
                .asistenciaGeneralPorcentaje(asistenciaGeneral)
                .tasaDesercionPorcentaje(tasaDesercion)
                .usuariosEnrolados(usuariosEnrolados)
                .nuevosUsuariosMes(nuevosUsuariosMes)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<DashboardStatsDTO.AsistenciaMensualDTO> obtenerAsistenciaMensual() {
        String sql = """
                SELECT TO_CHAR(s.fecha_clase, 'YYYY-MM') AS anio_mes,
                       CAST(EXTRACT(MONTH FROM s.fecha_clase) AS INTEGER) AS mes_num,
                       SUM(CASE WHEN LOWER(d.estado) = 'asiste' THEN 1 ELSE 0 END) AS presentes,
                       COUNT(*) AS total
                FROM sesion_asistencia s
                JOIN detalle_asistencia d ON d.sesion_id = s.id
                WHERE s.fecha_clase >= (CURRENT_DATE - INTERVAL '6 months')
                GROUP BY TO_CHAR(s.fecha_clase, 'YYYY-MM'), EXTRACT(MONTH FROM s.fecha_clase)
                ORDER BY anio_mes ASC
                """;

        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        List<DashboardStatsDTO.AsistenciaMensualDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            String anioMes = (String) row[0];
            int mesNum = ((Number) row[1]).intValue();
            long presentes = ((Number) row[2]).longValue();
            long total = ((Number) row[3]).longValue();
            double porcentaje = total > 0 ? Math.round((presentes * 1000.0) / total) / 10.0 : 0.0;
            String mesNombre = (mesNum >= 1 && mesNum <= 12) ? MESES_CORTOS[mesNum] : anioMes;

            result.add(DashboardStatsDTO.AsistenciaMensualDTO.builder()
                    .mes(mesNombre)
                    .anioMes(anioMes)
                    .porcentaje(porcentaje)
                    .presentes(presentes)
                    .totalRegistros(total)
                    .build());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<DashboardStatsDTO.DistribucionEdadDTO> obtenerDistribucionEdad() {
        Map<String, Long> buckets = new LinkedHashMap<>();
        buckets.put("18-35", 0L);
        buckets.put("36-50", 0L);
        buckets.put("51-65", 0L);
        buckets.put("65+", 0L);

        String sql = """
                SELECT CASE
                         WHEN u.edad BETWEEN 18 AND 35 THEN '18-35'
                         WHEN u.edad BETWEEN 36 AND 50 THEN '36-50'
                         WHEN u.edad BETWEEN 51 AND 65 THEN '51-65'
                         WHEN u.edad > 65 THEN '65+'
                         ELSE NULL
                       END AS rango,
                       COUNT(DISTINCT u.id_usuario) AS cantidad
                FROM usuario u
                JOIN rol r ON u.id_rol = r.id_rol
                JOIN inscripcion i ON i.usuario_id = u.id_usuario AND i.estado = 'ACTIVA'
                WHERE r.nombre_rol = 'VECINO' AND u.activo = true AND u.edad IS NOT NULL
                GROUP BY rango
                """;

        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        for (Object[] row : rows) {
            String rango = (String) row[0];
            if (rango != null && buckets.containsKey(rango)) {
                buckets.put(rango, ((Number) row[1]).longValue());
            }
        }

        List<DashboardStatsDTO.DistribucionEdadDTO> result = new ArrayList<>();
        buckets.forEach((rango, cantidad) ->
                result.add(DashboardStatsDTO.DistribucionEdadDTO.builder()
                        .rango(rango)
                        .cantidad(cantidad)
                        .build())
        );
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<DashboardStatsDTO.ResumenProgramaDTO> obtenerResumenProgramas() {
        String sql = """
                SELECT t.categoria AS programa,
                       COUNT(DISTINCT CASE WHEN t.activo = true THEN t.id END) AS talleres_activos,
                       COUNT(DISTINCT CASE WHEN i.estado = 'ACTIVA' THEN i.id END) AS inscritos,
                       COALESCE(ROUND(
                           100.0 * COUNT(DISTINCT CASE WHEN LOWER(d.estado) = 'asiste' THEN d.id END)
                           / NULLIF(COUNT(DISTINCT d.id), 0), 1), 0.0) AS porcentaje_asistencia
                FROM taller t
                LEFT JOIN inscripcion i ON i.taller_id = t.id
                LEFT JOIN sesion_asistencia s ON s.taller_id = t.id
                LEFT JOIN detalle_asistencia d ON d.sesion_id = s.id
                GROUP BY t.categoria
                ORDER BY inscritos DESC, t.categoria ASC
                """;

        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        List<DashboardStatsDTO.ResumenProgramaDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(DashboardStatsDTO.ResumenProgramaDTO.builder()
                    .programa((String) row[0])
                    .talleresActivos(((Number) row[1]).longValue())
                    .inscritos(((Number) row[2]).longValue())
                    .porcentajeAsistencia(((Number) row[3]).doubleValue())
                    .build());
        }
        return result;
    }

    private long queryLong(String sql) {
        Object res = entityManager.createNativeQuery(sql).getSingleResult();
        return res != null ? ((Number) res).longValue() : 0L;
    }

    private double queryDouble(String sql) {
        Object res = entityManager.createNativeQuery(sql).getSingleResult();
        return res != null ? ((Number) res).doubleValue() : 0.0;
    }
}
