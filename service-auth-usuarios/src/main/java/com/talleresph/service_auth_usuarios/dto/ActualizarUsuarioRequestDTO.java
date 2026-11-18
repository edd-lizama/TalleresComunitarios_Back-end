package com.talleresph.service_auth_usuarios.dto;

/**
 * Body de PUT /api/usuarios/{id}. Los campos null no se modifican.
 * fotoPerfil: DataURL Base64 ("data:image/jpeg;base64,...") o "" para quitar la foto.
 */
public class ActualizarUsuarioRequestDTO {
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private String fotoPerfil;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}
