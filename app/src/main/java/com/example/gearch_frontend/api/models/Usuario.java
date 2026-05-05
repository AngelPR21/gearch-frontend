package com.example.gearch_frontend.api.models;

import com.example.gearch_frontend.api.models.enums.RolUsuario;

public class Usuario {
    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String telefono;
    private RolUsuario rol;
    private Long tallerAdministradoId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public Long getTallerAdministradoId() { return tallerAdministradoId; }
    public void setTallerAdministradoId(Long tallerAdministradoId) { this.tallerAdministradoId = tallerAdministradoId; }
}