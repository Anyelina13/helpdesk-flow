package com.utn.helpdesk;

import com.utn.helpdesk.model.EstadoIncidencia;
import com.utn.helpdesk.service.ValidadorTransicionEstado;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidadorTransicionEstadoTest {

    private final ValidadorTransicionEstado validador = new ValidadorTransicionEstado();

    @Test
    void registradaAListaEsValida() {
        assertTrue(validador.esValida(EstadoIncidencia.REGISTRADA, EstadoIncidencia.LISTA));
    }

    @Test
    void registradaAFinalizadaEsInvalida() {
        assertFalse(validador.esValida(EstadoIncidencia.REGISTRADA, EstadoIncidencia.FINALIZADA));
    }

    @Test
    void finalizadaAEnDesarrolloEsInvalida() {
        assertFalse(validador.esValida(EstadoIncidencia.FINALIZADA, EstadoIncidencia.EN_DESARROLLO));
    }
}
