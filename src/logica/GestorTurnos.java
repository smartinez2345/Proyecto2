package logica;

import modelo.*;

public class GestorTurnos {

    private Cafe cafe;
    private static final int MIN_COCINEROS = 1;
    private static final int MIN_MESEROS = 2;

    public GestorTurnos(Cafe cafe) {
        this.cafe = cafe;
    }

    public SolicitudCambioTurno solicitarCambio(Empleado solicitante, Empleado reemplazo) {
        if (!validarMinimosConCambio(solicitante, reemplazo)) {
            System.out.println("ERROR: El cambio no puede realizarse, "
                + "no se cumple el mínimo de 1 cocinero y 2 meseros.");
            return null;
        }
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(solicitante, reemplazo);
        cafe.agregarSolicitud(solicitud);
        System.out.println("Solicitud de cambio registrada: "
            + solicitante.getLogin() + " ↔ " + reemplazo.getLogin());
        return solicitud;
    }

    public void aprobarCambio(SolicitudCambioTurno solicitud) {
        solicitud.aprobar();
        System.out.println("Cambio de turno APROBADO: "
            + solicitud.getSolicitante().getLogin()
            + " ↔ " + solicitud.getReemplazo().getLogin());
    }

    public void rechazarCambio(SolicitudCambioTurno solicitud) {
        solicitud.rechazar();
        System.out.println("Cambio de turno RECHAZADO.");
    }

    public void mostrarTurnos() {
        System.out.println("\n===== TURNOS SEMANALES =====");
        for (Empleado e : cafe.getEmpleados()) {
            String tipo = e instanceof Cocinero ? "Cocinero" : "Mesero";
            System.out.println(e.getLogin() + " (" + tipo + ") | Turno: " + e.getTurnoSemana());
        }
        System.out.println("============================\n");
    }

    public void mostrarSolicitudesPendientes() {
        System.out.println("\n===== SOLICITUDES PENDIENTES =====");
        for (SolicitudCambioTurno s : cafe.getSolicitudes()) {
            if (s.getEstado().equals("PENDIENTE")) {
                System.out.println(s.getSolicitante().getLogin()
                    + " ↔ " + s.getReemplazo().getLogin()
                    + " | Estado: " + s.getEstado());
            }
        }
        System.out.println("==================================\n");
    }

    private boolean validarMinimosConCambio(Empleado solicitante, Empleado reemplazo) {
        if ((solicitante instanceof Cocinero && reemplazo instanceof Cocinero) ||
            (solicitante instanceof Mesero && reemplazo instanceof Mesero)) {
            return true;
        }
        int cocineros = 0, meseros = 0;
        for (Empleado e : cafe.getEmpleados()) {
            if (e instanceof Cocinero) cocineros++;
            else if (e instanceof Mesero) meseros++;
        }
        if (solicitante instanceof Cocinero) {
            return (cocineros - 1) >= MIN_COCINEROS && (meseros + 1) >= MIN_MESEROS;
        } else {
            return (meseros - 1) >= MIN_MESEROS && (cocineros + 1) >= MIN_COCINEROS;
        }
    }
}