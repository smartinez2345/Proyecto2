package modelo;

/**
 * Torneo con tarifa de entrada. El premio en metálico se calcula
 * con base en el dinero recaudado en inscripciones.
 * Los empleados entran gratis pero no reciben el premio en metálico.
 */
public class TorneoCompetitivo extends Torneo {
    private static final long serialVersionUID = 1L;

    private double tarifaEntrada;   // costo por participante inscrito (cliente)
    private double premioMetalico;  // calculado al cerrar inscripciones

    public TorneoCompetitivo(String nombre, String diaSemana, Juego juego,
                             int numParticipantes, double tarifaEntrada) {
        super(nombre, diaSemana, juego, numParticipantes);
        this.tarifaEntrada = tarifaEntrada;
        this.premioMetalico = 0;
    }

    public double getTarifaEntrada() { return tarifaEntrada; }
    public double getPremioMetalico() { return premioMetalico; }

    /**
     * Recalcula el premio con base en los cupos pagados (solo clientes,
     * no empleados). Llamar antes de mostrar el premio al público.
     */
    public void calcularPremio() {
        double recaudado = 0;
        for (InscripcionTorneo ins : getInscripciones()) {
            if (ins.getUsuario() instanceof Cliente) {
                recaudado += tarifaEntrada * ins.totalCupos();
            }
        }
        this.premioMetalico = recaudado;
    }

    @Override
    public boolean esAmistoso() { return false; }

    @Override
    public String descripcionPremio() {
        calcularPremio();
        return "Premio en metálico: $" + String.format("%.2f", premioMetalico)
            + " (tarifa: $" + String.format("%.2f", tarifaEntrada) + " por participante)";
    }

    @Override
    public String toString() {
        return "TorneoCompetitivo[" + getNombre() + ", " + getDiaSemana()
            + ", tarifa=$" + tarifaEntrada + "]";
    }
}