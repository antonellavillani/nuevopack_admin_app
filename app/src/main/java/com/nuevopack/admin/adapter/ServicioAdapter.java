package com.nuevopack.admin.adapter;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Servicio;
import com.nuevopack.admin.util.ApiConfig;
import com.nuevopack.admin.view.EditarServicioActivity;
import com.nuevopack.admin.view.ServicioBottomSheet;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ViewHolder> {

    private final List<Servicio> servicios;
    private final Context context;

    public ServicioAdapter(Context context, List<Servicio> servicios) {
        this.context = context;
        this.servicios = servicios;
    }

    @NonNull
    @Override
    public ServicioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioAdapter.ViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);
        holder.nombre.setText(servicio.getNombre());

        holder.imagen.setImageResource(R.drawable.placeholder);

        holder.itemView.setOnClickListener(v -> {
            ServicioBottomSheet dialogo = ServicioBottomSheet.newInstance(servicio);
            dialogo.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "detalleServicio");
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarServicioActivity.class);
            intent.putExtra("id", servicio.getId());
            intent.putExtra("nombre", servicio.getNombre());
            intent.putExtra("descripcion", servicio.getDescripcion());
            intent.putExtra("foto", servicio.getFoto());
            ((Activity) context).startActivityForResult(intent, 101);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("¿Eliminar servicio?")
                    .setMessage("¿Estás seguro de que querés eliminar el servicio \"" + servicio.getNombre() + "\"?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        eliminarServicio(servicio.getId(), holder.getAdapterPosition());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    private void eliminarServicio(int id, int position) {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "backend/api/servicios_abm/eliminar_servicio.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String datos = "id=" + URLEncoder.encode(String.valueOf(id), "UTF-8");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(datos);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    ((Activity) context).runOnUiThread(() -> {
                        servicios.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "No se pudo eliminar el servicio", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        ImageView imagen;
        Button btnEditar;
        Button btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreServicio);
            imagen = itemView.findViewById(R.id.imagenServicio);
            btnEditar = itemView.findViewById(R.id.btnEditarServicio);
            btnEliminar = itemView.findViewById(R.id.btnEliminarServicio);
        }

    }
}