package com.municipalidad.talleresph.admin_web_service.service;

import com.municipalidad.talleresph.admin_web_service.dto.ProfesorRequestDTO;
import com.municipalidad.talleresph.admin_web_service.dto.ProfesorResponseDTO;
import com.municipalidad.talleresph.admin_web_service.model.Rol;
import com.municipalidad.talleresph.admin_web_service.model.Taller;
import com.municipalidad.talleresph.admin_web_service.model.Usuario;
import com.municipalidad.talleresph.admin_web_service.repository.RolRepository;
import com.municipalidad.talleresph.admin_web_service.repository.TallerRepository;
import com.municipalidad.talleresph.admin_web_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfesorAdminService {

    private static final String ROL_PROFESOR_BD = "MONITOR";
    private static final String PASSWORD_POR_DEFECTO = "123";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TallerRepository tallerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<ProfesorResponseDTO> listarProfesores() {
        return usuarioRepository.findByRolNombreRolOrderByIdUsuarioAsc(ROL_PROFESOR_BD)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfesorResponseDTO crearProfesor(ProfesorRequestDTO request) {
        String correoLimpio = request.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correoLimpio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un usuario con el correo " + correoLimpio);
        }

        String rutEnviado = request.getRut() != null ? request.getRut().trim() : "";
        boolean esRutPlaceholder = rutEnviado.isBlank()
                || "11111111-1".equals(rutEnviado)
                || "11.111.111-1".equals(rutEnviado);

        String rutFinal = esRutPlaceholder
                ? generarRutProvisionalUnico()
                : rutEnviado;

        if (usuarioRepository.existsByRut(rutFinal)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el RUT " + rutFinal);
        }

        String passwordInicial = (request.getPassword() != null && !request.getPassword().isBlank())
                ? request.getPassword()
                : PASSWORD_POR_DEFECTO;

        Rol rolMonitor = rolRepository.findByNombreRol(ROL_PROFESOR_BD)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Rol MONITOR no configurado en BD"));

        String nombreCompleto = request.getNombre().trim();
        String nombre = nombreCompleto;
        String apellido = request.getApellido();

        if ((apellido == null || apellido.isBlank()) && nombreCompleto.contains(" ")) {
            int idx = nombreCompleto.indexOf(' ');
            nombre = nombreCompleto.substring(0, idx).trim();
            apellido = nombreCompleto.substring(idx + 1).trim();
        }

        Usuario profesor = new Usuario();
        profesor.setRol(rolMonitor);
        profesor.setRut(rutFinal);
        profesor.setNombre(nombre);
        profesor.setApellido(apellido != null ? apellido : "");
        profesor.setCorreo(correoLimpio);
        profesor.setTelefono(request.getTelefono());
        profesor.setDireccion(request.getDireccion());
        profesor.setCargo(request.getCargo());
        profesor.setEspecialidad(request.getEspecialidad());
        profesor.setCentroSalud(request.getCentro());
        profesor.setEsDeLaComuna(true);
        profesor.setActivo(request.getActivo() != null ? request.getActivo() : true);
        profesor.setPassword(passwordEncoder.encode(passwordInicial));

        Usuario guardado = usuarioRepository.save(profesor);
        return toResponseDTO(guardado);
    }

    @Transactional
    public ProfesorResponseDTO actualizarProfesor(Long id, ProfesorRequestDTO request) {
        Usuario profesor = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con id " + id));

        if (profesor.getRol() == null || !ROL_PROFESOR_BD.equalsIgnoreCase(profesor.getRol().getNombreRol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El usuario indicado no corresponde a un profesor/monitor");
        }

        String nuevoCorreo = request.getCorreo().trim().toLowerCase();
        if (!profesor.getCorreo().equalsIgnoreCase(nuevoCorreo) && usuarioRepository.existsByCorreo(nuevoCorreo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está en uso por otro usuario");
        }

        String nuevoRut = request.getRut() != null ? request.getRut().trim() : "";
        boolean esRutPlaceholder = nuevoRut.isBlank()
                || "11111111-1".equals(nuevoRut)
                || "11.111.111-1".equals(nuevoRut);

        if (!esRutPlaceholder) {
            if (!profesor.getRut().equalsIgnoreCase(nuevoRut) && usuarioRepository.existsByRut(nuevoRut)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El RUT ya está en uso por otro usuario");
            }
            profesor.setRut(nuevoRut);
        }

        String nombreCompleto = request.getNombre().trim();
        if ((request.getApellido() == null || request.getApellido().isBlank()) && nombreCompleto.contains(" ")) {
            int idx = nombreCompleto.indexOf(' ');
            profesor.setNombre(nombreCompleto.substring(0, idx).trim());
            profesor.setApellido(nombreCompleto.substring(idx + 1).trim());
        } else {
            profesor.setNombre(nombreCompleto);
            if (request.getApellido() != null) {
                profesor.setApellido(request.getApellido());
            }
        }

        profesor.setCorreo(nuevoCorreo);
        profesor.setTelefono(request.getTelefono());
        profesor.setDireccion(request.getDireccion());
        profesor.setCargo(request.getCargo());
        profesor.setEspecialidad(request.getEspecialidad());
        profesor.setCentroSalud(request.getCentro());

        if (request.getActivo() != null) {
            profesor.setActivo(request.getActivo());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            profesor.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Usuario actualizado = usuarioRepository.save(profesor);
        return toResponseDTO(actualizado);
    }

    @Transactional
    public void eliminarProfesor(Long id) {
        Usuario profesor = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con id " + id));

        profesor.setActivo(false);
        usuarioRepository.save(profesor);
    }

    private String generarRutProvisionalUnico() {
        String rut;
        do {
            int num = ThreadLocalRandom.current().nextInt(30_000_000, 99_000_000);
            rut = num + "-K";
        } while (usuarioRepository.existsByRut(rut));
        return rut;
    }

    private ProfesorResponseDTO toResponseDTO(Usuario usuario) {
        List<Taller> talleres = tallerRepository.findByMonitorIdUsuarioOrderByIdAsc(usuario.getIdUsuario());
        List<ProfesorResponseDTO.TallerResumenDTO> talleresAsignados = talleres.stream()
                .map(t -> ProfesorResponseDTO.TallerResumenDTO.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .programaSalud(t.getCategoria())
                        .dias(t.getDias())
                        .horario(t.getHorario())
                        .activo(t.getActivo())
                        .build())
                .collect(Collectors.toList());

        return ProfesorResponseDTO.builder()
                .id(usuario.getIdUsuario())
                .rut(usuario.getRut())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .cargo(usuario.getCargo())
                .especialidad(usuario.getEspecialidad())
                .centro(usuario.getCentroSalud())
                .activo(usuario.getActivo())
                .rol(usuario.getRol() != null ? usuario.getRol().getNombreRol() : ROL_PROFESOR_BD)
                .talleresAsignados(talleresAsignados)
                .build();
    }
}