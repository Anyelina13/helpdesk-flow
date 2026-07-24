package com.utn.helpdesk;

import com.utn.helpdesk.model.*;
import com.utn.helpdesk.service.GestorIncidencias;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorIncidenciasTest {

    @Test
    void noPuedeFinalizarSinDescripcionDeSolucion() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia incidencia = gestor.registrar("Titulo", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.LISTA);
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.EN_DESARROLLO);
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.EN_VALIDACION);

        assertThrows(IllegalStateException.class, () ->
            gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.FINALIZADA)
        );
    }

    @Test
    void puedeFinalizarCuandoTieneDescripcionDeSolucion() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia incidencia = gestor.registrar("Titulo", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.LISTA);
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.EN_DESARROLLO);
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.EN_VALIDACION);
        gestor.registrarSolucion(incidencia.getId(), "Se reinicio el servicio afectado");

        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.FINALIZADA);

        assertEquals(EstadoIncidencia.FINALIZADA, gestor.getEstado(incidencia.getId()));
    }
}
