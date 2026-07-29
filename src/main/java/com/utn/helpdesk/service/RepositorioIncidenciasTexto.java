package com.utn.helpdesk.service;

import com.utn.helpdesk.model.EstadoIncidencia;
import com.utn.helpdesk.model.Impacto;
import com.utn.helpdesk.model.Incidencia;
import com.utn.helpdesk.model.Urgencia;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

// Esta clase guarda y lee las incidencias en un archivo de texto, para que los datos no se pierdan al cerrar el programa
public class RepositorioIncidenciasTexto {
    // Cada dato de una incidencia se separa con este simbolo dentro del archivo
    private static final String SEPARADOR = "\\|";

    private final Path archivo;

    public RepositorioIncidenciasTexto(Path archivo) {
        this.archivo = archivo;
    }

    // Escribe todas las incidencias en el archivo, una por linea, reemplazando lo que hubiera antes
    public void guardar(List<Incidencia> incidencias) {
        List<String> lineas = incidencias.stream().map(this::aLinea).toList();
        try {
            Files.write(archivo, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Lee el archivo y devuelve la lista de incidencias, o una lista vacia si el archivo no existe todavia
    public List<Incidencia> cargar() {
        if (!Files.exists(archivo)) {
            return List.of();
        }
        try {
            return Files.readAllLines(archivo, StandardCharsets.UTF_8).stream()
                .filter(linea -> !linea.isBlank())
                .map(this::aIncidencia)
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String aLinea(Incidencia i) {
        return String.join("|",
            i.getId(),
            escapar(i.getTitulo()),
            escapar(i.getDescripcion()),
            escapar(i.getCategoria()),
            i.getImpacto().name(),
            i.getUrgencia().name(),
            i.getFechaCreacion().toString(),
            i.getEstado().name(),
            escapar(i.getSolucion()),
            i.getFechaCierre() == null ? "" : i.getFechaCierre().toString(),
            String.valueOf(i.isExpedite()));
    }

    private Incidencia aIncidencia(String linea) {
        String[] campos = linea.split(SEPARADOR, -1);
        return Incidencia.reconstruir(
            campos[0],
            desescapar(campos[1]),
            desescapar(campos[2]),
            desescapar(campos[3]),
            Impacto.valueOf(campos[4]),
            Urgencia.valueOf(campos[5]),
            LocalDate.parse(campos[6]),
            EstadoIncidencia.valueOf(campos[7]),
            desescapar(campos[8]),
            campos[9].isEmpty() ? null : LocalDate.parse(campos[9]),
            Boolean.parseBoolean(campos[10]));
    }

    // Cambia el simbolo separador y la barra invertida por otro texto, para que no arruinen el formato del archivo
    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("|", "\\p");
    }

    private String desescapar(String valor) {
        if (valor.isEmpty()) {
            return null;
        }
        return valor.replace("\\p", "|").replace("\\\\", "\\");
    }
}
