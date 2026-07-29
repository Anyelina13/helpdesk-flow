package com.utn.helpdesk.model;

import com.utn.helpdesk.service.CalculadorPrioridad;
import java.time.LocalDate;
import java.util.UUID;

public class Incidencia {
    private final String id;
    private final String titulo;
    private final String descripcion;
    private final String categoria;
    private final Impacto impacto;
    private final Urgencia urgencia;
    private final Prioridad prioridad;
    private final LocalDate fechaCreacion;
    private EstadoIncidencia estado;
    private String solucion;
    private LocalDate fechaCierre;
    private boolean expedite = false;

    public Incidencia(String titulo, String descripcion, String categoria,
                       Impacto impacto, Urgencia urgencia) {
        this(titulo, descripcion, categoria, impacto, urgencia, LocalDate.now());
    }

    // Aqui se crea una incidencia nueva y se revisa que los datos basicos esten completos
    public Incidencia(String titulo, String descripcion, String categoria,
                       Impacto impacto, Urgencia urgencia, LocalDate fechaCreacion) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El titulo no puede estar vacio");
        }
        if (descripcion == null || descripcion.length() < 10) {
            throw new IllegalArgumentException("La descripcion debe tener al menos 10 caracteres");
        }
        if (impacto == null) {
            throw new IllegalArgumentException("El impacto es obligatorio");
        }
        if (urgencia == null) {
            throw new IllegalArgumentException("La urgencia es obligatoria");
        }
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.impacto = impacto;
        this.urgencia = urgencia;
        // La prioridad se calcula una sola vez, en el momento de crear la incidencia, y ya no cambia
        this.prioridad = new CalculadorPrioridad().calcular(impacto, urgencia);
        this.fechaCreacion = fechaCreacion;
        this.estado = EstadoIncidencia.REGISTRADA;
    }

    private Incidencia(String id, String titulo, String descripcion, String categoria,
                        Impacto impacto, Urgencia urgencia, LocalDate fechaCreacion,
                        EstadoIncidencia estado, String solucion, LocalDate fechaCierre,
                        boolean expedite) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.impacto = impacto;
        this.urgencia = urgencia;
        this.prioridad = new CalculadorPrioridad().calcular(impacto, urgencia);
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.solucion = solucion;
        this.fechaCierre = fechaCierre;
        this.expedite = expedite;
    }

    // Este metodo recrea una incidencia que ya existia, usando los datos guardados en el archivo
    // No revisa las validaciones de una incidencia nueva porque esos datos ya fueron validados antes
    public static Incidencia reconstruir(String id, String titulo, String descripcion, String categoria,
                                          Impacto impacto, Urgencia urgencia, LocalDate fechaCreacion,
                                          EstadoIncidencia estado, String solucion, LocalDate fechaCierre,
                                          boolean expedite) {
        return new Incidencia(id, titulo, descripcion, categoria, impacto, urgencia, fechaCreacion,
            estado, solucion, fechaCierre, expedite);
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public Impacto getImpacto() { return impacto; }
    public Urgencia getUrgencia() { return urgencia; }
    public Prioridad getPrioridad() { return prioridad; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public EstadoIncidencia getEstado() { return estado; }
    public String getSolucion() { return solucion; }
    public LocalDate getFechaCierre() { return fechaCierre; }

    public void setEstado(EstadoIncidencia nuevoEstado) {
        this.estado = nuevoEstado;
        if (nuevoEstado == EstadoIncidencia.FINALIZADA) {
            this.fechaCierre = LocalDate.now();
        }
    }

    public void registrarSolucion(String descripcionSolucion) {
        this.solucion = descripcionSolucion;
    }

    public boolean isExpedite() { return expedite; }

    public void marcarExpedite() {
        this.expedite = true;
    }
}
