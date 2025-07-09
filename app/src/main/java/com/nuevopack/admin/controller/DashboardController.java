package com.nuevopack.admin.controller;

import com.nuevopack.admin.model.Alerta;
import com.nuevopack.admin.model.Actividad;
import com.nuevopack.admin.model.ResumenCard;

public class DashboardController {

    public ResumenCard obtenerResumenServicios() {
        return new ResumenCard("Servicios", "6 cargados");
    }

    public ResumenCard obtenerResumenPrecios() {
        return new ResumenCard("Precios", "12 registrados");
    }

    public ResumenCard obtenerResumenUsuarios() {
        return new ResumenCard("Usuarios", "3 activos");
    }

}