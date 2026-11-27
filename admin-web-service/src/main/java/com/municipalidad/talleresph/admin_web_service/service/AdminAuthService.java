package com.municipalidad.talleresph.admin_web_service.service;

import com.municipalidad.talleresph.admin_web_service.dto.AdminAuthResponseDTO;
import com.municipalidad.talleresph.admin_web_service.dto.AdminLoginRequestDTO;
import com.municipalidad.talleresph.admin_web_service.model.Usuario;
import com.municipalidad.talleresph.admin_web_service.repository.UsuarioRepository;
import com.municipalidad.talleresph.admin_web_service.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostConstruct
    public void asegurarPasswordAdminSemilla() {
        usuarioRepository.findByCorreo("admin@saludmph.cl").ifPresent(admin -> {
            admin.setPassword(passwordEncoder.encode("123"));
            admin.setActivo(true);
            usuarioRepository.save(admin);
        });
    }

    @Transactional(readOnly = true)
    public AdminAuthResponseDTO login(AdminLoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario se encuentra desactivado");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        if (usuario.getRol() == null || !"ADMIN".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso exclusivo para administradores");
        }

        String token = jwtUtil.generateToken(usuario);

        return AdminAuthResponseDTO.builder()
                .token(token)
                .usuario(AdminAuthResponseDTO.UsuarioAdminDTO.builder()
                        .idUsuario(usuario.getIdUsuario())
                        .rut(usuario.getRut())
                        .nombre(usuario.getNombre())
                        .apellido(usuario.getApellido())
                        .correo(usuario.getCorreo())
                        .rol(usuario.getRol().getNombreRol())
                        .cargo(usuario.getCargo())
                        .centroSalud(usuario.getCentroSalud())
                        .build())
                .build();
    }
}