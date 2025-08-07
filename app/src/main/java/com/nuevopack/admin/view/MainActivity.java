package com.nuevopack.admin.view;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.nuevopack.admin.util.ApiConfig;
import org.json.JSONObject;

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
        String emailGuardado = prefs.getString("emailGuardado", null);

        if (sesionActiva && nombreUsuario != null && !nombreUsuario.isEmpty() && emailGuardado != null && !emailGuardado.isEmpty()) {
            // Verificación con el backend si el usuario está aprobado antes de continuar
            String url = ApiConfig.BASE_URL + "backend/api/login_google.php?email=" + emailGuardado + "&nombre=" + nombreUsuario;

            new Thread(() -> {
                try {
                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }

                    JSONObject response = new JSONObject(json.toString());
                    String status = response.getString("status");

                    runOnUiThread(() -> {
                        if (status.equals("autorizado")) {
                            // Usuario aprobado, continuar a InicioActivity
                            startActivity(new Intent(MainActivity.this, InicioActivity.class));
                            finish();
                        } else if (status.equals("pendiente")) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Aprobación pendiente")
                                    .setMessage("Tu cuenta está pendiente de aprobación. Por favor, comunicarse con NuevoPack.")
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        } else if (status.equals("nuevo")) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Cuenta registrada con éxito")
                                    .setMessage("Tu cuenta fue registrada pero necesita ser aprobada. Por favor, comunicarse con NuevoPack.")
                                    .setPositiveButton("Aceptar", null)
                                    .show();
                        } else {
                            Toast.makeText(this, "Estado de cuenta desconocido.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this,
                            "Error al verificar sesión con el servidor", Toast.LENGTH_LONG).show());
                }
            }).start();
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
                String nombre = account.getGivenName();
                String apellido = account.getFamilyName();
                String email = account.getEmail();

                String nombreEncoded = URLEncoder.encode(nombre, "UTF-8");
                String apellidoEncoded = URLEncoder.encode(apellido, "UTF-8");
                String emailEncoded = URLEncoder.encode(email, "UTF-8");

                // 1. Llamada al backend
                String url = ApiConfig.BASE_URL + "backend/api/login_google.php?email=" + emailEncoded + "&nombre=" + nombreEncoded + "&apellido=" + apellidoEncoded;

                new Thread(() -> {
                    try {
                        java.net.URL urlObj = new java.net.URL(url);
                        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection();
                        conn.setRequestMethod("GET");

                        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                        StringBuilder json = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }

                        org.json.JSONObject response = new org.json.JSONObject(json.toString());
                        String status = response.getString("status");

                        runOnUiThread(() -> {
                            if (status.equals("autorizado")) {
                                // Guardar sesión
                                SharedPreferences.Editor editor = PreferenceManager
                                        .getDefaultSharedPreferences(this)
                                        .edit();
                                editor.putBoolean("mantenerSesion", true);
                                editor.putString("emailGuardado", email);
                                editor.putString("nombreUsuario", nombre);
                                editor.apply();

                                startActivity(new Intent(this, InicioActivity.class));
                                finish();
                            }
                            else if (status.equals("pendiente")) {
                                new AlertDialog.Builder(this)
                                        .setTitle("Aprobación pendiente")
                                        .setMessage("Tu cuenta está pendiente de aprobación. Por favor, comunicarse con NuevoPack.")
                                        .setPositiveButton("Aceptar", null)
                                        .show();
                            }   else if (status.equals("nuevo")) {
                                new AlertDialog.Builder(this)
                                        .setTitle("Cuenta registrada con éxito")
                                        .setMessage("Tu cuenta fue registrada pero necesita ser aprobada. Por favor, comunicarse con NuevoPack.")
                                        .setPositiveButton("Aceptar", null)
                                        .show();
                            } else {
                                Toast.makeText(this, "Ocurrió un error.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(this,
                                "Error al verificar con el servidor", Toast.LENGTH_LONG).show());
                    }
                }).start();

            } catch (ApiException e) {
                Log.e("GoogleLogin", "Error al iniciar sesión con Google", e);
                Toast.makeText(this, "Error: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}