package com.talleresph.service_auth_usuarios.service;

import com.talleresph.service_auth_usuarios.dto.ActualizarUsuarioRequestDTO;
import com.talleresph.service_auth_usuarios.dto.UsuarioResponseDTO;
import com.talleresph.service_auth_usuarios.exception.AccesoDenegadoException;
import com.talleresph.service_auth_usuarios.exception.RecursoNoEncontradoException;
import com.talleresph.service_auth_usuarios.model.Usuario;
import com.talleresph.service_auth_usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    // Límite de caracteres para la foto en Base64 (~10 MB, igual que la especificación del frontend)
    private static final int MAX_FOTO_CHARS = 10_000_000;

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Convierte la entidad al JSON que espera el frontend.
     * "estadisticas" queda en null: se calcula con tablas del servicio de talleres (Dev 2),
     * así que debe resolverse con una consulta a ese servicio o en un gateway.
     */
    public UsuarioResponseDTO toResponse(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getIdUsuario())
                .rut(usuario.getRut())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol() == null ? null : usuario.getRol().getNombreRol())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .fotoPerfil(usuario.getFotoPerfil())
                .cargo(usuario.getCargo())
                .centroSalud(usuario.getCentroSalud())
                .estadisticas(null)
                .build();
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Integer id, ActualizarUsuarioRequestDTO request,
                                         String correoAutenticado, boolean esAdmin) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // Solo el propio usuario (o un ADMIN) puede modificar su perfil
        if (!esAdmin && !usuario.getCorreo().equalsIgnoreCase(correoAutenticado)) {
            throw new AccesoDenegadoException("No tienes permiso para modificar este usuario");
        }

        if (request.getNombre() != null) {
            usuario.setNombre(requerirTexto(request.getNombre(), "nombre"));
        }
        if (request.getApellido() != null) {
            usuario.setApellido(requerirTexto(request.getApellido(), "apellido"));
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }
        if (request.getFotoPerfil() != null) {
            usuario.setFotoPerfil(validarFoto(request.getFotoPerfil()));
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    private String requerirTexto(String valor, String campo) {
        if (valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacío");
        }
        return valor.trim();
    }

    private String validarFoto(String foto) {
        if (foto.isEmpty()) {
            return null; // "" = quitar la foto
        }
        if (!foto.startsWith("data:image/")) {
            throw new IllegalArgumentException("fotoPerfil debe ser una imagen en formato DataURL Base64");
        }
        if (foto.length() > MAX_FOTO_CHARS) {
            throw new IllegalArgumentException("La imagen supera el tamaño máximo permitido");
        }
        return foto;
    }
}
