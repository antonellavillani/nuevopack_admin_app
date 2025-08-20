package com.nuevopack.admin.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Estadistica;
import com.nuevopack.admin.adapter.EstadisticasAdapter;
import com.nuevopack.admin.util.ApiConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EstadisticasActivity extends BaseActivity {

    private RecyclerView recyclerEstadisticas;
    private EstadisticasAdapter estadisticasAdapter;
    private List<Estadistica> listaEstadisticas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContenidoLayout(R.layout.activity_estadisticas);

        setTitle("Estadísticas del sitio");

        recyclerEstadisticas = findViewById(R.id.recyclerEstadisticas);
        recyclerEstadisticas.setLayoutManager(new LinearLayoutManager(this));

        listaEstadisticas = new ArrayList<>();
        estadisticasAdapter = new EstadisticasAdapter(listaEstadisticas);
        recyclerEstadisticas.setAdapter(estadisticasAdapter);
        // Placeholders nulos para que se vean los shimmer
        for (int i = 0; i < 5; i++) {
            listaEstadisticas.add(null);
        }
        estadisticasAdapter.notifyDataSetChanged();

        // Después cargar los datos reales
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        String url = ApiConfig.BASE_URL + "backend/api/get_estadisticas.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");

                            // Limpiar lista previa
                            listaEstadisticas.clear();

                            // Agregar ítems
                            listaEstadisticas.add(new Estadistica(
                                    "Usuarios últimos 7 días",
                                    data.getString("usuarios7d"),
                                    "Usuarios únicos registrados en la última semana"
                            ));
                            listaEstadisticas.add(new Estadistica(
                                    "Sesiones últimos 7 días",
                                    data.getString("sesiones7d"),
                                    "Cantidad de sesiones iniciadas en la última semana"
                            ));
                            listaEstadisticas.add(new Estadistica(
                                    "Usuarios en tiempo real",
                                    data.getString("usuariosTiempoReal"),
                                    "Usuarios navegando actualmente en el sitio"
                            ));
                            listaEstadisticas.add(new Estadistica(
                                    "Formularios enviados",
                                    data.getString("formularios"),
                                    "Cantidad de formularios completados en los últimos 7 días"
                            ));
                            listaEstadisticas.add(new Estadistica(
                                    "Uso de la Calculadora",
                                    data.getString("calculadora"),
                                    "Cantidad de veces que se usó la calculadora de precios"
                            ));

                            // Notificar cambios al adapter
                            estadisticasAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Error: " + response.optString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al parsear datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}
