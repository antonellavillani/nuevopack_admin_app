package com.nuevopack.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Actividad;
import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder> {

    private List<Actividad> actividades;

    public ActividadAdapter(List<Actividad> actividades) {
        this.actividades = actividades;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Actividad actividad = actividades.get(position);
        holder.textoActividad.setText("• " + actividad.getDescripcion() + " (" + actividad.getFecha() + ")");
    }

    @Override
    public int getItemCount() {
        return actividades.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textoActividad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textoActividad = itemView.findViewById(R.id.textoActividad);
        }
    }
}