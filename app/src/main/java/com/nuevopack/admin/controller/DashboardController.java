package com.nuevopack.admin.controller;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nuevopack.admin.model.ResumenCard;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

    public void obtenerResumenEstadisticas(ResumenCallback callback) {
        String titulo = "Estadísticas del sitio";
        String descripcion = "Usuarios, sesiones y actividad de los últimos 7 días";

        callback.onSuccess(new ResumenCard(titulo, descripcion));
    }

    public interface DeleteCallback {
        void onSuccess(String mensaje);
        void onError(String error);
    }

    public void eliminarActividad(Context context, String url, int idActividad, DeleteCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.has("success")) {
                            callback.onSuccess("Actividad eliminada");
                        } else {
                            callback.onError(obj.optString("error", "Error desconocido"));
                        }
                    } catch (Exception e) {
                        callback.onError("Error procesando la respuesta");
                    }
                },
                error -> callback.onError("Error de conexión")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(idActividad));
                return params;
            }
        };

        queue.add(postRequest);
    }
}