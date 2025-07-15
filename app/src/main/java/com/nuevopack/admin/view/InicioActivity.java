package com.nuevopack.admin.view;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nuevopack.admin.R;
import com.nuevopack.admin.adapter.ActividadAdapter;
import com.nuevopack.admin.controller.DashboardController;
import com.nuevopack.admin.model.ResumenCard;
import com.nuevopack.admin.model.Actividad;
import com.nuevopack.admin.model.Alerta;
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

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Menú lateral
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView iconMenu = findViewById(R.id.iconMenu);
        ImageView iconClose = findViewById(R.id.iconClose);

        iconMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        iconClose.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        // Botón Inicio
        Button btnInicio = findViewById(R.id.btnInicio);
        btnInicio.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Botón Servicios
        Button btnServicios = findViewById(R.id.btnServicios);
        btnServicios.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, ServiciosActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // cerrar el menú después de navegar
        });

        // Botón Precios
        Button btnPrecios = findViewById(R.id.btnPrecios);
        btnPrecios.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, PreciosActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Botón Usuarios
        Button btnUsuarios = findViewById(R.id.btnUsuarios);
        btnUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, UsuariosActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Instanciar el controlador
        DashboardController dashboard = new DashboardController();

        // === Servicios ===
        View cardServicios = findViewById(R.id.includeCardServicios);
        ResumenCard serviciosCard = dashboard.obtenerResumenServicios();
        TextView tituloServicios = cardServicios.findViewById(R.id.tituloCard);
        TextView textoServicios = cardServicios.findViewById(R.id.textoCard);
        tituloServicios.setText(serviciosCard.getTitulo());
        textoServicios.setText(serviciosCard.getDescripcion());

        // === Precios ===
        View cardPrecios = findViewById(R.id.includeCardPrecios);
        ResumenCard preciosCard = dashboard.obtenerResumenPrecios();
        TextView tituloPrecios = cardPrecios.findViewById(R.id.tituloCard);
        TextView textoPrecios = cardPrecios.findViewById(R.id.textoCard);
        tituloPrecios.setText(preciosCard.getTitulo());
        textoPrecios.setText(preciosCard.getDescripcion());

        // === Usuarios ===
        View cardUsuarios = findViewById(R.id.includeCardUsuarios);
        ResumenCard usuariosCard = dashboard.obtenerResumenUsuarios();
        TextView tituloUsuarios = cardUsuarios.findViewById(R.id.tituloCard);
        TextView textoUsuarios = cardUsuarios.findViewById(R.id.textoCard);
        tituloUsuarios.setText(usuariosCard.getTitulo());
        textoUsuarios.setText(usuariosCard.getDescripcion());

        // === Actividad Reciente ===
        View cardActividad = findViewById(R.id.includeCardActividad);
        RecyclerView recyclerActividad = cardActividad.findViewById(R.id.recyclerActividad);
        recyclerActividad.setLayoutManager(new LinearLayoutManager(this));

        List<Actividad> listaActividades = new ArrayList<>();
        ActividadAdapter adapter = new ActividadAdapter(listaActividades);
        recyclerActividad.setAdapter(adapter);

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
                    String descripcion = obj.getString("descripcion");
                    String fecha = obj.getString("fecha");
                    nuevasActividades.add(new Actividad(descripcion, fecha));
                }

                runOnUiThread(() -> {
                    listaActividades.clear();
                    listaActividades.addAll(nuevasActividades);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    listaActividades.clear();
                    listaActividades.add(new Actividad("Sin conexión con el servidor de base de datos", ""));
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();

        // === Alertas ===
        View cardAlertas = findViewById(R.id.includeCardAlertas);
        RecyclerView recycler = cardAlertas.findViewById(R.id.recyclerActividad);
        TextView textoAlerta = cardAlertas.findViewById(R.id.contenidoInfoAlerta);

        // Ocultar RecyclerView y mostrar el TextView
        recycler.setVisibility(View.GONE);
        textoAlerta.setVisibility(View.VISIBLE);
        textoAlerta.setText("• Todos los sistemas funcionan correctamente.");

    }
}