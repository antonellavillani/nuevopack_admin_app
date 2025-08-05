package com.nuevopack.admin.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nuevopack.admin.R;
import com.nuevopack.admin.adapter.PrecioAdapter;
import com.nuevopack.admin.model.Precio;
import com.nuevopack.admin.util.ApiConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.content.Intent;

public class PreciosActivity extends BaseActivity {

    private final ArrayList<Precio> listaPrecios = new ArrayList<>();
    private PrecioAdapter adapter;
    private static final int REQUEST_NUEVO_PRECIO = 200;
    private static final int REQUEST_EDITAR_PRECIO = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContenidoLayout(R.layout.activity_precios);

        Button btnNuevoPrecio = findViewById(R.id.btnNuevoPrecio);
        btnNuevoPrecio.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearPrecioActivity.class);
            startActivityForResult(intent, REQUEST_NUEVO_PRECIO);
        });

        RecyclerView recycler = findViewById(R.id.recyclerPrecios);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PrecioAdapter(this, listaPrecios);
        recycler.setAdapter(adapter);

        cargarPrecios();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_NUEVO_PRECIO || requestCode == REQUEST_EDITAR_PRECIO) && resultCode == RESULT_OK) {
            cargarPrecios(); // Recargar los precios para mostrar cambios
        }
    }

    private void cargarPrecios() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "backend/api/get_precios.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    json.append(linea);
                }

                Log.d("PreciosActivity", "JSON recibido: " + json.toString());
                JSONArray array = new JSONArray(json.toString());
                ArrayList<Precio> nuevos = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("id");
                    int servicio_id = obj.getInt("servicio_id");
                    String nombre_servicio = obj.getString("nombre_servicio");
                    String descripcion = obj.getString("descripcion");
                    String tipo_unidad = obj.getString("tipo_unidad");
                    double precio = obj.getDouble("precio");
                    nuevos.add(new Precio(id, servicio_id, nombre_servicio, descripcion, tipo_unidad, precio));
                }

                runOnUiThread(() -> {
                    listaPrecios.clear();
                    listaPrecios.addAll(nuevos);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                Log.e("PreciosActivity", "Error al cargar precios", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar precios: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

}