package com.nuevopack.admin.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nuevopack.admin.R;
import com.nuevopack.admin.util.ApiConfig;

import java.util.HashMap;
import java.util.Map;

public class CrearUsuarioActivity extends AppCompatActivity {

    private EditText inputNombre, inputApellido, inputEmail, inputTelefono, inputPassword, inputRepetirPassword;
    private Button btnCrearUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_form);

        inputNombre = findViewById(R.id.inputNombre);
        inputApellido = findViewById(R.id.inputApellido);
        inputEmail = findViewById(R.id.inputEmail);
        inputTelefono = findViewById(R.id.inputTelefono);
        inputPassword = findViewById(R.id.inputPassword);
        inputRepetirPassword = findViewById(R.id.inputRepetirPassword);
        btnCrearUsuario = findViewById(R.id.btnCrearUsuario);

        btnCrearUsuario.setOnClickListener(v -> crearUsuario());

        Button btnResetearContrasena = findViewById(R.id.btnResetearContrasena);
        btnResetearContrasena.setVisibility(View.GONE);
    }

    private void crearUsuario() {
        String nombre = inputNombre.getText().toString().trim();
        String apellido = inputApellido.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String telefono = inputTelefono.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String repetir = inputRepetirPassword.getText().toString();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || repetir.isEmpty()) {
            Toast.makeText(this, "Completá todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repetir)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!esPasswordSegura(password)) {
            Toast.makeText(this, "La contraseña no cumple los requisitos", Toast.LENGTH_LONG).show();
            return;
        }

        String url = ApiConfig.BASE_URL + "backend/api/usuarios_abm/crear_usuario.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("OK")) {
                        Toast.makeText(this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish(); // volver a la lista
                    } else {
                        Toast.makeText(this, "Error: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("email", email);
                params.put("telefono", telefono);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private boolean esPasswordSegura(String pass) {
        // Al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial
        return pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).{8,}$");
    }
}
