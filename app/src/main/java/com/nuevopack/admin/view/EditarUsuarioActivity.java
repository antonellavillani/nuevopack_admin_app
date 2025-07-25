package com.nuevopack.admin.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Usuario;
import com.nuevopack.admin.util.ApiConfig;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditarUsuarioActivity extends AppCompatActivity {

    private EditText inputNombre, inputApellido, inputEmail, inputTelefono;
    private CheckBox checkAprobado;
    private Button btnGuardar, btnResetearContrasena;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_form);

        inputNombre = findViewById(R.id.inputNombre);
        inputApellido = findViewById(R.id.inputApellido);
        inputEmail = findViewById(R.id.inputEmail);
        inputEmail.setEnabled(false);
        inputTelefono = findViewById(R.id.inputTelefono);
        checkAprobado = findViewById(R.id.checkAprobado);
        btnGuardar = findViewById(R.id.btnCrearUsuario); // Mismo ID
        btnResetearContrasena = findViewById(R.id.btnResetearContrasena);
        TextView textViewTitulo = findViewById(R.id.textViewTitulo);
        ProgressBar progressBar = findViewById(R.id.progressBarReset);
        TextView mensajeSeguro = findViewById(R.id.mensajeSeguro);

        EditText inputPassword = findViewById(R.id.inputPassword);
        EditText inputRepetirPassword = findViewById(R.id.inputRepetirPassword);

        inputPassword.setVisibility(View.GONE);
        inputRepetirPassword.setVisibility(View.GONE);

        usuario = (Usuario) getIntent().getSerializableExtra("usuario");
        if (usuario != null) {
            textViewTitulo.setText("Editar " + usuario.getNombre() + " " + usuario.getApellido());
            inputNombre.setText(usuario.getNombre());
            inputApellido.setText(usuario.getApellido());
            inputEmail.setText(usuario.getEmail());
            inputTelefono.setText(usuario.getTelefono());
            checkAprobado.setChecked(usuario.isAprobado());
        } else {
            checkAprobado.setChecked(false);
        }

        btnResetearContrasena.setOnClickListener(v -> {
            btnResetearContrasena.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    URL u = new URL(ApiConfig.BASE_URL + "admin-xyz2025/recuperacion/recuperar_password.php");
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("POST");
                    c.setDoOutput(true);
                    String params = "email=" + URLEncoder.encode(inputEmail.getText().toString(), "UTF-8") +
                            "&origen=android";
                    c.getOutputStream().write(params.getBytes());
                    if (c.getResponseCode()==200) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            mensajeSeguro.setVisibility(View.VISIBLE);
                            mensajeSeguro.setText("Se envió el enlace para restablecer la contraseña al email del usuario. Si no es tu cuenta, recomendale al usuario que revise su correo.");
                        });
                    }
                } catch(Exception e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this,"Error al enviar link",Toast.LENGTH_SHORT).show();
                        btnResetearContrasena.setEnabled(true);
                    });
                }
            }).start();
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = inputNombre.getText().toString().trim();
            String apellido = inputApellido.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String telefono = inputTelefono.getText().toString().trim();
            boolean aprobado = checkAprobado.isChecked();

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nombre, apellido y email son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    URL url = new URL(ApiConfig.BASE_URL + "backend/api/usuarios_abm/editar_usuario.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    String params = "id=" + usuario.getId() +
                            "&nombre=" + URLEncoder.encode(nombre, "UTF-8") +
                            "&apellido=" + URLEncoder.encode(apellido, "UTF-8") +
                            "&email=" + URLEncoder.encode(email, "UTF-8") +
                            "&telefono=" + URLEncoder.encode(telefono, "UTF-8") +
                            "&aprobado=" + (aprobado ? "1" : "0");

                    OutputStream os = conn.getOutputStream();
                    os.write(params.getBytes());
                    os.flush();
                    os.close();

                    if (conn.getResponseCode() == 200) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Error en la conexión", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}
