package com.talleresph.service_auth_usuarios.service;

import com.talleresph.service_auth_usuarios.model.Rol;
import com.talleresph.service_auth_usuarios.model.Usuario;
import com.talleresph.service_auth_usuarios.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();
        Rol rol = usuario.getRol();
        if (rol != null) {
            // Prefijo ROLE_ para poder usar hasRole("ADMIN") en otros endpoints
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol()));
            if (rol.getPermisos() != null) {
                rol.getPermisos().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getNombrePermiso())));
            }
        }

        // Un usuario con activo = false no puede iniciar sesión
        boolean habilitado = !Boolean.FALSE.equals(usuario.getActivo());

        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),
                usuario.getPassword(),
                habilitado, true, true, true,
                authorities
        );
    }
}
