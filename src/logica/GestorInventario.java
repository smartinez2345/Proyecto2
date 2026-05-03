package logica;

import modelo.*;

public class GestorInventario {

    private Cafe cafe;

    public GestorInventario(Cafe cafe) {
        this.cafe = cafe;
    }

    public void agregarJuegoAPrestamo(Juego juego, int cantidad) {
        InventarioJuegos inv = cafe.buscarInventarioJuego(juego);
        if (inv != null) {
            inv.actualizarCantidad(cantidad);
        } else {
            cafe.agregarInventarioJuego(new InventarioJuegos(juego, cantidad));
        }
        System.out.println("Inventario préstamo actualizado: " + juego.getNombre() + " +" + cantidad);
    }

    public void agregarProductoCafeteria(ProductoCafeteria producto, int stock) {
        InventarioCafeteria inv = cafe.buscarInventarioCafeteria(producto);
        if (inv != null) {
            inv.actualizarStock(stock);
        } else {
            cafe.agregarInventarioCafeteria(new InventarioCafeteria(producto, stock));
        }
        System.out.println("Inventario cafetería actualizado: " + producto.getNombre() + " +" + stock);
    }

    public void moverJuegoAPrestamoDesdeVenta(Juego juego) {
        InventarioJuegos inv = cafe.buscarInventarioJuego(juego);
        if (inv != null) {
            inv.actualizarCantidad(1);
            System.out.println("Juego movido a préstamo: " + juego.getNombre());
        } else {
            cafe.agregarInventarioJuego(new InventarioJuegos(juego, 1));
            System.out.println("Juego agregado a préstamo: " + juego.getNombre());
        }
    }

    public void marcarJuegoRobado(Juego juego) {
        juego.setEstado("Desaparecido");
        InventarioJuegos inv = cafe.buscarInventarioJuego(juego);
        if (inv != null) {
            inv.actualizarCantidad(-1);
        }
        System.out.println("Juego marcado como desaparecido: " + juego.getNombre());
    }

    public void repararJuego(Juego juego) {
        juego.setEstado("Bueno");
        System.out.println("Juego reparado: " + juego.getNombre());
    }

    public void mostrarInventarioJuegos() {
        System.out.println("\n===== INVENTARIO DE JUEGOS =====");
        for (InventarioJuegos inv : cafe.getInventariosJuegos()) {
            System.out.println(inv.getJuego().getNombre()
                + " | Estado: " + inv.getJuego().getEstado()
                + " | Disponibles: " + inv.getCantidadDisponible()
                + " | Dificil: " + inv.getJuego().isEsDificil());
        }
        System.out.println("================================\n");
    }

    public void mostrarInventarioCafeteria() {
        System.out.println("\n===== INVENTARIO CAFETERÍA =====");
        for (InventarioCafeteria inv : cafe.getInventariosCafeteria()) {
            System.out.println(inv.getProducto().getNombre()
                + " | Precio: $" + inv.getProducto().getPrecio()
                + " | Stock: " + inv.getStock());
        }
        System.out.println("================================\n");
    }
}