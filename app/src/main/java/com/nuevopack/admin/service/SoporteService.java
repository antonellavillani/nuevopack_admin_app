package com.nuevopack.admin.service;

import android.util.Log;

import com.nuevopack.admin.model.SoporteRequest;
import com.nuevopack.admin.util.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SoporteService {

    public interface Callback {
        void onSuccess();
        void onError(String errorMsg);
    }

    public void enviarSoporte(SoporteRequest request, Callback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "backend/api/enviar_soporte.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();
                json.put("asunto", request.getAsunto());
                json.put("mensaje", request.getMensaje());

                JSONArray imgs = new JSONArray(request.getImagenesBase64());
                json.put("imagenes", imgs);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(json.toString());
                wr.flush();
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);

                Log.d("RESPUESTA_SOPORTE", result.toString());

                JSONObject res = new JSONObject(result.toString());
                boolean ok = res.getBoolean("ok");

                if (ok) {
                    callback.onSuccess();
                } else {
                    callback.onError(res.optString("msg", "Error al enviar."));
                }

            } catch (Exception e) {
                Log.e("ErrorEnviarSoporte", e.toString());
                callback.onError("Error al enviar consulta.");
            }
        }).start();
    }
}
