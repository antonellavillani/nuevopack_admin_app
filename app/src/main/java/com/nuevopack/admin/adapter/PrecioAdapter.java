package com.nuevopack.admin.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Precio;
import com.nuevopack.admin.util.ApiConfig;
import com.nuevopack.admin.view.EditarPrecioActivity;

import java.util.ArrayList;

public class PrecioAdapter extends RecyclerView.Adapter<PrecioAdapter.ViewHolder> {

    private final ArrayList<Precio> precios;
    private final LayoutInflater inflater;
    private final Context context;

    public PrecioAdapter(Context context, ArrayList<Precio> precios) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.precios = precios;
    }

    @NonNull
    @Override
    public PrecioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_precio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrecioAdapter.ViewHolder holder, int position) {
        Precio precio = precios.get(position);
        holder.nombre_servicio.setText(precio.getNombreServicio());
        holder.descripcion.setText(precio.getDescripcion());
        holder.tipo_unidad.setText("Unidad: " + precio.getTipoUnidad());
        holder.valor.setText("$" + String.format("%.2f", precio.getPrecio()));

        holder.btnEditarPrecio.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditarPrecioActivity.class);
            intent.putExtra("id", precio.getId());
            intent.putExtra("servicio_id", precio.getServicioId());
            intent.putExtra("nombre_servicio", precio.getNombreServicio());
            intent.putExtra("descripcion", precio.getDescripcion());
            intent.putExtra("tipo_unidad", precio.getTipoUnidad());
            intent.putExtra("precio", precio.getPrecio());
            ((Activity) v.getContext()).startActivityForResult(intent, 201);
        });

        holder.btnEliminarPrecio.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("¿Eliminar servicio?")
                    .setMessage("¿Estás seguro de que querés eliminar este precio?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        eliminarPrecio(precio.getId(), holder.getAdapterPosition());
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return precios.size();
    }

    private void eliminarPrecio(int idPrecio, int position) {
        String url = ApiConfig.BASE_URL + "backend/api/precios_abm/eliminar_precio.php";

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("OK")) {
                        precios.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Precio eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(idPrecio));
                return params;
            }
        };

        queue.add(postRequest);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre_servicio, descripcion, tipo_unidad, valor;
        Button btnEditarPrecio, btnEliminarPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre_servicio = itemView.findViewById(R.id.nombreServicio);
            descripcion = itemView.findViewById(R.id.descripcionPrecio);
            tipo_unidad = itemView.findViewById(R.id.tipoUnidadPrecio);
            valor = itemView.findViewById(R.id.valorPrecio);
            btnEditarPrecio = itemView.findViewById(R.id.btnEditarPrecio);
            btnEliminarPrecio = itemView.findViewById(R.id.btnEliminarPrecio);
        }
    }
}