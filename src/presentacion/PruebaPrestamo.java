package presentacion;

import logica.*;
import modelo.*;

public class PruebaPrestamo {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  PRUEBA 2: PRÉSTAMO DE JUEGOS");
        System.out.println("====================================\n");

        Cafe cafe = new Cafe(30);
        GestorUsuarios gestorUsuarios = new GestorUsuarios(cafe);
        GestorPrestamos gestorPrestamos = new GestorPrestamos(cafe);
        GestorInventario gestorInventario = new GestorInventario(cafe);

        Juego parques = new Juego("Parqués", 1950, "Colombia", "TABLERO", 2, 4, 5, false);
        Juego uno = new Juego("Uno", 1971, "Mattel", "CARTAS", 2, 10, 0, false);
        Juego twister = new Juego("Twister", 1966, "Hasbro", "ACCION", 2, 6, 5, false);
        Juego juegoAdultos = new Juego("Cards Against Humanity", 2011, "CAH", "CARTAS", 4, 20, 18, true);

        gestorInventario.agregarJuegoAPrestamo(parques, 2);
        gestorInventario.agregarJuegoAPrestamo(uno, 1);
        gestorInventario.agregarJuegoAPrestamo(twister, 1);
        gestorInventario.agregarJuegoAPrestamo(juegoAdultos, 1);

        Mesero mesero = (Mesero) gestorUsuarios.registrarEmpleado("mesero1", "pass", "Lunes-Viernes", "MESERO");
        mesero.agregarJuegoConocido(juegoAdultos);

        Cliente cliente = gestorUsuarios.registrarCliente("pedro99", "pass");
        Mesa mesa = gestorPrestamos.crearMesa(3, false, false, cliente);
        Prestamo prestamo = gestorPrestamos.iniciarPrestamo(mesa);

        System.out.println("\n--- Caso 1: Préstamo normal ---");
        gestorPrestamos.agregarJuegoAPrestamo(prestamo, parques);

        System.out.println("\n--- Caso 2: Juego para adultos en mesa con menores ---");
        Mesa mesaMenores = gestorPrestamos.crearMesa(2, false, true, cliente);
        Prestamo prestamoMenores = gestorPrestamos.iniciarPrestamo(mesaMenores);
        gestorPrestamos.agregarJuegoAPrestamo(prestamoMenores, juegoAdultos);

        System.out.println("\n--- Caso 3: Intentar más de 2 juegos ---");
        gestorPrestamos.agregarJuegoAPrestamo(prestamo, uno);
        gestorPrestamos.agregarJuegoAPrestamo(prestamo, twister);

        gestorPrestamos.mostrarPrestamo(prestamo);

        System.out.println("\n--- Cerrando préstamo ---");
        gestorPrestamos.cerrarPrestamo(prestamo);

        System.out.println("====================================");
        System.out.println("  PRUEBA 2 COMPLETADA");
        System.out.println("====================================");
    }
}