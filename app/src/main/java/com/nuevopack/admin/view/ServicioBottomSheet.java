package com.nuevopack.admin.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nuevopack.admin.R;
import com.nuevopack.admin.model.Servicio;
import com.nuevopack.admin.util.ApiConfig;

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
            String nombreArchivo = args.getString(ARG_FOTO);
            String urlImagen = ApiConfig.BASE_URL + "uploads/" + nombreArchivo;
            urlImagen = urlImagen.replaceAll("(?<!(http:|https:))/+", "/");
            Log.d("URL_IMAGEN", urlImagen);

            Glide.with(this)
                    .load(urlImagen)
                    .error(R.drawable.imagen_error)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("GLIDE_ERROR", "Error al cargar imagen", e);
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imagen);
        }

        return vista;
    }
}