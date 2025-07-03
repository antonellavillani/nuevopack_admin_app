package com.nuevopack.admin.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.nuevopack.admin.R;
import com.nuevopack.admin.controller.DashboardController;
import com.nuevopack.admin.model.ResumenCard;
import com.nuevopack.admin.model.Actividad;
import com.nuevopack.admin.model.Alerta;

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
        Actividad actividad = dashboard.obtenerUltimaActividad();
        TextView tituloActividad = cardActividad.findViewById(R.id.tituloInfo);
        tituloActividad.setText(actividad.getDescripcion());

        // === Alertas ===
        View cardAlertas = findViewById(R.id.includeCardAlertas);
        Alerta alerta = dashboard.obtenerAlertaSistema();
        TextView tituloAlertas = cardAlertas.findViewById(R.id.tituloInfo);
        tituloAlertas.setText(alerta.getMensaje());
    }
}