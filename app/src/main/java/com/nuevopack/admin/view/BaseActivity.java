package com.nuevopack.admin.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.nuevopack.admin.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        drawerLayout = findViewById(R.id.drawerLayout);
        setupNavbarAndSidebar();
    }

    protected void setContenidoLayout(@LayoutRes int layoutResId) {
        ViewGroup contenedor = findViewById(R.id.contentContainer);
        contenedor.removeAllViews(); // Limpiar cualquier contenido previo
        getLayoutInflater().inflate(layoutResId, contenedor, true);
    }

    private void setupNavbarAndSidebar() {
        drawerLayout = findViewById(R.id.drawerLayout);

        // NAVBAR
        View navbar = findViewById(R.id.includeNavbar);
        ImageView iconMenu = findViewById(R.id.iconMenu);
        ImageView iconAccount = findViewById(R.id.iconAccount);

        // Ajustar padding dinámico para barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(navbar, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = statusBarHeight;
            v.setLayoutParams(params);

            return insets;
        });


        iconMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        iconAccount.setOnClickListener(v -> mostrarPopupCuenta());

        // SIDEBAR
        ImageView iconClose = findViewById(R.id.iconClose);
        iconClose.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        Button btnInicio     = findViewById(R.id.btnInicio);
        Button btnServicios  = findViewById(R.id.btnServicios);
        Button btnPrecios    = findViewById(R.id.btnPrecios);
        Button btnUsuarios   = findViewById(R.id.btnUsuarios);
        Button btnSoporte   = findViewById(R.id.btnSoporte);
        Button btnCerrar     = findViewById(R.id.btnCerrarSesion);

        btnInicio.setOnClickListener(v -> {
            startActivity(new Intent(this, InicioActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        btnServicios.setOnClickListener(v -> {
            startActivity(new Intent(this, ServiciosActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        btnPrecios.setOnClickListener(v -> {
            startActivity(new Intent(this, PreciosActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        btnUsuarios.setOnClickListener(v -> {
            startActivity(new Intent(this, UsuariosActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        btnSoporte.setOnClickListener(v -> {
            startActivity(new Intent(this, SoporteActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        btnCerrar.setOnClickListener(v -> confirmarCerrarSesion());
    }

    private void mostrarPopupCuenta() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nombre = prefs.getString("nombreUsuario", "Nombre Apellido");
        String correo = prefs.getString("emailGuardado", "usuario@email.com");

        View view = getLayoutInflater().inflate(R.layout.popup_account_menu, null);
        ((TextView) view.findViewById(R.id.tvNombre)).setText(nombre);
        ((TextView) view.findViewById(R.id.tvCorreo)).setText(correo);

        Button btnCerrar = view.findViewById(R.id.btnCerrarSesion);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnCerrar.setOnClickListener(v -> {
            dialog.dismiss();
            confirmarCerrarSesion();
        });

        dialog.show();
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que desea cerrar su sesión?")
                .setPositiveButton("Sí", (d, which) -> {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    prefs.edit().remove("mantenerSesion").apply();

                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
