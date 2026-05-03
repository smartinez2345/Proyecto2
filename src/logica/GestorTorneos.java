package logica;

import modelo.*;
import java.util.List;

/**
 * Gestor de torneos del Board Game Café.
 * Implementa todos los requerimientos funcionales del Proyecto #2.
 */
public class GestorTorneos {

    private Cafe cafe;

    public GestorTorneos(Cafe cafe) {
        this.cafe = cafe;
    }

    // ══════════════════════════════════════════════════════════════════
    //  CREACIÓN DE TORNEOS  (solo Administrador)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Crea un torneo AMISTOSO.
     * Valida nombre único, participantes > 0 e inventario de préstamos suficiente.
     */
    public TorneoAmistoso crearTorneoAmistoso(String nombre, String diaSemana,
                                              Juego juego, int numParticipantes,
                                              double porcentajeDescuento) {
        if (!validarCreacionTorneo(nombre, juego, numParticipantes)) return null;

        TorneoAmistoso torneo = new TorneoAmistoso(
                nombre, diaSemana, juego, numParticipantes, porcentajeDescuento);
        cafe.agregarTorneo(torneo);
        System.out.println("Torneo amistoso creado: " + nombre
            + " | Día: " + diaSemana
            + " | Juego: " + juego.getNombre()
            + " | Cupos: " + numParticipantes
            + " | Premio: " + torneo.descripcionPremio());
        return torneo;
    }

    /**
     * Crea un torneo COMPETITIVO.
     */
    public TorneoCompetitivo crearTorneoCompetitivo(String nombre, String diaSemana,
                                                    Juego juego, int numParticipantes,
                                                    double tarifaEntrada) {
        if (!validarCreacionTorneo(nombre, juego, numParticipantes)) return null;
        if (tarifaEntrada <= 0) {
            System.out.println("ERROR: La tarifa de entrada debe ser mayor a 0.");
            return null;
        }
        TorneoCompetitivo torneo = new TorneoCompetitivo(
                nombre, diaSemana, juego, numParticipantes, tarifaEntrada);
        cafe.agregarTorneo(torneo);
        System.out.println("Torneo competitivo creado: " + nombre
            + " | Día: " + diaSemana
            + " | Juego: " + juego.getNombre()
            + " | Cupos: " + numParticipantes
            + " | Tarifa: $" + String.format("%.2f", tarifaEntrada));
        return torneo;
    }

    /** Valida condiciones comunes para crear cualquier torneo. */
    private boolean validarCreacionTorneo(String nombre, Juego juego, int numParticipantes) {
        if (nombre == null || nombre.isBlank()) {
            System.out.println("ERROR: El nombre del torneo no puede estar vacío.");
            return false;
        }
        if (cafe.buscarTorneoPorNombre(nombre) != null) {
            System.out.println("ERROR: Ya existe un torneo con ese nombre.");
            return false;
        }
        if (numParticipantes <= 0) {
            System.out.println("ERROR: El número de participantes debe ser mayor a 0.");
            return false;
        }
        // El número de participantes puede superar el máximo de jugadores del juego,
        // siempre que las copias disponibles en el inventario de préstamos lo permitan.
        if (!hayCopiaSuficiente(juego, numParticipantes)) {
            System.out.println("ERROR: No hay copias suficientes en inventario de préstamos "
                + "para un torneo de " + numParticipantes + " participantes del juego '"
                + juego.getNombre() + "' (max jugadores por copia: "
                + juego.getMaxJugadores() + ").");
            return false;
        }
        return true;
    }

    /**
     * Necesitamos ceil(numParticipantes / maxJugadores) copias en inventario.
     */
    private boolean hayCopiaSuficiente(Juego juego, int numParticipantes) {
        InventarioJuegos inv = cafe.buscarInventarioJuego(juego);
        if (inv == null) return false;
        int copiasNecesarias = (int) Math.ceil((double) numParticipantes / juego.getMaxJugadores());
        return inv.getCantidadDisponible() >= copiasNecesarias;
    }

    // ══════════════════════════════════════════════════════════════════
    //  INSCRIPCIÓN DE CLIENTES
    // ══════════════════════════════════════════════════════════════════

