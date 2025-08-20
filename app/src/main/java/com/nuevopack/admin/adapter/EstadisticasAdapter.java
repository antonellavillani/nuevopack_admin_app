package com.nuevopack.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.shimmer.ShimmerFrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Estadistica;

import java.util.List;

public class EstadisticasAdapter extends RecyclerView.Adapter<EstadisticasAdapter.ViewHolder> {

    private List<Estadistica> lista;

    public EstadisticasAdapter(List<Estadistica> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estadistica, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Estadistica estadistica = lista.get(position);

        if (estadistica == null) {
            // Mostrar shimmer
            holder.shimmerLayout.setVisibility(View.VISIBLE);
            holder.contentLayout.setVisibility(View.GONE);
            holder.shimmerLayout.startShimmer();
        } else {
            // Mostrar contenido real
            holder.shimmerLayout.stopShimmer();
            holder.shimmerLayout.setVisibility(View.GONE);
            holder.contentLayout.setVisibility(View.VISIBLE);

            holder.titulo.setText(estadistica.getTitulo());
            holder.valor.setText(estadistica.getValor());
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, valor;
        ShimmerFrameLayout shimmerLayout;
        View contentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tituloEstadistica);
            valor = itemView.findViewById(R.id.valorEstadistica);
            shimmerLayout = itemView.findViewById(R.id.shimmerLayout);
            contentLayout = itemView.findViewById(R.id.contentLayout);
        }
    }
}
