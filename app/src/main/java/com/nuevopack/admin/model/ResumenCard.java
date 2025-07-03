package com.nuevopack.admin.model;

public class ResumenCard {
    private String titulo;
    private String descripcion;

    public ResumenCard(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}