    /**
     * Inscribe a un cliente en un torneo (1–3 cupos).
     * Si es fanático del juego del torneo, usa primero cupos reservados.
     */
    public boolean inscribirCliente(Torneo torneo, Cliente cliente, int cantidad) {
        if (torneo == null || cliente == null) {
            System.out.println("ERROR: Torneo o cliente inválido.");
            return false;
        }
        if (cantidad < 1 || cantidad > 3) {
            System.out.println("ERROR: Puede inscribir entre 1 y 3 participantes.");
            return false;
        }
        if (torneo.yaInscrito(cliente)) {
            System.out.println("ERROR: '" + cliente.getLogin()
                + "' ya está inscrito en '" + torneo.getNombre() + "'.");
            return false;
        }
        if (torneo.cuposTotalesDisponibles() < cantidad) {
            System.out.println("ERROR: No hay suficientes cupos. Disponibles: "
                + torneo.cuposTotalesDisponibles());
            return false;
        }

        boolean esFanatico = cliente.esFanaticoDeJuego(torneo.getJuego());
        boolean exito = torneo.inscribir(cliente, cantidad, esFanatico);
        if (exito) {
            System.out.println("Cliente '" + cliente.getLogin() + "' inscrito en '"
                + torneo.getNombre() + "' con " + cantidad + " participante(s)."
                + (esFanatico ? " [Fanático - cupo preferencial]" : ""));
        } else {
            System.out.println("ERROR: No se pudo completar la inscripción.");
        }
        return exito;
    }

    /**
     * Desinscribe a un cliente (elimina TODOS sus cupos).
     */
    public boolean desinscribirCliente(Torneo torneo, Cliente cliente) {
        if (!torneo.yaInscrito(cliente)) {
            System.out.println("ERROR: '" + cliente.getLogin()
                + "' no está inscrito en '" + torneo.getNombre() + "'.");
            return false;
        }
        boolean exito = torneo.desinscribir(cliente);
        if (exito) {
            System.out.println("Cliente '" + cliente.getLogin()
                + "' desinscrito de '" + torneo.getNombre() + "'. Cupos liberados.");
        }
        return exito;
    }

    // ══════════════════════════════════════════════════════════════════
    //  INSCRIPCIÓN DE EMPLEADOS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Inscribe a un empleado en un torneo.
     * Reglas:
     * - No puede estar cubriendo turno el día del torneo.
     * - En torneos competitivos entra gratis pero sin premio en metálico.
     */
    public boolean inscribirEmpleado(Torneo torneo, Empleado empleado, int cantidad) {
        if (torneo == null || empleado == null) {
            System.out.println("ERROR: Torneo o empleado inválido.");
            return false;
        }
        // Turno es un día exacto: comparar directamente
        if (empleadoTieneTurnoEnDia(empleado, torneo.getDiaSemana())) {
            System.out.println("ERROR: '" + empleado.getLogin()
                + "' tiene turno el " + torneo.getDiaSemana()
                + " y no puede inscribirse.");
            return false;
        }
        if (cantidad < 1 || cantidad > 3) {
            System.out.println("ERROR: Puede inscribir entre 1 y 3 participantes.");
            return false;
        }
        if (torneo.yaInscrito(empleado)) {
            System.out.println("ERROR: '" + empleado.getLogin()
                + "' ya está inscrito en '" + torneo.getNombre() + "'.");
            return false;
        }
        if (torneo.cuposTotalesDisponibles() < cantidad) {
            System.out.println("ERROR: No hay suficientes cupos disponibles.");
            return false;
        }

        boolean exito = torneo.inscribir(empleado, cantidad, false);
        if (exito) {
            String nota = torneo.esAmistoso() ? "" : " [Gratis, sin premio en metálico]";
            System.out.println("Empleado '" + empleado.getLogin() + "' inscrito en '"
                + torneo.getNombre() + "' con " + cantidad + " participante(s)." + nota);
        } else {
            System.out.println("ERROR: No se pudo completar la inscripción del empleado.");
        }
        return exito;
    }

