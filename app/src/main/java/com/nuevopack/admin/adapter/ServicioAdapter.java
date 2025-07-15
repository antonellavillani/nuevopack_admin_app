package com.nuevopack.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Servicio;
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
            // Acá se abre la pantalla de edición
            Toast.makeText(v.getContext(), "Editar: " + servicio.getNombre(), Toast.LENGTH_SHORT).show();
        });

        holder.btnEliminar.setOnClickListener(v -> {
            // Acá se abre la pantalla de eliminación
            Toast.makeText(v.getContext(), "Eliminar: " + servicio.getNombre(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return servicios.size();
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