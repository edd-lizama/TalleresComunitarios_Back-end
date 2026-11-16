package com.municipalidad.talleresph.service_inscripciones_gestion.service;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService 
{

    private final InscripcionService inscripcionService;
    private final AsistenciaService asistenciaService;

    @Transactional
    public MensajeResponseDTO procesarLote(SyncLoteRequestDTO lote) 
    {
        if (lote == null || lote.getOperaciones() == null || lote.getOperaciones().isEmpty()) 
        {
            return new MensajeResponseDTO("No hay operaciones pendientes para sincronizar");
        }

        int procesadas = 0;

        for (OperacionSyncDTO op : lote.getOperaciones()) 
            {
            String tipo = op.getTipo() != null ? op.getTipo().toUpperCase() : "";
            Map<String, Object> payload = op.getPayload() != null ? op.getPayload() : Map.of();
            LocalDate fechaEvento = parsearFecha(op.getFecha());

            switch (tipo) {
                case "INSCRIPCION" -> 
                {
                    Long usuarioId = extraerLong(payload, "usuarioId", lote.getUsuarioId());
                    Long tallerId = extraerLong(payload, "tallerId", null);
                    try {
                        inscripcionService.inscribirVecino(new InscripcionRequestDTO(usuarioId, tallerId));
                    } catch (ResponseStatusException e) {
                        log.warn("Sync INSCRIPCION omitida (opId={}): {}", op.getId(), e.getReason());
                    }
                }

                case "ANULAR_INSCRIPCION" -> 
                {
                    Long usuarioId = extraerLong(payload, "usuarioId", lote.getUsuarioId());
                    Long tallerId = extraerLong(payload, "tallerId", null);
                    try {
                        inscripcionService.anularInscripcion(usuarioId, tallerId);
                    } catch (ResponseStatusException e) {
                        log.warn("Sync ANULAR_INSCRIPCION omitida (opId={}): {}", op.getId(), e.getReason());
                    }
                }

                case "GUARDAR_ASISTENCIA" -> 
                {
                    Long tallerId = extraerLong(payload, "tallerId", null);
                    Long monitorId = extraerLong(payload, "monitorId", lote.getUsuarioId());
                    LocalDate fechaClase = payload.get("fecha") != null
                            ? parsearFecha(payload.get("fecha").toString())
                            : fechaEvento;
                    String fotoEvidencia = payload.get("fotoEvidencia") != null
                            ? payload.get("fotoEvidencia").toString()
                            : null;

                    List<AlumnoAsistenciaItemDTO> alumnos = extraerListaAlumnos(payload);

                    GuardarAsistenciaRequestDTO dtoAsistencia = GuardarAsistenciaRequestDTO.builder()
                            .monitorId(monitorId)
                            .fecha(fechaClase)
                            .fotoEvidencia(fotoEvidencia)
                            .alumnos(alumnos)
                            .build();

                    asistenciaService.guardarAsistencia(tallerId, dtoAsistencia);
                }

                case "ACTUALIZAR_PERFIL" -> 
                {
                    log.info("Operación ACTUALIZAR_PERFIL recibida en lote (opId={})", op.getId());
                }
                default -> log.warn("Tipo de operación offline desconocido: {}", tipo);
            }

            procesadas++;
        }

        return new MensajeResponseDTO("Sincronización completada: " + procesadas + " operaciones procesadas");
    }

    @SuppressWarnings("unchecked")
    private List<AlumnoAsistenciaItemDTO> extraerListaAlumnos(Map<String, Object> payload) 
    {
        Object rawLista = payload.containsKey("alumnosActualizados")
                ? payload.get("alumnosActualizados")
                : payload.get("alumnos");

        List<AlumnoAsistenciaItemDTO> resultado = new ArrayList<>();
        if (rawLista instanceof List<?> lista) 
        {
            for (Object itemObj : lista) 
            {
                if (itemObj instanceof Map<?, ?> mapItem)
                {
                    Map<String, Object> alumnoMap = (Map<String, Object>) mapItem;
                    Long id = extraerLong(alumnoMap, "id", null);
                    String estado = alumnoMap.get("estado") != null ? alumnoMap.get("estado").toString() : "pendiente";
                    String justificacion = alumnoMap.get("justificacion") != null ? alumnoMap.get("justificacion").toString() : "";

                    if (id != null) 
                    {
                        resultado.add(new AlumnoAsistenciaItemDTO(id, estado, justificacion));
                    }
                }
            }
        }
        return resultado;
    }

    private Long extraerLong(Map<String, Object> map, String key, Long fallback) 
    {
        Object val = map.get(key);

        if (val instanceof Number num) 
        {
            return num.longValue();
        }

        if (val instanceof String str && !str.isBlank()) 
        {
            return Long.parseLong(str);
        }
        return fallback;
    }

    private LocalDate parsearFecha(String fechaStr) 
    {
        if (fechaStr == null || fechaStr.isBlank()) 
        {
            return LocalDate.now();
        }
        try 
        {
            return OffsetDateTime.parse(fechaStr).toLocalDate();
        } 
        catch (Exception e) 
        {
            try 
            {
                return LocalDate.parse(fechaStr.substring(0, 10));
            } 
            catch (Exception ex) 
            {
                return LocalDate.now();
            }
            
        }

    }
}