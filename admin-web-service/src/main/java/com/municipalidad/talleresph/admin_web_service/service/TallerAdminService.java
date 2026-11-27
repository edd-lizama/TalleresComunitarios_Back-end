package com.municipalidad.talleresph.admin_web_service.service;

import com.municipalidad.talleresph.admin_web_service.dto.*;
import com.municipalidad.talleresph.admin_web_service.model.Recinto;
import com.municipalidad.talleresph.admin_web_service.model.Taller;
import com.municipalidad.talleresph.admin_web_service.model.Usuario;
import com.municipalidad.talleresph.admin_web_service.repository.RecintoRepository;
import com.municipalidad.talleresph.admin_web_service.repository.TallerRepository;
import com.municipalidad.talleresph.admin_web_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TallerAdminService {

    private final TallerRepository tallerRepository;
    private final RecintoRepository recintoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<TallerAdminResponseDTO> listarTalleres() {
        return tallerRepository.findAllWithMonitorOrderByIdAsc()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TallerAdminResponseDTO crearTaller(TallerAdminRequestDTO request) {
        Taller taller = new Taller();
        aplicarDatosTaller(taller, request, true);
        Taller guardado = tallerRepository.save(taller);
        return toResponseDTO(guardado);
    }

    @Transactional
    public TallerAdminResponseDTO actualizarTaller(Long id, TallerAdminRequestDTO request) {
        Taller taller = tallerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado con id " + id));

        aplicarDatosTaller(taller, request, false);
        Taller actualizado = tallerRepository.save(taller);
        return toResponseDTO(actualizado);
    }

    @Transactional
    public TallerAdminResponseDTO cambiarEstado(Long id, CambiarEstadoTallerDTO request) {
        Taller taller = tallerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Taller no encontrado con id " + id));

        boolean nuevoEstado;
        if (request != null && request.getActivo() != null) {
            nuevoEstado = request.getActivo();
        } else if (request != null && request.getEstado() != null && !request.getEstado().isBlank()) {
            nuevoEstado = !"Suspendido".equalsIgnoreCase(request.getEstado().trim());
        } else {
            nuevoEstado = !Boolean.TRUE.equals(taller.getActivo());
        }

        taller.setActivo(nuevoEstado);
        Taller actualizado = tallerRepository.save(taller);
        return toResponseDTO(actualizado);
    }

    @Transactional(readOnly = true)
    public List<RecintoDTO> listarRecintos() {
        return recintoRepository.findByActivoTrueOrderByIdAsc()
                .stream()
                .map(r -> RecintoDTO.builder()
                        .id(r.getId())
                        .nombre(r.getNombre())
                        .direccion(r.getDireccion())
                        .lat(r.getLat())
                        .lng(r.getLng())
                        .activo(r.getActivo())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramaSaludDTO> listarProgramas() {
        List<Taller> todos = tallerRepository.findAll();
        Map<String, Long> conteoPorCategoria = todos.stream()
                .collect(Collectors.groupingBy(Taller::getCategoria, Collectors.counting()));

        return conteoPorCategoria.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> ProgramaSaludDTO.builder()
                        .nombre(entry.getKey())
                        .cantidadTalleres(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private void aplicarDatosTaller(Taller taller, TallerAdminRequestDTO request, boolean esNuevo) {
        taller.setNombre(request.getNombre().trim());
        taller.setCategoria(request.getProgramaSalud().trim());
        taller.setDescripcion(request.getDescripcion().trim());
        taller.setImagen(request.getImagen());
        taller.setDias(request.getDias().trim());
        taller.setHorario(request.getHorario().trim());
        taller.setHorarioLargo(
                request.getHorarioLargo() != null && !request.getHorarioLargo().isBlank()
                        ? request.getHorarioLargo().trim()
                        : request.getHorario().trim()
        );

        int inscritosActuales = esNuevo ? 0 : tallerRepository.countInscritosActivosByTallerId(taller.getId()).intValue();
        int cupoMaximo = request.getCupoMaximo();
        taller.setCuposTotales(cupoMaximo);
        taller.setCuposDisponibles(Math.max(0, cupoMaximo - inscritosActuales));

        if (request.getRecintoId() != null) {
            Recinto recinto = recintoRepository.findById(request.getRecintoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recinto no encontrado con id " + request.getRecintoId()));
            taller.setLugar(recinto.getNombre());
            taller.setLat(recinto.getLat());
            taller.setLng(recinto.getLng());
        } else if (request.getLugar() != null && request.getLat() != null && request.getLng() != null) {
            taller.setLugar(request.getLugar().trim());
            taller.setLat(request.getLat());
            taller.setLng(request.getLng());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar recintoId o lugar con coordenadas (lat, lng)");
        }

        if (request.getProfesorId() != null) {
            Usuario profesor = usuarioRepository.findById(request.getProfesorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con id " + request.getProfesorId()));
            taller.setMonitor(profesor);
        } else {
            taller.setMonitor(null);
        }

        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            taller.setActivo(!"Suspendido".equalsIgnoreCase(request.getEstado().trim()));
        } else if (esNuevo) {
            taller.setActivo(true);
        }
    }

    private TallerAdminResponseDTO toResponseDTO(Taller taller) {
        Long inscritosActuales = tallerRepository.countInscritosActivosByTallerId(taller.getId());
        String profesorNombre = "Sin profesor asignado";
        Long profesorId = null;

        if (taller.getMonitor() != null) {
            profesorId = taller.getMonitor().getIdUsuario();
            String apellido = taller.getMonitor().getApellido() != null ? " " + taller.getMonitor().getApellido() : "";
            profesorNombre = (taller.getMonitor().getNombre() + apellido).trim();
        }

        boolean activo = Boolean.TRUE.equals(taller.getActivo());

        return TallerAdminResponseDTO.builder()
                .id(taller.getId())
                .nombre(taller.getNombre())
                .programaSalud(taller.getCategoria())
                .descripcion(taller.getDescripcion())
                .imagen(taller.getImagen())
                .dias(taller.getDias())
                .horario(taller.getHorario())
                .horarioLargo(taller.getHorarioLargo())
                .cupoMaximo(taller.getCuposTotales())
                .cuposDisponibles(taller.getCuposDisponibles())
                .inscritosActuales(inscritosActuales)
                .recinto(taller.getLugar())
                .lat(taller.getLat())
                .lng(taller.getLng())
                .profesorId(profesorId)
                .profesorNombre(profesorNombre)
                .estado(activo ? "Activo" : "Suspendido")
                .activo(activo)
                .build();
    }
}
