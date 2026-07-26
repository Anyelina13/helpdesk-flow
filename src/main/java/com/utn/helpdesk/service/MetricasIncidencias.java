package com.utn.helpdesk.service;

import com.utn.helpdesk.model.EstadoIncidencia;
import com.utn.helpdesk.model.Incidencia;
import com.utn.helpdesk.model.Prioridad;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricasIncidencias {

    public long totalIncidencias(List<Incidencia> incidencias) {
        return incidencias.size();
    }

    public long cantidadFinalizadas(List<Incidencia> incidencias) {
        return incidencias.stream()
            .filter(i -> i.getEstado() == EstadoIncidencia.FINALIZADA)
            .count();
    }

    public long cantidadAbiertas(List<Incidencia> incidencias) {
        return incidencias.stream()
            .filter(i -> i.getEstado() != EstadoIncidencia.FINALIZADA)
            .count();
    }

    public long throughput(List<Incidencia> incidencias, LocalDate desde, LocalDate hasta) {
        return incidencias.stream()
            .filter(i -> i.getEstado() == EstadoIncidencia.FINALIZADA)
            .filter(i -> !i.getFechaCierre().isBefore(desde) && !i.getFechaCierre().isAfter(hasta))
            .count();
    }

    public double leadTimePromedioEnDias(List<Incidencia> incidencias) {
        List<Incidencia> finalizadas = incidencias.stream()
            .filter(i -> i.getEstado() == EstadoIncidencia.FINALIZADA)
            .toList();
        if (finalizadas.isEmpty()) {
            return 0.0;
        }
        long totalDias = finalizadas.stream()
            .mapToLong(i -> ChronoUnit.DAYS.between(i.getFechaCreacion(), i.getFechaCierre()))
            .sum();
        return (double) totalDias / finalizadas.size();
    }

    public Map<Prioridad, Long> cantidadPorPrioridad(List<Incidencia> incidencias) {
        return incidencias.stream()
            .collect(Collectors.groupingBy(Incidencia::getPrioridad, Collectors.counting()));
    }
}
