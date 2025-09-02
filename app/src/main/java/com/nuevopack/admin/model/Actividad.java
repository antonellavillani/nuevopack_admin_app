package com.nuevopack.admin.model;

public class Actividad {
    private int id;
    private String descripcion;
    private String fecha;

    // Constructor
    public Actividad(int id, String descripcion, String fecha) {
        this.id = id;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public String getFecha() {
        return fecha;
    }
}