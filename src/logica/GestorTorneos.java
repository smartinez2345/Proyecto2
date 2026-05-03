package logica;

import modelo.*;
import java.util.List;

/**
 * Gestor de torneos del Board Game Café.
 * Implementa todas las reglas de negocio del Proyecto #2.
 */
public class GestorTorneos {

    private Cafe cafe;

    public GestorTorneos(Cafe cafe) {
        this.cafe = cafe;
    }

    // ══════════════════════════════════════════════════════════════════
    //  CREACIÓN DE TORNEOS (solo Administrador)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Crea un torneo AMISTOSO.
     * Valida que haya suficientes copias del juego en el inventario de préstamos.
     *
     * @param nombre              nombre del torneo
     * @param diaSemana           día de la semana (e.g. "LUNES")
     * @param juego               juego del torneo
     * @param numParticipantes    número total de cupos
     * @param porcentajeDescuento porcentaje del bono (e.g. 0.15 = 15 %)
     * @return el torneo creado, o null si no se pudo crear
     */
    public TorneoAmistoso crearTorneoAmistoso(String nombre, String diaSemana,
                                              Juego juego, int numParticipantes,
                                              double porcentajeDescuento) {
        if (!validarCreacionTorneo(nombre, juego, numParticipantes)) return null;

        TorneoAmistoso torneo = new TorneoAmistoso(nombre, diaSemana, juego,
                                                    numParticipantes, porcentajeDescuento);
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

        TorneoCompetitivo torneo = new TorneoCompetitivo(nombre, diaSemana, juego,
                                                          numParticipantes, tarifaEntrada);
        cafe.agregarTorneo(torneo);
        System.out.println("Torneo competitivo creado: " + nombre
            + " | Día: " + diaSemana
            + " | Juego: " + juego.getNombre()
            + " | Cupos: " + numParticipantes
            + " | Tarifa: $" + String.format("%.2f", tarifaEntrada));
        return torneo;
    }

