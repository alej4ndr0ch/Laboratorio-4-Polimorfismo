package src.model;

import java.time.LocalDate;

public class CafePergamino extends LoteModel {
    private final double humidity;

    public CafePergamino(String name, int date, double weight,
            int amount, Type calidad,
            double humidity) {

        super(name, date, weight, amount, calidad);

        if (!Double.isFinite(humidity)
                || humidity < 8 || humidity > 14) {
            throw new IllegalArgumentException(
                    "La humedad debe estar entre 8 % y 14 %.");
        }

        this.humidity = humidity;
    }

    public double getHumidity() {
        return humidity;
    }

    public double calcularPago() {
        double precioPorKg;

        switch (getCalidad()) {
            case A:
                precioPorKg = 12;
                break;
            case B:
                precioPorKg = 9;
                break;
            case C:
                precioPorKg = 6;
                break;
            default:
                throw new IllegalStateException(
                        "Calidad no válida.");
        }

        double pagoBase = getWeight() * precioPorKg;
        double descuento = 0;

        if (humidity > 12) {
            double puntosExtra = Math.ceil(humidity - 12);
            descuento = pagoBase * puntosExtra * 0.03;
        }

        return pagoBase - descuento;
    }

    public boolean estaVencido(LocalDate fechaConsulta) {
        if (fechaConsulta == null) {
            throw new IllegalArgumentException(
                    "Debes indicar la fecha de consulta.");
        }

        return false;
    }

    public boolean estaVencido() {
        return false;
    }

    @Override
    public LocalDate getFechaVencimiento() {
        return null;
    }
}
