package src.controller;

import src.model.Bodega;
import src.model.LoteModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class LoteController {

    private final List<LoteModel> lotes = new ArrayList<>();

    private final Map<Integer, Double> pagos = new HashMap<>();
    private final List<Bodega> bodegas = new ArrayList<>();

    public String preliquidar(LoteModel lote) {
        if (lote == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar un lote.");
        }

        LocalDate vencimiento = lote.getFechaVencimiento();

        return String.format(
                "Pago: Q%.2f%nVencimiento: %s",
                lote.calcularPago(),
                vencimiento == null ? "No se vence" : vencimiento);
    }

    public void descartarVencidos(
            Bodega bodega, LocalDate fechaConsulta) {

        if (bodega == null || fechaConsulta == null) {
            throw new IllegalArgumentException(
                    "Debes indicar la bodega y la fecha de consulta.");
        }

        Iterator<LoteModel> recorrido = bodega.iterator();

        while (recorrido.hasNext()) {
            LoteModel lote = recorrido.next();

            LocalDate vencimiento = lote.getFechaVencimiento();

            if (vencimiento != null
                    && fechaConsulta.isAfter(vencimiento)) {

                lote.descartarPorVencimiento();
                recorrido.remove();
            }
        }
    }

    public Bodega registrarBodega(double capacidad, boolean camaraFria) {
        Bodega nueva = new Bodega(capacidad, camaraFria);
        bodegas.add(nueva);
        return nueva;
    }

    public List<Bodega> getBodegas() {
        return new ArrayList<>(bodegas);
    }

    public Bodega buscarBodega(int codigo) {
        for (Bodega bodega : bodegas) {
            if (bodega.getCodigo() == codigo) {
                return bodega;
            }
        }

        throw new IllegalArgumentException(
                "No existe una bodega con ese código.");
    }

    public List<LoteModel> getLotesOrdenados() {
        List<LoteModel> copia = new ArrayList<>(lotes);
        copia.sort(null);
        return copia;
    }

    public void recibirLote(LoteModel lote, Bodega bodega) {
        if (lote == null || bodega == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el lote y la bodega.");
        }

        if (!bodegas.contains(bodega)) {
            throw new IllegalArgumentException(
                    "La bodega no está registrada.");
        }

        if (pagos.containsKey(lote.getCode())) {
            throw new IllegalStateException(
                    "Este lote ya fue recibido.");
        }

        LocalDate vencimiento = lote.getFechaVencimiento();

        if (vencimiento != null
                && LocalDate.now().isAfter(vencimiento)) {
            throw new IllegalStateException(
                    "No se puede recibir un lote vencido.");
        }

        double pagoCompleto = lote.calcularPago();

        bodega.almacenarLote(lote);

        lotes.add(lote);
        pagos.put(lote.getCode(), pagoCompleto);
    }
}