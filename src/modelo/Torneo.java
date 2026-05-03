package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta que representa un torneo del Board Game Café.
 * Subclases: TorneoAmistoso y TorneoCompetitivo.
 */
public abstract class Torneo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String diaSemana;        // "LUNES", "MARTES", etc.
    private Juego juego;
    private int numParticipantes;    // capacidad total
    private int cuposOcupados;       // cupos regulares tomados
    private int cuposFanaticos;      // 20% reservado para fanáticos (redondeado arriba)
    private int cuposFanaticosOcupados;
    private List<InscripcionTorneo> inscripciones;

    public Torneo(String nombre, String diaSemana, Juego juego, int numParticipantes) {
        this.nombre = nombre;
        this.diaSemana = diaSemana.toUpperCase();
        this.juego = juego;
        this.numParticipantes = numParticipantes;
        this.cuposOcupados = 0;
        this.cuposFanaticosOcupados = 0;
        this.cuposFanaticos = (int) Math.ceil(numParticipantes * 0.20);
        this.inscripciones = new ArrayList<>();
    }

    // ─── Getters ──────────────────────────────────────────────────────
    public String getNombre()                    { return nombre; }
    public String getDiaSemana()                 { return diaSemana; }
    public Juego getJuego()                      { return juego; }
    public int getNumParticipantes()             { return numParticipantes; }
    public int getCuposOcupados()                { return cuposOcupados; }
    public int getCuposFanaticos()               { return cuposFanaticos; }
    public int getCuposFanaticosOcupados()       { return cuposFanaticosOcupados; }
    public List<InscripcionTorneo> getInscripciones() { return inscripciones; }

    public int totalCuposOcupados()      { return cuposFanaticosOcupados + cuposOcupados; }
    public int cuposRegularesDisponibles() {
        int regulares = numParticipantes - cuposFanaticos;
        return regulares - cuposOcupados;
    }
    public int cuposFanaticosDisponibles() { return cuposFanaticos - cuposFanaticosOcupados; }
    public int cuposTotalesDisponibles()   { return numParticipantes - totalCuposOcupados(); }

    // ─── Inscripción ──────────────────────────────────────────────────

    /**
     * Inscribe al usuario tomando entre 1 y 3 cupos.
     * Si es fanático usa primero los cupos reservados; si se agotan, toma regulares.
     */
    public boolean inscribir(Usuario usuario, int cantidad, boolean esFanatico) {
        if (yaInscrito(usuario)) return false;
        if (cantidad < 1 || cantidad > 3) return false;

        int fanaticosATomar = 0;
        int regularesATomar = 0;

        if (esFanatico) {
            int dispFan = cuposFanaticosDisponibles();
            fanaticosATomar = Math.min(dispFan, cantidad);
            regularesATomar = cantidad - fanaticosATomar;
        } else {
            regularesATomar = cantidad;
        }

        if (regularesATomar > cuposRegularesDisponibles()) return false;

        cuposFanaticosOcupados += fanaticosATomar;
        cuposOcupados          += regularesATomar;
        inscripciones.add(new InscripcionTorneo(usuario, fanaticosATomar, regularesATomar));
        return true;
    }

    /** Desinscribe al usuario liberando todos sus cupos. */
    public boolean desinscribir(Usuario usuario) {
        InscripcionTorneo ins = buscarInscripcion(usuario);
        if (ins == null) return false;
        cuposFanaticosOcupados -= ins.getCuposFanaticos();
        cuposOcupados          -= ins.getCuposRegulares();
        inscripciones.remove(ins);
        return true;
    }

    public boolean yaInscrito(Usuario usuario) { return buscarInscripcion(usuario) != null; }

    public InscripcionTorneo buscarInscripcion(Usuario usuario) {
        for (InscripcionTorneo ins : inscripciones) {
            if (ins.getUsuario().getLogin().equals(usuario.getLogin())) return ins;
        }
        return null;
    }

    // ─── Abstractos ───────────────────────────────────────────────────
    public abstract boolean esAmistoso();
    public abstract String descripcionPremio();

    @Override
    public String toString() {
        return "Torneo[" + nombre + ", " + diaSemana + ", " + juego.getNombre()
            + ", " + totalCuposOcupados() + "/" + numParticipantes
            + ", " + (esAmistoso() ? "Amistoso" : "Competitivo") + "]";
    }
}