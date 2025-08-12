package com.nuevopack.admin.controller;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import com.nuevopack.admin.model.SoporteRequest;
import com.nuevopack.admin.service.SoporteService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SoporteController {

    private final Context context;
    private final SoporteService service;

    public SoporteController(Context context) {
        this.context = context;
        this.service = new SoporteService();
    }

    public void enviarSoporte(String asunto, String mensaje, List<Uri> imagenesUri, SoporteService.Callback callback) {
        try {
            List<String> imagenesBase64 = new ArrayList<>();

            for (Uri uri : imagenesUri) {
                InputStream is = context.getContentResolver().openInputStream(uri);
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                is.close();
                String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                imagenesBase64.add(base64);
            }

            SoporteRequest request = new SoporteRequest(asunto, mensaje, imagenesBase64);
            service.enviarSoporte(request, callback);

        } catch (Exception e) {
            callback.onError("Error procesando imágenes.");
        }
    }
}
