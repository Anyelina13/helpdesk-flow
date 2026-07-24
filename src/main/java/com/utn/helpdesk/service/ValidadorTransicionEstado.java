package com.utn.helpdesk.service;

import com.utn.helpdesk.model.EstadoIncidencia;
import java.util.*;

public class ValidadorTransicionEstado {
    private static final Map<EstadoIncidencia, Set<EstadoIncidencia>> TRANSICIONES_VALIDAS = Map.of(
        EstadoIncidencia.REGISTRADA, Set.of(EstadoIncidencia.LISTA),
        EstadoIncidencia.LISTA, Set.of(EstadoIncidencia.EN_DESARROLLO),
        EstadoIncidencia.EN_DESARROLLO, Set.of(EstadoIncidencia.EN_VALIDACION),
        EstadoIncidencia.EN_VALIDACION, Set.of(EstadoIncidencia.FINALIZADA, EstadoIncidencia.EN_DESARROLLO),
        EstadoIncidencia.FINALIZADA, Set.of()
    );

    public boolean esValida(EstadoIncidencia origen, EstadoIncidencia destino) {
        return TRANSICIONES_VALIDAS.getOrDefault(origen, Set.of()).contains(destino);
    }
}
