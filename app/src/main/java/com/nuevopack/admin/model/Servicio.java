package com.nuevopack.admin.model;

public class Servicio {
    private int id;
    private String nombre;
    private String descripcion;
    private String foto;

    public Servicio(int id, String nombre, String descripcion, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.foto = foto;
    }

    // Constructor para Spinner (solo id y nombre)
    public Servicio(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getFoto() { return foto; }
}