package presentacion;

import logica.*;
import modelo.*;

public class PruebaInventario {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  PRUEBA 5: GESTIÓN INVENTARIO");
        System.out.println("====================================\n");

        Cafe cafe = new Cafe(30);
        GestorInventario gestorInventario = new GestorInventario(cafe);
        GestorTurnos gestorTurnos = new GestorTurnos(cafe);
        GestorUsuarios gestorUsuarios = new GestorUsuarios(cafe);

        Administrador admin = new Administrador("admin", "admin123");
        cafe.setAdministrador(admin);
        System.out.println("Administrador creado: " + admin.getLogin());

        Juego catan = new Juego("Catan", 1995, "Kosmos", "TABLERO", 3, 4, 10, false);
        Juego risk = new Juego("Risk", 1957, "Hasbro", "TABLERO", 2, 6, 10, false);
        Juego jenga = new Juego("Jenga", 1983, "Hasbro", "ACCION", 1, 8, 5, false);

        gestorInventario.agregarJuegoAPrestamo(catan, 3);
        gestorInventario.agregarJuegoAPrestamo(risk, 2);
        gestorInventario.agregarJuegoAPrestamo(jenga, 1);

        System.out.println("\n--- Estado inicial del inventario ---");
        gestorInventario.mostrarInventarioJuegos();

        System.out.println("--- Marcar Risk como robado ---");
        gestorInventario.marcarJuegoRobado(risk);
        gestorInventario.mostrarInventarioJuegos();

        System.out.println("--- Reparar Jenga ---");
        gestorInventario.repararJuego(jenga);
        gestorInventario.mostrarInventarioJuegos();

        System.out.println("--- Mover Catan adicional a préstamo ---");
        gestorInventario.moverJuegoAPrestamoDesdeVenta(catan);
        gestorInventario.mostrarInventarioJuegos();

        System.out.println("--- Gestión de turnos ---");
        gestorUsuarios.registrarEmpleado("cocinero1", "pass", "Lunes-Miercoles", "COCINERO");
        Empleado mesero1 = gestorUsuarios.registrarEmpleado("mesero1", "pass", "Jueves-Sabado", "MESERO");
        Empleado mesero2 = gestorUsuarios.registrarEmpleado("mesero2", "pass", "Lunes-Miercoles", "MESERO");

        gestorTurnos.mostrarTurnos();

        SolicitudCambioTurno solicitud = gestorTurnos.solicitarCambio(mesero1, mesero2);
        gestorTurnos.mostrarSolicitudesPendientes();

        if (solicitud != null) {
            gestorTurnos.aprobarCambio(solicitud);
            gestorTurnos.mostrarTurnos();
        }

        System.out.println("====================================");
        System.out.println("  PRUEBA 5 COMPLETADA");
        System.out.println("====================================");
    }
}