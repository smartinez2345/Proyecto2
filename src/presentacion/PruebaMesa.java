package presentacion;

import logica.*;
import modelo.*;

public class PruebaMesa {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  PRUEBA 1: REGISTRO Y MESA");
        System.out.println("====================================\n");

        Cafe cafe = new Cafe(20);
        GestorUsuarios gestorUsuarios = new GestorUsuarios(cafe);
        GestorPrestamos gestorPrestamos = new GestorPrestamos(cafe);

        // Registrar clientes (CORREGIDO: Cliente con C mayúscula)
        Cliente cliente1 = gestorUsuarios.registrarCliente("juan123", "pass1");
        Cliente cliente2 = gestorUsuarios.registrarCliente("maria456", "pass2");

        // Login
        gestorUsuarios.login("juan123", "pass1");

        // Crear mesas (ahora el método recibe Cliente)
        Mesa mesa1 = gestorPrestamos.crearMesa(4, false, false, cliente1);
        Mesa mesa2 = gestorPrestamos.crearMesa(3, true, false, cliente2);

        // Intentar exceder capacidad
        System.out.println("\n--- Intentando exceder capacidad ---");
        gestorPrestamos.crearMesa(20, false, false, cliente1);

        // Mostrar estado
        System.out.println("\n--- Estado actual ---");
        System.out.println("Mesas activas: " + cafe.getMesas().size());
        for (Mesa m : cafe.getMesas()) {
            System.out.println(m);
        }

        // Estas líneas estaban fuera del main, ahora dentro
        System.out.println("\n====================================");
        System.out.println("  PRUEBA 1 COMPLETADA");
        System.out.println("====================================");
    }
}