package com.municipalidad.talleresph.service_inscripciones_gestion.service;

import com.municipalidad.talleresph.service_inscripciones_gestion.dto.*;
import com.municipalidad.talleresph.service_inscripciones_gestion.model.*;
import com.municipalidad.talleresph.service_inscripciones_gestion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenciaService 
{
    private final TallerRepository tallerRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final SesionAsistenciaRepository sesionAsistenciaRepository;
    private final DetalleAsistenciaRepository detalleAsistenciaRepository;

    @Transactional(readOnly = true)
    public AsistenciaDiaResponseDTO obtenerAsistenciaDia(Long tallerId, LocalDate fecha) 
    {
        LocalDate fechaConsulta = (fecha != null) ? fecha : LocalDate.now();

        if (!tallerRepository.existsById(tallerId)) 
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado");
        }

        List<Inscripcion> inscripcionesActivas = inscripcionRepository
                .findByTallerIdAndEstadoOrderByIdAsc(tallerId, "ACTIVA");

        Optional<SesionAsistencia> sesionOpt = sesionAsistenciaRepository
                .findByTallerIdAndFechaClase(tallerId, fechaConsulta);

        String fotoEvidencia = null;
        Map<Long, DetalleAsistencia> detallesPorAlumno = new HashMap<>();

        if (sesionOpt.isPresent()) 
        {
            SesionAsistencia sesion = sesionOpt.get();
            fotoEvidencia = sesion.getFotoEvidencia();
            List<DetalleAsistencia> listaDetalles = detalleAsistenciaRepository.findBySesionId(sesion.getId());
            if (listaDetalles != null) 
            {
                for (DetalleAsistencia d : listaDetalles) 
                {
                    if (d.getUsuario() != null && d.getUsuario().getId() != null) 
                    {
                        detallesPorAlumno.put(d.getUsuario().getId(), d);
                    }
                }
            }
        }

        final Map<Long, DetalleAsistencia> mapaDetalles = detallesPorAlumno;

        List<AlumnoAsistenciaResponseDTO> nomina = inscripcionesActivas.stream()
                .map(inscripcion -> {
                    Usuario alumno = inscripcion.getUsuario();
                    DetalleAsistencia detalle = mapaDetalles.get(alumno.getId());

                    String estado = "pendiente";
                    String justificacion = "";

                    if (detalle != null) 
                    {
                        if (detalle.getEstado() != null && !detalle.getEstado().isBlank()) 
                        {
                            estado = detalle.getEstado();
                        }
                        if (detalle.getJustificacion() != null) 
                        {
                            justificacion = detalle.getJustificacion();
                        } 
                        else if (detalle.getObservacion() != null) 
                        {
                            justificacion = detalle.getObservacion();
                        }
                    }

                    return AlumnoAsistenciaResponseDTO.builder()
                            .id(alumno.getId())
                            .nombre(alumno.getNombre() + (alumno.getApellido() != null ? " " + alumno.getApellido() : ""))
                            .edad(alumno.getEdad())
                            .genero(alumno.getGenero())
                            .estado(estado)
                            .justificacion(justificacion)
                            .build();
                })
                .collect(Collectors.toList());

        return AsistenciaDiaResponseDTO.builder()
                .tallerId(tallerId)
                .fecha(fechaConsulta)
                .fotoEvidencia(fotoEvidencia)
                .alumnos(nomina)
                .build();
    }

    @Transactional
    public MensajeResponseDTO guardarAsistencia(Long tallerId, GuardarAsistenciaRequestDTO request) 
    {
        LocalDate fechaClase = (request.getFecha() != null) ? request.getFecha() : LocalDate.now();

        Taller taller = tallerRepository.findById(tallerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado"));

        Long monitorId = request.getMonitorId() != null
                ? request.getMonitorId()
                : (taller.getMonitor() != null ? taller.getMonitor().getId() : null);

        if (monitorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se especificó monitorId para la sesión");
        }

        Usuario monitor = usuarioRepository.findById(monitorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitor no encontrado"));

        // UPSERT de cabecera de sesión (taller_id, fecha_clase)
        SesionAsistencia sesion = sesionAsistenciaRepository
                .findByTallerIdAndFechaClase(tallerId, fechaClase)
                .orElseGet(() -> SesionAsistencia.builder()
                        .taller(taller)
                        .monitor(monitor)
                        .fechaClase(fechaClase)
                        .build());

        sesion.setMonitor(monitor);
        if (request.getFotoEvidencia() != null && !request.getFotoEvidencia().isBlank()) 
        {
            sesion.setFotoEvidencia(request.getFotoEvidencia());
        }
        final SesionAsistencia sesionGuardada = sesionAsistenciaRepository.save(sesion);

        // UPSERT de detalle individual por alumno
        if (request.getAlumnos() != null) 
        {
            for (AlumnoAsistenciaItemDTO item : request.getAlumnos()) 
            {
                Usuario alumno = usuarioRepository.findById(item.getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Alumno no encontrado con ID: " + item.getId()));

                DetalleAsistencia detalle = detalleAsistenciaRepository
                        .findBySesionIdAndUsuarioId(sesionGuardada.getId(), alumno.getId())
                        .orElseGet(() -> DetalleAsistencia.builder()
                                .sesion(sesionGuardada)
                                .usuario(alumno)
                                .build());

                detalle.setEstado(item.getEstado() != null ? item.getEstado() : "pendiente");
                String justificacion = item.getJustificacion() != null ? item.getJustificacion() : "";
                detalle.setJustificacion(justificacion);
                detalleAsistenciaRepository.save(detalle);
            }
        }

        return new MensajeResponseDTO("Asistencia registrada correctamente");
    }

    @Transactional(readOnly = true)
    public EstadisticasMonitorDTO obtenerEstadisticasMonitor(Long monitorId) 
    {
        int talleres = (int) tallerRepository.countByMonitorIdAndActivoTrue(monitorId);
        int registros = (int) detalleAsistenciaRepository.countRegistrosByMonitorId(monitorId);

        YearMonth mesActual = YearMonth.now();
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();
        int esteMes = (int) detalleAsistenciaRepository.countRegistrosMesByMonitorId(monitorId, inicioMes, finMes);

        return EstadisticasMonitorDTO.builder()
                .talleres(talleres)
                .registros(registros)
                .esteMes(esteMes)
                .build();
    }
}
