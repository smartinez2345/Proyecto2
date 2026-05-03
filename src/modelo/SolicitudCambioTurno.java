package modelo;

import java.io.Serializable;

public class SolicitudCambioTurno implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Empleado solicitante;
    private Empleado reemplazo;
    private String estado;

    public SolicitudCambioTurno(Empleado solicitante, Empleado reemplazo) {
        this.solicitante = solicitante;
        this.reemplazo = reemplazo;
        this.estado = "PENDIENTE";
    }

    public void aprobar() {
        this.estado = "APROBADA";
        String turnoTemp = solicitante.getTurnoSemana();
        solicitante.setTurnoSemana(reemplazo.getTurnoSemana());
        reemplazo.setTurnoSemana(turnoTemp);
    }

    public void rechazar() {
        this.estado = "RECHAZADA";
    }

    public Empleado getSolicitante() { return solicitante; }
    public Empleado getReemplazo() { return reemplazo; }
    public String getEstado() { return estado; }

    @Override
    public String toString() {
        return "SolicitudCambioTurno[solicitante=" + solicitante.getLogin() +
               ", reemplazo=" + reemplazo.getLogin() + ", estado=" + estado + "]";
    }
}