    /**
     * Desinscribe a un empleado (elimina TODOS sus cupos).
     */
    public boolean desinscribirEmpleado(Torneo torneo, Empleado empleado) {
        if (!torneo.yaInscrito(empleado)) {
            System.out.println("ERROR: '" + empleado.getLogin()
                + "' no está inscrito en '" + torneo.getNombre() + "'.");
            return false;
        }
        boolean exito = torneo.desinscribir(empleado);
        if (exito) {
            System.out.println("Empleado '" + empleado.getLogin()
                + "' desinscrito de '" + torneo.getNombre() + "'.");
        }
        return exito;
    }

    // ══════════════════════════════════════════════════════════════════
    //  PREMIOS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Entrega el bono de descuento al ganador de un torneo amistoso.
     * El bono se agrega a la lista del cliente (puede tener varios de distintos torneos).
     */
    public boolean entregarBonoAmistoso(TorneoAmistoso torneo, Cliente ganador) {
        if (!torneo.yaInscrito(ganador)) {
            System.out.println("ERROR: '" + ganador.getLogin()
                + "' no estaba inscrito en el torneo.");
            return false;
        }
        ganador.agregarBonoDescuento(torneo.getPorcentajeDescuento());
        System.out.println("Bono de descuento del "
            + (int)(torneo.getPorcentajeDescuento() * 100) + "% asignado a '"
            + ganador.getLogin() + "'. Total bonos acumulados: "
            + ganador.getBonosDescuento().size());
        return true;
    }

    /**
     * Calcula y retorna el premio en metálico del torneo competitivo.
     * Los empleados no reciben premio en metálico.
     * @return monto del premio, o 0.0 si el ganador es empleado
     */
    public double calcularPremioCompetitivo(TorneoCompetitivo torneo, Usuario ganador) {
        if (ganador instanceof Empleado) {
            System.out.println("INFO: '" + ganador.getLogin()
                + "' es empleado y no recibe el premio en metálico.");
            return 0.0;
        }
        torneo.calcularPremio();
        double premio = torneo.getPremioMetalico();
        System.out.println("Premio en metálico para '" + ganador.getLogin()
            + "': $" + String.format("%.2f", premio));
        return premio;
    }

    // ══════════════════════════════════════════════════════════════════
    //  VISUALIZACIÓN
    // ══════════════════════════════════════════════════════════════════

    /** Muestra todos los torneos registrados. */
    public void mostrarTorneos() {
        List<Torneo> torneos = cafe.getTorneos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return;
        }
        System.out.println("\n========== TORNEOS ==========");
        for (Torneo t : torneos) {
            System.out.println(t);
            System.out.println("  Cupos fanáticos  : " + t.getCuposFanaticos()
                + " (ocupados: " + t.getCuposFanaticosOcupados() + ")");
            System.out.println("  Cupos regulares  : " + t.cuposRegularesDisponibles() + " disponibles");
            System.out.println("  Total disponibles: " + t.cuposTotalesDisponibles());
            System.out.println("  Premio           : " + t.descripcionPremio());
        }
        System.out.println("=============================\n");
    }

    /** Muestra las inscripciones de un torneo específico. */
    public void mostrarInscripciones(Torneo torneo) {
        System.out.println("\n--- Inscripciones: " + torneo.getNombre() + " ---");
        if (torneo.getInscripciones().isEmpty()) {
            System.out.println("  Sin inscripciones aún.");
            return;
        }
        for (InscripcionTorneo ins : torneo.getInscripciones()) {
            String tipo = (ins.getUsuario() instanceof Cliente) ? "Cliente" : "Empleado";
            System.out.println("  " + tipo + " | " + ins.getUsuario().getLogin()
                + " | Cupos: " + ins.totalCupos()
                + " (fanáticos=" + ins.getCuposFanaticos()
                + ", regulares=" + ins.getCuposRegulares() + ")");
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════════════════════════
    //  AUXILIAR: verificar turno
    // ══════════════════════════════════════════════════════════════════

    /**
     * Compara el turno del empleado (día exacto, ej. "LUNES")
     * contra el día del torneo. Insensible a mayúsculas/minúsculas.
     */
    public boolean empleadoTieneTurnoEnDia(Empleado empleado, String diaTorneo) {
        String turno = empleado.getTurnoSemana().toUpperCase().trim();
        String dia   = diaTorneo.toUpperCase().trim();
        return turno.equals(dia);
    }
}