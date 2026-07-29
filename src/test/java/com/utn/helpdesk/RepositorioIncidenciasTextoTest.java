package com.utn.helpdesk;

import com.utn.helpdesk.model.*;
import com.utn.helpdesk.service.RepositorioIncidenciasTexto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RepositorioIncidenciasTextoTest {

    private Path archivoTemporal;

    @AfterEach
    void limpiar() throws IOException {
        if (archivoTemporal != null) {
            Files.deleteIfExists(archivoTemporal);
        }
    }

    @Test
    void cargarDevuelveListaVaciaSiElArchivoNoExiste() throws IOException {
        archivoTemporal = Files.createTempFile("incidencias", ".txt");
        Files.delete(archivoTemporal);
        RepositorioIncidenciasTexto repositorio = new RepositorioIncidenciasTexto(archivoTemporal);

        assertTrue(repositorio.cargar().isEmpty());
    }

    @Test
    void guardarYCargarPreservaLosDatosDeUnaIncidenciaAbierta() throws IOException {
        archivoTemporal = Files.createTempFile("incidencias", ".txt");
        RepositorioIncidenciasTexto repositorio = new RepositorioIncidenciasTexto(archivoTemporal);
        Incidencia original = new Incidencia("Servidor caido", "El servidor no responde hace rato",
            "Infraestructura", Impacto.ALTO, Urgencia.ALTA);

        repositorio.guardar(List.of(original));
        List<Incidencia> recargadas = repositorio.cargar();

        assertEquals(1, recargadas.size());
        Incidencia recargada = recargadas.get(0);
        assertEquals(original.getId(), recargada.getId());
        assertEquals(original.getTitulo(), recargada.getTitulo());
        assertEquals(original.getDescripcion(), recargada.getDescripcion());
        assertEquals(original.getCategoria(), recargada.getCategoria());
        assertEquals(original.getImpacto(), recargada.getImpacto());
        assertEquals(original.getUrgencia(), recargada.getUrgencia());
        assertEquals(original.getPrioridad(), recargada.getPrioridad());
        assertEquals(original.getFechaCreacion(), recargada.getFechaCreacion());
        assertEquals(original.getEstado(), recargada.getEstado());
        assertNull(recargada.getSolucion());
        assertNull(recargada.getFechaCierre());
        assertFalse(recargada.isExpedite());
    }

    @Test
    void guardarYCargarPreservaUnaIncidenciaFinalizadaConCaracteresEspeciales() throws IOException {
        archivoTemporal = Files.createTempFile("incidencias", ".txt");
        RepositorioIncidenciasTexto repositorio = new RepositorioIncidenciasTexto(archivoTemporal);
        Incidencia original = Incidencia.reconstruir("id-1", "Falla en | el pipe \\ del sistema",
            "Descripcion con | pipes y \\ barras invertidas", "Red | Software",
            Impacto.MEDIO, Urgencia.MEDIA, LocalDate.now().minusDays(2), EstadoIncidencia.FINALIZADA,
            "Se aplico un parche | de emergencia", LocalDate.now(), true);

        repositorio.guardar(List.of(original));
        Incidencia recargada = repositorio.cargar().get(0);

        assertEquals(original.getTitulo(), recargada.getTitulo());
        assertEquals(original.getDescripcion(), recargada.getDescripcion());
        assertEquals(original.getCategoria(), recargada.getCategoria());
        assertEquals(original.getSolucion(), recargada.getSolucion());
        assertEquals(original.getFechaCierre(), recargada.getFechaCierre());
        assertTrue(recargada.isExpedite());
    }
}
