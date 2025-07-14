package com.nuevopack.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Precio;

import java.util.ArrayList;

public class PrecioAdapter extends RecyclerView.Adapter<PrecioAdapter.ViewHolder> {

    private final ArrayList<Precio> precios;
    private final LayoutInflater inflater;

    public PrecioAdapter(Context context, ArrayList<Precio> precios) {
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
    }

    @Override
    public int getItemCount() {
        return precios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre_servicio, descripcion, tipo_unidad, valor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre_servicio = itemView.findViewById(R.id.nombreServicio);
            descripcion = itemView.findViewById(R.id.descripcionPrecio);
            tipo_unidad = itemView.findViewById(R.id.tipoUnidadPrecio);
            valor = itemView.findViewById(R.id.valorPrecio);
        }
    }
}