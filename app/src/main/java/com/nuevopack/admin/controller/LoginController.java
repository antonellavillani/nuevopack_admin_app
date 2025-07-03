package com.nuevopack.admin.controller;

import com.nuevopack.admin.model.Usuario;

public class LoginController {

    // Verificación de usuario
    public boolean loginValido(Usuario usuario) {
        // Hardcodeo de usuario válido para prueba
        return usuario.getEmail().equals("admin@nuevopack.com") &&
                usuario.getPassword().equals("admin123");
    }
}