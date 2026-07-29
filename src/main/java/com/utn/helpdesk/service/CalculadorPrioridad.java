package com.utn.helpdesk.service;

import com.utn.helpdesk.model.*;

// Esta clase solo se encarga de decidir la prioridad, no hace nada mas
public class CalculadorPrioridad {
    public Prioridad calcular(Impacto impacto, Urgencia urgencia) {
        // Impacto alto y urgencia alta juntos dan la prioridad mas seria
        if (impacto == Impacto.ALTO && urgencia == Urgencia.ALTA) return Prioridad.CRITICA;
        if (impacto == Impacto.ALTO) return Prioridad.ALTA;
        if (urgencia == Urgencia.ALTA) return Prioridad.ALTA;
        // Cualquier otra combinacion se considera un caso comun
        return Prioridad.NORMAL;
    }
}
