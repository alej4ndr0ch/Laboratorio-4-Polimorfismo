package src.model;

import java.time.LocalDate;

public class Cardamomo extends LoteModel {

    private final double porcentajeVerde;
    private final LocalDate fechaVencimiento;

    public Cardamomo(String name, int date, double weight,
            int amount, Type calidad,
            double porcentajeVerde) {

        super(name, date, weight, amount, calidad);

        if (!Double.isFinite(porcentajeVerde)
                || porcentajeVerde < 0
                || porcentajeVerde > 100) {
            throw new IllegalArgumentException(
                    "El porcentaje de grano verde debe estar entre 0 y 100.");
        }

        this.porcentajeVerde = porcentajeVerde;

        LocalDate fechaIngreso = LocalDate.of(
                date / 10000,
                (date / 100) % 100,
                date % 100);

        this.fechaVencimiento = fechaIngreso.plusDays(180);
    }

    public double getPorcentajeVerde() {
        return porcentajeVerde;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public double calcularPago() {
        double precioPorKg;

        switch (getCalidad()) {
            case A:
                precioPorKg = 60;
                break;
            case B:
                precioPorKg = 48;
                break;
            case C:
                precioPorKg = 35;
                break;
            default:
                throw new IllegalStateException(
                        "Calidad no válida.");
        }

        double pago = getWeight() * precioPorKg;

        if (porcentajeVerde >= 85) {
            pago += pago * 0.10;
        }

        return pago;
    }

    public boolean estaVencido(LocalDate fechaConsulta) {
        if (fechaConsulta == null) {
            throw new IllegalArgumentException(
                    "Debes indicar la fecha de consulta.");
        }

        return fechaConsulta.isAfter(fechaVencimiento);
    }

    public boolean estaVencido() {
        return estaVencido(LocalDate.now());
    }
}