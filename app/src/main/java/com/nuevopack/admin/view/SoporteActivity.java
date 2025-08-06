package com.nuevopack.admin.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.nuevopack.admin.R;
import com.nuevopack.admin.util.ApiConfig;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.cardview.widget.CardView;

public class SoporteActivity extends BaseActivity {

    private static final int REQUEST_IMAGEN = 200;
    private Uri imagenUri;
    private Button btnElegirImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContenidoLayout(R.layout.activity_soporte);

        EditText edtAsunto = findViewById(R.id.edtAsunto);
        EditText edtMensaje = findViewById(R.id.edtMensaje);
        ImageView imgAdjunta = findViewById(R.id.imgAdjunta);
        btnElegirImagen = findViewById(R.id.btnAdjuntarImg);
        Button btnEnviar = findViewById(R.id.btnEnviarSoporte);

        btnElegirImagen.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_IMAGEN);
        });

        btnEnviar.setOnClickListener(v -> {
            String asunto = edtAsunto.getText().toString().trim();
            String mensaje = edtMensaje.getText().toString().trim();

            if (asunto.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(this, "Completá asunto y mensaje.", Toast.LENGTH_SHORT).show();
                return;
            }
            enviarSoporte(asunto, mensaje);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGEN && resultCode == RESULT_OK && data != null) {
            imagenUri = data.getData();
            ImageView imgAdj = findViewById(R.id.imgAdjunta);
            CardView cardAdj = findViewById(R.id.cardAdjunto);
            imgAdj.setImageURI(imagenUri);
            cardAdj.setVisibility(View.VISIBLE);
            btnElegirImagen.setText(R.string.soporte_form_cambiar_imagen);
        }
    }

    private void enviarSoporte(String asunto, String mensaje) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Enviando...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL+"backend/api/enviar_soporte.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();
                json.put("asunto", asunto);
                json.put("mensaje", mensaje);
                if (imagenUri != null) { // Convertir imagen a Base64 si existe
                    InputStream is = getContentResolver().openInputStream(imagenUri);
                    assert is != null;
                    byte[] bytes = new byte[is.available()];
                    is.read(bytes);
                    is.close();
                    String base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
                    json.put("imagen", base64);
                } else {
                    json.put("imagen", "");
                }

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(json.toString());
                wr.flush();
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                // Log para depuración
                Log.d("RESPUESTA_SOPORTE", result.toString());

                // Leer respuesta
                JSONObject res = new JSONObject(result.toString());
                boolean ok = res.getBoolean("ok");

                runOnUiThread(() -> {
                    dialog.dismiss();
                    if(ok){
                        Toast.makeText(this, "Consulta enviada exitosamente.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String msg = "Error al enviar.";
                        try {
                            if(res.has("msg")) {
                                msg = res.getString("msg");
                            }
                        } catch (Exception e) {
                            // En caso de que no sea string
                            msg = "Error al enviar.";
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Error al enviar consulta.", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
