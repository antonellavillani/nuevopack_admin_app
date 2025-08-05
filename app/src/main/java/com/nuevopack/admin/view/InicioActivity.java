package com.nuevopack.admin.view;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nuevopack.admin.R;
import com.nuevopack.admin.adapter.ActividadAdapter;
import com.nuevopack.admin.adapter.AlertasAdapter;
import com.nuevopack.admin.controller.DashboardController;
import com.nuevopack.admin.model.ResumenCard;
import com.nuevopack.admin.model.Actividad;
import com.nuevopack.admin.util.ApiConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContenidoLayout(R.layout.activity_inicio);

        // Obtener nombre del usuario de SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nombreUsuario = prefs.getString("nombreUsuario", "Usuario");

        // Mostrar en el TextView de bienvenida
        TextView txtBienvenida = findViewById(R.id.tituloBienvenido);
        String mensajeBienvenida = getString(R.string.titulo_bienvenido, nombreUsuario);
        txtBienvenida.setText(mensajeBienvenida);

        // CARD Servicios – va a ServiciosActivity
        View cardServicios = findViewById(R.id.includeCardServicios);
        cardServicios.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, ServiciosActivity.class);
            startActivity(intent);
        });

        // CARD Precios – va a PreciosActivity
        View cardPrecios = findViewById(R.id.includeCardPrecios);
        cardPrecios.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, PreciosActivity.class);
            startActivity(intent);
        });

        // CARD Usuarios – va a UsuariosActivity
        View cardUsuarios = findViewById(R.id.includeCardUsuarios);
        cardUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, UsuariosActivity.class);
            startActivity(intent);
        });

        // Accesos rápidos
        Button btnNuevoServicio = findViewById(R.id.btnAccesoNuevoServicio);
        Button btnNuevoPrecio = findViewById(R.id.btnAccesoNuevoPrecio);
        Button btnNuevoUsuario = findViewById(R.id.btnAccesoNuevoUsuario);

        btnNuevoServicio.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, CrearServicioActivity.class);
            startActivity(intent);
        });

        btnNuevoPrecio.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, CrearPrecioActivity.class);
            startActivity(intent);
        });

        btnNuevoUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, CrearUsuarioActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosDashboard(); // Se recarga siempre que la Activity vuelve al frente
    }

    private void cargarDatosDashboard() {
        DashboardController dashboard = new DashboardController();

        // Servicios
        View cardServicios = findViewById(R.id.includeCardServicios);
        String urlServicios = ApiConfig.BASE_URL + "backend/api/get_total_servicios.php";
        dashboard.obtenerResumenServicios(urlServicios, new DashboardController.ResumenCallback() {
            @Override
            public void onSuccess(ResumenCard serviciosCard) {
                runOnUiThread(() -> {
                    ((TextView) cardServicios.findViewById(R.id.tituloCard)).setText(serviciosCard.getTitulo());
                    ((TextView) cardServicios.findViewById(R.id.textoCard)).setText(serviciosCard.getDescripcion());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    ((TextView) cardServicios.findViewById(R.id.tituloCard)).setText(R.string.btn_sidebar_servicios);
                    ((TextView) cardServicios.findViewById(R.id.textoCard)).setText(R.string.error_conexion);
                });
            }
        });

        // Precios
        View cardPrecios = findViewById(R.id.includeCardPrecios);
        String urlPrecios = ApiConfig.BASE_URL + "backend/api/get_total_precios.php";
        dashboard.obtenerResumenPrecios(urlPrecios, new DashboardController.ResumenCallback() {
            @Override
            public void onSuccess(ResumenCard preciosCard) {
                runOnUiThread(() -> {
                    ((TextView) cardPrecios.findViewById(R.id.tituloCard)).setText(preciosCard.getTitulo());
                    ((TextView) cardPrecios.findViewById(R.id.textoCard)).setText(preciosCard.getDescripcion());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    ((TextView) cardPrecios.findViewById(R.id.tituloCard)).setText(R.string.btn_sidebar_precios);
                    ((TextView) cardPrecios.findViewById(R.id.textoCard)).setText(R.string.error_conexion);
                });
            }
        });

        // Usuarios
        View cardUsuarios = findViewById(R.id.includeCardUsuarios);
        String urlUsuarios = ApiConfig.BASE_URL + "backend/api/get_total_usuarios.php";
        dashboard.obtenerResumenUsuarios(urlUsuarios, new DashboardController.ResumenCallback() {
            @Override
            public void onSuccess(ResumenCard usuariosCard) {
                runOnUiThread(() -> {
                    ((TextView) cardUsuarios.findViewById(R.id.tituloCard)).setText(usuariosCard.getTitulo());
                    ((TextView) cardUsuarios.findViewById(R.id.textoCard)).setText(usuariosCard.getDescripcion());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    ((TextView) cardUsuarios.findViewById(R.id.tituloCard)).setText(R.string.btn_sidebar_usuarios);
                    ((TextView) cardUsuarios.findViewById(R.id.textoCard)).setText(R.string.error_conexion);
                });
            }
        });

        // Actividad reciente
        View cardActividad = findViewById(R.id.includeCardActividad);
        RecyclerView recyclerActividad = cardActividad.findViewById(R.id.recyclerActividad);
        List<Actividad> listaActividades = new ArrayList<>();
        ActividadAdapter adapter = new ActividadAdapter(listaActividades);
        recyclerActividad.setLayoutManager(new LinearLayoutManager(this));
        recyclerActividad.setAdapter(adapter);
        TextView textoActividad = cardActividad.findViewById(R.id.contenidoInfoAlerta);

        // Mostrar mensaje de carga
        recyclerActividad.setVisibility(View.GONE);
        textoActividad.setVisibility(View.VISIBLE);
        textoActividad.setText(R.string.texto_cargando_actividad_reciente);


        String url = ApiConfig.BASE_URL + "backend/api/obtener_actividades.php";
        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONArray jsonArray = new JSONArray(json.toString());
                List<Actividad> nuevasActividades = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    nuevasActividades.add(new Actividad(
                            obj.getString("descripcion"),
                            obj.getString("fecha")
                    ));
                }

                runOnUiThread(() -> {
                    listaActividades.clear();
                    listaActividades.addAll(nuevasActividades);
                    adapter.notifyDataSetChanged();

                    if (nuevasActividades.isEmpty()) {
                        recyclerActividad.setVisibility(View.GONE);
                        textoActividad.setVisibility(View.VISIBLE);
                        textoActividad.setText(R.string.texto_sin_actividad_reciente);
                    } else {
                        recyclerActividad.setVisibility(View.VISIBLE);
                        textoActividad.setVisibility(View.GONE);
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    listaActividades.clear();
                    adapter.notifyDataSetChanged();
                    recyclerActividad.setVisibility(View.GONE);
                    textoActividad.setVisibility(View.VISIBLE);
                    textoActividad.setText(R.string.error_carga_actividad_reciente);
                });
            }
        }).start();

        // Alertas
        View cardAlertas = findViewById(R.id.includeCardAlertas);
        RecyclerView recycler = cardAlertas.findViewById(R.id.recyclerAlertas);
        TextView textoAlerta = cardAlertas.findViewById(R.id.contenidoInfoAlerta);

        recycler.setVisibility(View.GONE);
        textoAlerta.setVisibility(View.VISIBLE);
        textoAlerta.setText(R.string.texto_cargando_alertas);

        String urlAlertas = ApiConfig.BASE_URL + "backend/api/get_alertas.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlAlertas, null,
                response -> {
                    try {
                        JSONArray alertas = response.getJSONArray("alertas");

                        if (alertas.length() == 0) {
                            textoAlerta.setText(R.string.texto_sin_alertas);
                        } else {
                            ArrayList<String> listaAlertas = new ArrayList<>();
                            for (int i = 0; i < alertas.length(); i++) {
                                listaAlertas.add("❌ " + alertas.getString(i));
                            }

                            recycler.setVisibility(View.VISIBLE);
                            textoAlerta.setVisibility(View.GONE);
                            recycler.setLayoutManager(new LinearLayoutManager(this));
                            recycler.setAdapter(new AlertasAdapter(listaAlertas));
                        }

                    } catch (Exception e) {
                        textoAlerta.setText(R.string.error_procesar_alertas);
                    }
                },
                error -> textoAlerta.setText(R.string.error_cargado_alertas)
        );

        queue.add(request);
    }
}