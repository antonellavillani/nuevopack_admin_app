package com.nuevopack.admin.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.nuevopack.admin.controller.SoporteController;
import com.nuevopack.admin.service.SoporteService;
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
            ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("Enviando...");
            progress.setCancelable(false);
            progress.show();

            SoporteController controller = new SoporteController(this);
            controller.enviarSoporte(asunto, mensaje, imagenesUri, new SoporteService.Callback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        progress.dismiss();
                        Toast.makeText(SoporteActivity.this, "Consulta enviada exitosamente.", Toast.LENGTH_LONG).show();
                        finish();
        });
    }

            @Override
            public void onError(String errorMsg) {
                runOnUiThread(() -> {
                    progress.dismiss();
                    Toast.makeText(SoporteActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
            }
        });
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
}
