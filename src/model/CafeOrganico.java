package src.model;

import java.time.LocalDate;

public class CafeOrganico extends CafePergamino {

    private final String entidadCertificadora;
    private final LocalDate fechaValidezSello;

    public CafeOrganico(String name, int date, double weight,
                        int amount, Type calidad, double humidity,
                        String entidadCertificadora,
                        LocalDate fechaValidezSello) {

        super(name, date, weight, amount, calidad, humidity);

        if (entidadCertificadora == null
                || entidadCertificadora.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes indicar la entidad certificadora.");
        }

        if (fechaValidezSello == null) {
            throw new IllegalArgumentException(
                    "Debes indicar la fecha de validez del sello.");
        }


        LocalDate fechaIngreso = LocalDate.of(
                date / 10000,
                (date / 100) % 100,
                date % 100);

        if (fechaValidezSello.isBefore(fechaIngreso)) {
            throw new IllegalArgumentException(
                    "El sello está vencido en la fecha de ingreso.");
        }

        this.entidadCertificadora = entidadCertificadora;
        this.fechaValidezSello = fechaValidezSello;
    }

    public String getEntidadCertificadora() {
        return entidadCertificadora;
    }

    public LocalDate getFechaValidezSello() {
        return fechaValidezSello;
    }

    @Override
    public double calcularPago() {
        double pagoPergamino = super.calcularPago();
        double bonificacion = getWeight() * 0.75;

        return pagoPergamino + bonificacion;
    }
}
