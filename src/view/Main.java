package src.view;

import src.controller.LoteController;
import src.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private final LoteController controlador;

    public Main(LoteController controlador) {
        this.controlador = controlador;
    }

    public static void main(String[] args) {
        LoteController controlador = new LoteController();
        Main vista = new Main(controlador);
        vista.mostrarMenu();
    }

    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println("\n--- Gestión de productos agrícolas ---");
            System.out.println("1. Registrar bodega");
            System.out.println("2. Consultar bodegas");
            System.out.println("3. Preliquidar y recibir lote");
            System.out.println("4. Consultar lotes en orden de salida");
            System.out.println("5. Descartar lotes vencidos");
            System.out.println("6. Salir");

            opcion = leerEntero("Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1:
                        registrarBodega();
                        break;
                    case 2:
                        mostrarBodegas();
                        break;
                    case 3:
                        preliquidarYRecibir();
                        break;
                    case 4:
                        mostrarLotes();
                        break;
                    case 5:
                        descartarVencidos();
                        break;
                    case 6:
                        System.out.println("Programa finalizado.");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("No se pudo completar: " + e.getMessage());
            }

        } while (opcion != 6);
    }

    private void registrarBodega() {
        double capacidad = leerDecimal("Capacidad máxima en kg: ");
        boolean fria = leerSiNo("¿Es cámara fría? (s/n): ");

        Bodega nueva = controlador.registrarBodega(capacidad, fria);

        System.out.println(
                "Bodega registrada. Código: " + nueva.getCodigo());
    }

    private void mostrarBodegas() {
        List<Bodega> bodegas = controlador.getBodegas();

        if (bodegas.isEmpty()) {
            System.out.println("No hay bodegas registradas.");
            return;
        }

        for (Bodega bodega : bodegas) {
            System.out.printf(
                    "Código: %d | Tipo: %s | Ocupado: %.2f kg"
                            + " | Disponible: %.2f kg%n",
                    bodega.getCodigo(),
                    bodega.esCamaraFria() ? "Cámara fría" : "Seca",
                    bodega.getPesoOcupado(),
                    bodega.getCapacidadDisponible());
        }
    }

    private void preliquidarYRecibir() {
        LoteModel propuesta = solicitarLote();

        System.out.println("\n--- Preliquidación ---");
        System.out.println(controlador.preliquidar(propuesta));

        if (!leerSiNo("¿El productor acepta? (s/n): ")) {
            System.out.println("El lote no fue registrado.");
            return;
        }

        if (controlador.getBodegas().isEmpty()) {
            System.out.println(
                    "Primero debes registrar una bodega."
                            + " El lote no fue recibido.");
            return;
        }

        // Permite corregir la bodega sin crear otra propuesta.
        while (true) {
            mostrarBodegas();

            int codigo = leerEntero(
                    "Código de bodega (0 para cancelar): ");

            if (codigo == 0) {
                System.out.println("Recepción cancelada.");
                return;
            }

            try {
                Bodega bodega = controlador.buscarBodega(codigo);
                controlador.recibirLote(propuesta, bodega);

                System.out.printf(
                        "Lote recibido. Código: %d | Pago registrado: Q%.2f%n",
                        propuesta.getCode(),
                        propuesta.calcularPago());
                return;

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private LoteModel solicitarLote() {
        System.out.println("\n--- Categoría del lote ---");
        System.out.println("1. Café en pergamino");
        System.out.println("2. Café orgánico");
        System.out.println("3. Cardamomo");
        System.out.println("4. Hortaliza");
        System.out.println("5. Queso fresco");

        int categoria = leerEntero("Categoría: ");

        if (categoria < 1 || categoria > 5) {
            throw new IllegalArgumentException("Categoría no válida.");
        }

        String nombre = leerTexto("Nombre del lote: ");
        LocalDate fecha = leerFecha("Fecha de ingreso (AAAA-MM-DD): ");
        int date = fecha.getYear() * 10000
                + fecha.getMonthValue() * 100
                + fecha.getDayOfMonth();

        int cantidad = leerEntero("Cantidad (amount): ");
        double peso = leerDecimal("Peso recibido en kg: ");
        LoteModel.Type calidad = leerCalidad();

        switch (categoria) {
            case 1:
                return new CafePergamino(
                        nombre, date, peso, cantidad, calidad,
                        leerDecimal("Humedad (%): "));

            case 2:
                double humedad = leerDecimal("Humedad (%): ");
                String entidad = leerTexto("Entidad certificadora: ");
                LocalDate validez = leerFecha(
                        "Sello válido hasta (AAAA-MM-DD): ");

                return new CafeOrganico(
                        nombre, date, peso, cantidad, calidad,
                        humedad, entidad, validez);

            case 3:
                return new Cardamomo(
                        nombre, date, peso, cantidad, calidad,
                        leerDecimal("Grano verde (%): "));

            case 4:
                return new Hortaliza(
                        nombre, date, peso, cantidad, calidad,
                        leerEntero("Vida útil en días: "));

            case 5:
                return new QuesoFresco(
                        nombre, date, peso, cantidad, calidad,
                        leerDecimal("Grasa (%): "));

            default:
                throw new IllegalArgumentException("Categoría no válida.");
        }
    }

    private void mostrarLotes() {
        List<LoteModel> lotes = controlador.getLotesOrdenados();

        if (lotes.isEmpty()) {
            System.out.println("No hay lotes registrados.");
            return;
        }

        for (LoteModel lote : lotes) {
            LocalDate vencimiento = lote.getFechaVencimiento();

            System.out.printf(
                    "Código: %d | Categoría: %s | Calidad: %s"
                            + " | Disponible: %.2f kg | Estado: %s"
                            + " | Vencimiento: %s%n",
                    lote.getCode(),
                    lote.getClass().getSimpleName(),
                    lote.getCalidad(),
                    lote.getPesoDisponible(),
                    lote.getEstado(),
                    vencimiento == null ? "No se vence" : vencimiento);
        }
    }

    private void descartarVencidos() {
        if (controlador.getBodegas().isEmpty()) {
            System.out.println("No hay bodegas registradas.");
            return;
        }

        LocalDate hoy = LocalDate.now();

        for (Bodega bodega : controlador.getBodegas()) {
            controlador.descartarVencidos(bodega, hoy);
        }

        System.out.println(
                "Revisión finalizada con fecha " + hoy
                        + ". Se retiraron los lotes vencidos encontrados.");
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();

            if (!texto.isEmpty()) {
                return texto;
            }

            System.out.println("El dato no puede quedar vacío.");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número entero válido.");
            }
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            try {
                double numero = Double.parseDouble(
                        leerTexto(mensaje).replace(',', '.'));

                if (Double.isFinite(numero)) {
                    return numero;
                }
            } catch (NumberFormatException e) {
                // Solicitar nuevamente el dato.
            }

            System.out.println("Ingresa un número decimal válido.");
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                LocalDate fecha = LocalDate.parse(leerTexto(mensaje));

                if (fecha.getYear() >= 1 && fecha.getYear() <= 9999) {
                    return fecha;
                }
            } catch (DateTimeParseException e) {
                // Solicitar nuevamente el dato.
            }

            System.out.println(
                    "Ingresa una fecha válida, por ejemplo: 2026-11-21.");
        }
    }

    private LoteModel.Type leerCalidad() {
        while (true) {
            String calidad = leerTexto("Calidad (A/B/C): ");

            if (calidad.equalsIgnoreCase("A")) {
                return LoteModel.Type.A;
            }
            if (calidad.equalsIgnoreCase("B")) {
                return LoteModel.Type.B;
            }
            if (calidad.equalsIgnoreCase("C")) {
                return LoteModel.Type.C;
            }

            System.out.println("La calidad debe ser A, B o C.");
        }
    }

    private boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje);

            if (respuesta.equalsIgnoreCase("s")) {
                return true;
            }
            if (respuesta.equalsIgnoreCase("n")) {
                return false;
            }

            System.out.println("Responde s o n.");
        }
    }
}