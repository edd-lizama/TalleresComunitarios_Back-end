package com.talleresph.service_auth_usuarios.dto;

/**
 * Respuesta de login y registro según el contrato del frontend:
 * { "token": "...", "usuario": { id, rut, nombre, apellido, correo, rol, ... } }
 */
public class AuthResponseDTO {
    private String token;
    private UsuarioResponseDTO usuario;

    public AuthResponseDTO(String token, UsuarioResponseDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UsuarioResponseDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioResponseDTO usuario) { this.usuario = usuario; }
}
