package com.nuevopack.admin.view;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Servicio;
import com.nuevopack.admin.model.Precio;
import com.nuevopack.admin.util.ApiConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class EditarPrecioActivity extends AppCompatActivity {

    private Spinner spinnerServicios;
    private EditText editDescripcion, editTipoUnidad, editPrecio;
    private Button btnEditarPrecio;
    private TextView titulo;

    private List<Servicio> listaServicios = new ArrayList<>();
    private Precio precioActual;
    private static final String URL_LISTAR_SERVICIOS = ApiConfig.BASE_URL + "backend/api/precios_abm/listar_servicios.php";
    private static final String URL_EDITAR_PRECIO = ApiConfig.BASE_URL + "backend/api/precios_abm/editar_precio.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_precio);

        titulo = findViewById(R.id.textViewTitulo);
        spinnerServicios = findViewById(R.id.spinnerServicios);
        editDescripcion = findViewById(R.id.editDescripcion);
        editTipoUnidad = findViewById(R.id.editTipoUnidad);
        editPrecio = findViewById(R.id.editPrecio);
        btnEditarPrecio = findViewById(R.id.btnCrearPrecio);

        // Cambiar texto del botón
        btnEditarPrecio.setText("Guardar cambios");

        // Obtener objeto Precio enviado por Intent
        precioActual = new Precio(
                getIntent().getIntExtra("id", -1),
                getIntent().getIntExtra("servicio_id", -1),
                getIntent().getStringExtra("nombre_servicio"),
                getIntent().getStringExtra("descripcion"),
                getIntent().getStringExtra("tipo_unidad"),
                getIntent().getDoubleExtra("precio", 0.0)
        );

        cargarServicios();
        btnEditarPrecio.setOnClickListener(v -> editarPrecio());
    }

    private void cargarServicios() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_LISTAR_SERVICIOS,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<String> nombresServicios = new ArrayList<>();

                        int posicionSeleccionada = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Servicio servicio = new Servicio(
                                    obj.getInt("id"),
                                    obj.getString("nombre")
                            );
                            listaServicios.add(servicio);
                            nombresServicios.add(servicio.getNombre());

                            if (servicio.getId() == precioActual.getServicioId()) {
                                posicionSeleccionada = i;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, nombresServicios);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerServicios.setAdapter(adapter);
                        spinnerServicios.setSelection(posicionSeleccionada);

                        titulo.setText("Editar precio de " + precioActual.getNombreServicio());

                        editDescripcion.setText(precioActual.getDescripcion());
                        editTipoUnidad.setText(precioActual.getTipoUnidad());
                        editPrecio.setText(String.valueOf(precioActual.getPrecio()));

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar servicios", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error al cargar servicios", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void editarPrecio() {
        Servicio servicioSeleccionado = listaServicios.get(spinnerServicios.getSelectedItemPosition());
        String descripcion = editDescripcion.getText().toString().trim();
        String tipoUnidad = editTipoUnidad.getText().toString().trim();
        String precioStr = editPrecio.getText().toString().trim();

        if (descripcion.isEmpty() || tipoUnidad.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Completá todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_EDITAR_PRECIO,
                response -> {
                    Toast.makeText(this, "Precio actualizado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                },
                error -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(precioActual.getId()));
                params.put("servicio_id", String.valueOf(servicioSeleccionado.getId()));
                params.put("descripcion", descripcion);
                params.put("tipo_unidad", tipoUnidad);
                params.put("precio", precioStr);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
