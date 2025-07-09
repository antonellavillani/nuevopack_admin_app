package com.nuevopack.admin.model;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String password_hash;

    // Constructor completo
    public Usuario(int id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    // Constructor para login
    public Usuario(String email, String password) {
        this.email = email;
        this.password_hash = password;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword_hash() { return password_hash; }
}