package com.utn.helpdesk;

import com.utn.helpdesk.model.*;
import com.utn.helpdesk.service.GestorIncidencias;
import com.utn.helpdesk.service.MetricasIncidencias;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MetricasIncidenciasTest {

    private final MetricasIncidencias metricas = new MetricasIncidencias();

    private void finalizarConSolucion(GestorIncidencias gestor, Incidencia incidencia) {
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.LISTA);
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.EN_DESARROLLO);
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.EN_VALIDACION);
        gestor.registrarSolucion(incidencia.getId(), "Se reinicio el servicio afectado");
        gestor.cambiarEstado(incidencia.getId(), EstadoIncidencia.FINALIZADA);
    }

    @Test
    void cuentaTotalesAbiertasYFinalizadasCorrectamente() {
        GestorIncidencias gestor = new GestorIncidencias();
        gestor.registrar("Titulo 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        gestor.registrar("Titulo 2", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);
        Incidencia finalizada = gestor.registrar("Titulo 3", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        finalizarConSolucion(gestor, finalizada);

        List<Incidencia> todas = gestor.listarTodas();

        assertEquals(3, metricas.totalIncidencias(todas));
        assertEquals(1, metricas.cantidadFinalizadas(todas));
        assertEquals(2, metricas.cantidadAbiertas(todas));
    }

    @Test
    void throughputCuentaSoloLasFinalizadasDentroDelPeriodo() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia finalizadaHoy = gestor.registrar("Titulo 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        finalizarConSolucion(gestor, finalizadaHoy);
        gestor.registrar("Titulo 2", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);

        List<Incidencia> todas = gestor.listarTodas();
        LocalDate hoy = LocalDate.now();

        assertEquals(1, metricas.throughput(todas, hoy, hoy));
        assertEquals(0, metricas.throughput(todas, hoy.minusDays(5), hoy.minusDays(1)));
    }

    @Test
    void leadTimePromedioSeCalculaConLaDiferenciaEntreCreacionYCierre() {
        GestorIncidencias gestor = new GestorIncidencias();
        Incidencia antigua = new Incidencia("Titulo 1", "Descripcion valida larga", null,
            Impacto.ALTO, Urgencia.ALTA, LocalDate.now().minusDays(5));
        Incidencia reciente = new Incidencia("Titulo 2", "Descripcion valida larga", null,
            Impacto.ALTO, Urgencia.ALTA, LocalDate.now());

        antigua.setEstado(EstadoIncidencia.LISTA);
        antigua.setEstado(EstadoIncidencia.EN_DESARROLLO);
        antigua.setEstado(EstadoIncidencia.EN_VALIDACION);
        antigua.setEstado(EstadoIncidencia.FINALIZADA);

        reciente.setEstado(EstadoIncidencia.LISTA);
        reciente.setEstado(EstadoIncidencia.EN_DESARROLLO);
        reciente.setEstado(EstadoIncidencia.EN_VALIDACION);
        reciente.setEstado(EstadoIncidencia.FINALIZADA);

        double promedio = metricas.leadTimePromedioEnDias(List.of(antigua, reciente));

        assertEquals(2.5, promedio);
    }

    @Test
    void leadTimePromedioEsCeroSinIncidenciasFinalizadas() {
        Incidencia abierta = new Incidencia("Titulo", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);

        assertEquals(0.0, metricas.leadTimePromedioEnDias(List.of(abierta)));
    }

    @Test
    void cantidadPorPrioridadAgrupaCorrectamente() {
        GestorIncidencias gestor = new GestorIncidencias();
        gestor.registrar("Critica 1", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        gestor.registrar("Critica 2", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        gestor.registrar("Normal", "Descripcion valida larga", null, Impacto.BAJO, Urgencia.BAJA);

        var conteo = metricas.cantidadPorPrioridad(gestor.listarTodas());

        assertEquals(2L, conteo.get(Prioridad.CRITICA));
        assertEquals(1L, conteo.get(Prioridad.NORMAL));
        assertNull(conteo.get(Prioridad.ALTA));
    }
}
