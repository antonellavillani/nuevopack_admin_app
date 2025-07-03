package com.nuevopack.admin.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;

import com.nuevopack.admin.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Pantalla Login
        EditText inputPassword = findViewById(R.id.inputPassword);
        ImageView eyeToggle = findViewById(R.id.eyeToggle);
        Button btnLogin = findViewById(R.id.btnLogin);

        eyeToggle.setOnClickListener(v -> {
            if (inputPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_hide_password); // ícono "ocultar"
                inputPassword.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_regular));

            } else {
                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_show_password); // ícono "ver"
                inputPassword.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_regular));
            }
            inputPassword.setSelection(inputPassword.getText().length());
        });

        // Botón 'Iniciar sesión'
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InicioActivity.class);
            startActivity(intent);
        });
    }
}