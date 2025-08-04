package com.nuevopack.admin.controller;

import com.nuevopack.admin.model.ResumenCard;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DashboardController {
    public interface ResumenCallback {
        void onSuccess(ResumenCard resumen);
        void onError(String error);
    }

    public void obtenerResumenServicios(String url, ResumenCallback callback) {
        obtenerResumenGeneral(url, "Servicios", "cargados", callback);
    }

    public void obtenerResumenPrecios(String url, ResumenCallback callback) {
        obtenerResumenGeneral(url, "Precios", "registrados", callback);
    }

    public void obtenerResumenUsuarios(String url, ResumenCallback callback) {
        obtenerResumenGeneral(url, "Usuarios", "activos", callback);
    }

    private void obtenerResumenGeneral(String url, String titulo, String textoUnidad, ResumenCallback callback) {
        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                JSONObject response = new JSONObject(json.toString());
                int total = response.getInt("total");

                callback.onSuccess(new ResumenCard(titulo, total + " " + textoUnidad));
            } catch (Exception e) {
                callback.onError("Error al obtener " + titulo.toLowerCase() + ": " + e.getMessage());
            }
        }).start();
    }
}