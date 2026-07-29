package com.utn.helpdesk.service;

import com.utn.helpdesk.model.EstadoIncidencia;
import com.utn.helpdesk.model.Incidencia;
import com.utn.helpdesk.model.Prioridad;

// Esta clase agrega la regla de EXPEDITE por encima de GestorIncidencias, sin modificarlo
public class ServicioExpedite {
    private final GestorIncidencias gestor;

    public ServicioExpedite(GestorIncidencias gestor) {
        this.gestor = gestor;
    }

    // Solo se puede marcar como EXPEDITE una incidencia con la prioridad mas alta
    public void marcarComoExpedite(String id) {
        Incidencia incidencia = gestor.buscarPorId(id);
        if (incidencia.getPrioridad() != Prioridad.CRITICA) {
            throw new IllegalStateException("Solo una incidencia critica puede marcarse como EXPEDITE");
        }
        incidencia.marcarExpedite();
    }

    // Antes de cambiar el estado revisa que no haya otra incidencia EXPEDITE activa al mismo tiempo
    public void cambiarEstadoExpedite(String id, EstadoIncidencia nuevoEstado) {
        Incidencia incidencia = gestor.buscarPorId(id);
        boolean entraAFlujoActivo = nuevoEstado == EstadoIncidencia.EN_DESARROLLO
            || nuevoEstado == EstadoIncidencia.EN_VALIDACION;
        if (incidencia.isExpedite() && entraAFlujoActivo && hayOtraExpediteActiva(id)) {
            throw new IllegalStateException("Ya existe una incidencia EXPEDITE activa en desarrollo o validacion");
        }
        gestor.cambiarEstado(id, nuevoEstado);
    }

    private boolean hayOtraExpediteActiva(String idActual) {
        return gestor.listarTodas().stream()
            .filter(i -> !i.getId().equals(idActual))
            .filter(Incidencia::isExpedite)
            .anyMatch(i -> i.getEstado() == EstadoIncidencia.EN_DESARROLLO
                || i.getEstado() == EstadoIncidencia.EN_VALIDACION);
    }
}
