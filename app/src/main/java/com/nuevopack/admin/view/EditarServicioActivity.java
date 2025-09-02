package com.nuevopack.admin.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nuevopack.admin.R;
import com.nuevopack.admin.util.ApiConfig;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.text.TextUtils;

import java.io.OutputStreamWriter;
import java.net.URLEncoder;

public class EditarServicioActivity extends AppCompatActivity {
    private EditText inputNombre, inputDescripcion;
    private ImageView imagePreview;
    private String imageBase64 = "";
    private int servicioId;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicio_form);

        inputNombre = findViewById(R.id.inputNombre);
        inputDescripcion = findViewById(R.id.inputDescripcion);
        imagePreview = findViewById(R.id.imagePreview);
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnEliminarImagen = findViewById(R.id.btnEliminarImagen);
        Button btnGuardar = findViewById(R.id.btnGuardarServicio);

        // Recuperar datos del Intent
        servicioId = getIntent().getIntExtra("id", -1);
        String nombre = getIntent().getStringExtra("nombre");
        String descripcion = getIntent().getStringExtra("descripcion");
        String fotoUrl = getIntent().getStringExtra("foto");

        TextView textViewTitulo = findViewById(R.id.textViewTitulo);
        textViewTitulo.setText("Editar " + nombre);
        inputNombre.setText(nombre);
        inputDescripcion.setText(descripcion);

        // Cargar imagen actual (si hay)
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            String urlImagen = ApiConfig.BASE_URL + "uploads/" + fotoUrl;
            urlImagen = urlImagen.replaceAll("(?<!(http:|https:))/+", "/");

            Glide.with(this)
                    .asBitmap()
                    .load(urlImagen)
                    .error(R.drawable.sin_imagen)
                    .into(new com.bumptech.glide.request.target.BitmapImageViewTarget(imagePreview) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            if (resource != null) {
                                super.setResource(resource);
                                imagePreview.setImageBitmap(resource);
                                imageBase64 = "";

                            } else {
                                // Imagen no cargó: mostrar placeholder y limpiar base64
                                imagePreview.setImageResource(R.drawable.sin_imagen);
                                imageBase64 = "";
                            }
                        }
                    });
        } else {
            // No había fotoUrl → placeholder por default
            imagePreview.setImageResource(R.drawable.sin_imagen);
            imageBase64 = "";
        }

        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());

        btnEliminarImagen.setOnClickListener(v -> {
            imagePreview.setImageResource(R.drawable.sin_imagen);
            imageBase64 = "ELIMINAR";
        });

        btnGuardar.setText("Actualizar");
        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = inputNombre.getText().toString().trim();
            String nuevaDescripcion = inputDescripcion.getText().toString().trim();

            if (TextUtils.isEmpty(nuevoNombre) || TextUtils.isEmpty(nuevaDescripcion)) {
                Toast.makeText(this, "Completá todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            actualizarServicio(servicioId, nuevoNombre, nuevaDescripcion, imageBase64);
        });
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imagenUri = data.getData();
            imagePreview.setImageURI(imagenUri);

            try {
                InputStream inputStream = getContentResolver().openInputStream(imagenUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageBytes = baos.toByteArray();
                imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (Exception e) {
                Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void actualizarServicio(int id, String nombre, String descripcion, String imagenBase64) {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "backend/api/servicios_abm/editar_servicio.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String datos = "id=" + URLEncoder.encode(String.valueOf(id), "UTF-8") +
                        "&nombre=" + URLEncoder.encode(nombre, "UTF-8") +
                        "&descripcion=" + URLEncoder.encode(descripcion, "UTF-8") +
                        "&foto=" + URLEncoder.encode(imagenBase64, "UTF-8");

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(datos);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Error al actualizar el servicio", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
