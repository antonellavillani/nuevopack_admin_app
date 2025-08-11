package com.nuevopack.admin.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.nuevopack.admin.model.Usuario;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.nuevopack.admin.util.ApiConfig;
public class LoginController {

    public void validarLogin(Context context, Usuario usuario, LoginCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "backend/api/login_usuario.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // JSON que se envía al servidor
                JSONObject json = new JSONObject();
                json.put("email", usuario.getEmail());
                json.put("password", usuario.getPassword_hash());

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                // Leer respuesta
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();

                JSONObject respuesta = new JSONObject(result.toString());
                boolean success = respuesta.getBoolean("success");
                String mensaje = respuesta.optString("message", "");
                String nombre = respuesta.optString("nombre", "");

                // Llamar al callback en el hilo principal
                new Handler(Looper.getMainLooper()).post(() -> callback.onResultado(success, mensaje, nombre));

            } catch (Exception e) {
                Log.e("LoginController", "Error al conectar con el servidor: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onResultado(false, "Error al conectar con el servidor.", null);
                });
            }
        }).start();
    }
}