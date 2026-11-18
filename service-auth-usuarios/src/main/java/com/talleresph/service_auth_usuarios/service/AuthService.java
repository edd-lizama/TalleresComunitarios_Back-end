package com.talleresph.service_auth_usuarios.service;

import com.talleresph.service_auth_usuarios.dto.AuthResponseDTO;
import com.talleresph.service_auth_usuarios.dto.LoginRequestDTO;
import com.talleresph.service_auth_usuarios.dto.RegistroRequestDTO;
import com.talleresph.service_auth_usuarios.exception.RecursoDuplicadoException;
import com.talleresph.service_auth_usuarios.model.Perfil;
import com.talleresph.service_auth_usuarios.model.Rol;
import com.talleresph.service_auth_usuarios.model.Usuario;
import com.talleresph.service_auth_usuarios.repository.PerfilRepository;
import com.talleresph.service_auth_usuarios.repository.RolRepository;
import com.talleresph.service_auth_usuarios.repository.UsuarioRepository;
import com.talleresph.service_auth_usuarios.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String TIPO_PERFIL_POR_DEFECTO = "PARTICIPANTE";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UsuarioService usuarioService;
    private final String defaultRole;

    public AuthService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                       PerfilRepository perfilRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                       UserDetailsService userDetailsService, UsuarioService usuarioService,
                       @Value("${app.auth.default-role}") String defaultRole) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.usuarioService = usuarioService;
        this.defaultRole = defaultRole;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        // Lanza AuthenticationException (401) si las credenciales no son válidas o el usuario está inactivo
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreo());

        String token = jwtUtil.generateToken(usuario, userDetails);
        return new AuthResponseDTO(token, usuarioService.toResponse(usuario));
    }

    @Transactional
    public AuthResponseDTO register(RegistroRequestDTO request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new RecursoDuplicadoException("El correo ya está en uso");
        }
        if (usuarioRepository.existsByRut(request.getRut())) {
            throw new RecursoDuplicadoException("El RUT ya está registrado");
        }

        // El rol NUNCA viene del cliente ni se deduce del correo: el registro público siempre crea un VECINO
        Rol rol = rolRepository.findByNombreRol(defaultRole)
                .orElseThrow(() -> new IllegalStateException("Rol por defecto no configurado en la base de datos: " + defaultRole));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setRut(request.getRut());
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setApellido(request.getApellido());
        nuevoUsuario.setCorreo(request.getCorreo());
        nuevoUsuario.setEdad(request.getEdad());
        nuevoUsuario.setGenero(request.getGenero());
        nuevoUsuario.setTelefono(request.getTelefono());
        nuevoUsuario.setDireccion(request.getDireccion());
        nuevoUsuario.setEsDeLaComuna(Boolean.TRUE.equals(request.getEsDeLaComuna()));
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(rol);
        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        Perfil perfil = new Perfil();
        perfil.setUsuario(nuevoUsuario);
        perfil.setTipoPerfil(TIPO_PERFIL_POR_DEFECTO);
        perfil.setConsentimientoFoto(false);
        perfilRepository.save(perfil);

        UserDetails userDetails = userDetailsService.loadUserByUsername(nuevoUsuario.getCorreo());
        String token = jwtUtil.generateToken(nuevoUsuario, userDetails);

        return new AuthResponseDTO(token, usuarioService.toResponse(nuevoUsuario));
    }
}
