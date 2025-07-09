package com.nuevopack.admin.model;

public class Actividad {
    private String descripcion;
    private String fecha;

    // Constructor
    public Actividad(String descripcion, String fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    // Getters
    public String getDescripcion() {
        return descripcion;
    }
    public String getFecha() {
        return fecha;
    }
}