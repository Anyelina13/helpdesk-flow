package com.utn.helpdesk.model;

// Los pasos por los que pasa una incidencia, en este orden
// No se puede saltar pasos ni retroceder, salvo de EN_VALIDACION a EN_DESARROLLO
public enum EstadoIncidencia {
    REGISTRADA, LISTA, EN_DESARROLLO, EN_VALIDACION, FINALIZADA
}
