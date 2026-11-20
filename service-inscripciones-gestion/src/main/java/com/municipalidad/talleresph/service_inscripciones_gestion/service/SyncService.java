package com.municipalidad.talleresph.service_inscripciones_gestion.service;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.*;
import com.municipalidad.talleresph.service_inscripciones_gestion.model.Usuario;
import com.municipalidad.talleresph.service_inscripciones_gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService 
{
    private final InscripcionService inscripcionService;
    private final AsistenciaService asistenciaService;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public SyncResponseDTO procesarLote(SyncLoteRequestDTO lote) 
    {
        if (lote == null || lote.getOperaciones() == null || lote.getOperaciones().isEmpty()) 
        {
            return SyncResponseDTO.builder()
                    .sincronizado(true)
                    .procesadas(0)
                    .mensaje("No hay operaciones pendientes para sincronizar")
                    .build();
        }

        // Ordenar cronológicamente por id ascendente
        List<OperacionSyncDTO> operaciones = new ArrayList<>(lote.getOperaciones());
        operaciones.sort(Comparator.comparing(OperacionSyncDTO::getId, Comparator.nullsLast(Comparator.naturalOrder())));

        int procesadas = 0;

        for (OperacionSyncDTO op : operaciones) 
        {
            String tipo = op.getTipo() != null ? op.getTipo().toUpperCase() : "";
            Map<String, Object> payload = op.getPayload() != null ? op.getPayload() : Map.of();
            LocalDate fechaEvento = parsearFecha(op.getFecha());

            switch (tipo) {
                case "GUARDAR_ASISTENCIA" -> 
                {
                    Long tallerId = extraerLong(payload, "tallerId", null);
                    Long monitorId = extraerLong(payload, "monitorId", lote.getUsuarioId());
                    LocalDate fechaClase = (payload.get("fecha") != null && !payload.get("fecha").toString().isBlank())
                            ? parsearFecha(payload.get("fecha").toString())
                            : fechaEvento;
                    String fotoEvidencia = payload.get("fotoEvidencia") != null
                            ? payload.get("fotoEvidencia").toString()
                            : null;

                    List<AlumnoAsistenciaItemDTO> alumnos = extraerListaAlumnos(payload);

                    if (tallerId != null) 
                    {
                        asistenciaService.guardarAsistencia(tallerId, monitorId, fechaClase, fotoEvidencia, alumnos);
                    }
                    else 
                    {
                        log.warn("Sync GUARDAR_ASISTENCIA omitida (opId={}): tallerId es null", op.getId());
                    }
                }

                case "JUSTIFICAR_ASISTENCIA" -> 
                {
                    Long detalleId = extraerLong(payload, "detalleId", null);
                    Long usuarioId = extraerLong(payload, "usuarioId", lote.getUsuarioId());
                    Long sesionId = extraerLong(payload, "sesionId", null);
                    Long tallerId = extraerLong(payload, "tallerId", null);
                    LocalDate fecha = (payload.get("fecha") != null && !payload.get("fecha").toString().isBlank())
                            ? parsearFecha(payload.get("fecha").toString())
                            : fechaEvento;
                    String justificacion = payload.get("justificacion") != null
                            ? payload.get("justificacion").toString()
                            : (payload.get("observacion") != null ? payload.get("observacion").toString() : "");

                    JustificacionRequestDTO justDTO = JustificacionRequestDTO.builder()
                            .detalleId(detalleId)
                            .usuarioId(usuarioId)
                            .sesionId(sesionId)
                            .tallerId(tallerId)
                            .fecha(fecha)
                            .justificacion(justificacion)
                            .build();

                    try 
                    {
                        asistenciaService.guardarJustificacion(justDTO);
                    } 
                    catch (ResponseStatusException e) 
                    {
                        log.warn("Sync JUSTIFICAR_ASISTENCIA omitida (opId={}): {}", op.getId(), e.getReason());
                    }
                }

                case "INSCRIPCION" -> 
                {
                    Long usuarioId = extraerLong(payload, "usuarioId", lote.getUsuarioId());
                    Long tallerId = extraerLong(payload, "tallerId", null);
                    try 
                    {
                        inscripcionService.inscribirUsuario(usuarioId, tallerId);
                    } 
                    catch (ResponseStatusException e) 
                    {
                        log.warn("Sync INSCRIPCION omitida (opId={}): {}", op.getId(), e.getReason());
                    }
                }

                case "ANULAR_INSCRIPCION" -> 
                {
                    Long usuarioId = extraerLong(payload, "usuarioId", lote.getUsuarioId());
                    Long tallerId = extraerLong(payload, "tallerId", null);
                    try 
                    {
                        inscripcionService.anularInscripcion(usuarioId, tallerId);
                    } 
                    catch (ResponseStatusException e) 
                    {
                        log.warn("Sync ANULAR_INSCRIPCION omitida (opId={}): {}", op.getId(), e.getReason());
                    }
                }

                case "ACTUALIZAR_PERFIL" -> 
                {
                    Long usuarioId = extraerLong(payload, "usuarioId", extraerLong(payload, "id", lote.getUsuarioId()));
                    if (usuarioId != null) 
                    {
                        usuarioRepository.findById(usuarioId).ifPresentOrElse(usuario -> {
                            if (payload.containsKey("nombre") && payload.get("nombre") != null) {
                                usuario.setNombre(payload.get("nombre").toString());
                            }
                            if (payload.containsKey("apellido") && payload.get("apellido") != null) {
                                usuario.setApellido(payload.get("apellido").toString());
                            }
                            if (payload.containsKey("telefono") && payload.get("telefono") != null) {
                                usuario.setTelefono(payload.get("telefono").toString());
                            }
                            if (payload.containsKey("direccion") && payload.get("direccion") != null) {
                                usuario.setDireccion(payload.get("direccion").toString());
                            }
                            if (payload.containsKey("fotoPerfil") && payload.get("fotoPerfil") != null) {
                                usuario.setFotoPerfil(payload.get("fotoPerfil").toString());
                            } else if (payload.containsKey("foto_perfil") && payload.get("foto_perfil") != null) {
                                usuario.setFotoPerfil(payload.get("foto_perfil").toString());
                            }
                            usuarioRepository.save(usuario);
                            log.info("Operación ACTUALIZAR_PERFIL aplicada con éxito para usuarioId={} (opId={})", usuarioId, op.getId());
                        }, () -> log.warn("Sync ACTUALIZAR_PERFIL: usuario con id {} no encontrado", usuarioId));
                    } 
                    else 
                    {
                        log.warn("Sync ACTUALIZAR_PERFIL omitida: no se pudo determinar el usuarioId (opId={})", op.getId());
                    }
                }

                default -> log.warn("Tipo de operación offline desconocido: {}", tipo);
            }

            procesadas++;
        }

        return SyncResponseDTO.builder()
                .sincronizado(true)
                .procesadas(procesadas)
                .mensaje("Lote sincronizado correctamente")
                .build();
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
                    String justificacion = alumnoMap.get("justificacion") != null 
                            ? alumnoMap.get("justificacion").toString() 
                            : (alumnoMap.get("observacion") != null ? alumnoMap.get("observacion").toString() : "");

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
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                return fallback;
            }
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
