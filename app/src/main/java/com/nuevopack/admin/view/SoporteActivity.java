package com.nuevopack.admin.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nuevopack.admin.R;
import com.nuevopack.admin.adapter.ImagesAdapter;
import com.nuevopack.admin.util.ApiConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SoporteActivity extends BaseActivity {

    private static final int REQUEST_IMAGEN = 200;
    private List<Uri> imagenesUri = new ArrayList<>();
    private ImageView imgAdjunta;
    private TextView textAdjunta;
    private CardView cardAdjunto;
    private Button btnElegirImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContenidoLayout(R.layout.activity_soporte);

        EditText edtAsunto = findViewById(R.id.edtAsunto);
        EditText edtMensaje = findViewById(R.id.edtMensaje);
        imgAdjunta = findViewById(R.id.imgAdjunta);
        textAdjunta = findViewById(R.id.textAdjunta);
        cardAdjunto = findViewById(R.id.cardAdjunto);
        btnElegirImagen = findViewById(R.id.btnAdjuntarImg);
        Button btnEnviar = findViewById(R.id.btnEnviarSoporte);

        btnElegirImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, REQUEST_IMAGEN);
        });

        cardAdjunto.setOnClickListener(v -> {
            if (!imagenesUri.isEmpty()) showImageModal();
        });

        btnEnviar.setOnClickListener(v -> {
            String asunto = edtAsunto.getText().toString().trim();
            String mensaje = edtMensaje.getText().toString().trim();

            if (asunto.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(this, "Completá asunto y mensaje.", Toast.LENGTH_SHORT).show();
                return;
            }
            enviarSoporte(asunto, mensaje, imagenesUri);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGEN && resultCode == RESULT_OK && data != null) {
            imagenesUri.clear();
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    imagenesUri.add(uri);
                }
            } else if (data.getData() != null) {
                imagenesUri.add(data.getData());
            }
            actualizarPreview();
        }
    }

    private void actualizarPreview() {
        if (imagenesUri.isEmpty()) {
            cardAdjunto.setVisibility(View.GONE);
            btnElegirImagen.setText(R.string.soporte_form_imagen);
        } else {
            imgAdjunta.setImageURI(imagenesUri.get(0));
            cardAdjunto.setVisibility(View.VISIBLE);
            btnElegirImagen.setText(R.string.soporte_form_cambiar_imagen);
            if (imagenesUri.size() > 1) {
                textAdjunta.setText("+" + (imagenesUri.size() - 1));
            } else {
                textAdjunta.setText("1 imagen");
            }
        }
    }

    private void showImageModal() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_images_modal);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new ImagesAdapter(imagenesUri, this));
        dialog.findViewById(R.id.btnCloseModal).setOnClickListener(v -> dialog.dismiss());

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        dialog.show();
    }

    private void enviarSoporte(String asunto, String mensaje, List<Uri> imagenes) {
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

                JSONArray imgsBase64 = new JSONArray();
                for (Uri uri : imagenes) {
                    InputStream is = getContentResolver().openInputStream(uri);
                    byte[] bytes = new byte[is.available()];
                    is.read(bytes);
                    is.close();
                    String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                    imgsBase64.put(base64);
                }
                json.put("imagenes", imgsBase64);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(json.toString());
                wr.flush();
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                Log.d("RESPUESTA_SOPORTE", result.toString());

                JSONObject res = new JSONObject(result.toString());
                boolean ok = res.getBoolean("ok");

                runOnUiThread(() -> {
                    dialog.dismiss();
                    if(ok){
                        Toast.makeText(this, "Consulta enviada exitosamente.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String msg = res.optString("msg", "Error al enviar.");
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Error al enviar consulta.", Toast.LENGTH_LONG).show();
                    Log.e("ErrorEnviarSoporte", e.toString());
                });
            }
        }).start();
    }
}
