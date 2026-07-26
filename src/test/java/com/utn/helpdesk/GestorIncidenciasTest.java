package com.utn.helpdesk;

import com.utn.helpdesk.model.*;
import com.utn.helpdesk.service.GestorIncidencias;
import org.junit.jupiter.api.Test;
import java.util.List;
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

    @Test
    void buscarPorIdDevuelveLaIncidenciaRegistrada() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia incidencia = gestor.registrar("Titulo", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);

        assertEquals(incidencia, gestor.buscarPorId(incidencia.getId()));
        assertNull(gestor.buscarPorId("id-inexistente"));
    }

    @Test
    void listarTodasDevuelveTodasLasIncidenciasRegistradas() {
        GestorIncidencias gestor = new GestorIncidencias();
        gestor.registrar("Titulo 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        gestor.registrar("Titulo 2", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);

        assertEquals(2, gestor.listarTodas().size());
    }

    @Test
    void filtrarAbiertasPorPrioridadDevuelveSoloLasQueCumplenAmbosCriterios() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia critica = gestor.registrar("Critica abierta", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        Incidencia normalAbierta = gestor.registrar("Normal abierta", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);
        Incidencia criticaFinalizada = gestor.registrar("Critica finalizada", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        gestor.cambiarEstado(criticaFinalizada.getId(), EstadoIncidencia.LISTA);
        gestor.cambiarEstado(criticaFinalizada.getId(), EstadoIncidencia.EN_DESARROLLO);
        gestor.cambiarEstado(criticaFinalizada.getId(), EstadoIncidencia.EN_VALIDACION);
        gestor.registrarSolucion(criticaFinalizada.getId(), "Se reinicio el servicio afectado");
        gestor.cambiarEstado(criticaFinalizada.getId(), EstadoIncidencia.FINALIZADA);

        List<Incidencia> abiertas = gestor.listarAbiertas();
        List<Incidencia> criticasAbiertas = gestor.filtrarPorPrioridad(Prioridad.CRITICA).stream()
            .filter(abiertas::contains)
            .toList();

        assertTrue(criticasAbiertas.contains(critica));
        assertFalse(criticasAbiertas.contains(normalAbierta));
        assertFalse(criticasAbiertas.contains(criticaFinalizada));
    }

    @Test
    void filtrarPorEstadoDevuelveSoloLasQueCoinciden() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia registrada = gestor.registrar("Titulo 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        Incidencia enLista = gestor.registrar("Titulo 2", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);
        gestor.cambiarEstado(enLista.getId(), EstadoIncidencia.LISTA);

        List<Incidencia> resultado = gestor.filtrarPorEstado(EstadoIncidencia.LISTA);

        assertEquals(List.of(enLista), resultado);
    }

    @Test
    void listarFinalizadasDevuelveSoloLasFinalizadas() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia abierta = gestor.registrar("Titulo 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        Incidencia finalizada = gestor.registrar("Titulo 2", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        gestor.cambiarEstado(finalizada.getId(), EstadoIncidencia.LISTA);
        gestor.cambiarEstado(finalizada.getId(), EstadoIncidencia.EN_DESARROLLO);
        gestor.cambiarEstado(finalizada.getId(), EstadoIncidencia.EN_VALIDACION);
        gestor.registrarSolucion(finalizada.getId(), "Se reinicio el servicio afectado");
        gestor.cambiarEstado(finalizada.getId(), EstadoIncidencia.FINALIZADA);

        assertEquals(List.of(finalizada), gestor.listarFinalizadas());
        assertTrue(gestor.listarAbiertas().contains(abierta));
    }
}