    /** Valida condiciones comunes antes de crear cualquier torneo. */
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
        // El número puede ser mayor al máximo de jugadores del juego,
        // siempre que el inventario de préstamos lo permita
        if (!hayCopiaSuficiente(juego, numParticipantes)) {
            System.out.println("ERROR: No hay copias suficientes en inventario de préstamos "
                + "para soportar " + numParticipantes + " participantes del juego '"
                + juego.getNombre() + "' (máx jugadores: " + juego.getMaxJugadores() + ").");
            return false;
        }
        return true;
    }

    /**
     * Verifica que el inventario de préstamos tenga suficientes copias.
     * Se necesita ceil(numParticipantes / maxJugadores) copias.
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
     * Inscribe a un cliente en un torneo.
     * Un cliente puede inscribir hasta 3 participantes por torneo.
     * Se verifica si es fanático del juego para asignar cupo preferencial.
     *
     * @param torneo   el torneo al que se quiere inscribir
     * @param cliente  el cliente que se inscribe
     * @param cantidad número de participantes (1-3)
     * @return true si la inscripción fue exitosa
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
            System.out.println("ERROR: El cliente '" + cliente.getLogin()
                + "' ya está inscrito en el torneo '" + torneo.getNombre() + "'.");
            return false;
        }
        if (torneo.cuposTotalesDisponibles() < cantidad) {
            System.out.println("ERROR: No hay suficientes cupos disponibles. "
                + "Disponibles: " + torneo.cuposTotalesDisponibles());
            return false;
        }

        boolean esFanatico = cliente.esFanaticoDeJuego(torneo.getJuego());
        boolean exito = torneo.inscribir(cliente, cantidad, esFanatico);

        if (exito) {
            System.out.println("Cliente '" + cliente.getLogin() + "' inscrito en '"
                + torneo.getNombre() + "' con " + cantidad + " participante(s)."
                + (esFanatico ? " [Fanático - cupo preferencial]" : ""));
        } else {
            System.out.println("ERROR: No se pudo inscribir al cliente en el torneo.");
        }
        return exito;
    }

    /**
     * Desinscribe a un cliente del torneo (elimina todos sus cupos).
     */
    public boolean desinscribirCliente(Torneo torneo, Cliente cliente) {
        if (!torneo.yaInscrito(cliente)) {
            System.out.println("ERROR: El cliente '" + cliente.getLogin()
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
     * El empleado NO puede estar cubriendo turno el día del torneo.
     * En torneos competitivos, entra gratis pero sin premio en metálico.
     *
     * @param torneo   el torneo
     * @param empleado el empleado
     * @param cantidad número de participantes (1-3)
     * @return true si la inscripción fue exitosa
     */
    public boolean inscribirEmpleado(Torneo torneo, Empleado empleado, int cantidad) {
        if (torneo == null || empleado == null) {
            System.out.println("ERROR: Torneo o empleado inválido.");
            return false;
        }
        // Verificar que no esté en turno el día del torneo
        if (empleadoTieneTurnoEnDia(empleado, torneo.getDiaSemana())) {
            System.out.println("ERROR: El empleado '" + empleado.getLogin()
                + "' tiene turno el " + torneo.getDiaSemana()
                + " y no puede inscribirse en el torneo.");
            return false;
        }
        if (cantidad < 1 || cantidad > 3) {
            System.out.println("ERROR: Puede inscribir entre 1 y 3 participantes.");
            return false;
        }
        if (torneo.yaInscrito(empleado)) {
            System.out.println("ERROR: El empleado '" + empleado.getLogin()
                + "' ya está inscrito en '" + torneo.getNombre() + "'.");
            return false;
        }
        if (torneo.cuposTotalesDisponibles() < cantidad) {
            System.out.println("ERROR: No hay suficientes cupos disponibles.");
            return false;
        }

        // Empleados no son fanáticos (no tienen lista de favoritos en este diseño)
        boolean exito = torneo.inscribir(empleado, cantidad, false);
        if (exito) {
            String nota = torneo.esAmistoso() ? "" : " [Gratis, sin premio en metálico]";
            System.out.println("Empleado '" + empleado.getLogin() + "' inscrito en '"
                + torneo.getNombre() + "' con " + cantidad + " participante(s)." + nota);
        } else {
            System.out.println("ERROR: No se pudo inscribir al empleado.");
        }
        return exito;
    }

    /**
     * Desinscribe a un empleado del torneo.
     */
    public boolean desinscribirEmpleado(Torneo torneo, Empleado empleado) {
        if (!torneo.yaInscrito(empleado)) {
            System.out.println("ERROR: El empleado '" + empleado.getLogin()
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
     * El bono reemplaza cualquier bono previo (no acumulable).
     *
     * @param torneo  el torneo amistoso terminado
     * @param ganador el cliente ganador
     * @return true si se entregó correctamente
     */
    public boolean entregarBonoAmistoso(TorneoAmistoso torneo, Cliente ganador) {
        if (!torneo.yaInscrito(ganador)) {
            System.out.println("ERROR: El cliente '" + ganador.getLogin()
                + "' no estaba inscrito en el torneo.");
            return false;
        }
        ganador.asignarBonoDescuento(torneo.getPorcentajeDescuento());
        System.out.println("Bono de descuento del "
            + (int)(torneo.getPorcentajeDescuento() * 100) + "% asignado a '"
            + ganador.getLogin() + "'. Válido en su próxima compra.");
        return true;
    }

    /**
     * Calcula y muestra el premio en metálico del torneo competitivo.
     * Los empleados no reciben el premio.
     */
    public double calcularPremioCompetitivo(TorneoCompetitivo torneo, Usuario ganador) {
        if (ganador instanceof Empleado) {
            System.out.println("INFO: El ganador '" + ganador.getLogin()
                + "' es empleado y no recibe el premio en metálico.");
            return 0;
        }
        torneo.calcularPremio();
        double premio = torneo.getPremioMetalico();
        System.out.println("Premio en metálico para '" + ganador.getLogin()
            + "': $" + String.format("%.2f", premio));
        return premio;
    }

    // ══════════════════════════════════════════════════════════════════
    //  CONSULTAS Y VISUALIZACIÓN
    // ══════════════════════════════════════════════════════════════════

    /** Lista todos los torneos del café. */
    public void mostrarTorneos() {
        List<Torneo> torneos = cafe.getTorneos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos registrados.");
            return;
        }
        System.out.println("\n========== TORNEOS ==========");
        for (Torneo t : torneos) {
            System.out.println(t);
            System.out.println("  Cupos fanáticos reservados : " + t.getCuposFanaticos()
                + " (ocupados: " + t.getCuposFanaticosOcupados() + ")");
            System.out.println("  Cupos regulares disponibles: " + t.cuposRegularesDisponibles());
            System.out.println("  Total disponibles          : " + t.cuposTotalesDisponibles());
            System.out.println("  Premio                     : " + t.descripcionPremio());
        }
        System.out.println("=============================\n");
    }

    /** Detalle de inscripciones de un torneo. */
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
                + " (fan=" + ins.getCuposFanaticos() + ", reg=" + ins.getCuposRegulares() + ")");
        }
        System.out.println();
    }

    // ══════════════════════════════════════════════════════════════════
    //  MÉTODOS AUXILIARES
    // ══════════════════════════════════════════════════════════════════

    /**
     * Determina si un empleado tiene turno el día del torneo.
     * El turno se compara en formato "DIA" (ej. "LUNES").
     * Si el turno es un rango "Lunes-Viernes", se verifica si el día está en ese rango.
     */
    public boolean empleadoTieneTurnoEnDia(Empleado empleado, String diaTorneo) {
        String turno = empleado.getTurnoSemana().toUpperCase().trim();
        String dia   = diaTorneo.toUpperCase().trim();

        // Turno exacto: "LUNES"
        if (turno.equals(dia)) return true;

        // Turno rango: "LUNES-VIERNES"
        if (turno.contains("-")) {
            String[] partes = turno.split("-");
            if (partes.length == 2) {
                int idxInicio = indiceDia(partes[0].trim());
                int idxFin    = indiceDia(partes[1].trim());
                int idxDia    = indiceDia(dia);
                if (idxInicio >= 0 && idxFin >= 0 && idxDia >= 0) {
                    return idxDia >= idxInicio && idxDia <= idxFin;
                }
            }
        }
        return false;
    }

    private int indiceDia(String dia) {
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};
        for (int i = 0; i < dias.length; i++) {
            if (dias[i].equals(dia.toUpperCase())) return i;
        }
        return -1;
    }
}