package presentacion;

import logica.*;
import modelo.*;

public class PruebaCompraJuegos {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  PRUEBA 4: COMPRA DE JUEGOS");
        System.out.println("====================================\n");

        Cafe cafe = new Cafe(30);
        GestorUsuarios gestorUsuarios = new GestorUsuarios(cafe);
        GestorVentas gestorVentas = new GestorVentas(cafe);

        Cliente cliente = gestorUsuarios.registrarCliente("sofia77", "pass");

        Juego catan = new Juego("Catan", 1995, "Kosmos", "TABLERO", 3, 4, 10, false);
        Juego dixit = new Juego("Dixit", 2008, "Libellud", "CARTAS", 3, 6, 8, false);

        System.out.println("\n--- Caso 1: Compra normal ---");
        Venta venta1 = gestorVentas.iniciarVentaJuegos(cliente);
        gestorVentas.agregarJuegoAVenta(venta1, catan, 1, 120000);
        gestorVentas.agregarJuegoAVenta(venta1, dixit, 2, 80000);
        gestorVentas.finalizarVenta(venta1);
        System.out.println("Puntos acumulados: " + cliente.getPuntosFidelidad());

        System.out.println("\n--- Caso 2: Compra con código de descuento ---");
        Venta venta2 = gestorVentas.iniciarVentaJuegos(cliente);
        gestorVentas.agregarJuegoAVenta(venta2, catan, 1, 120000);
        gestorVentas.aplicarDescuentoCodigo(venta2, false);
        gestorVentas.finalizarVenta(venta2);

        System.out.println("\n--- Caso 3: Usar puntos de fidelidad ---");
        System.out.println("Puntos disponibles antes: " + cliente.getPuntosFidelidad());
        Venta venta3 = gestorVentas.iniciarVentaJuegos(cliente);
        gestorVentas.agregarJuegoAVenta(venta3, dixit, 1, 80000);
        gestorVentas.usarPuntosFidelidad(venta3, cliente, 500);
        gestorVentas.finalizarVenta(venta3);
        System.out.println("Puntos disponibles después: " + cliente.getPuntosFidelidad());

        gestorVentas.mostrarHistorialVentas();

        System.out.println("====================================");
        System.out.println("  PRUEBA 4 COMPLETADA");
        System.out.println("====================================");
    }
}