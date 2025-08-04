package com.nuevopack.admin.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ApiException;

import android.util.Log;
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
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;

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
        Button btnGoogleCustom = findViewById(R.id.btnGoogleCustom);

        // Verificar si hay una sesión activa
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sesionActiva = prefs.getBoolean("mantenerSesion", false);
        String nombreUsuario = prefs.getString("nombreUsuario", null);

        if (sesionActiva && nombreUsuario != null && !nombreUsuario.isEmpty()) {
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

            loginController.validarLogin(this, usuario, (exito, mensaje, nombre) -> {
                if (exito) {
                    // Guardar preferencias si se marcó el checkbox 'Mantener sesión iniciada'
                    if (checkboxRecordarme.isChecked()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("mantenerSesion", true);
                        editor.putString("emailGuardado", email);
                        editor.putString("nombreUsuario", nombre);
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

        // Configurar login con Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Click del botón de Google
        btnGoogleCustom.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                String nombre = account.getDisplayName();
                String email = account.getEmail();

                // Próximo: verificar si el email está autorizado en la base de datos
                // Por ahora: permitir el acceso directamente

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putBoolean("mantenerSesion", true);
                editor.putString("emailGuardado", email);
                editor.putString("nombreUsuario", nombre);
                editor.apply();

                startActivity(new Intent(this, InicioActivity.class));
                finish();

            } catch (ApiException e) {
                Log.e("GoogleLogin", "Error al iniciar sesión con Google", e);
                Toast.makeText(this, "Error: " + e.getStatusCode(), Toast.LENGTH_LONG).show();            }
        }
    }
}