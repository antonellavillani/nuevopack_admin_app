package com.nuevopack.admin.view;
import com.nuevopack.admin.controller.ResumenCardController;
import com.nuevopack.admin.model.ResumenCard;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.nuevopack.admin.R;

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

        // Crear controlador
        ResumenCardController cardController = new ResumenCardController();

        // === Card Servicios ===
        View cardServicios = findViewById(R.id.includeCardServicios);
        ResumenCard serviciosCard = cardController.getServiciosCard();
        TextView tituloServicios = cardServicios.findViewById(R.id.tituloCard);
        TextView textoServicios = cardServicios.findViewById(R.id.textoCard);
        tituloServicios.setText(serviciosCard.getTitulo());
        textoServicios.setText(serviciosCard.getDescripcion());

        // === Card Precios ===
        View cardPrecios = findViewById(R.id.includeCardPrecios);
        ResumenCard preciosCard = cardController.getPreciosCard();
        TextView tituloPrecios = cardPrecios.findViewById(R.id.tituloCard);
        TextView textoPrecios = cardPrecios.findViewById(R.id.textoCard);
        tituloPrecios.setText(preciosCard.getTitulo());
        textoPrecios.setText(preciosCard.getDescripcion());

        // === Card Usuarios ===
        View cardUsuarios = findViewById(R.id.includeCardUsuarios);
        ResumenCard usuariosCard = cardController.getUsuariosCard();
        TextView tituloUsuarios = cardUsuarios.findViewById(R.id.tituloCard);
        TextView textoUsuarios = cardUsuarios.findViewById(R.id.textoCard);
        tituloUsuarios.setText(usuariosCard.getTitulo());
        textoUsuarios.setText(usuariosCard.getDescripcion());

        // === Card Actividad Reciente ===
        View cardActividad = findViewById(R.id.includeCardActividad);
        ResumenCard actividadCard = cardController.getActividadCard();
        TextView tituloActividad = cardActividad.findViewById(R.id.tituloInfo);
        tituloActividad.setText(actividadCard.getTitulo());

        // === Card Alertas ===
        View cardAlertas = findViewById(R.id.includeCardAlertas);
        ResumenCard alertasCard = cardController.getAlertasCard();
        TextView tituloAlertas = cardAlertas.findViewById(R.id.tituloInfo);
        tituloAlertas.setText(alertasCard.getTitulo());
    }
}