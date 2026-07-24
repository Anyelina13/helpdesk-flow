package com.utn.helpdesk;

import com.utn.helpdesk.model.*;
import com.utn.helpdesk.service.CalculadorPrioridad;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadorPrioridadTest {

    private final CalculadorPrioridad calculador = new CalculadorPrioridad();

    @Test
    void impactoAltoUrgenciaAltaEsCritica() {
        assertEquals(Prioridad.CRITICA, calculador.calcular(Impacto.ALTO, Urgencia.ALTA));
    }

    @Test
    void impactoAltoUrgenciaMediaOBajaEsAlta() {
        assertEquals(Prioridad.ALTA, calculador.calcular(Impacto.ALTO, Urgencia.MEDIA));
        assertEquals(Prioridad.ALTA, calculador.calcular(Impacto.ALTO, Urgencia.BAJA));
    }
}
