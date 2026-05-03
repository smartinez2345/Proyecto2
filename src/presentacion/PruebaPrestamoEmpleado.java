package presentacion;

import logica.*;
import modelo.*;

public class PruebaPrestamoEmpleado {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  PRUEBA 8: PRÉSTAMO EMPLEADO");
        System.out.println("====================================\n");

        Cafe cafe = new Cafe(30);
        GestorUsuarios gu = new GestorUsuarios(cafe);
        GestorInventario gi = new GestorInventario(cafe);
        GestorPrestamos gp = new GestorPrestamos(cafe);

        Juego ajedrez = new Juego("Ajedrez", 600, "Genérico", "TABLERO", 2, 2, 5, false);
        gi.agregarJuegoAPrestamo(ajedrez, 1);

        Empleado mesero = gu.registrarEmpleado("meseroP", "pass", "Tarde", "MESERO");
        mesero.setEnTurno(false);

        Prestamo p = gp.iniciarPrestamoEmpleado(mesero);
        if (p != null) {
            gp.agregarJuegoAPrestamoEmpleado(p, ajedrez, mesero);
            gp.mostrarPrestamo(p);
            gp.cerrarPrestamoEmpleado(p);
        }

        System.out.println("\n--- Intentar préstamo con empleado en turno ---");
        mesero.setEnTurno(true);
        Prestamo p2 = gp.iniciarPrestamoEmpleado(mesero);

        System.out.println("\n====================================");
        System.out.println("  PRUEBA 8 COMPLETADA");
        System.out.println("====================================");
    }
}