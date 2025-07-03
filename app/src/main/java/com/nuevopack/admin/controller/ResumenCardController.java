package com.nuevopack.admin.controller;

import com.nuevopack.admin.model.ResumenCard;
import java.util.ArrayList;
import java.util.List;

public class ResumenCardController {

    public ResumenCard getServiciosCard() {
        return new ResumenCard("Servicios", "6 cargados");
    }

    public ResumenCard getPreciosCard() {
        return new ResumenCard("Precios", "12 registrados");
    }

    public ResumenCard getUsuariosCard() {
        return new ResumenCard("Usuarios", "3 activos");
    }

    public ResumenCard getActividadCard() {
        return new ResumenCard("Actividad Reciente", "");
    }

    public ResumenCard getAlertasCard() {
        return new ResumenCard("Alertas del sistema", "");
    }

    // Devolver una lista con todas las cards
    public List<ResumenCard> getTodasLasCards() {
        List<ResumenCard> lista = new ArrayList<>();
        lista.add(getServiciosCard());
        lista.add(getPreciosCard());
        lista.add(getUsuariosCard());
        lista.add(getActividadCard());
        lista.add(getAlertasCard());
        return lista;
    }
}