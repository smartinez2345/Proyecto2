package presentacion;

import logica.*;
import modelo.*;

public class PruebaVentaCafeteria {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  PRUEBA 3: VENTA CAFETERÍA");
        System.out.println("====================================\n");

        Cafe cafe = new Cafe(30);
        GestorUsuarios gestorUsuarios = new GestorUsuarios(cafe);
        GestorInventario gestorInventario = new GestorInventario(cafe);
        GestorVentas gestorVentas = new GestorVentas(cafe);
        GestorPrestamos gestorPrestamos = new GestorPrestamos(cafe);

        Bebida cafe1 = new Bebida("Café Americano", 5000, false, true);
        Bebida cerveza = new Bebida("Cerveza", 8000, true, false);
        Pasteleria brownie = new Pasteleria("Brownie de chocolate", 6000);
        brownie.agregarAlergeno("gluten");
        brownie.agregarAlergeno("maní");

        gestorInventario.agregarProductoCafeteria(cafe1, 10);
        gestorInventario.agregarProductoCafeteria(cerveza, 10);
        gestorInventario.agregarProductoCafeteria(brownie, 5);

        Cliente cliente = gestorUsuarios.registrarCliente("laura22", "pass");
        Mesa mesa = gestorPrestamos.crearMesa(2, false, false, cliente);

        System.out.println("\n--- Caso 1: Venta normal ---");
        Venta venta = gestorVentas.iniciarVentaCafeteria(cliente);
        gestorVentas.agregarProductoAVenta(venta, cafe1, 1, mesa);
        gestorVentas.agregarProductoAVenta(venta, brownie, 2, mesa);
        gestorVentas.finalizarVenta(venta);

        System.out.println("\n--- Caso 2: Bebida alcohólica con menores ---");
        Mesa mesaMenores = gestorPrestamos.crearMesa(3, false, true, cliente);
        Venta venta2 = gestorVentas.iniciarVentaCafeteria(cliente);
        gestorVentas.agregarProductoAVenta(venta2, cerveza, 1, mesaMenores);
        gestorVentas.finalizarVenta(venta2);

        System.out.println("\n--- Caso 3: Bebida caliente con juego de acción ---");
        // Primero creamos una mesa y prestamos un juego de acción
        Mesa mesaAccion = gestorPrestamos.crearMesa(2, false, false, cliente);
        Juego twister = new Juego("Twister", 1966, "Hasbro", "ACCION", 2, 6, 5, false);
        gestorInventario.agregarJuegoAPrestamo(twister, 1);
        Prestamo pAccion = gestorPrestamos.iniciarPrestamo(mesaAccion);
        gestorPrestamos.agregarJuegoAPrestamo(pAccion, twister);
        
        Venta venta3 = gestorVentas.iniciarVentaCafeteria(cliente);
        boolean resultado = gestorVentas.agregarProductoAVenta(venta3, cafe1, 1, mesaAccion);
        if (resultado) {
            gestorVentas.finalizarVenta(venta3);
        }
        gestorPrestamos.cerrarPrestamo(pAccion);

        System.out.println("====================================");
        System.out.println("  PRUEBA 3 COMPLETADA");
        System.out.println("====================================");
    }
}