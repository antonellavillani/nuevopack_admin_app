package com.nuevopack.admin.model;

public class Estadistica {
    private String titulo;
    private String valor;
    private String descripcion;

    public Estadistica(String titulo, String valor, String descripcion) {
        this.titulo = titulo;
        this.valor = valor;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
