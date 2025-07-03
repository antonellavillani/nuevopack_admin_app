package com.nuevopack.admin.model;

public class Usuario {
    private String email;
    private String password;

    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}