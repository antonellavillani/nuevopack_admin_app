package com.nuevopack.admin.model;
import java.io.Serializable;

public class Usuario implements Serializable {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private boolean aprobado;
    private String password_hash;

    // Constructor completo
    public Usuario(int id, String nombre, String apellido, String email, String telefono, boolean aprobado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.aprobado = aprobado;
    }

    // Constructor para login
    public Usuario(String email, String password) {
        this.email = email;
        this.password_hash = password;
    }

    // Constructor para listado de usuarios
    public Usuario(int id, String nombre, String apellido, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNombreCompleto() { return nombre + " " + apellido; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getPassword_hash() { return password_hash; }
    public boolean isAprobado() { return aprobado; }
}
