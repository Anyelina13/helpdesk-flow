package com.utn.helpdesk.model;

import java.util.UUID;

public class Incidencia {
    private final String id;
    private final String titulo;
    private final String descripcion;
    private final Impacto impacto;
    private final Urgencia urgencia;

    public Incidencia(String titulo, String descripcion, String categoria,
                       Impacto impacto, Urgencia urgencia) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El titulo no puede estar vacio");
        }
        if (descripcion == null || descripcion.length() < 10) {
            throw new IllegalArgumentException("La descripcion debe tener al menos 10 caracteres");
        }
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.impacto = impacto;
        this.urgencia = urgencia;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
}
