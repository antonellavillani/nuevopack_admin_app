package com.nuevopack.admin.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nuevopack.admin.R;
import com.nuevopack.admin.adapter.ServicioAdapter;
import com.nuevopack.admin.model.Servicio;
import com.nuevopack.admin.util.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServiciosActivity extends AppCompatActivity {

    private ArrayList<Servicio> listaServicios = new ArrayList<>();
    private ServicioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        RecyclerView recyclerView = findViewById(R.id.recyclerServicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServicioAdapter(this, listaServicios);
        recyclerView.setAdapter(adapter);

        cargarServicios();
    }

    private void cargarServicios() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "backend/api/get_servicios.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    jsonBuilder.append(linea);
                }

                JSONArray array = new JSONArray(jsonBuilder.toString());
                ArrayList<Servicio> nuevos = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("id");
                    String nombre = obj.getString("nombre");
                    String descripcion = obj.getString("descripcion");
                    String foto = obj.getString("foto");
                    nuevos.add(new Servicio(id, nombre, descripcion, foto));
                }

                runOnUiThread(() -> {
                    listaServicios.clear();
                    listaServicios.addAll(nuevos);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar servicios", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}