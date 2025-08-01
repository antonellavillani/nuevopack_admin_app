package com.nuevopack.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuevopack.admin.R;

import java.util.List;

public class AlertasAdapter extends RecyclerView.Adapter<AlertasAdapter.AlertaViewHolder> {

    private final List<String> lista;

    public AlertasAdapter(List<String> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public AlertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alerta, parent, false);
        return new AlertaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertaViewHolder holder, int position) {
        holder.texto.setText(lista.get(position));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class AlertaViewHolder extends RecyclerView.ViewHolder {
        TextView texto;

        public AlertaViewHolder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.textoItemAlerta);
        }
    }
}
