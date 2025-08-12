package com.nuevopack.admin.model;

import java.util.List;

public class SoporteRequest {
    private String asunto;
    private String mensaje;
    private List<String> imagenesBase64;

    public SoporteRequest(String asunto, String mensaje, List<String> imagenesBase64) {
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.imagenesBase64 = imagenesBase64;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<String> getImagenesBase64() {
        return imagenesBase64;
    }
}
