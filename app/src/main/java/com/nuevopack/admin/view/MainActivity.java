package com.nuevopack.admin.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.widget.Toast;
import com.nuevopack.admin.controller.LoginController;
import com.nuevopack.admin.model.Usuario;

import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;

import com.nuevopack.admin.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias a views
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        ImageView eyeToggle = findViewById(R.id.eyeToggle);
        Button btnLogin = findViewById(R.id.btnLogin);
        CheckBox checkboxRecordarme = findViewById(R.id.checkboxRecordarme);

        // Verificar si hay una sesión activa
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sesionActiva = prefs.getBoolean("mantenerSesion", false);

        if (sesionActiva) {
            startActivity(new Intent(MainActivity.this, InicioActivity.class));
            finish(); // Cerrar MainActivity
        }

        // Mostrar/ocultar contraseña
        eyeToggle.setOnClickListener(v -> {
            if (inputPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_hide_password);
                inputPassword.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_regular));
            } else {
                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_show_password);
                inputPassword.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_regular));
            }
            inputPassword.setSelection(inputPassword.getText().length());
        });

        // Botón 'Iniciar sesión'
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(this, "Ingresá tu email", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuario = new Usuario(email, password);
            LoginController loginController = new LoginController();

            loginController.validarLogin(this, usuario, (exito, mensaje) -> {
                if (exito) {
                    // Guardar preferencias si se marcó el checkbox 'Mantener sesión iniciada'
                    if (checkboxRecordarme.isChecked()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("mantenerSesion", true);
                        editor.putString("emailGuardado", email);
                        editor.apply();
                    }

                    Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}