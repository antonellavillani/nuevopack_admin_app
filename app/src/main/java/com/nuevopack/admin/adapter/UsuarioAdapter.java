package com.nuevopack.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Usuario;
import com.nuevopack.admin.view.EditarUsuarioActivity;

import java.util.ArrayList;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private final ArrayList<Usuario> usuarios;
    private final LayoutInflater inflater;

    public UsuarioAdapter(Context context, ArrayList<Usuario> usuarios) {
        this.inflater = LayoutInflater.from(context);
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public UsuarioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.ViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.nombreCompleto.setText(usuario.getNombreCompleto());
        holder.id.setText("ID: " + usuario.getId());
        holder.email.setText("Email: " + usuario.getEmail());
        holder.telefono.setText("Teléfono: " + usuario.getTelefono());
        holder.aprobado.setText(usuario.isAprobado() ? "Aprobado" : "No aprobado");

        holder.btnEditarUsuario.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditarUsuarioActivity.class);
            intent.putExtra("usuario", usuario);
            ((Activity) context).startActivityForResult(intent, 301);
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreCompleto, id, email, telefono, aprobado;
        Button btnEditarUsuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreCompleto = itemView.findViewById(R.id.nombreUsuario);
            id = itemView.findViewById(R.id.idUsuario);
            email = itemView.findViewById(R.id.emailUsuario);
            telefono = itemView.findViewById(R.id.telefonoUsuario);
            aprobado = itemView.findViewById(R.id.aprobadoUsuario);
            btnEditarUsuario = itemView.findViewById(R.id.btnEditarUsuario);
        }
    }
}