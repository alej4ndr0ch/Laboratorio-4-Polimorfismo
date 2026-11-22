package src.model;

import java.time.LocalDate;

public abstract class LoteModel implements Comparable<LoteModel>{
    private final int code;
    private static int siguienteCode = 1;
    private String name;
    private int date;
    private double weight;
    private int amount;
    private Type calidad;
    private Bodega bodega;

    public enum Type {
        A, B, C
    }

    public enum Estado {
        DISPONIBLE, AGOTADO, DESCARTADO
    }

    private double pesoDisponible;
    private boolean descartado = false;

    public LoteModel(String name, int date, double weight, int amount, Type calidad) {
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new IllegalArgumentException(
                    "El peso inicial debe ser un número positivo.");
        }

        if (calidad == null) {
            throw new IllegalArgumentException(
                    "Debes indicar la calidad del lote.");
        }

        this.code = siguienteCode++;
        this.name = name;
        this.date = date;
        this.weight = weight;
        this.amount = amount;
        this.pesoDisponible = weight;
        this.calidad = calidad;
    }

    public String name() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getDate() {
        return date;
    }

    public int getCode() {
        return code;
    }

    public double getPesoDisponible() {
        return pesoDisponible;
    }

    public double getWeight() {
        return weight;
    }

    public Type getCalidad() {
        return calidad;
    }

    public Estado getEstado() {
        if (descartado) {
            return Estado.DESCARTADO;
        }

        if (pesoDisponible == 0) {
            return Estado.AGOTADO;
        }

        return Estado.DISPONIBLE;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void retirarPeso(double kilogramos) {
        if (getEstado() != Estado.DISPONIBLE) {
            throw new IllegalStateException(
                    "El lote no está disponible.");
        }

        if (!Double.isFinite(kilogramos) || kilogramos <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser un número positivo.");
        }

        if (kilogramos > pesoDisponible) {
            throw new IllegalArgumentException(
                    "No hay suficiente peso disponible.");
        }

        pesoDisponible -= kilogramos;
    }

    void asignarBodega(Bodega nuevaBodega) {
        if (nuevaBodega == null) {
            throw new IllegalArgumentException(
                    "Debes indicar una bodega.");
        }

        if (bodega != null) {
            throw new IllegalStateException(
                    "El lote ya tiene una bodega y no puede cambiarse.");
        }

        this.bodega = nuevaBodega;
    }

    public boolean requiereCamaraFria() {
        return false;
    }

    public void descartarPorVencimiento() {
        descartado = true;
    }

    public abstract double calcularPago();

    public abstract LocalDate getFechaVencimiento();

    public LocalDate getFechaIngreso() {
        return LocalDate.of(
                date / 10000,
                (date / 100) % 100,
                date % 100);
    }

    @Override
    public int compareTo(LoteModel otro) {
        LocalDate vencimientoActual = getFechaVencimiento();
        LocalDate vencimientoOtro = otro.getFechaVencimiento();

        boolean perecederoActual = vencimientoActual != null;
        boolean perecederoOtro = vencimientoOtro != null;

        if (perecederoActual && !perecederoOtro) {
            return -1;
        }

        if (!perecederoActual && perecederoOtro) {
            return 1;
        }

        int resultado;

        if (perecederoActual) {
            resultado = vencimientoActual.compareTo(vencimientoOtro);
        } else {
            resultado = getFechaIngreso()
                    .compareTo(otro.getFechaIngreso());
        }

        if (resultado != 0) {
            return resultado;
        }

        resultado = getCalidad().compareTo(otro.getCalidad());

        if (resultado != 0) {
            return resultado;
        }

        return String.valueOf(getCode())
                .compareTo(String.valueOf(otro.getCode()));
    }
}
