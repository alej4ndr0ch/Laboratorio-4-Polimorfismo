package src.model;

import java.time.LocalDate;

public class QuesoFresco extends LoteModel {

    private final double porcentajeGrasa;
    private final LocalDate fechaVencimiento;

    public QuesoFresco(String name, int date, double weight,
            int amount, Type calidad,
            double porcentajeGrasa) {

        super(name, date, weight, amount, calidad);

        if (calidad == Type.C) {
            throw new IllegalArgumentException(
                    "No se acepta queso fresco de calidad C.");
        }

        if (!Double.isFinite(porcentajeGrasa)
                || porcentajeGrasa < 10
                || porcentajeGrasa > 40) {
            throw new IllegalArgumentException(
                    "El porcentaje de grasa debe estar entre 10 y 40.");
        }

        this.porcentajeGrasa = porcentajeGrasa;

        LocalDate fechaIngreso = LocalDate.of(
                date / 10000,
                (date / 100) % 100,
                date % 100);

        this.fechaVencimiento = fechaIngreso.plusDays(21);
    }

    public double getPorcentajeGrasa() {
        return porcentajeGrasa;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public double calcularPago() {
        double precioPorKg;

        switch (getCalidad()) {
            case A:
                precioPorKg = 38;
                break;
            case B:
                precioPorKg = 30;
                break;
            default:
                throw new IllegalStateException(
                        "Calidad no válida para queso fresco.");
        }

        double pagoBase = getWeight() * precioPorKg;
        double costoEmpaque = getWeight() * 1.50;

        return pagoBase - costoEmpaque;
    }

    public boolean requiereCamaraFria() {
        return true;
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