package com.nuevopack.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Menú
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView iconMenu = findViewById(R.id.iconMenu);
        ImageView iconClose = findViewById(R.id.iconClose);
        iconMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        iconClose.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        // === Card Servicios ===
        View cardServicios = findViewById(R.id.includeCardServicios);
        TextView tituloServicios = cardServicios.findViewById(R.id.tituloCard);
        TextView textoServicios = cardServicios.findViewById(R.id.textoCard);
        tituloServicios.setText("Servicios");
        textoServicios.setText("6 cargados");

        // === Card Precios ===
        View cardPrecios = findViewById(R.id.includeCardPrecios);
        TextView tituloPrecios = cardPrecios.findViewById(R.id.tituloCard);
        TextView textoPrecios = cardPrecios.findViewById(R.id.textoCard);
        tituloPrecios.setText("Precios");
        textoPrecios.setText("12 actualizados");

        // === Card Usuarios ===
        View cardUsuarios = findViewById(R.id.includeCardUsuarios);
        TextView tituloUsuarios = cardUsuarios.findViewById(R.id.tituloCard);
        TextView textoUsuarios = cardUsuarios.findViewById(R.id.textoCard);
        tituloUsuarios.setText("Usuarios");
        textoUsuarios.setText("3 registrados");

        // === Card Actividad Reciente ===
        View cardActividad = findViewById(R.id.includeCardActividad);
        TextView tituloActividad = cardActividad.findViewById(R.id.tituloInfo);
        tituloActividad.setText("Actividad Reciente");

        // === Card Alertas ===
        View cardAlertas = findViewById(R.id.includeCardAlertas);
        TextView tituloAlertas = cardAlertas.findViewById(R.id.tituloInfo);
        tituloAlertas.setText("Alertas del sistema");
    }
}