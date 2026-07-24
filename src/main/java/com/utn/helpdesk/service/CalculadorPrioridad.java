package com.utn.helpdesk.service;

import com.utn.helpdesk.model.*;

public class CalculadorPrioridad {
    public Prioridad calcular(Impacto impacto, Urgencia urgencia) {
        if (impacto == Impacto.ALTO && urgencia == Urgencia.ALTA) return Prioridad.CRITICA;
        if (impacto == Impacto.ALTO) return Prioridad.ALTA;
        return Prioridad.NORMAL;
    }
}
