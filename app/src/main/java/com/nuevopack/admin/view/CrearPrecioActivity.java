package com.nuevopack.admin.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Servicio;
import com.nuevopack.admin.util.ApiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearPrecioActivity extends AppCompatActivity {

    private Spinner spinnerServicios;
    private EditText editDescripcion, editTipoUnidad, editPrecio;
    private Button btnCrearPrecio;

    private List<Servicio> listaServicios = new ArrayList<>();
    private static final String URL_LISTAR_SERVICIOS = (ApiConfig.BASE_URL + "backend/api/precios_abm/listar_servicios.php");
    private static final String URL_CREAR_PRECIO = (ApiConfig.BASE_URL + "backend/api/precios_abm/crear_precio.php");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precio_form);

        spinnerServicios = findViewById(R.id.spinnerServicios);
        editDescripcion = findViewById(R.id.editDescripcion);
        editTipoUnidad = findViewById(R.id.editTipoUnidad);
        editPrecio = findViewById(R.id.editPrecio);
        btnCrearPrecio = findViewById(R.id.btnCrearPrecio);

        cargarServicios();

        btnCrearPrecio.setOnClickListener(v -> crearPrecio());
    }

    private void cargarServicios() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_LISTAR_SERVICIOS,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<String> nombresServicios = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Servicio servicio = new Servicio(
                                    obj.getInt("id"),
                                    obj.getString("nombre")
                            );
                            listaServicios.add(servicio);
                            nombresServicios.add(servicio.getNombre());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, nombresServicios);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerServicios.setAdapter(adapter);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar servicios", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión al cargar servicios", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void crearPrecio() {
        Servicio servicioSeleccionado = listaServicios.get(spinnerServicios.getSelectedItemPosition());
        String descripcion = editDescripcion.getText().toString().trim();
        String tipoUnidad = editTipoUnidad.getText().toString().trim();
        String precio = editPrecio.getText().toString().trim();

        if (descripcion.isEmpty() || tipoUnidad.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Por favor completá todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_CREAR_PRECIO,
                response -> {
                    Toast.makeText(this, "Precio creado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                },
                error -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("servicio_id", String.valueOf(servicioSeleccionado.getId()));
                params.put("descripcion", descripcion);
                params.put("tipo_unidad", tipoUnidad);
                params.put("precio", precio);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
