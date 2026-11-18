package com.talleresph.service_biometria.security;

/** Principal que el filtro JWT deja en el contexto de seguridad (datos tomados del token). */
public record UsuarioAutenticado(Long idUsuario, String correo) {
}
