package presentacion;

import logica.*;
import modelo.*;

public class PruebaPersistencia {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  PRUEBA 6: PERSISTENCIA");
        System.out.println("====================================\n");

        Cafe cafe = new Cafe(20);
        GestorUsuarios gu = new GestorUsuarios(cafe);
        GestorInventario gi = new GestorInventario(cafe);
        
        Administrador admin = new Administrador("admin", "admin123");
        cafe.setAdministrador(admin);
        gu.registrarCliente("cliente1", "pass");
        Juego uno = new Juego("Uno", 1971, "Mattel", "CARTAS", 2, 10, 0, false);
        gi.agregarJuegoAPrestamo(uno, 3);

        String archivo = "data/cafe.dat";
        cafe.guardarEstado(archivo);

        Cafe cafeCargado = Cafe.cargarEstado(archivo);
        if (cafeCargado != null) {
            System.out.println("\n--- Estado cargado ---");
            System.out.println("Capacidad: " + cafeCargado.getCapacidadMaxima());
            System.out.println("Clientes: " + cafeCargado.getClientes().size());
            System.out.println("Juegos en inventario: " + cafeCargado.getInventariosJuegos().size());
            for (InventarioJuegos inv : cafeCargado.getInventariosJuegos()) {
                System.out.println(" - " + inv.getJuego().getNombre() + ": " + inv.getCantidadDisponible());
            }
        }

        System.out.println("\n====================================");
        System.out.println("  PRUEBA 6 COMPLETADA");
        System.out.println("====================================");
    }
}