package com.utn.helpdesk;

import com.utn.helpdesk.model.EstadoIncidencia;
import com.utn.helpdesk.model.Incidencia;
import com.utn.helpdesk.model.Impacto;
import com.utn.helpdesk.model.Prioridad;
import com.utn.helpdesk.model.Urgencia;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class IncidenciaTest {

    @Test
    void tituloNoPuedeEstarVacio() {
        assertThrows(IllegalArgumentException.class, () ->
            new Incidencia("", "Descripcion valida con mas de 10 caracteres", null, null, null)
        );
    }

    @Test
    void descripcionDebeTenerAlMenosDiezCaracteres() {
        assertThrows(IllegalArgumentException.class, () ->
            new Incidencia("Titulo valido", "corta", null, null, null)
        );
    }

    @Test
    void impactoYUrgenciaQuedanCorrectamenteAsignados() {
        Incidencia i = new Incidencia("Titulo valido", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        assertEquals(Impacto.ALTO, i.getImpacto());
        assertEquals(Urgencia.ALTA, i.getUrgencia());
    }

    @Test
    void categoriaQuedaCorrectamenteAsignada() {
        Incidencia i = new Incidencia("Titulo valido", "Descripcion valida larga", "Red", Impacto.ALTO, Urgencia.ALTA);
        assertEquals("Red", i.getCategoria());
    }

    @Test
    void impactoNoPuedeSerNulo() {
        assertThrows(IllegalArgumentException.class, () ->
            new Incidencia("Titulo valido", "Descripcion valida larga", null, null, Urgencia.ALTA)
        );
    }

    @Test
    void urgenciaNoPuedeSerNula() {
        assertThrows(IllegalArgumentException.class, () ->
            new Incidencia("Titulo valido", "Descripcion valida larga", null, Impacto.ALTO, null)
        );
    }

    @Test
    void prioridadSeCalculaAutomaticamenteAlCrearLaIncidencia() {
        Incidencia i = new Incidencia("Titulo valido", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        assertEquals(Prioridad.CRITICA, i.getPrioridad());
    }

    @Test
    void fechaCreacionSeAsignaAutomaticamente() {
        Incidencia i = new Incidencia("Titulo valido", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        assertEquals(LocalDate.now(), i.getFechaCreacion());
    }

    @Test
    void incidenciaComienzaEnEstadoRegistrada() {
        Incidencia i = new Incidencia("Titulo valido", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        assertEquals(EstadoIncidencia.REGISTRADA, i.getEstado());
    }

    @Test
    void reconstruirRecreaUnaIncidenciaConTodosSusDatosGuardados() {
        LocalDate creacion = LocalDate.now().minusDays(3);
        LocalDate cierre = LocalDate.now();

        Incidencia i = Incidencia.reconstruir("id-existente", "Titulo", "Descripcion valida larga",
            "Red", Impacto.ALTO, Urgencia.ALTA, creacion, EstadoIncidencia.FINALIZADA,
            "Se reinicio el servicio", cierre, true);

        assertEquals("id-existente", i.getId());
        assertEquals("Titulo", i.getTitulo());
        assertEquals("Red", i.getCategoria());
        assertEquals(Prioridad.CRITICA, i.getPrioridad());
        assertEquals(creacion, i.getFechaCreacion());
        assertEquals(EstadoIncidencia.FINALIZADA, i.getEstado());
        assertEquals("Se reinicio el servicio", i.getSolucion());
        assertEquals(cierre, i.getFechaCierre());
        assertTrue(i.isExpedite());
    }

    @Test
    void debeGenerarIdentificadorUnico() {
        Incidencia i1 = new Incidencia("T1", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);
        Incidencia i2 = new Incidencia("T2", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);
        assertNotEquals(i1.getId(), i2.getId());
    }
}
