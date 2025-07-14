package com.nuevopack.admin.model;

public class Precio {
    private int id;
    private int servicio_id;
    private String nombre_servicio;
    private String descripcion;
    private String tipo_unidad;
    private double precio;

    public Precio(int id, int servicio_id, String nombre_servicio, String descripcion, String tipo_unidad, double precio) {
        this.id = id;
        this.servicio_id = servicio_id;
        this.nombre_servicio = nombre_servicio;
        this.descripcion = descripcion;
        this.tipo_unidad = tipo_unidad;
        this.precio = precio;
    }

    // Getters
    public int getId() { return id; }
    public int getServicioId() { return servicio_id; }
    public String getNombreServicio() { return nombre_servicio; }
    public String getDescripcion() { return descripcion; }
    public String getTipoUnidad() { return tipo_unidad; }
    public double getPrecio() { return precio; }
}