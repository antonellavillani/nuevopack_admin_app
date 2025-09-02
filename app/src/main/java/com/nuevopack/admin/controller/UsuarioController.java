package com.nuevopack.admin.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nuevopack.admin.model.Usuario;
import com.nuevopack.admin.util.ApiConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsuarioController {

    public interface UsuarioCallback {
        void onSuccess(ArrayList<Usuario> listaUsuarios);
        void onError(String mensaje);
    }

    public static void obtenerUsuarios(Context context, UsuarioCallback callback) {
        String url = ApiConfig.BASE_URL + "config/get_usuarios.php";

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<Usuario> lista = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String nombre = obj.getString("nombre");
                            String apellido = obj.getString("apellido");
                            String email = obj.getString("email");

                            lista.add(new Usuario(id, nombre, apellido, email));
                        }
                        callback.onSuccess(lista);
                    } catch (JSONException e) {
                        callback.onError("Error al parsear usuarios");
                    }
                },
                error -> {
                    callback.onError("Error en la conexión: " + error.getMessage());
                    Log.e("Volley", error.toString());
                });

        queue.add(request);
    }

    public static void eliminarUsuario(Context context, int idUsuario, final UsuarioCallback callback) {
        String url = ApiConfig.BASE_URL + "backend/api/usuarios_abm/eliminar_usuario.php";

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            callback.onSuccess(null);
                        } else {
                            callback.onError(obj.getString("error"));
                        }
                    } catch (JSONException e) {
                        callback.onError("Error al parsear respuesta");
                    }
                },
                error -> callback.onError("Error en la conexión: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(idUsuario));
                return params;
            }
        };

        queue.add(request);
    }
}