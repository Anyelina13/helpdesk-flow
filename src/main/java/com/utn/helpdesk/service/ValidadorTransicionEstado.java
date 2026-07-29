package com.utn.helpdesk.service;

import com.utn.helpdesk.model.EstadoIncidencia;
import java.util.*;

public class ValidadorTransicionEstado {
    // Por cada estado se indica a que otros estados se puede pasar
    // Desde EN_VALIDACION se puede volver a EN_DESARROLLO si algo no quedo bien
    private static final Map<EstadoIncidencia, Set<EstadoIncidencia>> TRANSICIONES_VALIDAS = Map.of(
        EstadoIncidencia.REGISTRADA, Set.of(EstadoIncidencia.LISTA),
        EstadoIncidencia.LISTA, Set.of(EstadoIncidencia.EN_DESARROLLO),
        EstadoIncidencia.EN_DESARROLLO, Set.of(EstadoIncidencia.EN_VALIDACION),
        EstadoIncidencia.EN_VALIDACION, Set.of(EstadoIncidencia.FINALIZADA, EstadoIncidencia.EN_DESARROLLO),
        EstadoIncidencia.FINALIZADA, Set.of()
    );

    // Revisa si el cambio de un estado a otro esta permitido
    public boolean esValida(EstadoIncidencia origen, EstadoIncidencia destino) {
        return TRANSICIONES_VALIDAS.getOrDefault(origen, Set.of()).contains(destino);
    }
}
