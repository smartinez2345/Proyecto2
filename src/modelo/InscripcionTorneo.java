package modelo;

import java.io.Serializable;

/**
 * Representa la inscripción de un usuario en un torneo,
 * indicando cuántos cupos tomó de la zona fanáticos y cuántos regulares.
 */
public class InscripcionTorneo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Usuario usuario;
    private int cuposFanaticos;  // cupos tomados de la reserva fanáticos
    private int cuposRegulares;  // cupos tomados del pool regular

    public InscripcionTorneo(Usuario usuario, int cuposFanaticos, int cuposRegulares) {
        this.usuario = usuario;
        this.cuposFanaticos = cuposFanaticos;
        this.cuposRegulares = cuposRegulares;
    }

    public Usuario getUsuario()      { return usuario; }
    public int getCuposFanaticos()   { return cuposFanaticos; }
    public int getCuposRegulares()   { return cuposRegulares; }
    public int totalCupos()          { return cuposFanaticos + cuposRegulares; }

    @Override
    public String toString() {
        return "Inscripcion[usuario=" + usuario.getLogin()
            + ", cuposFan=" + cuposFanaticos
            + ", cuposReg=" + cuposRegulares + "]";
    }
}