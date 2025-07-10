package com.nuevopack.admin.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Servicio;

public class ServicioBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_DESCRIPCION = "descripcion";
    private static final String ARG_FOTO = "foto";

    public static ServicioBottomSheet newInstance(Servicio servicio) {
        ServicioBottomSheet fragment = new ServicioBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE, servicio.getNombre());
        args.putString(ARG_DESCRIPCION, servicio.getDescripcion());
        args.putString(ARG_FOTO, servicio.getFoto());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.bottomsheet_servicio, container, false);

        TextView nombre = vista.findViewById(R.id.nombreCompleto);
        TextView descripcion = vista.findViewById(R.id.descripcionServicio);
        ImageView imagen = vista.findViewById(R.id.imagenCompleta);

        Bundle args = getArguments();
        if (args != null) {
            nombre.setText(args.getString(ARG_NOMBRE));
            descripcion.setText(args.getString(ARG_DESCRIPCION));
            imagen.setImageResource(R.drawable.placeholder);
        }

        return vista;
    }
}