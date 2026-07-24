package com.utn.helpdesk.service;

import com.utn.helpdesk.model.*;
import java.util.*;

public class GestorIncidencias {
    private final Map<String, Incidencia> incidencias = new HashMap<>();
    private final Map<String, EstadoIncidencia> estados = new HashMap<>();
    private final Map<String, String> soluciones = new HashMap<>();
    private final ValidadorTransicionEstado validador = new ValidadorTransicionEstado();

    public Incidencia registrar(String titulo, String descripcion, String categoria,
                                 Impacto impacto, Urgencia urgencia) {
        Incidencia i = new Incidencia(titulo, descripcion, categoria, impacto, urgencia);
        incidencias.put(i.getId(), i);
        estados.put(i.getId(), EstadoIncidencia.REGISTRADA);
        return i;
    }

    public void cambiarEstado(String id, EstadoIncidencia nuevoEstado) {
        EstadoIncidencia actual = estados.get(id);
        if (!validador.esValida(actual, nuevoEstado)) {
            throw new IllegalStateException("Transicion invalida: " + actual + " -> " + nuevoEstado);
        }
        if (nuevoEstado == EstadoIncidencia.FINALIZADA && soluciones.get(id) == null) {
            throw new IllegalStateException("No se puede finalizar sin descripcion de solucion");
        }
        estados.put(id, nuevoEstado);
    }

    public void registrarSolucion(String id, String descripcionSolucion) {
        soluciones.put(id, descripcionSolucion);
    }

    public EstadoIncidencia getEstado(String id) { return estados.get(id); }
}
