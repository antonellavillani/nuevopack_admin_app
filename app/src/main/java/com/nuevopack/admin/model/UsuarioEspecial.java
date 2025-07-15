package com.nuevopack.admin.model;

public class UsuarioEspecial {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private boolean aprobado;

    public UsuarioEspecial(int id, String nombre, String apellido, String email, String telefono, boolean aprobado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.aprobado = aprobado;
    }

    public int getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isAprobado() {
        return aprobado;
    }
}