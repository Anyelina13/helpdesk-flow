package com.utn.helpdesk.service;

import com.utn.helpdesk.model.*;
import java.util.*;

public class GestorIncidencias {
    private final Map<String, Incidencia> incidencias = new HashMap<>();
    private final ValidadorTransicionEstado validador = new ValidadorTransicionEstado();

    public Incidencia registrar(String titulo, String descripcion, String categoria,
                                 Impacto impacto, Urgencia urgencia) {
        Incidencia i = new Incidencia(titulo, descripcion, categoria, impacto, urgencia);
        incidencias.put(i.getId(), i);
        return i;
    }

    // Aqui se controla que un cambio de estado sea permitido antes de aplicarlo
    public void cambiarEstado(String id, EstadoIncidencia nuevoEstado) {
        Incidencia incidencia = incidencias.get(id);
        EstadoIncidencia actual = incidencia.getEstado();
        if (!validador.esValida(actual, nuevoEstado)) {
            throw new IllegalStateException("Transicion invalida: " + actual + " -> " + nuevoEstado);
        }
        // No se deja finalizar una incidencia si todavia no tiene solucion registrada
        if (nuevoEstado == EstadoIncidencia.FINALIZADA && incidencia.getSolucion() == null) {
            throw new IllegalStateException("No se puede finalizar sin descripcion de solucion");
        }
        incidencia.setEstado(nuevoEstado);
    }

    public void registrarSolucion(String id, String descripcionSolucion) {
        incidencias.get(id).registrarSolucion(descripcionSolucion);
    }

    public EstadoIncidencia getEstado(String id) { return incidencias.get(id).getEstado(); }

    public Incidencia buscarPorId(String id) { return incidencias.get(id); }

    public List<Incidencia> listarTodas() { return new ArrayList<>(incidencias.values()); }

    public List<Incidencia> filtrarPorEstado(EstadoIncidencia estado) {
        return incidencias.values().stream()
            .filter(i -> i.getEstado() == estado)
            .toList();
    }

    public List<Incidencia> filtrarPorPrioridad(Prioridad prioridad) {
        return incidencias.values().stream()
            .filter(i -> i.getPrioridad() == prioridad)
            .toList();
    }

    public List<Incidencia> listarAbiertas() {
        return incidencias.values().stream()
            .filter(i -> i.getEstado() != EstadoIncidencia.FINALIZADA)
            .toList();
    }

    public List<Incidencia> listarFinalizadas() {
        return filtrarPorEstado(EstadoIncidencia.FINALIZADA);
    }

    // Este metodo se usa para volver a cargar en memoria las incidencias que ya estaban guardadas
    public void agregar(Incidencia incidencia) {
        incidencias.put(incidencia.getId(), incidencia);
    }
}
