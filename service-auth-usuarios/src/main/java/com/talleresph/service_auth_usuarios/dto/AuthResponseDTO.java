package com.talleresph.service_auth_usuarios.dto;

public class AuthResponseDTO {
    private String token;
    private String nombreCompleto;
    private String rol;

    public AuthResponseDTO(String token, String nombreCompleto, String rol) {
        this.token = token;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
