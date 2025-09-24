package com.nuevopack.admin.adapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
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

        String urlImagen = servicio.getFoto();
        if (urlImagen != null && !urlImagen.isEmpty() && !"null".equalsIgnoreCase(urlImagen)) {
            urlImagen = ApiConfig.BASE_URL + "uploads/" + urlImagen;
            urlImagen = urlImagen.replaceAll("(?<!(http:|https:))/+", "/");

            holder.shimmerLayout.startShimmer();
            Glide.with(context)
                    .load(urlImagen)
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.sin_imagen)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            holder.shimmerLayout.stopShimmer();
                            holder.shimmerLayout.setShimmer(null);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.shimmerLayout.stopShimmer();
                            holder.shimmerLayout.setShimmer(null);
                            return false;
                        }
                    })
                    .into(holder.imagenServicio);
        } else {
            holder.imagenServicio.setImageResource(R.drawable.sin_imagen);
            holder.shimmerLayout.stopShimmer();
            holder.shimmerLayout.setShimmer(null);
        }

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

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(responseCode < 400 ? conn.getInputStream() : conn.getErrorStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                ((Activity) context).runOnUiThread(() -> {
                    if (responseCode == 200) {
                        Toast.makeText(context, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                        servicios.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        String mensaje = response.toString();

                        // Si se recibe SQLSTATE 23000
                        if (mensaje.contains("SQLSTATE[23000]")) {
                            mensaje = "El servicio no puede eliminarse porque tiene precios asociados. Eliminalos primero o desvincúlalos.";
                        }

                        new AlertDialog.Builder(context)
                                .setTitle("No se puede eliminar")
                                .setMessage(mensaje)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });

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
        ShimmerFrameLayout shimmerLayout;
        ImageView imagenServicio;


        ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreServicio);
            imagen = itemView.findViewById(R.id.imagenServicio);
            btnEditar = itemView.findViewById(R.id.btnEditarServicio);
            btnEliminar = itemView.findViewById(R.id.btnEliminarServicio);
            shimmerLayout = itemView.findViewById(R.id.shimmerLayout);
            imagenServicio = itemView.findViewById(R.id.imagenServicio);
        }

    }
}