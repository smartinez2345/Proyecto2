package modelo;

/**
 * Torneo sin tarifa de entrada. El premio es un bono de descuento
 * redimible en la siguiente compra del ganador.
 */
public class TorneoAmistoso extends Torneo {
    private static final long serialVersionUID = 1L;

    private double porcentajeDescuento; // ej. 0.15 = 15%

    public TorneoAmistoso(String nombre, String diaSemana, Juego juego,
                          int numParticipantes, double porcentajeDescuento) {
        super(nombre, diaSemana, juego, numParticipantes);
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public double getPorcentajeDescuento() { return porcentajeDescuento; }

    @Override
    public boolean esAmistoso() { return true; }

    @Override
    public String descripcionPremio() {
        return "Bono de descuento del " + (int)(porcentajeDescuento * 100) + "% en la siguiente compra";
    }

    @Override
    public String toString() {
        return "TorneoAmistoso[" + getNombre() + ", " + getDiaSemana()
            + ", premio=" + descripcionPremio() + "]";
    }
}