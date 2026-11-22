package src.model;

import java.time.LocalDate;

public class Hortaliza extends LoteModel {
    private final int vidaUtil;
    private final LocalDate fechaVencimiento;

    public Hortaliza(String name, int date, double weight,
            int amount, Type calidad, int vidaUtil) {
                
        super(name, date, weight, amount, calidad);

        if (vidaUtil < 3 || vidaUtil > 14) {
            throw new IllegalArgumentException(
                    "La vida útil debe estar en un rango entre 3 y 14 días.");
        }

        this.vidaUtil = vidaUtil;

        LocalDate fechaIngreso = LocalDate.of(
                date / 10000,
                (date / 100) % 100,
                date % 100);

        this.fechaVencimiento = fechaIngreso.plusDays(vidaUtil);
    }

    public int getVidaUtil() {
        return vidaUtil;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public double calcularPago() {
        double precioPorKg;

        switch (getCalidad()) {
            case A:
                precioPorKg = 4;
                break;
            case B:
                precioPorKg = 3;
                break;
            case C:
                precioPorKg = 2;
                break;
            default:
                throw new IllegalStateException(
                        "Calidad no válida.");
        }

        double pesoPagado = getWeight() * 0.95;

        return pesoPagado * precioPorKg;
    }

    public boolean estaVencido(LocalDate fechaConsulta) {
        if (fechaConsulta == null) {
            throw new IllegalArgumentException(
                    "Debes indicar la fecha de consulta.");
        }

        return fechaConsulta.isAfter(fechaVencimiento);
    }

    @Override
    public boolean requiereCamaraFria() {
        return true;
    }
}
