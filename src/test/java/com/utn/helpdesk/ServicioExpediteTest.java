package com.utn.helpdesk;

import com.utn.helpdesk.model.*;
import com.utn.helpdesk.service.GestorIncidencias;
import com.utn.helpdesk.service.ServicioExpedite;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServicioExpediteTest {

    @Test
    void soloUnaIncidenciaCriticaPuedeMarcarseComoExpedite() {
        GestorIncidencias gestor = new GestorIncidencias();
        ServicioExpedite servicio = new ServicioExpedite(gestor);
        Incidencia normal = gestor.registrar("Titulo", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);

        assertThrows(IllegalStateException.class, () -> servicio.marcarComoExpedite(normal.getId()));
    }

    @Test
    void marcarComoExpediteFuncionaParaUnaIncidenciaCritica() {
        GestorIncidencias gestor = new GestorIncidencias();
        ServicioExpedite servicio = new ServicioExpedite(gestor);
        Incidencia critica = gestor.registrar("Titulo", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);

        servicio.marcarComoExpedite(critica.getId());

        assertTrue(critica.isExpedite());
    }

    @Test
    void noPermiteDosIncidenciasExpediteActivasEnDesarrolloOValidacionALaVez() {
        GestorIncidencias gestor = new GestorIncidencias();
        ServicioExpedite servicio = new ServicioExpedite(gestor);
        Incidencia i1 = gestor.registrar("Titulo 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        Incidencia i2 = gestor.registrar("Titulo 2", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        servicio.marcarComoExpedite(i1.getId());
        servicio.marcarComoExpedite(i2.getId());

        gestor.cambiarEstado(i1.getId(), EstadoIncidencia.LISTA);
        servicio.cambiarEstadoExpedite(i1.getId(), EstadoIncidencia.EN_DESARROLLO);

        gestor.cambiarEstado(i2.getId(), EstadoIncidencia.LISTA);
        assertThrows(IllegalStateException.class, () ->
            servicio.cambiarEstadoExpedite(i2.getId(), EstadoIncidencia.EN_DESARROLLO)
        );
    }

    @Test
    void unaIncidenciaExpediteLiberaElCupoAlSalirDeDesarrolloOValidacion() {
        GestorIncidencias gestor = new GestorIncidencias();
        ServicioExpedite servicio = new ServicioExpedite(gestor);
        Incidencia i1 = gestor.registrar("Titulo 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        Incidencia i2 = gestor.registrar("Titulo 2", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        servicio.marcarComoExpedite(i1.getId());
        servicio.marcarComoExpedite(i2.getId());

        gestor.cambiarEstado(i1.getId(), EstadoIncidencia.LISTA);
        servicio.cambiarEstadoExpedite(i1.getId(), EstadoIncidencia.EN_DESARROLLO);
        servicio.cambiarEstadoExpedite(i1.getId(), EstadoIncidencia.EN_VALIDACION);
        gestor.registrarSolucion(i1.getId(), "Se reinicio el servicio afectado");
        servicio.cambiarEstadoExpedite(i1.getId(), EstadoIncidencia.FINALIZADA);

        gestor.cambiarEstado(i2.getId(), EstadoIncidencia.LISTA);
        servicio.cambiarEstadoExpedite(i2.getId(), EstadoIncidencia.EN_DESARROLLO);

        assertEquals(EstadoIncidencia.EN_DESARROLLO, gestor.getEstado(i2.getId()));
    }

    @Test
    void incidenciasNoExpediteNoSeVenAfectadasPorElLimite() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia i1 = gestor.registrar("Titulo 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        Incidencia i2 = gestor.registrar("Titulo 2", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);

        gestor.cambiarEstado(i1.getId(), EstadoIncidencia.LISTA);
        gestor.cambiarEstado(i1.getId(), EstadoIncidencia.EN_DESARROLLO);
        gestor.cambiarEstado(i2.getId(), EstadoIncidencia.LISTA);
        gestor.cambiarEstado(i2.getId(), EstadoIncidencia.EN_DESARROLLO);

        assertEquals(EstadoIncidencia.EN_DESARROLLO, gestor.getEstado(i1.getId()));
        assertEquals(EstadoIncidencia.EN_DESARROLLO, gestor.getEstado(i2.getId()));
    }
}
