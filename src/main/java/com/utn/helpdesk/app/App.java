package com.utn.helpdesk.app;

import com.utn.helpdesk.model.*;
import com.utn.helpdesk.service.GestorIncidencias;
import com.utn.helpdesk.service.MetricasIncidencias;
import com.utn.helpdesk.service.RepositorioIncidenciasTexto;
import com.utn.helpdesk.service.ServicioExpedite;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final Path ARCHIVO_DATOS = Path.of("incidencias.txt");

    private final Scanner scanner = new Scanner(System.in);
    private final GestorIncidencias gestor = new GestorIncidencias();
    private final ServicioExpedite servicioExpedite = new ServicioExpedite(gestor);
    private final MetricasIncidencias metricas = new MetricasIncidencias();
    private final RepositorioIncidenciasTexto repositorio = new RepositorioIncidenciasTexto(ARCHIVO_DATOS);

    public static void main(String[] args) {
        new App().ejecutar();
    }

    // Punto de partida del programa, muestra el menu una y otra vez hasta que se elija salir
    private void ejecutar() {
        // Al empezar se cargan las incidencias que quedaron guardadas de la vez anterior
        repositorio.cargar().forEach(gestor::agregar);

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            switch (leerOpcion()) {
                case 1 -> registrarIncidencia();
                case 2 -> cambiarEstado();
                case 3 -> registrarSolucion();
                case 4 -> marcarExpedite();
                case 5 -> listarTodas();
                case 6 -> buscarPorId();
                case 7 -> filtrarPorEstado();
                case 8 -> filtrarPorPrioridad();
                case 9 -> listar(gestor.listarAbiertas(), "Incidencias abiertas");
                case 10 -> listar(gestor.listarFinalizadas(), "Incidencias finalizadas");
                case 11 -> mostrarMetricas();
                case 0 -> continuar = false;
                default -> System.out.println("Opcion invalida");
            }
        }
        System.out.println("Hasta luego.");
    }

    private void mostrarMenu() {
        System.out.println("""

            === HelpDesk Flow ===
            1.  Registrar incidencia
            2.  Cambiar estado de una incidencia
            3.  Registrar solucion
            4.  Marcar incidencia como EXPEDITE
            5.  Listar todas las incidencias
            6.  Buscar incidencia por ID
            7.  Filtrar por estado
            8.  Filtrar por prioridad
            9.  Mostrar incidencias abiertas
            10. Mostrar incidencias finalizadas
            11. Ver metricas
            0.  Salir
            """);
        System.out.print("Elegi una opcion: ");
    }

    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void registrarIncidencia() {
        try {
            System.out.print("Titulo: ");
            String titulo = scanner.nextLine();
            System.out.print("Descripcion (min. 10 caracteres): ");
            String descripcion = scanner.nextLine();
            System.out.print("Categoria: ");
            String categoria = scanner.nextLine();
            Impacto impacto = leerEnum("Impacto", Impacto.values());
            Urgencia urgencia = leerEnum("Urgencia", Urgencia.values());

            Incidencia incidencia = gestor.registrar(titulo, descripcion, categoria, impacto, urgencia);
            guardar();
            System.out.println("Incidencia registrada con ID: " + incidencia.getId());
            System.out.println("Prioridad calculada: " + incidencia.getPrioridad());
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar la incidencia: " + e.getMessage());
        }
    }

    private void cambiarEstado() {
        Incidencia incidencia = pedirIncidenciaPorId();
        if (incidencia == null) return;
        EstadoIncidencia nuevoEstado = leerEnum("Nuevo estado", EstadoIncidencia.values());
        try {
            if (incidencia.isExpedite()) {
                servicioExpedite.cambiarEstadoExpedite(incidencia.getId(), nuevoEstado);
            } else {
                gestor.cambiarEstado(incidencia.getId(), nuevoEstado);
            }
            guardar();
            System.out.println("Estado actualizado a " + nuevoEstado);
        } catch (IllegalStateException e) {
            System.out.println("No se pudo cambiar el estado: " + e.getMessage());
        }
    }

    private void registrarSolucion() {
        Incidencia incidencia = pedirIncidenciaPorId();
        if (incidencia == null) return;
        System.out.print("Descripcion de la solucion aplicada: ");
        gestor.registrarSolucion(incidencia.getId(), scanner.nextLine());
        guardar();
        System.out.println("Solucion registrada.");
    }

    private void marcarExpedite() {
        Incidencia incidencia = pedirIncidenciaPorId();
        if (incidencia == null) return;
        try {
            servicioExpedite.marcarComoExpedite(incidencia.getId());
            guardar();
            System.out.println("Incidencia marcada como EXPEDITE.");
        } catch (IllegalStateException e) {
            System.out.println("No se pudo marcar como EXPEDITE: " + e.getMessage());
        }
    }

    // Guarda en el archivo el estado actual de todas las incidencias, se llama despues de cada cambio
    private void guardar() {
        repositorio.guardar(gestor.listarTodas());
    }

    private void listarTodas() {
        listar(gestor.listarTodas(), "Todas las incidencias");
    }

    private void buscarPorId() {
        Incidencia incidencia = pedirIncidenciaPorId();
        if (incidencia != null) {
            imprimirIncidencia(incidencia);
        }
    }

    private void filtrarPorEstado() {
        EstadoIncidencia estado = leerEnum("Estado", EstadoIncidencia.values());
        listar(gestor.filtrarPorEstado(estado), "Incidencias en estado " + estado);
    }

    private void filtrarPorPrioridad() {
        Prioridad prioridad = leerEnum("Prioridad", Prioridad.values());
        listar(gestor.filtrarPorPrioridad(prioridad), "Incidencias con prioridad " + prioridad);
    }

    private void mostrarMetricas() {
        List<Incidencia> todas = gestor.listarTodas();
        System.out.println("Total de incidencias: " + metricas.totalIncidencias(todas));
        System.out.println("Finalizadas: " + metricas.cantidadFinalizadas(todas));
        System.out.println("Abiertas: " + metricas.cantidadAbiertas(todas));
        System.out.println("Lead time promedio (dias): " + metricas.leadTimePromedioEnDias(todas));
        System.out.println("Cantidad por prioridad: " + metricas.cantidadPorPrioridad(todas));
    }

    private Incidencia pedirIncidenciaPorId() {
        System.out.print("ID de la incidencia: ");
        String id = scanner.nextLine();
        Incidencia incidencia = gestor.buscarPorId(id);
        if (incidencia == null) {
            System.out.println("No existe una incidencia con ese ID.");
        }
        return incidencia;
    }

    private void listar(List<Incidencia> incidencias, String titulo) {
        System.out.println("--- " + titulo + " (" + incidencias.size() + ") ---");
        incidencias.forEach(this::imprimirIncidencia);
    }

    private void imprimirIncidencia(Incidencia i) {
        System.out.printf("[%s] %s | estado=%s | prioridad=%s | expedite=%s%n",
            i.getId(), i.getTitulo(), i.getEstado(), i.getPrioridad(), i.isExpedite());
    }

    // Pide un valor de la lista dada y no deja seguir hasta que el usuario escriba uno valido
    private <T extends Enum<T>> T leerEnum(String etiqueta, T[] valores) {
        while (true) {
            System.out.print(etiqueta + " " + List.of(valores) + ": ");
            String entrada = scanner.nextLine().trim().toUpperCase();
            for (T valor : valores) {
                if (valor.name().equals(entrada)) {
                    return valor;
                }
            }
            System.out.println("Valor invalido, intenta de nuevo.");
        }
    }
}
