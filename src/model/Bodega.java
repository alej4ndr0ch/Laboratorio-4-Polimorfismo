package src.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

public class Bodega implements Iterable<LoteModel> {

    private static int siguienteCodigo = 1;

    private final int codigo;
    private final double capacidadMaxima;
    private final boolean camaraFria;

    private final List<LoteModel> lotes = new ArrayList<>();

    public Bodega(double capacidadMaxima, boolean camaraFria) {
        if (!Double.isFinite(capacidadMaxima)
                || capacidadMaxima <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser un número positivo.");
        }

        this.codigo = siguienteCodigo++;
        this.capacidadMaxima = capacidadMaxima;
        this.camaraFria = camaraFria;
    }

    public int getCodigo() {
        return codigo;
    }

    public double getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public boolean esCamaraFria() {
        return camaraFria;
    }

    public List<LoteModel> getLotes() {
        List<LoteModel> copia = new ArrayList<>(lotes);
        copia.sort(null);

        return Collections.unmodifiableList(copia);
    }

    public double getPesoOcupado() {
        double total = 0;

        for (LoteModel lote : lotes) {
            if (lote.getEstado() != LoteModel.Estado.DESCARTADO) {
                total += lote.getPesoDisponible();
            }
        }

        return total;
    }

    public double getCapacidadDisponible() {
        return capacidadMaxima - getPesoOcupado();
    }

    public void retirarLoteAgotado(LoteModel lote) {
    if (lote == null
            || lote.getEstado() != LoteModel.Estado.AGOTADO) {
        throw new IllegalArgumentException(
            "El lote debe estar agotado."
        );
    }

    if (!lotes.remove(lote)) {
        throw new IllegalArgumentException(
            "El lote no está almacenado en esta bodega."
        );
    }
}

    public void almacenarLote(LoteModel lote) {
        if (lote == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar un lote.");
        }

        if (lote.getBodega() != null) {
            throw new IllegalStateException(
                    "El lote ya está asignado a una bodega.");
        }

        if (lote.getEstado() != LoteModel.Estado.DISPONIBLE) {
            throw new IllegalStateException(
                    "Solo se pueden almacenar lotes disponibles.");
        }

        if (lote.requiereCamaraFria() != camaraFria) {
            throw new IllegalArgumentException(
                    "El producto no corresponde al tipo de bodega.");
        }

        double pesoTotal = getPesoOcupado() + lote.getWeight();

        if (pesoTotal > capacidadMaxima) {
            throw new IllegalArgumentException(
                    "La bodega no tiene suficiente capacidad.");
        }

        lote.asignarBodega(this);
        lotes.add(lote);
    }

    @Override
    public Iterator<LoteModel> iterator() {
        lotes.sort(null);
        return lotes.iterator();
    }
}