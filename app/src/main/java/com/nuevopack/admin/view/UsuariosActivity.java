package com.nuevopack.admin.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nuevopack.admin.R;
import com.nuevopack.admin.adapter.UsuarioAdapter;
import com.nuevopack.admin.model.Usuario;
import com.nuevopack.admin.util.ApiConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UsuariosActivity extends BaseActivity {

    private final ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    private UsuarioAdapter adapter;
    private static final int REQUEST_NUEVO_USUARIO = 300;
    private static final int REQUEST_EDITAR_USUARIO = 301;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String mensaje = data.getQueryParameter("mensaje");
            if (mensaje != null && mensaje.equals("ok")) {
                Toast.makeText(this, "Contraseña modificada con éxito", Toast.LENGTH_LONG).show();
            }
        }

        setContenidoLayout(R.layout.activity_usuarios);

        RecyclerView recycler = findViewById(R.id.recyclerUsuarios);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsuarioAdapter(this, listaUsuarios);
        recycler.setAdapter(adapter);

        cargarUsuarios();

        Button btnNuevoUsuario = findViewById(R.id.btnNuevoUsuario);
        btnNuevoUsuario.setOnClickListener(v -> {
            Intent crearIntent = new Intent(UsuariosActivity.this, CrearUsuarioActivity.class);
            startActivityForResult(crearIntent, REQUEST_NUEVO_USUARIO);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_NUEVO_USUARIO || requestCode == REQUEST_EDITAR_USUARIO) && resultCode == RESULT_OK) {
            cargarUsuarios(); // recarga la lista
        }
    }

    private void cargarUsuarios() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "backend/api/get_usuarios_especiales.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    json.append(linea);
                }

                JSONArray array = new JSONArray(json.toString());
                ArrayList<Usuario> nuevos = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("id");
                    String nombre = obj.getString("nombre");
                    String apellido = obj.getString("apellido");
                    String email = obj.getString("email");
                    String telefono = obj.getString("telefono");
                    boolean aprobado = obj.getInt("aprobado") == 1;
                    nuevos.add(new Usuario(id, nombre, apellido, email, telefono, aprobado));
                }

                runOnUiThread(() -> {
                    listaUsuarios.clear();
                    listaUsuarios.addAll(nuevos);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                Log.e("UsuariosActivity", "Error al cargar usuarios", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}